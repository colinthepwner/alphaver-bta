package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.gen.CypressPalette;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class CypressScatter {
	private CypressScatter() {}

	private static final int HEIGHT = 128;

	public static void plants(World world, Random rand, int x, int y, int z, Block<?> plant) {
		int id = plant.id();
		for (int i = 0; i < 64; i++) {
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			if (py < 0 || py >= HEIGHT) {
				continue;
			}
			if (world.getBlockId(px, py, pz) == 0 && plant.getLogic().canStay(world, new TilePos(px, py, pz))) {
				world.setBlock(px, py, pz, id);
			}
		}
	}

	public static void onWater(World world, Random rand, int x, int y, int z, Block<?> block) {
		int water = Blocks.FLUID_WATER_STILL.id();
		int ice = Blocks.ICE.id();
		for (int i = 0; i < 64; i++) {
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			int below = world.getBlockId(px, py - 1, pz);
			if (world.getBlockId(px, py, pz) == 0 && (below == water || below == ice)) {
				world.setBlock(px, py, pz, block.id());
			}
		}
	}

	public static void saltPillars(World world, Random rand, int x, int y, int z) {
		int salt = AVBlocks.SALT_BLOCK.id();
		int water = Blocks.FLUID_WATER_STILL.id();
		int leaves = CypressPalette.leaves();
		int lily = AVBlocks.WATER_LILY.id();
		int flame = AVBlocks.CELESTIAL_FLAME.id();
		for (int i = 0; i < 64; i++) {
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			int below = world.getBlockId(px, py - 1, pz);
			boolean supported = below != 0 && below != water && below != flame && below != leaves && below != lily;
			if (world.getBlockId(px, py, pz) == 0 && supported) {
				world.setBlock(px, py, pz, salt);
				if (rand.nextInt(100) > 50) {
					world.setBlock(px, py + 1, pz, salt);
					if (rand.nextInt(100) > 50) {
						world.setBlock(px, py + 2, pz, salt);
					}
				}
			}
		}
	}

	public static void celestialFlames(World world, Random rand, int x, int y, int z) {
		int flame = AVBlocks.CELESTIAL_FLAME.id();
		for (int i = 0; i < 4; i++) {
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			boolean allowed = false;
			if (py > 90) {
				allowed = true;
			} else if (py > 80) {
				allowed = rand.nextInt(100) > 80;
			}
			if (allowed && world.getBlockId(px, py, pz) == 0) {
				world.setBlock(px, py, pz, flame);
			}
		}
	}

	public static void sunkenMetal(World world, Random rand, int x, int y, int z) {
		int still = Blocks.FLUID_WATER_STILL.id();
		int flowing = Blocks.FLUID_WATER_FLOWING.id();
		for (int i = 0; i < 6; i++) {
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			int below = world.getBlockId(px, py - 1, pz);
			if (below == still || below == flowing) {
				continue;
			}
			for (int depth = 0; depth != 5 && world.getBlockId(px, py + depth, pz) == still; depth++) {
				if (depth == 4) {
					world.setBlock(px, py, pz, rand.nextInt(20) > 16 ? Blocks.BLOCK_GOLD.id() : Blocks.BLOCK_IRON.id());
				}
			}
		}
	}

	public static void reeds(World world, Random rand, int x, int y, int z) {
		Block<?> reed = Blocks.SUGARCANE;
		for (int i = 0; i < 20; i++) {
			int px = x + rand.nextInt(4) - rand.nextInt(4);
			int pz = z + rand.nextInt(4) - rand.nextInt(4);
			if (y < 1 || y >= HEIGHT) {
				continue;
			}
			if (world.getBlockId(px, y, pz) == 0
				&& (world.getBlockMaterial(px - 1, y - 1, pz) == Materials.WATER
				|| world.getBlockMaterial(px + 1, y - 1, pz) == Materials.WATER
				|| world.getBlockMaterial(px, y - 1, pz - 1) == Materials.WATER
				|| world.getBlockMaterial(px, y - 1, pz + 1) == Materials.WATER)) {
				int height = 2 + rand.nextInt(rand.nextInt(3) + 1);
				for (int dy = 0; dy < height; dy++) {
					if (y + dy < HEIGHT && reed.getLogic().canStay(world, new TilePos(px, y + dy, pz))) {
						world.setBlock(px, y + dy, pz, reed.id());
					}
				}
			}
		}
	}

	public static void cacti(World world, Random rand, int x, int y, int z) {
		Block<?> cactus = Blocks.CACTUS;
		for (int i = 0; i < 10; i++) {
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			if (py < 0 || py >= HEIGHT) {
				continue;
			}
			if (world.getBlockId(px, py, pz) == 0) {
				int height = 1 + rand.nextInt(rand.nextInt(3) + 1);
				for (int dy = 0; dy < height; dy++) {
					if (py + dy < HEIGHT && cactus.getLogic().canStay(world, new TilePos(px, py + dy, pz))) {
						world.setBlock(px, py + dy, pz, cactus.id());
					}
				}
			}
		}
	}

	public static void spring(World world, int x, int y, int z, Block<?> fluid) {
		int stone = Blocks.STONE.id();
		if (world.getBlockId(x, y + 1, z) != stone || world.getBlockId(x, y - 1, z) != stone) {
			return;
		}
		int here = world.getBlockId(x, y, z);
		if (here != 0 && here != stone) {
			return;
		}
		int stoneSides = 0;
		int openSides = 0;
		int[][] sides = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
		for (int[] side : sides) {
			int id = world.getBlockId(x + side[0], y, z + side[1]);
			if (id == stone) {
				stoneSides++;
			} else if (id == 0) {
				openSides++;
			}
		}
		if (stoneSides == 3 && openSides == 1) {
			world.setBlockWithNotify(x, y, z, fluid.id());
		}
	}
}
