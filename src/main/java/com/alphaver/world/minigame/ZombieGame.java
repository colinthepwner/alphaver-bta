package com.alphaver.world.minigame;

import com.alphaver.block.AVBlocks;
import com.alphaver.block.BlockLogicZombiesMachine;
import com.alphaver.item.AVItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

final class ZombieGame {

	private static final Map<MinigameStage, ZombieGame> GAMES = new EnumMap<>(MinigameStage.class);

	static final int POINTS_START = 1000;
	static final int POINTS_PER_KILL = 120;
	private static final int FIRST_WAVE = 12;

	private static final int WAVE_PAUSE = 200;
	private static final int MAX_ALIVE = 80;
	private static final double SPAWN_RANGE = 48.0;
	private static final int AMMO = 200;
	static final int DOWNED_TICKS = MinigameHudState.REVIVE_TICKS;
	private static final int FULL_HEALTH = 20;
	private static final int REGEN_AFTER = 120;
	private static final int REGEN_EVERY = 30;
	private static final int DOOR_FLOOD_LIMIT = 1024;

	private static final int[] FOCUS_COLUMNS = {0, 0, -1, 0, 1, 0, 0, -1, 0, 1, -1, -1, 1, 1, -1, 1, 1, -1};

	enum KnockOut { DOWNED, OUT }

	static final class ZombiePlayer {
		Player player;
		int points = POINTS_START;
		int perks;

		int downedTicks = -1;
		double downedX;
		double downedY;
		double downedZ;
		int regen;
		int lastHealth = FULL_HEALTH;
		boolean hasFocus;
		int focusX;
		int focusY;
		int focusZ;

		ZombiePlayer(Player player) {
			this.player = player;
		}
	}

	final World world;
	final MinigameStage stage;
	final ZombieStage data;
	final Map<String, ZombiePlayer> players = new LinkedHashMap<>();
	private final List<MobZombie> zombies = new ArrayList<>();
	private final Random rand = new Random();
	int wave = 1;
	private int waveSize = FIRST_WAVE;
	private int spawned;
	private int killed;
	private long ticks;
	private long lastWaveEnd;
	private long lastTickedAt = Long.MIN_VALUE;
	boolean eggStarted;
	int eggFound;

	private ZombieGame(World world, MinigameStage stage) {
		this.world = world;
		this.stage = stage;
		this.data = ZombieStage.of(stage);
	}

	@NotNull
	static ZombieGame join(@NotNull World world, @NotNull MinigameStage stage, @NotNull String key, @NotNull Player player) {
		ZombieGame game = GAMES.get(stage);
		if (game != null) {
			game.prunePlayers();
			if (game.world != world || game.players.isEmpty()) {

				game.end();
				game = null;
			}
		}
		if (game == null) {
			game = new ZombieGame(world, stage);
			game.data.setup(world);
			GAMES.put(stage, game);
		}
		ZombiePlayer zombiePlayer = new ZombiePlayer(player);
		if (!game.players.isEmpty()) {
			StringBuilder names = new StringBuilder();
			for (ZombiePlayer other : game.players.values()) {
				names.append(names.length() > 0 ? ", " : "").append(other.player.username);
			}
			game.tellOthers(key, player.username + " joined the game.");
			player.sendMessage("Playing with " + names + ". Your points and perks are your own; a door anyone opens is open for everyone.");
		}
		game.players.put(key, zombiePlayer);
		giveLoadout(player);
		player.setHealthRaw(FULL_HEALTH);
		double[] spawn = game.data.playerSpawn(world);
		AVMinigames.teleport(player, spawn[0], spawn[1], spawn[2], game.data.spawnYaw());
		return game;
	}

	private static void giveLoadout(Player player) {
		if (AVItems.ESSENCE_RIFLE != null) {
			player.inventory.setItem(0, new ItemStack(AVItems.ESSENCE_RIFLE, 1));
		}
		player.inventory.setItem(1, new ItemStack(Items.TOOL_SWORD_STONE, 1));
		if (AVItems.ESSENCE == null) {
			return;
		}
		int ammo = AMMO;
		for (int slot = 9; ammo > 0 && slot < 36; slot++) {
			int count = Math.min(64, ammo);
			player.inventory.setItem(slot, new ItemStack(AVItems.ESSENCE, count));
			ammo -= count;
		}
	}

