package com.alphaver.world.gen.noise;

import java.util.Random;

public final class AlphaPerlinNoise {
	private final int[] permutations = new int[512];
	private final double xCoord;
	private final double yCoord;
	private final double zCoord;

	public AlphaPerlinNoise(Random random) {
		this.xCoord = random.nextDouble() * 256.0;
		this.yCoord = random.nextDouble() * 256.0;
		this.zCoord = random.nextDouble() * 256.0;

		for (int i = 0; i < 256; i++) {
			this.permutations[i] = i;
		}

		for (int i = 0; i < 256; i++) {
			int j = random.nextInt(256 - i) + i;
			int swap = this.permutations[i];
			this.permutations[i] = this.permutations[j];
			this.permutations[j] = swap;
			this.permutations[i + 256] = this.permutations[i];
		}
	}

	public double generateNoise(double x, double y, double z) {
		double fx = x + this.xCoord;
		double fy = y + this.yCoord;
		double fz = z + this.zCoord;
		int ix = (int) fx;
		int iy = (int) fy;
		int iz = (int) fz;
		if (fx < ix) {
			ix--;
		}
		if (fy < iy) {
			iy--;
		}
		if (fz < iz) {
			iz--;
		}

		int px = ix & 0xFF;
		int py = iy & 0xFF;
		int pz = iz & 0xFF;
		fx -= ix;
		fy -= iy;
		fz -= iz;
		double u = fx * fx * fx * (fx * (fx * 6.0 - 15.0) + 10.0);
		double v = fy * fy * fy * (fy * (fy * 6.0 - 15.0) + 10.0);
		double w = fz * fz * fz * (fz * (fz * 6.0 - 15.0) + 10.0);

		int a = this.permutations[px] + py;
		int aa = this.permutations[a] + pz;
		int ab = this.permutations[a + 1] + pz;
		int b = this.permutations[px + 1] + py;
		int ba = this.permutations[b] + pz;
		int bb = this.permutations[b + 1] + pz;

		return lerp(w,
			lerp(v,
				lerp(u, grad(this.permutations[aa], fx, fy, fz), grad(this.permutations[ba], fx - 1.0, fy, fz)),
				lerp(u, grad(this.permutations[ab], fx, fy - 1.0, fz), grad(this.permutations[bb], fx - 1.0, fy - 1.0, fz))),
			lerp(v,
				lerp(u, grad(this.permutations[aa + 1], fx, fy, fz - 1.0), grad(this.permutations[ba + 1], fx - 1.0, fy, fz - 1.0)),
				lerp(u, grad(this.permutations[ab + 1], fx, fy - 1.0, fz - 1.0), grad(this.permutations[bb + 1], fx - 1.0, fy - 1.0, fz - 1.0))));
	}

	public double generateNoise(double x, double z) {
		return this.generateNoise(x, z, 0.0);
	}

	private static double lerp(double t, double a, double b) {
		return a + t * (b - a);
	}

	private static double grad(int hash, double x, double y, double z) {
		int h = hash & 15;
		double u = h < 8 ? x : y;
		double v = h < 4 ? y : (h != 12 && h != 14 ? z : x);
		return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v : -v);
	}

	public void populateNoiseArray(double[] data, double x, double y, double z,
	                               int xSize, int ySize, int zSize,
	                               double xScale, double yScale, double zScale, double octaveScale) {
		int index = 0;
		double amplitude = 1.0 / octaveScale;
		int lastY = -1;
		double lerpA = 0.0;
		double lerpB = 0.0;
		double lerpC = 0.0;
		double lerpD = 0.0;

		for (int xi = 0; xi < xSize; xi++) {
			double fx = (x + xi) * xScale + this.xCoord;
			int ix = (int) fx;
			if (fx < ix) {
				ix--;
			}
			int px = ix & 0xFF;
			fx -= ix;
			double u = fx * fx * fx * (fx * (fx * 6.0 - 15.0) + 10.0);

			for (int zi = 0; zi < zSize; zi++) {
				double fz = (z + zi) * zScale + this.zCoord;
				int iz = (int) fz;
				if (fz < iz) {
					iz--;
				}
				int pz = iz & 0xFF;
				fz -= iz;
				double w = fz * fz * fz * (fz * (fz * 6.0 - 15.0) + 10.0);

				for (int yi = 0; yi < ySize; yi++) {
					double fy = (y + yi) * yScale + this.yCoord;
					int iy = (int) fy;
					if (fy < iy) {
						iy--;
					}
					int py = iy & 0xFF;
					fy -= iy;
					double v = fy * fy * fy * (fy * (fy * 6.0 - 15.0) + 10.0);

					if (yi == 0 || py != lastY) {
						lastY = py;
						int a = this.permutations[px] + py;
						int aa = this.permutations[a] + pz;
						int ab = this.permutations[a + 1] + pz;
						int b = this.permutations[px + 1] + py;
						int ba = this.permutations[b] + pz;
						int bb = this.permutations[b + 1] + pz;
						lerpA = lerp(u, grad(this.permutations[aa], fx, fy, fz), grad(this.permutations[ba], fx - 1.0, fy, fz));
						lerpB = lerp(u, grad(this.permutations[ab], fx, fy - 1.0, fz), grad(this.permutations[bb], fx - 1.0, fy - 1.0, fz));
						lerpC = lerp(u, grad(this.permutations[aa + 1], fx, fy, fz - 1.0), grad(this.permutations[ba + 1], fx - 1.0, fy, fz - 1.0));
						lerpD = lerp(u, grad(this.permutations[ab + 1], fx, fy - 1.0, fz - 1.0), grad(this.permutations[bb + 1], fx - 1.0, fy - 1.0, fz - 1.0));
					}

					double low = lerp(v, lerpA, lerpB);
					double high = lerp(v, lerpC, lerpD);
					data[index++] += lerp(w, low, high) * amplitude;
				}
			}
		}
	}
}
