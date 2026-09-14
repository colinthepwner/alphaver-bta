package com.alphaver.world.gen;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;

import java.util.Random;

public final class CypressCaves {

	private static final int RANGE = 8;

	private final int idStone = Blocks.STONE.id();
	private final int idDirt = Blocks.DIRT.id();
	private final int idGrass = CypressPalette.grass();
	private final int idWaterStill = Blocks.FLUID_WATER_STILL.id();
	private final int idWaterFlowing = Blocks.FLUID_WATER_FLOWING.id();

	private final int idLava = Blocks.FLUID_LAVA_STILL.id();

	public void generate(long seed, int chunkX, int chunkZ, short[] data) {
		Random rand = new Random(seed);
		long xMultiplier = rand.nextLong() / 2L * 2L + 1L;
		long zMultiplier = rand.nextLong() / 2L * 2L + 1L;

		for (int originX = chunkX - RANGE; originX <= chunkX + RANGE; originX++) {
			for (int originZ = chunkZ - RANGE; originZ <= chunkZ + RANGE; originZ++) {
				rand.setSeed(originX * xMultiplier + originZ * zMultiplier ^ seed);
				this.recursiveGenerate(rand, originX, originZ, chunkX, chunkZ, data);
			}
		}
	}

	private void recursiveGenerate(Random rand, int originX, int originZ, int chunkX, int chunkZ, short[] data) {
		int caves = rand.nextInt(rand.nextInt(rand.nextInt(40) + 1) + 1);
		if (rand.nextInt(15) != 0) {
			caves = 0;
		}

		for (int i = 0; i < caves; i++) {
			double x = originX * 16 + rand.nextInt(16);
			double y = rand.nextInt(rand.nextInt(120) + 8);
			double z = originZ * 16 + rand.nextInt(16);
			int branches = 1;
			if (rand.nextInt(4) == 0) {
				this.generateLargeCaveNode(rand, chunkX, chunkZ, data, x, y, z);
				branches += rand.nextInt(4);
			}

			for (int b = 0; b < branches; b++) {
				float yaw = rand.nextFloat() * (float) Math.PI * 2.0F;
				float pitch = (rand.nextFloat() - 0.5F) * 2.0F / 8.0F;
				float radius = rand.nextFloat() * 2.0F + rand.nextFloat();
				this.generateCaveNode(rand, chunkX, chunkZ, data, x, y, z, radius, yaw, pitch, 0, 0, 1.0);
			}
		}
	}

	private void generateLargeCaveNode(Random rand, int chunkX, int chunkZ, short[] data, double x, double y, double z) {
		this.generateCaveNode(rand, chunkX, chunkZ, data, x, y, z,
			1.0F + rand.nextFloat() * 6.0F, 0.0F, 0.0F, -1, -1, 0.5);
	}

