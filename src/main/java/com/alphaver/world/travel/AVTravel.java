package com.alphaver.world.travel;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.AVDimensions;
import com.alphaver.world.hub.HubLayout;
import com.alphaver.world.minigame.AVMinigames;
import com.alphaver.world.minigame.MinigameKind;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.ChunkPos;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;

@SuppressWarnings("deprecation")
public final class AVTravel {
	private AVTravel() {}

	private static final int DOOR_SEARCH_RADIUS = 12;

	private static final int FOOTING_SEARCH = 8;

	public static boolean handle(World world, Entity entity, Dimension oldDim, Dimension newDim) {
		if (AVDimensions.HUB == null || AVDimensions.CYPRESS == null) {
			return false;
		}
		if (newDim == AVDimensions.HUB) {
			int[] exit = AVMinigames.hubExitFor(entity, oldDim);
			if (exit != null && entity instanceof Player player) {

				int[] spot = besideDoor(world, exit[0], exit[1], exit[2]);
				land(entity, spot[0] + 0.5, spot[1], spot[2] + 0.5, entity.yRot);
				AVMinigames.cameOutOfGame(player);
			} else {
				arriveInHub(world, entity);
			}
			return true;
		}
		if (newDim == AVDimensions.CYPRESS) {
			arriveInCypress(world, entity);
			return true;
		}
		MinigameKind game = newDim == AVDimensions.ZOMBIES ? MinigameKind.ZOMBIES
			: newDim == AVDimensions.FREERUN ? MinigameKind.FREERUN : null;
		if (game != null) {
			AVMinigames.arriveInLobby(world, entity, game);
			return true;
		}
		if (oldDim == AVDimensions.HUB || oldDim == AVDimensions.CYPRESS || oldDim == AVDimensions.ZOMBIES
			|| oldDim == AVDimensions.FREERUN) {
			arriveHome(world, entity);
			return true;
		}
		return false;
	}

	public static void recordReturn(Entity entity, int doorX, int doorLowerY, int doorZ, boolean axisX) {
		if (!(entity instanceof AVTravelData data)) {
			return;
		}
		int x = doorX;
		int z = doorZ;
		if (axisX) {
			x += entity.x < doorX + 0.5 ? -1 : 1;
		} else {
			z += entity.z < doorZ + 0.5 ? -1 : 1;
		}
		data.alphaver$setReturn(x, doorLowerY, z);
	}

	private static void arriveInHub(World world, Entity entity) {

		int[] spot = HubLayout.forSeed(world.getRandomSeed()).landingNear(MathHelper.floor(entity.x), MathHelper.floor(entity.z));
		loadAround(world, spot[0], spot[1]);
		land(entity, spot[0] + 0.5, HubLayout.FLOOR_Y + 1, spot[1] + 0.5, entity.yRot);
	}

	private static void arriveInCypress(World world, Entity entity) {
		AVTravelData home = entity instanceof Player && entity instanceof AVTravelData data && data.alphaver$homeTripPending() ? data : null;
		int[] spot;
		if (home != null && home.alphaver$hasHome()) {
			spot = besideDoor(world, home.alphaver$homeX(), home.alphaver$homeY(), home.alphaver$homeZ());
		} else {
			int x = MathHelper.floor(entity.x);
			int z = MathHelper.floor(entity.z);
			if (home != null) {
				int[] ground = findLand(world, x, z);
				x = ground[0];
				z = ground[1];
			}
			spot = cypressLanding(world, x, z, home == null);
		}
		land(entity, spot[0] + 0.5, spot[1], spot[2] + 0.5, entity.yRot);
		if (home != null) {
			home.alphaver$setHomeTripPending(false);
			if (!home.alphaver$hasHome()) {
				home.alphaver$setHome(spot[0], spot[1], spot[2]);
			}
		}
	}

	static int[] cypressLanding(World world, int x, int z, boolean door) {
		loadAround(world, x, z);
		int y;
		int[] footing = findFooting(world, x, z);
		if (footing != null) {
			x = footing[0];
			y = footing[1];
			z = footing[2];
		} else {

			y = Math.max(2, Math.min(world.getHeightValue(x, z), world.getHeightBlocks() - 4));
			groundUnder(world, x, y, z, 1);
			world.setBlockWithNotify(x, y, z, 0);
			world.setBlockWithNotify(x, y + 1, z, 0);
		}
		if (door) {
			buildDoorUnlessNear(world, x, y, z);
		}
		return new int[]{x, y, z};
	}

	private static void buildDoorUnlessNear(World world, int x, int y, int z) {
		if (doorNear(world, x, y, z)) {
			return;
		}
		groundUnder(world, x + 2, y, z, 0);
		world.setBlockWithNotify(x + 2, y, z, 0);
		world.setBlockWithNotify(x + 2, y + 1, z, 0);
		AVHubDoors.place(world, x + 2, y, z, true, true);
	}

	private static final int LAND_STEP = 48;
	private static final int LAND_RINGS = 8;

