package com.alphaver.world.minigame;

import com.alphaver.AlphaVer;
import com.alphaver.block.AVBlocks;
import com.alphaver.block.BlockLogicAlphaVerDoor;
import com.alphaver.entity.AVInventoryStash;
import com.alphaver.entity.AVStashData;
import com.alphaver.net.MessageMinigameState;
import com.alphaver.server.AVServerTeleport;
import com.alphaver.world.AVDimensions;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.hub.HubLayout;
import com.alphaver.world.minigame.map.CypressMapStore;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("deprecation")
public final class AVMinigames {
	private AVMinigames() {}

	public static final int ZOMBIES_DOOR_X = 1;
	public static final int FREERUN_DOOR_X = 3;
	private static final int SPAWN_WALL_Z = 0;

	public static final int ACTION_CHECKPOINT = 0;
	public static final int ACTION_RESTART = 1;
	public static final int ACTION_LEAVE = 2;
	public static final int ACTION_BUY = 3;

	private static final long DOOR_COOLDOWN_MILLIS = 1000L;

	private static final int DOOR_CHECK_INTERVAL = 100;

	private static final int TRIP_GRACE_TICKS = 100;
	private static final int FULL_AIR = 300;

	private static final Map<String, Visitor> VISITORS = new HashMap<>();
	private static final Map<String, Long> LAST_DOOR = new HashMap<>();

	private static final Map<World, CypressMapStore.State> LOBBY_SIGNS = new WeakHashMap<>();
	private static int doorCheckCounter;

	private static final class Visitor {

		@Nullable
		Player player;

		@Nullable
		World world;

		@Nullable
		MinigameStage stage;
		@Nullable
		ZombieGame zombies;
		@Nullable
		FreerunRun run;

		int tripTicks;

		@Nullable
		MinigameHudState sent;
	}

	public static void useDoor(@NotNull World world, @NotNull TilePosc door, @NotNull Player player, @NotNull MinigameKind kind) {
		if (world.isClientSide || !player.isAlive()) {
			return;
		}
		MinigameKind here = MinigameKind.ofWorld(world);
		if (here == null) {
			enter(world, door, player, kind);
			return;
		}
		if (!offCooldown(player)) {
			return;
		}
		int code = MinigameStage.doorCode(world.getBlockData(door));
		if (code == MinigameStage.CODE_EXIT) {
			exitToHub(player);
		} else if (code == MinigameStage.CODE_LOBBY) {
			toLobby(player, null);
		} else {
			MinigameStage stage = MinigameStage.byCode(here, code);
			if (stage == null) {
				toLobby(player, null);
			} else {
				startStage(world, player, stage);
			}
		}
	}

	private static void enter(World world, TilePosc door, Player player, MinigameKind kind) {
		int doorId = world.getBlockId(door.x(), door.y(), door.z());
		if (kind.dimension() == null) {
			return;
		}
		if (player.timeUntilPortal > 0) {

			player.handlePortal(doorId, null);
			return;
		}
		if (!AVWorlds.isHub(world) || !offCooldown(player)) {

			return;
		}

		int data = world.getBlockData(door);
		int lowerY = (data & BlockLogicAlphaVerDoor.UPPER) != 0 ? door.y() - 1 : door.y();
		int exitX = door.x();
		int exitZ = door.z();
		if ((data & BlockLogicAlphaVerDoor.AXIS_X) != 0) {
			exitX += player.x < door.x() + 0.5 ? -1 : 1;
		} else {
			exitZ += player.z < door.z() + 0.5 ? -1 : 1;
		}

		AVInventoryStash.take(player, kind.ordinal(), exitX, lowerY, exitZ);
		visitor(player).tripTicks = TRIP_GRACE_TICKS;
		player.timeInPortal = 1.0F;
		player.handlePortal(doorId, null);
	}

	private static void exitToHub(Player player) {
		String key = key(player);
		endGame(key, visitor(player));
		AVInventoryStash.clearGameItems(player);
		if (AVBlocks.HUB_DOOR == null || AVDimensions.HUB == null) {
			return;
		}
		if (player.timeUntilPortal <= 0) {
			player.timeInPortal = 1.0F;
		}

		player.handlePortal(AVBlocks.HUB_DOOR.id(), null);
	}

