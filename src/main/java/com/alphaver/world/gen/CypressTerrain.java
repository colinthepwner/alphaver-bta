package com.alphaver.world.gen;

import com.alphaver.world.gen.noise.AlphaNoiseOctaves;
import net.minecraft.core.block.Blocks;

import java.util.Random;

public final class CypressTerrain {
	public static final int HEIGHT = 128;
	public static final int SEA_LEVEL = 64;

	private final AlphaNoiseOctaves noiseGen1;
	private final AlphaNoiseOctaves noiseGen2;
	private final AlphaNoiseOctaves noiseGen3;
	private final AlphaNoiseOctaves noiseGen4;
	private final AlphaNoiseOctaves noiseGen5;
	private final AlphaNoiseOctaves noiseGen6;
	private final AlphaNoiseOctaves noiseGen7;

	private final AlphaNoiseOctaves mobSpawnerNoise;

	private final AlphaNoiseOctaves undergroundNoise;

	private final Random constructorRandom;

	private final boolean sandWorld;

	private final int idStone;
	private final int idWater;
	private final int idSand;
	private final int idGravel;
	private final int idGrass;
	private final int idDirt;
	private final int idBedrock;
	private final int idIce;
	private final int idSnowBlock;

	public CypressTerrain(long seed, boolean sandWorld) {
		this.constructorRandom = new Random(seed);
		Random rand = this.constructorRandom;
		this.noiseGen1 = new AlphaNoiseOctaves(rand, 16);
		this.noiseGen2 = new AlphaNoiseOctaves(rand, 16);
		this.noiseGen3 = new AlphaNoiseOctaves(rand, 8);
		this.noiseGen4 = new AlphaNoiseOctaves(rand, 4);
		this.noiseGen5 = new AlphaNoiseOctaves(rand, 4);
		this.noiseGen6 = new AlphaNoiseOctaves(rand, 10);
		this.noiseGen7 = new AlphaNoiseOctaves(rand, 16);
		this.mobSpawnerNoise = new AlphaNoiseOctaves(rand, 5);
		this.undergroundNoise = new AlphaNoiseOctaves(rand, 8);

		this.sandWorld = sandWorld;

		this.idStone = Blocks.STONE.id();
		this.idWater = Blocks.FLUID_WATER_STILL.id();
		this.idSand = Blocks.SAND.id();
		this.idGravel = Blocks.GRAVEL.id();
		this.idGrass = CypressPalette.grass();
		this.idDirt = Blocks.DIRT.id();
		this.idBedrock = Blocks.BEDROCK.id();
		this.idIce = Blocks.ICE.id();
		this.idSnowBlock = Blocks.BLOCK_SNOW.id();
	}

	public Random constructorRandom() {
		return this.constructorRandom;
	}

	public AlphaNoiseOctaves undergroundNoise() {
		return this.undergroundNoise;
	}

	public boolean isSandWorld() {
		return this.sandWorld;
	}