	void leave(@NotNull String key, boolean tellOthers) {
		ZombiePlayer gone = this.players.remove(key);
		if (this.players.isEmpty()) {
			this.end();
		} else if (gone != null && tellOthers) {
			this.tellOthers(key, gone.player.username + " left the game.");
		}
	}

	void tellOthers(@NotNull String key, @NotNull String message) {
		for (Map.Entry<String, ZombiePlayer> entry : this.players.entrySet()) {
			if (!entry.getKey().equals(key)) {
				entry.getValue().player.sendMessage(message);
			}
		}
	}

	boolean has(@NotNull String key) {
		return GAMES.get(this.stage) == this && this.players.containsKey(key);
	}

	private void prunePlayers() {
		this.players.values().removeIf(zombiePlayer -> zombiePlayer.player.removed || zombiePlayer.player.world != this.world);
	}

	private void end() {
		for (MobZombie zombie : this.zombies) {
			if (!zombie.removed) {
				zombie.remove();
			}
		}
		this.zombies.clear();

		for (EntityItem item : this.world.getEntitiesWithinAABB(EntityItem.class, this.data.bounds())) {
			item.remove();
		}
		if (GAMES.get(this.stage) == this) {
			GAMES.remove(this.stage);
		}
	}

	static void zombieDied(@NotNull MobZombie zombie, @Nullable Entity killer) {
		for (ZombieGame game : GAMES.values()) {
			if (!game.zombies.remove(zombie)) {
				continue;
			}
			game.killed++;
			if (killer instanceof Player player) {
				ZombiePlayer zombiePlayer = game.players.get(AVMinigames.key(player));
				if (zombiePlayer != null) {
					zombiePlayer.points += POINTS_PER_KILL;
				}
			}
			return;
		}
	}

	static boolean tracks(@NotNull MobZombie zombie) {
		for (ZombieGame game : GAMES.values()) {
			if (game.zombies.contains(zombie)) {
				return true;
			}
		}
		return false;
	}

	void tick(@NotNull String key, @NotNull Player player) {
		ZombiePlayer zombiePlayer = this.players.get(key);
		if (zombiePlayer == null) {
			return;
		}
		zombiePlayer.player = player;
		long now = this.world.getWorldTime();
		if (now != this.lastTickedAt) {
			this.lastTickedAt = now;
			this.tickGame();
		}
		this.tickPlayer(zombiePlayer);
	}

	private void tickGame() {
		this.ticks++;
		this.prunePlayers();
		if (this.ticks % 20 == 0) {
			this.removeStrays();
		}

		Iterator<MobZombie> it = this.zombies.iterator();
		int vanished = 0;
		while (it.hasNext()) {
			MobZombie zombie = it.next();
			if (zombie.removed || zombie.world != this.world) {

				it.remove();
				vanished++;
			}
		}
		for (int n = 0; n < vanished; n++) {
			this.spawned--;
			if (this.spawnZombie(false)) {
				this.spawned++;
			}
		}

		if (this.ticks - this.lastWaveEnd > WAVE_PAUSE && this.spawned < this.waveSize && this.spawned - this.killed < MAX_ALIVE
			&& this.spawnZombie(false)) {
			this.spawned++;
		}
		if (this.killed >= this.waveSize) {
			this.killed = 0;
			this.spawned = 0;
			this.waveSize = Math.max(1, (int) (this.waveSize * 1.2F * this.wave));
			this.lastWaveEnd = this.ticks;
			this.wave++;
		}

		double floor = this.data.teleportBelow();
		boolean retarget = this.ticks % 20 == 0;
		for (MobZombie zombie : this.zombies) {
			if (zombie.y < floor) {
				int[] point = this.pickSpawn(true);
				if (point != null) {
					zombie.moveTo(point[0] + 0.5, point[1] + 1.0, point[2] + 0.5, zombie.yRot, 0.0F);
					zombie.xd = 0.0;
					zombie.yd = 0.0;
					zombie.zd = 0.0;
					zombie.fallDistance = 0.0F;
				}
			}
			if (retarget) {
				Player target = this.nearestStanding(zombie.x, zombie.z);
				if (target != null && zombie.getTarget() != target) {
					zombie.setTarget(target);
				}
			}
		}
	}

