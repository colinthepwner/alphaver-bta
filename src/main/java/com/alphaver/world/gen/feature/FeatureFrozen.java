package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureFrozen {
	private FeatureFrozen() {}

	private static final int HEIGHT = 128;

	public static boolean frigidHighwood(World world, Random rand, int x, int y, int z) {
		rand.nextLong();

		int trunk = AVBlocks.FRIGID_TRUNK.id();
		int leaves = AVBlocks.FRIGID_LEAVES.id();
		int snow = Blocks.BLOCK_SNOW.id();
		int height = rand.nextInt(4) + 4;
		if (y < 1 || y + height + 1 > HEIGHT) {
			return false;
		}

		boolean clear = true;
		for (int iy = y; iy <= y + 1 + height; iy++) {
			int radius = 1;
			if (iy == y) {
				radius = 0;
			}
			if (iy >= y + 1 + height - 2) {
				radius = 2;
			}
			for (int ix = x - radius; ix <= x + radius && clear; ix++) {
				for (int iz = z - radius; iz <= z + radius && clear; iz++) {
					if (iy >= 0 && iy < HEIGHT) {
						int id = world.getBlockId(ix, iy, iz);
						if (id != 0 && id != leaves) {
							clear = false;
						}
					} else {
						clear = false;
					}
				}
			}
		}
		if (!clear) {
			return false;
		}
		if (world.getBlockId(x, y - 1, z) != snow || y >= HEIGHT - height - 1) {
			return false;
		}

		world.setBlock(x, y - 1, z, snow);
		for (int iy = y - 1 + height; iy <= y + height; iy++) {
			if (!Blocks.solid[world.getBlockId(x - 1, iy, z - 1)]) {
				world.setBlock(x, iy, z, leaves);
			}
		}
		for (int i = 0; i < height; i++) {
			int id = world.getBlockId(x, y + i, z);
			if (id == 0 || id == leaves) {
				world.setBlock(x, y + i, z, trunk);
			}
		}
		return true;
	}
}