	public void generateTerrain(int chunkX, int chunkZ, short[] blocks) {
		final int cellsPerChunk = 4;
		final int xSize = cellsPerChunk + 1;
		final int ySize = 17;
		final int zSize = cellsPerChunk + 1;
		double[] noise = this.initializeNoiseField(chunkX * cellsPerChunk, 0, chunkZ * cellsPerChunk,
			xSize, ySize, zSize);

		for (int cellX = 0; cellX < cellsPerChunk; cellX++) {
			for (int cellZ = 0; cellZ < cellsPerChunk; cellZ++) {
				for (int cellY = 0; cellY < 16; cellY++) {
					final double yLerp = 0.125;
					double d000 = noise[((cellX) * zSize + cellZ) * ySize + cellY];
					double d001 = noise[((cellX) * zSize + cellZ + 1) * ySize + cellY];
					double d100 = noise[((cellX + 1) * zSize + cellZ) * ySize + cellY];
					double d101 = noise[((cellX + 1) * zSize + cellZ + 1) * ySize + cellY];
					double s000 = (noise[((cellX) * zSize + cellZ) * ySize + cellY + 1] - d000) * yLerp;
					double s001 = (noise[((cellX) * zSize + cellZ + 1) * ySize + cellY + 1] - d001) * yLerp;
					double s100 = (noise[((cellX + 1) * zSize + cellZ) * ySize + cellY + 1] - d100) * yLerp;
					double s101 = (noise[((cellX + 1) * zSize + cellZ + 1) * ySize + cellY + 1] - d101) * yLerp;

					for (int subY = 0; subY < 8; subY++) {
						final double xLerp = 0.25;
						double dz0 = d000;
						double dz1 = d001;
						double sx0 = (d100 - d000) * xLerp;
						double sx1 = (d101 - d001) * xLerp;

						for (int subX = 0; subX < 4; subX++) {

							int index = (subX + cellX * 4) << 11 | (cellZ * 4) << 7 | (cellY * 8 + subY);
							final double zLerp = 0.25;
							double density = dz0;
							double sz = (dz1 - dz0) * zLerp;

							for (int subZ = 0; subZ < 4; subZ++) {
								int y = cellY * 8 + subY;
								int id = 0;
								if (y < SEA_LEVEL) {
									id = this.sandWorld && y >= SEA_LEVEL - 1 ? this.idSand : this.idWater;
								}
								if (density > 0.0) {
									id = this.idStone;
								}
								blocks[index] = (short) id;
								index += 128;
								density += sz;
							}

							dz0 += sx0;
							dz1 += sx1;
						}

						d000 += s000;
						d001 += s001;
						d100 += s100;
						d101 += s101;
					}
				}
			}
		}
	}

	private double[] initializeNoiseField(int x, int y, int z, int xSize, int ySize, int zSize) {
		double[] out = new double[xSize * ySize * zSize];
		final double horizontalScale = 684.412;
		final double verticalScale = 684.412;

		double[] noise6 = this.noiseGen6.generateNoiseOctaves(null, x, y, z, xSize, 1, zSize, 1.0, 0.0, 1.0);
		double[] noise7 = this.noiseGen7.generateNoiseOctaves(null, x, y, z, xSize, 1, zSize, 100.0, 0.0, 100.0);
		double[] noise3 = this.noiseGen3.generateNoiseOctaves(null, x, y, z, xSize, ySize, zSize,
			horizontalScale / 80.0, verticalScale / 160.0, horizontalScale / 80.0);
		double[] noise1 = this.noiseGen1.generateNoiseOctaves(null, x, y, z, xSize, ySize, zSize,
			horizontalScale, verticalScale, horizontalScale);
		double[] noise2 = this.noiseGen2.generateNoiseOctaves(null, x, y, z, xSize, ySize, zSize,
			horizontalScale, verticalScale, horizontalScale);

		int index = 0;
		int columnIndex = 0;

		for (int xi = 0; xi < xSize; xi++) {
			for (int zi = 0; zi < zSize; zi++) {
				double scale = (noise6[columnIndex] + 256.0) / 512.0;
				if (scale > 1.0) {
					scale = 1.0;
				}

				final double floor = 0.0;
				double depth = noise7[columnIndex] / 8000.0;
				if (depth < 0.0) {
					depth = -depth;
				}

				depth = depth * 3.0 - 3.0;
				if (depth < 0.0) {
					depth /= 2.0;
					if (depth < -1.0) {
						depth = -1.0;
					}
					depth /= 1.4;
					depth /= 2.0;
					scale = 0.0;
				} else {
					if (depth > 1.0) {
						depth = 1.0;
					}
					depth /= 6.0;
				}

				scale += 0.5;
				depth = depth * ySize / 16.0;
				double centre = ySize / 2.0 + depth * 4.0;
				columnIndex++;

				for (int yi = 0; yi < ySize; yi++) {
					double density;
					double falloff = (yi - centre) * 12.0 / scale;
					if (falloff < 0.0) {
						falloff *= 4.0;
					}

					double low = noise1[index] / 512.0;
					double high = noise2[index] / 512.0;
					double selector = (noise3[index] / 10.0 + 1.0) / 2.0;
					if (selector < 0.0) {
						density = low;
					} else if (selector > 1.0) {
						density = high;
					} else {
						density = low + (high - low) * selector;
					}

					density -= falloff;
					if (yi > ySize - 4) {
						double fade = (yi - (ySize - 4)) / 3.0F;
						density = density * (1.0 - fade) + -10.0 * fade;
					}

					if (yi < floor) {
						double fade = (floor - yi) / 4.0;
						if (fade < 0.0) {
							fade = 0.0;
						}
						if (fade > 1.0) {
							fade = 1.0;
						}
						density = density * (1.0 - fade) + -10.0 * fade;
					}

					out[index] = density;
					index++;
				}
			}
		}

		return out;
	}