	private void removeStrays() {
		for (Entity entity : this.world.getEntitiesWithinAABB(net.minecraft.core.entity.Mob.class, this.data.bounds())) {
			if (entity instanceof Player || entity.removed) {
				continue;
			}
			if (!(entity instanceof MobZombie zombie) || !this.zombies.contains(zombie)) {
				entity.remove();
			}
		}
	}

	private boolean spawnZombie(boolean anyDistance) {
		int[] point = this.pickSpawn(anyDistance);
		if (point == null) {
			return false;
		}
		MobZombie zombie = new MobZombie(this.world);
		zombie.moveTo(point[0] + 0.5, point[1] + 0.5, point[2] + 0.5, this.rand.nextFloat() * 360.0F, 0.0F);
		if (!this.world.entityJoinedWorld(zombie)) {
			return false;
		}
		Player target = this.nearestStanding(zombie.x, zombie.z);
		if (target != null) {
			zombie.setTarget(target);
		}
		this.zombies.add(zombie);
		return true;
	}

	@Nullable
	private int[] pickSpawn(boolean anyDistance) {
		List<int[]> near = new ArrayList<>();
		for (int[] point : this.data.zombieSpawns()) {
			if (anyDistance || this.nearestPlayerDistance(point[0] + 0.5, point[2] + 0.5) < SPAWN_RANGE) {
				near.add(point);
			}
		}
		if (near.isEmpty()) {
			return null;
		}
		near.sort(Comparator.comparingDouble(point -> this.nearestPlayerDistance(point[0] + 0.5, point[2] + 0.5)));
		return near.get(this.rand.nextInt(Math.min(near.size(), 3)));
	}

	private double nearestPlayerDistance(double x, double z) {
		double best = Double.MAX_VALUE;
		for (ZombiePlayer zombiePlayer : this.players.values()) {
			double dx = zombiePlayer.player.x - x;
			double dz = zombiePlayer.player.z - z;
			best = Math.min(best, Math.sqrt(dx * dx + dz * dz));
		}
		return best;
	}

	@Nullable
	private Player nearestStanding(double x, double z) {
		Player best = null;
		double bestDistance = Double.MAX_VALUE;
		for (ZombiePlayer zombiePlayer : this.players.values()) {
			if (zombiePlayer.downedTicks >= 0 || !zombiePlayer.player.isAlive()) {
				continue;
			}
			double dx = zombiePlayer.player.x - x;
			double dz = zombiePlayer.player.z - z;
			double distance = dx * dx + dz * dz;
			if (distance < bestDistance) {
				bestDistance = distance;
				best = zombiePlayer.player;
			}
		}
		return best;
	}

	private void tickPlayer(ZombiePlayer zombiePlayer) {
		Player player = zombiePlayer.player;
		if (zombiePlayer.downedTicks >= 0) {

			zombiePlayer.downedTicks--;
			player.xd = 0.0;
			player.zd = 0.0;
			if (player.yd > 0.0) {
				player.yd = 0.0;
			}
			double dx = player.x - zombiePlayer.downedX;
			double dz = player.z - zombiePlayer.downedZ;
			if (dx * dx + dz * dz > 0.25) {
				AVMinigames.teleport(player, zombiePlayer.downedX, zombiePlayer.downedY, zombiePlayer.downedZ, player.yRot);
			}
			if (zombiePlayer.downedTicks < 0) {
				player.setHealthRaw(FULL_HEALTH);
				zombiePlayer.lastHealth = FULL_HEALTH;
				zombiePlayer.regen = 0;
			} else if (player.getHealth() != 1) {
				player.setHealthRaw(1);
			}
			zombiePlayer.hasFocus = false;
			return;
		}

		int health = player.getHealth();
		if (health < zombiePlayer.lastHealth) {
			zombiePlayer.regen = 0;
		}
		zombiePlayer.regen++;
		if (zombiePlayer.regen > REGEN_AFTER) {
			zombiePlayer.regen -= REGEN_EVERY;
			if (health < FULL_HEALTH) {
				player.heal(1);
			}
		}
		zombiePlayer.lastHealth = player.getHealth();

		for (int slot = 0; slot < 9; slot++) {
			ItemStack stack = player.inventory.getItem(slot);
			if (stack != null && stack.getItem() instanceof ItemToolSword && stack.getMetadata() != 0) {
				stack.setMetadata(0);
			}
		}

		if ((this.ticks & 1) == 0) {
			this.scanFocus(zombiePlayer);
		}
	}

