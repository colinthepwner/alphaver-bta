package com.alphaver.world.minigame;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

final class ZombieArena {
	private ZombieArena() {}

	static final int CX = 0;
	static final int CZ = 96;

	static final int FLOOR = 64;

	static final int ROOF = FLOOR + 6;

	static final int HALF = 14;

	static final int[][] SPAWNS = {{-12, 0}, {12, 0}, {0, 12}, {-12, -12}, {12, -12}, {-12, 12}, {12, 12}};

	private static final int[][] COVER = {{-6, -6}, {5, -6}, {-6, 5}, {5, 5}};

	private static final int[] MACHINE_X = {-10, -6, 0, 6, 10};

	private static Block<?>[] machineBottoms() {
		return new Block<?>[]{AVBlocks.VENDING_HEALTH_BOOST, AVBlocks.VENDING_ARMOR, AVBlocks.WEAPON_UPGRADER, AVBlocks.VENDING_DASH,
			AVBlocks.VENDING_QUICK_REVIVE};
	}

	private static Block<?>[] machineTops() {
		return new Block<?>[]{AVBlocks.VENDING_HEALTH_BOOST_TOP, AVBlocks.VENDING_ARMOR_TOP, null, AVBlocks.VENDING_DASH_TOP,
			AVBlocks.VENDING_QUICK_REVIVE_TOP};
	}

	static void build(World world) {
		AVMinigames.focusChunks(world, CX, CZ);
		AVTravel.loadChunksAround(world, CX, CZ);
		TilePos pos = new TilePos();
		int edge = HALF + 1;
		int doorZ = CZ - HALF;
		Block<?>[] bottoms = machineBottoms();
		Block<?>[] tops = machineTops();
		for (int dx = -edge; dx <= edge; dx++) {
			for (int dz = -edge; dz <= edge; dz++) {
				int x = CX + dx;
				int z = CZ + dz;
				boolean wall = Math.abs(dx) == edge || Math.abs(dz) == edge;
				boolean corner = Math.abs(dx) == edge && Math.abs(dz) == edge;
				AVMinigames.setBlock(world, pos, x, FLOOR, z, AVBlocks.DIMENSION_FLOOR, 0);
				for (int y = FLOOR + 1; y < ROOF; y++) {
					if (dx == 0 && z == doorZ && y <= FLOOR + 2) {
						continue;
					}
					Block<?> block = null;
					if (corner) {
						block = AVBlocks.PILLAR;
					} else if (wall) {
						block = AVBlocks.DIMENSION_WALL;
					} else if (isCover(dx, dz) && y <= FLOOR + 3) {
						block = AVBlocks.PILLAR;
					} else if (dz == HALF) {
						int machine = machineIndex(dx);
						if (machine >= 0) {
							block = y == FLOOR + 1 ? bottoms[machine] : y == FLOOR + 2 ? tops[machine] : null;
						}
					}
					AVMinigames.setBlock(world, pos, x, y, z, block, 0);
				}
				boolean lamp = !wall && Math.floorMod(dx, 5) == 0 && Math.floorMod(dz, 5) == 0;
				AVMinigames.setBlock(world, pos, x, ROOF, z, lamp ? Blocks.GLOWSTONE : AVBlocks.DIMENSION_WALL, 0);
			}
		}

		AVMinigames.setDoor(world, pos, CX, FLOOR + 1, doorZ, AVBlocks.ZOMBIES_DOOR, MinigameStage.withDoorCode(0, MinigameStage.CODE_LOBBY));
	}

	private static int machineIndex(int dx) {
		for (int i = 0; i < MACHINE_X.length; i++) {
			if (MACHINE_X[i] == dx) {
				return i;
			}
		}
		return -1;
	}

	private static boolean isCover(int dx, int dz) {
		for (int[] c : COVER) {
			if (dx >= c[0] && dx <= c[0] + 1 && dz >= c[1] && dz <= c[1] + 1) {
				return true;
			}
		}
		return false;
	}
}
