package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureLowRiver {
	private final int radius;
	private final int chunkX;
	private final int chunkZ;

	public FeatureLowRiver(int radius, int chunkBlockX, int chunkBlockZ) {
		this.radius = radius;
		this.chunkX = chunkBlockX / 16;
		this.chunkZ = chunkBlockZ / 16;
	}

	public void generate(World world, Random rand, int x, int y, int z) {
		if (y < 10) {
			this.dig(world, rand, x, y, z);
		}
	}

	private void dig(World world, Random rand, int x, int y, int z) {
		int r = this.radius;
		if (r == 0) {
			return;
		}
		int lily = AVBlocks.LOW_LILY.id();
		int bedrock = Blocks.BEDROCK.id();
		int still = Blocks.FLUID_WATER_STILL.id();
		int stone = Blocks.STONE.id();
		int dirt = Blocks.DIRT.id();
		int riverbed = AVBlocks.LOW_RIVERBED.id();
		int wart = AVBlocks.LOW_WART.id();

		for (int dx = -r; dx <= r; dx++) {
			for (int dy = -r; dy <= r; dy++) {
				for (int dz = -r; dz <= r; dz++) {
					int bx = x + dx;
					int by = y + dy;
					int bz = z + dz;
					if (dx * dx + dy * dy + dz * dz > r * r
						|| bx / 16 - (this.chunkX < 0 ? 1 : 0) != this.chunkX
						|| bz / 16 - (this.chunkZ < 0 ? 1 : 0) != this.chunkZ
						|| by < 1
						|| world.getBlockId(bx, by, bz) == lily) {
						continue;
					}
					boolean notBedrock = world.getBlockId(bx, by, bz) != bedrock;
					if (!(by > 3 || notBedrock || rand.nextInt(4) < 3)) {
						continue;
					}
					if (by > 3) {
						world.setBlock(bx, by, bz, 0);
						continue;
					}
					world.setBlock(bx, by, bz, still);
					for (int ox = -1; ox <= 1; ox++) {
						for (int oz = -1; oz <= 1; oz++) {
							for (int oy = -1; oy <= 1; oy++) {
								int id = world.getBlockId(bx + ox, by + oy, bz + oz);
								if (id == stone) {
									world.setBlock(bx + ox, by + oy, bz + oz, riverbed);
								} else if (id == dirt) {
									world.setBlock(bx + ox, by + oy, bz + oz, wart);
								}
							}
						}
					}
					if (by == 3 && rand.nextInt(80) == 0) {
						world.setBlock(bx, by + 1, bz, lily);
					}
				}
			}
		}
	}
}