	@NotNull
	KnockOut knockedOut(@NotNull String key, @NotNull Player player) {
		ZombiePlayer zombiePlayer = this.players.get(key);
		if (zombiePlayer == null) {
			return KnockOut.OUT;
		}
		if (zombiePlayer.downedTicks >= 0) {
			player.setHealthRaw(1);
			return KnockOut.DOWNED;
		}
		if (MinigamePerk.QUICK_REVIVE.in(zombiePlayer.perks)) {
			this.tellOthers(key, player.username + " is down!");
			zombiePlayer.perks = 0;
			zombiePlayer.downedTicks = DOWNED_TICKS;
			zombiePlayer.downedX = player.x;
			zombiePlayer.downedY = player.bb.minY;
			zombiePlayer.downedZ = player.z;
			player.setHealthRaw(1);
			return KnockOut.DOWNED;
		}
		return KnockOut.OUT;
	}

	int adjustDamage(@NotNull String key, int damage) {
		ZombiePlayer zombiePlayer = this.players.get(key);
		if (zombiePlayer == null) {
			return damage;
		}
		if (zombiePlayer.downedTicks >= 0) {
			return -1;
		}
		if (MinigamePerk.HEALTH_BOOST.in(zombiePlayer.perks)) {
			return Math.max(1, damage / 2);
		}
		return damage;
	}

	boolean mayDash(@NotNull String key) {
		ZombiePlayer zombiePlayer = this.players.get(key);
		return zombiePlayer != null && MinigamePerk.DASH.in(zombiePlayer.perks);
	}

	private boolean isInteractive(int id) {
		if (id == 0) {
			return false;
		}
		if (AVBlocks.WIREFRAME_DOOR != null && id == AVBlocks.WIREFRAME_DOOR.id()) {
			return true;
		}
		BlockLogicZombiesMachine.Kind kind = BlockLogicZombiesMachine.kindOf(net.minecraft.core.block.Blocks.getBlock(id));
		return kind != null && kind.interactive;
	}

	private void scanFocus(ZombiePlayer zombiePlayer) {
		Player player = zombiePlayer.player;
		int blockX = (int) Math.floor(player.x);
		int blockZ = (int) Math.floor(player.z);
		int eye = (int) Math.ceil(player.bb.minY + 1.62);
		zombiePlayer.hasFocus = false;
		for (int column = 0; column < FOCUS_COLUMNS.length / 2; column++) {
			for (int dy = -2; dy <= 0; dy++) {
				int x = blockX + FOCUS_COLUMNS[column * 2];
				int y = eye + dy;
				int z = blockZ + FOCUS_COLUMNS[column * 2 + 1];
				if (this.isInteractive(this.world.getBlockId(x, y, z))) {
					zombiePlayer.hasFocus = true;
					zombiePlayer.focusX = x;
					zombiePlayer.focusY = y;
					zombiePlayer.focusZ = z;
				}
			}
		}
	}