	private static void startStage(World world, Player player, MinigameStage stage) {
		if (!stage.available()) {
			CypressMapStore.prepare();
			player.sendMessage(switch (CypressMapStore.state()) {
				case IDLE, CONVERTING -> stage.title + " is still being converted from Cypress. Try again in a minute.";
				default -> stage.title + " needs Cypress's maps, and no copy of Cypress could be found or read.";
			});
			return;
		}
		if (stage.kind == MinigameKind.ZOMBIES && !world.getDifficulty().canHostileMobsSpawn()) {

			player.sendMessage("Zombies can't be played on Peaceful.");
			return;
		}
		String key = key(player);
		Visitor visitor = visitor(player);
		if (!AVInventoryStash.holding(player)) {

			AVInventoryStash.take(player, stage.kind.ordinal(), HubLayout.SPAWN_X, HubLayout.FLOOR_Y + 1, HubLayout.SPAWN_Z);
		}
		endGame(key, visitor);
		AVInventoryStash.clearGameItems(player);
		heal(player);
		visitor.world = world;
		visitor.stage = stage;
		try {
			if (stage.kind == MinigameKind.ZOMBIES) {
				visitor.zombies = ZombieGame.join(world, stage, key, player);
				player.sendMessage(stage.title + ": hold out as long as you can. Walk up to a machine or a wireframe door and press your "
					+ "inventory key to buy it. The leave key (K) goes back to the lobby.");
			} else {
				FreerunRun run = new FreerunRun(stage);
				visitor.run = run;
				run.start(player);
				List<Visitor> others = othersOnStage(key, visitor);
				if (!others.isEmpty()) {
					StringBuilder names = new StringBuilder();
					for (Visitor other : others) {
						names.append(names.length() > 0 ? ", " : "").append(other.player.username);
						other.player.sendMessage(player.username + " is running " + stage.title + " too.");
					}
					player.sendMessage("Also running " + stage.title + ": " + names + ".");
				}
			}
		} catch (RuntimeException e) {
			AlphaVer.LOGGER.error("Could not start {}.", stage.title, e);
			toLobby(player, "Something went wrong starting " + stage.title + ".");
		}
	}

	public static void arriveInLobby(@NotNull World world, @NotNull Entity entity, @NotNull MinigameKind kind) {
		buildLobby(world, kind);
		entity.xd = 0.0;
		entity.yd = 0.0;
		entity.zd = 0.0;
		entity.fallDistance = 0.0F;
		entity.moveTo(MinigameLobby.SPAWN_X + 0.5, MinigameLobby.FLOOR_Y + 1, MinigameLobby.SPAWN_Z + 0.5, MinigameLobby.SPAWN_YAW, 0.0F);
		if (entity instanceof Player player) {
			visitor(player).tripTicks = 0;
			player.sendMessage(kind.title + ": walk through a door to play its stage. The door behind you leads back to the Hub.");
		}
	}

	@Nullable
	public static int[] hubExitFor(@NotNull Entity entity, @Nullable Dimension from) {
		if (from == null || from != AVDimensions.ZOMBIES && from != AVDimensions.FREERUN) {
			return null;
		}
		if (!(entity instanceof AVStashData data) || !data.alphaver$hasStash()) {
			return null;
		}
		return new int[]{data.alphaver$stashExitX(), data.alphaver$stashExitY(), data.alphaver$stashExitZ()};
	}

	public static void cameOutOfGame(@NotNull Player player) {
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		if (visitor != null) {
			endGame(key, visitor);
			visitor.tripTicks = 0;
		}
		restoreStash(player, null);
	}

	public static void toLobby(@NotNull Player player, @Nullable String message) {
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		if (visitor != null) {
			endGame(key, visitor);
		}
		World world = player.world;
		MinigameKind kind = MinigameKind.ofWorld(world);
		if (kind == null) {
			return;
		}
		AVInventoryStash.clearGameItems(player);
		heal(player);
		buildLobby(world, kind);
		teleport(player, MinigameLobby.SPAWN_X + 0.5, MinigameLobby.FLOOR_Y + 1, MinigameLobby.SPAWN_Z + 0.5, MinigameLobby.SPAWN_YAW);
		if (message != null) {
			player.sendMessage(message);
		}
	}

	private static void buildLobby(World world, MinigameKind kind) {
		LOBBY_SIGNS.put(world, CypressMapStore.state());
		MinigameLobby.build(world, kind);
	}