	public void replaceSurfaceBlocks(int chunkX, int chunkZ, short[] blocks, Random rand) {
		final int sandTop = this.idSand;
		final int sandFiller = this.idSand;
		final int seaLevel = SEA_LEVEL;
		final double scale = 0.03125;

		double[] sandNoise = this.noiseGen4.generateNoiseOctaves(null, chunkX * 16, chunkZ * 16, 0.0,
			16, 16, 1, scale, scale, 1.0);
		double[] gravelNoise = this.noiseGen4.generateNoiseOctaves(null, chunkZ * 16, 109.0134, chunkX * 16,
			16, 1, 16, scale, 1.0, scale);
		double[] stoneNoise = this.noiseGen5.generateNoiseOctaves(null, chunkX * 16, chunkZ * 16, 0.0,
			16, 16, 1, scale * 2.0, scale * 2.0, scale * 2.0);

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {

				boolean sand = sandNoise[x + z * 16] + rand.nextDouble() * 0.2 > 0.0;
				boolean gravel = gravelNoise[x + z * 16] + rand.nextDouble() * 0.2 > 3.0;
				int soilDepth = (int) (stoneNoise[x + z * 16] / 3.0 + 3.0 + rand.nextDouble() * 0.25);
				int remaining = -1;
				int top;
				int filler;
				if (this.sandWorld) {
					top = sandTop;
					filler = sandFiller;
				} else {
					top = this.idGrass;
					filler = this.idDirt;
				}

				for (int y = HEIGHT - 1; y >= 0; y--) {
					int index = (x * 16 + z) * HEIGHT + y;

					if (y >= 95 + rand.nextInt(6) - 1 && blocks[index] != 0) {
						blocks[index] = (short) this.idSnowBlock;

						for (int ice = (int) (this.mobSpawnerNoise.generateNoiseOctaves(chunkX * 13.2, chunkZ * 13.2) / 2.0);
						     ice > 0; ice--) {
							if (ice + y < HEIGHT && index + ice < blocks.length && blocks[index + ice] == 0) {
								blocks[index + ice] = (short) this.idIce;
							}
						}
					}

					if (y <= rand.nextInt(6) - 1) {
						blocks[index] = (short) this.idBedrock;
						continue;
					}

					int current = blocks[index];
					if (current == 0) {
						remaining = -1;
					} else if (current == this.idStone) {
						if (remaining == -1) {
							if (soilDepth <= 0) {
								top = 0;
								filler = this.idStone;
							} else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
								top = this.idGrass;
								filler = this.idDirt;
								if (this.sandWorld) {
									top = sandTop;
									filler = sandFiller;
								}
								if (gravel) {
									top = 0;
									filler = this.idGravel;
								}
								if (sand) {
									top = this.idSand;
									filler = this.idSand;
								}
							}

							if (y < seaLevel && top == 0) {
								top = this.idWater;
							}

							remaining = soilDepth;
							blocks[index] = (short) (y >= seaLevel - 1 ? top : filler);
						} else if (remaining > 0) {
							remaining--;
							blocks[index] = (short) filler;
						}
					}
				}
			}
		}
	}
}