	@NotNull
	private String prompt(ZombiePlayer zombiePlayer) {
		if (!zombiePlayer.hasFocus || zombiePlayer.downedTicks >= 0) {
			return "";
		}
		Block<?> block = this.world.getBlockType(new TilePos(zombiePlayer.focusX, zombiePlayer.focusY, zombiePlayer.focusZ));
		String key = "[" + MinigameHudState.KEY + "]";
		if (block == AVBlocks.WIREFRAME_DOOR) {
			return key + " Open [" + this.data.doorPrice(zombiePlayer.focusX, zombiePlayer.focusZ) + " points]";
		}
		BlockLogicZombiesMachine.Kind kind = BlockLogicZombiesMachine.kindOf(block);
		MinigamePerk perk = MinigamePerk.sold(kind);
		if (perk != null) {
			return perk.in(zombiePlayer.perks) ? "You already have this perk." : key + " buy " + perk.promptName + " [" + perk.price + " points]";
		}
		if (kind == BlockLogicZombiesMachine.Kind.UPGRADER) {
			ItemStack held = zombiePlayer.player.inventory.getCurrentItem();
			Upgrade upgrade = held == null ? null : upgradeOf(held.itemID);
			return upgrade == null ? "Cannot upgrade this." : key + " Upgrade [" + upgrade.price + " points]";
		}
		if (kind == BlockLogicZombiesMachine.Kind.GIVER) {
			return key + " Interact";
		}
		return "";
	}

	void interact(@NotNull String key, @NotNull Player player, @Nullable int[] clicked) {
		ZombiePlayer zombiePlayer = this.players.get(key);
		if (zombiePlayer == null || zombiePlayer.downedTicks >= 0) {
			return;
		}
		int x;
		int y;
		int z;
		if (clicked != null) {
			x = clicked[0];
			y = clicked[1];
			z = clicked[2];
			double dx = x + 0.5 - player.x;
			double dy = y + 0.5 - (player.bb.minY + 1.62);
			double dz = z + 0.5 - player.z;
			if (dx * dx + dy * dy + dz * dz > 36.0 || !this.isInteractive(this.world.getBlockId(x, y, z))) {
				return;
			}
		} else {
			this.scanFocus(zombiePlayer);
			if (!zombiePlayer.hasFocus) {
				return;
			}
			x = zombiePlayer.focusX;
			y = zombiePlayer.focusY;
			z = zombiePlayer.focusZ;
		}

		TilePos pos = new TilePos(x, y, z);
		Block<?> block = this.world.getBlockType(pos);
		if (block == AVBlocks.WIREFRAME_DOOR) {
			int price = this.data.doorPrice(x, z);
			if (zombiePlayer.points < price) {
				player.sendMessage("Not enough points!");
				return;
			}
			zombiePlayer.points -= price;
			this.openDoor(x, y, z);
			zombiePlayer.hasFocus = false;
			return;
		}
		BlockLogicZombiesMachine.Kind kind = BlockLogicZombiesMachine.kindOf(block);
		MinigamePerk perk = MinigamePerk.sold(kind);
		if (perk != null) {
			if (perk.in(zombiePlayer.perks)) {
				return;
			}
			if (zombiePlayer.points < perk.price) {
				player.sendMessage("Not enough points!");
				return;
			}
			zombiePlayer.points -= perk.price;
			zombiePlayer.perks |= perk.bit();
			return;
		}
		if (kind == BlockLogicZombiesMachine.Kind.UPGRADER) {
			int slot = player.inventory.getCurrentSlot();
			ItemStack held = player.inventory.getItem(slot);
			Upgrade upgrade = held == null ? null : upgradeOf(held.itemID);
			if (upgrade == null) {
				return;
			}
			if (zombiePlayer.points < upgrade.price) {
				player.sendMessage("Not enough points!");
				return;
			}
			zombiePlayer.points -= upgrade.price;
			player.inventory.setItem(slot, new ItemStack(upgrade.to, 1));
			return;
		}
		if (kind == BlockLogicZombiesMachine.Kind.GIVER) {
			this.data.interactGiver(this, player, x, y, z);
			zombiePlayer.hasFocus = false;
		}
	}