	private static void endGame(String key, Visitor visitor) {
		ZombieGame game = visitor.zombies;
		visitor.zombies = null;
		visitor.run = null;
		visitor.stage = null;
		if (game == null) {
			return;
		}
		try {
			game.leave(key, true);
		} catch (RuntimeException e) {
			AlphaVer.LOGGER.error("Could not clean up {}.", game.stage.title, e);
		}
	}

	private static void restoreStash(Player player, @Nullable String message) {
		int health = AVInventoryStash.health(player);
		if (!AVInventoryStash.giveBack(player)) {
			return;
		}
		if (player.isAlive()) {

			player.setHealthRaw(Math.max(1, Math.min(player.getMaxHealth(), health)));
		}
		player.fallDistance = 0.0F;
		if (message != null) {
			player.sendMessage(message);
		}
	}

	private static void heal(Player player) {
		player.setHealthRaw(player.getMaxHealth());
		player.remainingFireTicks = 0;
		player.airSupply = FULL_AIR;
		player.fallDistance = 0.0F;
	}

	public static void tick(@NotNull Player player) {
		World world = player.world;
		if (world == null || world.isClientSide) {
			return;
		}
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		MinigameKind here = MinigameKind.ofWorld(world);
		boolean holding = AVInventoryStash.holding(player);

		if (visitor != null) {
			if (visitor.player != player) {
				visitor.player = player;
				visitor.sent = null;
			}
			if (visitor.stage != null && visitor.world != world) {

				endGame(key, visitor);
			}
		}

		if (here == null) {
			boolean travelling = visitor != null && visitor.tripTicks > 0;
			if (travelling) {
				visitor.tripTicks--;
			}
			if (holding && !travelling && player.isAlive()) {
				restoreStash(player, "Back from the minigames: your own things are back.");
			}
			if (visitor != null) {
				syncHud(player, key, visitor, null);
				if (visitor.tripTicks <= 0) {
					VISITORS.remove(key);
				}
			}
			if (AVWorlds.isHub(world) && ++doorCheckCounter % DOOR_CHECK_INTERVAL == 0) {
				ensureSpawnDoors(world, player);
			}
			return;
		}

		if (visitor == null) {
			visitor = new Visitor();
			visitor.player = player;
			VISITORS.put(key, visitor);
		}
		visitor.world = world;
		visitor.tripTicks = 0;
		MinigameStage stage = visitor.stage;
		if (player.isAlive() && holding) {
			if (stage == null) {
				if (!MinigameLobby.inside(player.x, player.bb.minY, player.z)) {

					toLobby(player, null);
				} else if (LOBBY_SIGNS.get(world) != CypressMapStore.state()) {

					buildLobby(world, here);
				}
			} else if (visitor.zombies != null) {
				ZombieGame game = visitor.zombies;
				if (!game.has(key)) {
					toLobby(player, stage.title + " ended while you were away.");
				} else if (!world.getDifficulty().canHostileMobsSpawn()) {
					toLobby(player, "Zombies can't be played on Peaceful.");
				} else if (!game.data.contains(player.x, player.bb.minY, player.z)) {
					toLobby(player, "You left " + stage.title + ".");
				} else {
					game.tick(key, player);
				}
			} else if (visitor.run != null && visitor.run.tick(key, player)) {
				String time = MinigameHudState.formatTime(visitor.run.lastFinish());
				for (Visitor other : othersOnStage(key, visitor)) {
					other.player.sendMessage(player.username + " finished " + stage.title + " in " + time + ".");
				}
			}
		}
		syncHud(player, key, visitor, here);
	}

	private static List<Visitor> othersOnStage(String key, Visitor visitor) {
		List<Visitor> others = new ArrayList<>();
		for (Map.Entry<String, Visitor> entry : VISITORS.entrySet()) {
			Visitor other = entry.getValue();
			Player otherPlayer = other.player;
			if (!entry.getKey().equals(key) && other.run != null && other.stage == visitor.stage && other.world == visitor.world
				&& otherPlayer != null && !otherPlayer.removed && otherPlayer.world == visitor.world) {
				others.add(other);
			}
		}
		return others;
	}

