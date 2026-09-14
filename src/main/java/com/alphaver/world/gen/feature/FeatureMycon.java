package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.gen.noise.AlphaNoiseOctaves;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureMycon {
	private FeatureMycon() {}

	public static void generate(World world, Random rand, int chunkX, int chunkZ, AlphaNoiseOctaves noise) {
		int lowMycon = AVBlocks.LOW_MYCON.id();
		int vine = AVBlocks.LOW_VINE.id();
		boolean grown = false;
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				int x = chunkX * 16 + i;
				int z = chunkZ * 16 + j;
				if (noise.generateNoiseOctaves(x, z) <= 0.1) {
					continue;
				}
				world.setBlock(x, 3, z, lowMycon);
				if (rand.nextInt(400) == 0 && !grown) {
					giant(world, rand, x, z);
					grown = true;
				} else if (rand.nextInt(120) == 0 && world.getBlockId(x, 4, z) == 0) {
					world.setBlockWithNotify(x, 4, z, vine);
				}
			}
		}
	}

	private static void giant(World world, Random rand, int x, int z) {
		for (int dx = -1; dx <= 1; dx++) {
			for (int dz = -1; dz <= 1; dz++) {
				for (int dy = 0; dy <= 1; dy++) {
					cap(world, x + dx, 7 + dy, z + dz);
				}
			}
		}
		for (int arm = 0; arm < 3; arm++) {
			int signX = rand.nextInt(2) * 2 - 1;
			int signZ = rand.nextInt(2) * 2 - 1;
			int lengthX = rand.nextInt(2) + 2;
			int lengthZ = rand.nextInt(2) + 2;
			for (int i = 0; i < lengthX; i++) {
				for (int j = 0; j < lengthZ; j++) {
					cap(world, x + i * signX, 7, z + j * signZ);
				}
			}
		}
		int glowing = AVBlocks.MYCON_CAP_GLOWING.id();
		for (int k = -2; k <= 2; k++) {
			if (world.getBlockId(x + k, 7, z + 2) == glowing && rand.nextInt(6) == 0) {
				cap(world, x + k, 8, z + 2);
			}
			if (world.getBlockId(x + k, 7, z - 2) == glowing && rand.nextInt(6) == 0) {
				cap(world, x + k, 8, z - 2);
			}
			if (world.getBlockId(x + 2, 7, z + k) == glowing && rand.nextInt(6) == 0) {
				cap(world, x + 2, 8, z + k);
			}
			if (world.getBlockId(x - 2, 7, z + k) == glowing && rand.nextInt(6) == 0) {
				cap(world, x - 2, 8, z + k);
			}
		}
		int offsetX = -rand.nextInt(2);
		int offsetZ = -rand.nextInt(2);
		for (int i = offsetX; i < 2 + offsetX; i++) {
			for (int j = offsetZ; j < 2 + offsetZ; j++) {
				cap(world, x + i, 9, z + j);
			}
		}
		int stem = AVBlocks.MYCON_STEM.id();
		for (int y = 4; y < 10; y++) {
			world.setBlock(x, y, z, stem);
		}
	}

	private static void cap(World world, int x, int y, int z) {
		int cap = AVBlocks.MYCON_CAP.id();
		world.setBlockWithNotify(x, y, z, AVBlocks.MYCON_CAP_GLOWING.id());
		if (world.getBlockId(x - 1, y, z) == 0) {
			world.setBlockWithNotify(x - 1, y, z, cap);
		}
		if (world.getBlockId(x, y, z - 1) == 0) {
			world.setBlockWithNotify(x, y, z - 1, cap);
		}
		if (world.getBlockId(x + 1, y, z) == 0) {
			world.setBlockWithNotify(x + 1, y, z, cap);
		}
		if (world.getBlockId(x, y, z + 1) == 0) {
			world.setBlockWithNotify(x, y, z + 1, cap);
		}
		if (world.getBlockId(x, y + 1, z) == 0) {
			world.setBlockWithNotify(x, y + 1, z, cap);
		}
	}
}