	private void openDoor(int x, int y, int z) {
		int door = AVBlocks.WIREFRAME_DOOR.id();
		Deque<int[]> pending = new ArrayDeque<>();
		Set<Long> seen = new HashSet<>();
		pending.add(new int[]{x, y, z});
		TilePos pos = new TilePos();
		int removed = 0;
		while (!pending.isEmpty() && removed < DOOR_FLOOD_LIMIT) {
			int[] cell = pending.poll();
			long cellKey = ((long) cell[0] & 0x3FFFFFFL) << 38 | ((long) cell[1] & 0xFFFL) << 26 | ((long) cell[2] & 0x3FFFFFFL);
			if (!seen.add(cellKey) || this.world.getBlockId(cell[0], cell[1], cell[2]) != door) {
				continue;
			}
			AVMinigames.setBlock(this.world, pos, cell[0], cell[1], cell[2], null, 0);
			removed++;
			pending.add(new int[]{cell[0] + 1, cell[1], cell[2]});
			pending.add(new int[]{cell[0] - 1, cell[1], cell[2]});
			pending.add(new int[]{cell[0], cell[1] + 1, cell[2]});
			pending.add(new int[]{cell[0], cell[1] - 1, cell[2]});
			pending.add(new int[]{cell[0], cell[1], cell[2] + 1});
			pending.add(new int[]{cell[0], cell[1], cell[2] - 1});
		}
	}

	record Upgrade(Item to, int price) {}

	@Nullable
	static Upgrade upgradeOf(int itemId) {
		if (itemId == Items.TOOL_SWORD_WOOD.id) {
			return new Upgrade(Items.TOOL_SWORD_STONE, 500);
		}
		if (itemId == Items.TOOL_SWORD_STONE.id) {
			return new Upgrade(Items.TOOL_SWORD_IRON, 1000);
		}
		if (itemId == Items.TOOL_SWORD_IRON.id) {
			return new Upgrade(Items.TOOL_SWORD_DIAMOND, 5000);
		}
		if (itemId == Items.TOOL_SWORD_GOLD.id) {
			return new Upgrade(Items.TOOL_SWORD_STONE, 750);
		}
		if (itemId == Items.TOOL_SWORD_DIAMOND.id && AVItems.OBSIDIAN_SWORD != null) {
			return new Upgrade(AVItems.OBSIDIAN_SWORD, 15000);
		}
		if (AVItems.ESSENCE_RIFLE != null && itemId == AVItems.ESSENCE_RIFLE.id && AVItems.GRAY_GUN != null) {
			return new Upgrade(AVItems.GRAY_GUN, 16000);
		}
		return null;
	}

	void notifyAll(@NotNull String sound, @NotNull String status) {
		for (ZombiePlayer zombiePlayer : this.players.values()) {
			ZombieStage.soundFor(zombiePlayer.player, sound);
			zombiePlayer.player.sendStatusMessage(status);
		}
	}

	void fillHud(@NotNull String key, @NotNull MinigameHudState hud) {
		ZombiePlayer zombiePlayer = this.players.get(key);
		hud.wave = this.wave;
		hud.zombiesLeft = Math.max(0, this.waveSize - this.killed);
		hud.nextWaveTicks = this.nextWaveTicks();
		if (zombiePlayer != null) {
			hud.points = zombiePlayer.points;
			hud.perks = zombiePlayer.perks;
			hud.downedTicks = Math.max(0, zombiePlayer.downedTicks);
			hud.prompt = this.prompt(zombiePlayer);
		}

		List<MinigameHudState.Mate> mates = new ArrayList<>();
		for (Map.Entry<String, ZombiePlayer> entry : this.players.entrySet()) {
			if (entry.getKey().equals(key) || mates.size() >= MinigameHudState.MAX_MATES) {
				continue;
			}
			ZombiePlayer other = entry.getValue();
			mates.add(new MinigameHudState.Mate(String.valueOf(other.player.username), other.points, other.perks, other.downedTicks >= 0,
				0, -1));
		}
		hud.mates = mates.isEmpty() ? List.of() : List.copyOf(mates);
	}

	int survivedWaves() {
		return this.wave - 1;
	}

	int nextWaveTicks() {
		long waited = this.ticks - this.lastWaveEnd;
		return this.spawned == 0 && waited <= WAVE_PAUSE ? (int) (WAVE_PAUSE + 1 - waited) : 0;
	}
}
