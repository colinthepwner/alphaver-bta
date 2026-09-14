package com.alphaver.world.gen.noise;

import java.util.Random;

public final class AlphaNoiseOctaves {
	private final AlphaPerlinNoise[] octaves;

	public AlphaNoiseOctaves(Random random, int octaveCount) {
		this.octaves = new AlphaPerlinNoise[octaveCount];
		for (int i = 0; i < octaveCount; i++) {
			this.octaves[i] = new AlphaPerlinNoise(random);
		}
	}

	public double generateNoiseOctaves(double x, double z) {
		double total = 0.0;
		double scale = 1.0;
		for (AlphaPerlinNoise octave : this.octaves) {
			total += octave.generateNoise(x * scale, z * scale) / scale;
			scale /= 2.0;
		}
		return total;
	}

	public double[] generateNoiseOctaves(double[] data, double x, double y, double z,
	                                     int xSize, int ySize, int zSize,
	                                     double xScale, double yScale, double zScale) {
		if (data == null) {
			data = new double[xSize * ySize * zSize];
		} else {
			java.util.Arrays.fill(data, 0.0);
		}

		double scale = 1.0;
		for (AlphaPerlinNoise octave : this.octaves) {
			octave.populateNoiseArray(data, x, y, z, xSize, ySize, zSize,
				xScale * scale, yScale * scale, zScale * scale, scale);
			scale /= 2.0;
		}
		return data;
	}
}