	private static void syncHud(Player player, String key, Visitor visitor, @Nullable MinigameKind here) {
		MinigameHudState state = new MinigameHudState();
		if (here != null && AVInventoryStash.holding(player)) {
			state.kind = here.ordinal();
			MinigameStage stage = visitor.stage;
			if (stage != null) {
				state.stageCode = stage.code;
				state.stageTime = stage.time;
				if (visitor.zombies != null) {
					visitor.zombies.fillHud(key, state);
				}
				if (visitor.run != null) {
					visitor.run.fillHud(state);
					List<MinigameHudState.Mate> mates = new ArrayList<>();
					for (Visitor other : othersOnStage(key, visitor)) {
						mates.add(new MinigameHudState.Mate(String.valueOf(other.player.username), 0, 0, false, other.run.checkpoint(),
							other.run.lastFinish()));
					}
					mates.sort(Comparator.comparing(MinigameHudState.Mate::name));
					state.mates = mates.isEmpty() ? List.of()
						: List.copyOf(mates.subList(0, Math.min(mates.size(), MinigameHudState.MAX_MATES)));
				}
			}
		}
		if (EnvironmentHelper.isMultiplayerServer()) {
			MinigameHudState sent = visitor.sent;
			if (sent == null || state.differsFrom(sent)) {
				visitor.sent = state;
				NetworkHandler.sendToPlayer(player, new MessageMinigameState(state));
			}
		} else {
			visitor.sent = state;
			MinigameHudState.local = state;
		}
	}

	public static boolean knockedOut(@NotNull Player player) {
		World world = player.world;
		if (world == null || world.isClientSide || !AVWorlds.isMinigame(world) || !AVInventoryStash.holding(player)) {
			return false;
		}
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		if (visitor != null && visitor.zombies != null) {
			ZombieGame game = visitor.zombies;
			if (game.knockedOut(key, player) == ZombieGame.KnockOut.DOWNED) {
				return true;
			}
			int waves = game.survivedWaves();

			visitor.zombies = null;
			visitor.run = null;
			visitor.stage = null;
			game.tellOthers(key, player.username + " is out! They survived " + waves + " waves.");
			game.leave(key, false);

			toLobby(player, "Game over! You survived " + waves + " waves");
			return true;
		}
		toLobby(player, null);
		return true;
	}

	public static int adjustDamage(@NotNull Player player, int damage) {
		World world = player.world;
		if (world == null || world.isClientSide || !AVWorlds.isMinigame(world) || !AVInventoryStash.holding(player)) {
			return damage;
		}
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		if (visitor != null && visitor.zombies != null) {
			return visitor.zombies.adjustDamage(key, damage);
		}
		return -1;
	}

	public static boolean isPlaying(@Nullable Player player) {
		World world = player == null ? null : player.world;
		if (world == null || !AVWorlds.isMinigame(world)) {
			return false;
		}
		if (world.isClientSide) {
			return MinigameHudState.local.kind != MinigameHudState.NONE;
		}
		return AVInventoryStash.holding(player);
	}

	public static void action(@NotNull Player player, int action) {
		World world = player.world;
		if (world == null || world.isClientSide || !AVWorlds.isMinigame(world) || !player.isAlive()) {
			return;
		}
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		MinigameStage stage = visitor == null ? null : visitor.stage;
		if (stage == null) {
			return;
		}
		switch (action) {
			case ACTION_CHECKPOINT -> {
				if (visitor.run != null) {
					visitor.run.toLastCheckpoint(player);
				}
			}
			case ACTION_RESTART -> {
				if (visitor.run != null) {
					visitor.run.reset(player);
				}
			}
			case ACTION_LEAVE -> toLobby(player, "You left " + stage.title + ".");
			case ACTION_BUY -> {
				if (visitor.zombies != null) {
					visitor.zombies.interact(key, player, null);
				}
			}
			default -> {

			}
		}
	}

	public static void interactAt(@NotNull Player player, int x, int y, int z) {
		World world = player.world;
		if (world == null || world.isClientSide) {
			return;
		}
		String key = key(player);
		Visitor visitor = VISITORS.get(key);
		if (visitor != null && visitor.zombies != null) {
			visitor.zombies.interact(key, player, new int[]{x, y, z});
		}
	}

	public static void zombieDied(@NotNull MobZombie zombie, @Nullable Entity killer) {
		if (!zombie.world.isClientSide) {
			ZombieGame.zombieDied(zombie, killer);
		}
	}

	public static boolean keepsZombie(@NotNull MobZombie zombie) {
		return ZombieGame.tracks(zombie);
	}

