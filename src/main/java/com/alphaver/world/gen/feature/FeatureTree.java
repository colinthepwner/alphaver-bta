package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.gen.CypressPalette;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureTree implements CypressTreeFeature {
	private static final int HEIGHT = 128;

	private final Boolean highwood;

	public FeatureTree(Boolean highwood) {
		this.highwood = highwood;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		Random highwoodRoll = new Random(rand.nextLong() + x + y + z + world.getRandomSeed());
		if (this.highwood != null && (highwoodRoll.nextInt(150) == 0 || this.highwood)) {
			return HighwoodTree.SMALL.grow(world, rand, x, y, z);
		}
		return normal(world, rand, x, y, z);
	}

	private static boolean normal(World world, Random rand, int x, int y, int z) {
		int log = CypressPalette.log();
		int leaves = CypressPalette.leaves();
		if (rand.nextInt(100) <= 5) {
			log = AVBlocks.LOG_FLAMEWOOD.id();
			leaves = AVBlocks.LEAVES_FLAMEWOOD.id();
		}
		int height = rand.nextInt(4) + 4;
		int species = rand.nextInt(100);
		if (species >= 6 && species <= 14) {
			log = AVBlocks.LOG_TEA.id();
			leaves = AVBlocks.LEAVES_TEA.id();
			height = rand.nextInt(1) + 1;
		}

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

		int below = world.getBlockId(x, y - 1, z);
		if (below != CypressPalette.grass() && below != Blocks.DIRT.id() || y >= HEIGHT - height - 1) {
			return false;
		}

		world.setBlock(x, y - 1, z, Blocks.DIRT.id());
		for (int iy = y - 3 + height; iy <= y + height; iy++) {
			int fromTop = iy - (y + height);
			int radius = 1 - fromTop / 2;
			for (int ix = x - radius; ix <= x + radius; ix++) {
				int dx = ix - x;
				for (int iz = z - radius; iz <= z + radius; iz++) {
					int dz = iz - z;

					if ((Math.abs(dx) != radius || Math.abs(dz) != radius || rand.nextInt(2) != 0 && fromTop != 0)
						&& !Blocks.solid[world.getBlockId(ix, iy, iz)]) {
						world.setBlock(ix, iy, iz, leaves);
					}
				}
			}
		}
		for (int i = 0; i < height; i++) {
			int id = world.getBlockId(x, y + i, z);
			if (id == 0 || id == leaves) {
				world.setBlock(x, y + i, z, log);
			}
		}
		return true;
	}
}
