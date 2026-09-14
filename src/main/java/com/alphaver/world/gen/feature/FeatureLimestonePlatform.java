package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureLimestonePlatform {
	private FeatureLimestonePlatform() {}

	public static boolean isChunkWaterOnly(World world, int blockX, int blockZ) {
		int still = Blocks.FLUID_WATER_STILL.id();
		for (int i = 0; i != 16; i++) {
			for (int j = 0; j != 16; j++) {
				if (world.getBlockId(blockX + i, 63, blockZ + j) != still || world.getBlockId(blockX + i, 62, blockZ + j) != still) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean generate(World world, Random rand, int x, int y, int z) {
		if (y < 62 || y > 64) {
			return false;
		}
		int still = Blocks.FLUID_WATER_STILL.id();
		int flowing = Blocks.FLUID_WATER_FLOWING.id();
		int limestone = AVBlocks.LIMESTONE.id();

		for (int attempt = 0; attempt < 2; attempt++) {
			int pz = z + rand.nextInt(8) - rand.nextInt(8);
			int py = y + rand.nextInt(4) - rand.nextInt(4);
			int px = x + rand.nextInt(8) - rand.nextInt(8);
			if (world.getBlockId(px, py, pz) != still) {
				continue;
			}
			for (int cy = py; (world.getBlockId(pz, cy, px) == still || world.getBlockId(pz, cy, px) == flowing) && cy > 10; cy--) {
				world.setBlock(px, cy, pz, limestone);
			}
			boolean wideOnX = rand.nextInt(100) <= 50;
			int radiusX = 2 + rand.nextInt(wideOnX ? 6 : 2);
			int radiusZ = 2 + rand.nextInt(!wideOnX ? 6 : 2);
			sphere(world, px, py, pz, radiusX, radiusZ, limestone, still, flowing);
		}
		return true;
	}

	private static void sphere(World world, int x, int y, int z, int radiusX, int radiusZ, int limestone, int still, int flowing) {
		for (int dx = -radiusX; dx <= radiusX; dx++) {
			for (int dy = -2; dy <= 2; dy++) {
				for (int dz = -radiusZ; dz <= radiusZ; dz++) {
					if (dx * dx + dy * dy + dz * dz > radiusX * radiusX) {
						continue;
					}
					int id = world.getBlockId(x + dx, y + dy, z + dz);
					if (id == flowing || id == still) {
						world.setBlock(x + dx, y + dy, z + dz, limestone);
					}
				}
			}
		}
	}
}