	static void teleport(@NotNull Player player, double x, double feetY, double z, float yaw) {
		if (player.world != null) {
			prepareArea(player.world, (int) Math.floor(x), (int) Math.floor(z));
		}
		player.xd = 0.0;
		player.yd = 0.0;
		player.zd = 0.0;
		player.fallDistance = 0.0F;
		if (EnvironmentHelper.isServerEnvironment()) {

			AVServerTeleport.teleport(player, x, feetY, z, yaw);
		} else {
			player.moveTo(x, feetY, z, yaw, player.xRot);
		}
	}

	static void prepareArea(World world, int x, int z) {
		focusChunks(world, x, z);
		AVTravel.loadChunksAround(world, x, z);
	}

	static void focusChunks(World world, int x, int z) {
		if (!EnvironmentHelper.isServerEnvironment()) {
			world.getChunkProvider().setCurrentChunkOver(x >> 4, z >> 4);
		}
	}

	static int standingY(World world, int x, int startY, int z) {
		prepareArea(world, x, z);
		for (int dy = 0; dy <= 8; dy++) {
			if (canStand(world, x, startY - dy, z)) {
				return startY - dy;
			}
		}
		for (int dy = 1; dy <= 8; dy++) {
			if (canStand(world, x, startY + dy, z)) {
				return startY + dy;
			}
		}
		return startY;
	}

	private static boolean canStand(World world, int x, int y, int z) {
		return world.getBlockMaterial(x, y - 1, z).isSolid() && !world.getBlockMaterial(x, y, z).isSolid()
			&& !world.getBlockMaterial(x, y + 1, z).isSolid();
	}

	static void setBlock(World world, TilePos pos, int x, int y, int z, @Nullable Block<?> block, int data) {
		int id = block == null ? 0 : block.id();
		pos.set(x, y, z);
		if (world.getBlockId(x, y, z) == id && (id == 0 || world.getBlockData(pos) == data)) {
			return;
		}
		if (block == null) {
			world.setBlockTypeNotify(pos, Blocks.AIR);
		} else {
			world.setBlockTypeDataNotify(pos, block, data);
		}
	}

	static void setDoor(World world, TilePos pos, int x, int lowerY, int z, Block<?> door, int data) {
		setBlock(world, pos, x, lowerY, z, door, data);
		setBlock(world, pos, x, lowerY + 1, z, door, data | BlockLogicAlphaVerDoor.UPPER);
	}

	private static void ensureSpawnDoors(World world, Player player) {
		if (AVBlocks.ZOMBIES_DOOR == null || AVBlocks.FREERUN_DOOR == null) {
			return;
		}
		if (Math.abs(player.x - HubLayout.SPAWN_X) > 64 || Math.abs(player.z - HubLayout.SPAWN_Z) > 64
			|| !world.getChunkProvider().isChunkLoaded(0, 0)) {
			return;
		}
		placeInSpawnWall(world, ZOMBIES_DOOR_X, AVBlocks.ZOMBIES_DOOR);
		placeInSpawnWall(world, FREERUN_DOOR_X, AVBlocks.FREERUN_DOOR);
	}

	private static void placeInSpawnWall(World world, int x, Block<?> door) {
		int y = HubLayout.FLOOR_Y + 1;
		int z = SPAWN_WALL_Z;
		int wall = AVBlocks.DIMENSION_WALL.id();
		int upper = world.getBlockId(x, y + 1, z);
		if (world.getBlockId(x, y, z) != wall || upper != wall && upper != AVBlocks.GLASS_FORTIFIED.id()
			|| world.getBlockId(x, y, z + 1) != 0 || world.getBlockId(x, y + 1, z + 1) != 0) {
			return;
		}
		setDoor(world, new TilePos(), x, y, z, door, BlockLogicAlphaVerDoor.FAR);
	}

	private static boolean offCooldown(Player player) {
		String key = key(player);
		long now = System.currentTimeMillis();
		Long last = LAST_DOOR.get(key);
		if (last != null && now - last < DOOR_COOLDOWN_MILLIS) {
			return false;
		}
		LAST_DOOR.put(key, now);
		return true;
	}

	private static Visitor visitor(Player player) {
		return VISITORS.computeIfAbsent(key(player), k -> new Visitor());
	}

	static String key(Player player) {
		return player.uuid != null ? player.uuid.toString() : String.valueOf(player.username);
	}
}