	private void generateCaveNode(Random rand, int chunkX, int chunkZ, short[] data,
	                              double x, double y, double z,
	                              float radius, float yaw, float pitch,
	                              int step, int length, double verticalScale) {
		double centreX = chunkX * 16 + 8;
		double centreZ = chunkZ * 16 + 8;
		float yawDrift = 0.0F;
		float pitchDrift = 0.0F;
		Random local = new Random(rand.nextLong());
		if (length <= 0) {
			int maxLength = RANGE * 16 - 16;
			length = maxLength - local.nextInt(maxLength / 4);
		}

		boolean room = false;
		if (step == -1) {
			step = length / 2;
			room = true;
		}

		int branchAt = local.nextInt(length / 2) + length / 4;
		boolean steep = local.nextInt(6) == 0;

		for (; step < length; step++) {
			double width = 1.5 + MathHelper.sin(step * (float) Math.PI / length) * radius * 1.0F;
			double height = width * verticalScale;
			float horizontal = MathHelper.cos(pitch);
			float vertical = MathHelper.sin(pitch);
			x += MathHelper.cos(yaw) * horizontal;
			y += vertical;
			z += MathHelper.sin(yaw) * horizontal;
			if (steep) {
				pitch *= 0.92F;
			} else {
				pitch *= 0.7F;
			}

			pitch += pitchDrift * 0.1F;
			yaw += yawDrift * 0.1F;
			pitchDrift *= 0.9F;
			yawDrift *= 0.75F;
			pitchDrift += (local.nextFloat() - local.nextFloat()) * local.nextFloat() * 2.0F;
			yawDrift += (local.nextFloat() - local.nextFloat()) * local.nextFloat() * 4.0F;

			if (!room && step == branchAt && radius > 1.0F) {
				this.generateCaveNode(rand, chunkX, chunkZ, data, x, y, z,
					local.nextFloat() * 0.5F + 0.5F, yaw - (float) (Math.PI / 2), pitch / 3.0F, step, length, 1.0);
				this.generateCaveNode(rand, chunkX, chunkZ, data, x, y, z,
					local.nextFloat() * 0.5F + 0.5F, yaw + (float) (Math.PI / 2), pitch / 3.0F, step, length, 1.0);
				return;
			}

			if (!room && local.nextInt(4) == 0) {
				continue;
			}

			double dx = x - centreX;
			double dz = z - centreZ;
			double remaining = length - step;
			double reach = radius + 2.0F + 16.0F;
			if (dx * dx + dz * dz - remaining * remaining > reach * reach) {
				return;
			}

			if (x < centreX - 16.0 - width * 2.0 || z < centreZ - 16.0 - width * 2.0
				|| x > centreX + 16.0 + width * 2.0 || z > centreZ + 16.0 + width * 2.0) {
				continue;
			}

			int minX = MathHelper.floor(x - width) - chunkX * 16 - 1;
			int maxX = MathHelper.floor(x + width) - chunkX * 16 + 1;
			int minY = MathHelper.floor(y - height) - 1;
			int maxY = MathHelper.floor(y + height) + 1;
			int minZ = MathHelper.floor(z - width) - chunkZ * 16 - 1;
			int maxZ = MathHelper.floor(z + width) - chunkZ * 16 + 1;
			if (minX < 0) {
				minX = 0;
			}
			if (maxX > 16) {
				maxX = 16;
			}
			if (minY < 1) {
				minY = 1;
			}
			if (maxY > 120) {
				maxY = 120;
			}
			if (minZ < 0) {
				minZ = 0;
			}
			if (maxZ > 16) {
				maxZ = 16;
			}

			boolean water = false;
			for (int bx = minX; !water && bx < maxX; bx++) {
				for (int bz = minZ; !water && bz < maxZ; bz++) {
					for (int by = maxY + 1; !water && by >= minY - 1; by--) {
						int index = (bx * 16 + bz) * CypressTerrain.HEIGHT + by;
						if (by >= 0 && by < CypressTerrain.HEIGHT) {
							int id = data[index];
							if (id == this.idWaterFlowing || id == this.idWaterStill) {
								water = true;
							}
							if (by != minY - 1 && bx != minX && bx != maxX - 1 && bz != minZ && bz != maxZ - 1) {
								by = minY;
							}
						}
					}
				}
			}

			if (water) {
				continue;
			}

			for (int bx = minX; bx < maxX; bx++) {
				double nx = (bx + chunkX * 16 + 0.5 - x) / width;

				for (int bz = minZ; bz < maxZ; bz++) {
					double nz = (bz + chunkZ * 16 + 0.5 - z) / width;
					int index = (bx * 16 + bz) * CypressTerrain.HEIGHT + maxY;
					boolean hitGrass = false;

					for (int by = maxY - 1; by >= minY; by--) {
						double ny = (by + 0.5 - y) / height;
						if (ny > -0.7 && nx * nx + ny * ny + nz * nz < 1.0) {
							int id = data[index];
							if (id == this.idGrass) {
								hitGrass = true;
							}

							if (id == this.idStone || id == this.idDirt || id == this.idGrass) {
								if (by < 10) {
									data[index] = (short) this.idLava;
								} else {
									data[index] = 0;
									if (hitGrass && data[index - 1] == this.idDirt) {
										data[index - 1] = (short) this.idGrass;
									}
								}
							}
						}

						index--;
					}
				}
			}

			if (room) {
				break;
			}
		}
	}
}