	private static int[] findLand(World world, int x, int z) {
		for (int ring = 0; ring <= LAND_RINGS; ring++) {
			for (int dx = -ring; dx <= ring; dx++) {
				for (int dz = -ring; dz <= ring; dz++) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) {
						continue;
					}
					int probeX = x + dx * LAND_STEP;
					int probeZ = z + dz * LAND_STEP;
					loadColumn(world, probeX, probeZ);
					if (surfaceFooting(world, probeX, probeZ) >= 0) {
						return new int[]{probeX, probeZ};
					}
				}
			}
		}
		return new int[]{x, z};
	}

	private static void loadColumn(World world, int x, int z) {
		int chunkX = Math.floorDiv(x, 16);
		int chunkZ = Math.floorDiv(z, 16);
		if (!EnvironmentHelper.isServerEnvironment()) {
			world.getChunkProvider().setCurrentChunkOver(chunkX, chunkZ);
		}
		world.getChunkFromBlockCoords(x, z);
		if (!world.getChunkProvider().isChunkLoaded(chunkX, chunkZ)) {
			world.getChunkProvider().prepareChunk(new ChunkPos(chunkX, chunkZ), true);
		}
	}

	private static void arriveHome(World world, Entity entity) {
		if (entity instanceof AVTravelData data && data.alphaver$hasReturn()) {
			int[] spot = besideDoor(world, data.alphaver$returnX(), data.alphaver$returnY(), data.alphaver$returnZ());
			data.alphaver$clearReturn();
			land(entity, spot[0] + 0.5, spot[1], spot[2] + 0.5, entity.yRot);
			return;
		}
		int x = MathHelper.floor(entity.x);
		int z = MathHelper.floor(entity.z);
		loadAround(world, x, z);
		int[] footing = findFooting(world, x, z);
		if (footing != null) {
			land(entity, footing[0] + 0.5, footing[1], footing[2] + 0.5, entity.yRot);
		} else {

			land(entity, x + 0.5, world.getHeightValue(x, z), z + 0.5, entity.yRot);
		}
	}

	public static int[] besideDoor(World world, int x, int y, int z) {
		loadAround(world, x, z);
		if (canStandAt(world, x, y, z)) {
			return new int[]{x, y, z};
		}
		int[] footing = findFooting(world, x, z);
		return footing != null ? footing : new int[]{x, world.getHeightValue(x, z), z};
	}

	public static void loadChunksAround(World world, int x, int z) {
		loadAround(world, x, z);
	}

	private static void loadAround(World world, int x, int z) {
		if (!EnvironmentHelper.isServerEnvironment()) {
			world.getChunkProvider().setCurrentChunkOver(Math.floorDiv(x, 16), Math.floorDiv(z, 16));
		}
		for (int dx = -16; dx <= 16; dx += 16) {
			for (int dz = -16; dz <= 16; dz += 16) {
				world.getChunkFromBlockCoords(x + dx, z + dz);
				int chunkX = Math.floorDiv(x + dx, 16);
				int chunkZ = Math.floorDiv(z + dz, 16);
				if (!world.getChunkProvider().isChunkLoaded(chunkX, chunkZ)) {
					world.getChunkProvider().prepareChunk(new ChunkPos(chunkX, chunkZ), true);
				}
			}
		}
	}

	@Nullable
	private static int[] findFooting(World world, int x, int z) {
		for (int ring = 0; ring <= FOOTING_SEARCH; ring++) {
			for (int dx = -ring; dx <= ring; dx++) {
				for (int dz = -ring; dz <= ring; dz++) {
					if (Math.max(Math.abs(dx), Math.abs(dz)) != ring) {
						continue;
					}
					int feet = surfaceFooting(world, x + dx, z + dz);
					if (feet >= 0) {
						return new int[]{x + dx, feet, z + dz};
					}
				}
			}
		}
		return null;
	}

	private static int surfaceFooting(World world, int x, int z) {
		for (int y = world.getHeightBlocks() - 1; y >= 1; y--) {
			Material material = world.getBlockMaterial(x, y, z);
			if (material == Materials.LEAVES || !(material.isSolid() || material.isLiquid())) {
				continue;
			}
			return material.isSolid() && isFree(world, x, y + 1, z) && isFree(world, x, y + 2, z) ? y + 1 : -1;
		}
		return -1;
	}

	private static boolean canStandAt(World world, int x, int y, int z) {
		Material ground = world.getBlockMaterial(x, y - 1, z);
		return ground.isSolid() && ground != Materials.LEAVES && isFree(world, x, y, z) && isFree(world, x, y + 1, z);
	}

	private static boolean isFree(World world, int x, int y, int z) {
		Material material = world.getBlockMaterial(x, y, z);
		return !material.isSolid() && !material.isLiquid() && material != Materials.LEAVES && !isDoor(world.getBlockId(x, y, z));
	}

	private static boolean isDoor(int id) {
		return AVBlocks.HUB_DOOR != null
			&& (id == AVBlocks.HUB_DOOR.id() || id == AVBlocks.CYPRESS_DOOR_LOWER.id() || id == AVBlocks.CYPRESS_DOOR_UPPER.id()
			|| (AVBlocks.ZOMBIES_DOOR != null && (id == AVBlocks.ZOMBIES_DOOR.id() || id == AVBlocks.FREERUN_DOOR.id())));
	}

	private static void land(Entity entity, double x, double feetY, double z, float yaw) {
		entity.xd = 0.0;
		entity.yd = 0.0;
		entity.zd = 0.0;
		entity.moveTo(x, feetY, z, yaw, 0.0F);
	}

	private static void groundUnder(World world, int x, int y, int z, int radius) {
		int floor = AVBlocks.DIMENSION_FLOOR.id();
		for (int dx = -radius; dx <= radius; dx++) {
			for (int dz = -radius; dz <= radius; dz++) {
				if (!world.getBlockMaterial(x + dx, y - 1, z + dz).isSolid()) {
					world.setBlockWithNotify(x + dx, y - 1, z + dz, floor);
				}
			}
		}
	}

	private static boolean doorNear(World world, int x, int y, int z) {
		int door = AVBlocks.HUB_DOOR.id();
		for (int dx = -DOOR_SEARCH_RADIUS; dx <= DOOR_SEARCH_RADIUS; dx++) {
			for (int dz = -DOOR_SEARCH_RADIUS; dz <= DOOR_SEARCH_RADIUS; dz++) {
				for (int dy = -4; dy <= 4; dy++) {
					if (world.getBlockId(x + dx, y + dy, z + dz) == door) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
