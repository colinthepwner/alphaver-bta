package com.alphaver.asset;

import java.awt.image.BufferedImage;

final class DoorIconCarving {
	private DoorIconCarving() {}

	static final int SIZE = 16;

	private static final int ORIGIN = 4;

	private static final double FLOOR = 0.28;

	private static final double TOP_SHADOW = 0.30;

	private static final double SIDE_SHADOW = 0.15;

	private static final double RIM_LIGHT = 0.20;
	private static final double RIM_SIDE = 0.5;

	private static final double INNER_EDGE = 0.6;

	private static final double SMOOTH = 0.8;

	private static final double CONTRAST = 0.25;

	private static final double BLACK = 0.05;

	private static final double BLACK_STEP = 0.14;

	static BufferedImage carve(BufferedImage doorTile, BufferedImage picture, boolean mirrorDoor) {
		BufferedImage door = atSize(doorTile);
		BufferedImage icon = atSize(picture);
		BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

		boolean[] inside = new boolean[SIZE * SIZE];
		double[] luminance = new double[SIZE * SIZE];
		double lo = Double.MAX_VALUE;
		double hi = -Double.MAX_VALUE;
		for (int v = 0; v < SIZE; v++) {
			for (int u = 0; u < SIZE; u++) {
				int argb = icon.getRGB(u, v);
				if ((argb >>> 24) >= 128) {
					int i = v * SIZE + u;
					inside[i] = true;
					luminance[i] = luminance(argb);
					lo = Math.min(lo, luminance[i]);
					hi = Math.max(hi, luminance[i]);
				}
			}
		}
		if (lo > hi) {

			return out;
		}
		boolean twoSteps = hi - lo >= CONTRAST;
		double middle = (lo + hi) / 2.0;
		int[] depth = new int[SIZE * SIZE];
		for (int i = 0; i < depth.length; i++) {
			depth[i] = !inside[i] ? 0 : !twoSteps ? 1 : luminance[i] <= BLACK ? 3 : luminance[i] < middle ? 2 : 1;
		}

		double[] mean = new double[3];
		int span = SIZE / 2;
		for (int row = ORIGIN; row < ORIGIN + span; row++) {
			for (int column = ORIGIN; column < ORIGIN + span; column++) {
				int argb = door.getRGB(column, row);
				mean[0] += (argb >> 16) & 0xFF;
				mean[1] += (argb >> 8) & 0xFF;
				mean[2] += argb & 0xFF;
			}
		}
		for (int k = 0; k < 3; k++) {
			mean[k] /= span * span;
		}

		for (int v = 0; v < SIZE; v++) {
			for (int u = 0; u < SIZE; u++) {
				int column = ORIGIN + u / 2;
				int texel = door.getRGB(mirrorDoor ? SIZE - 1 - column : column, ORIGIN + v / 2);
				int z = depthAt(depth, u, v);
				double factor;
				if (z == 0) {
					double rim = Math.min(1.0, (depthAt(depth, u, v - 1) > 0 ? 1.0 : 0.0) + (depthAt(depth, u - 1, v) > 0 ? RIM_SIDE : 0.0));
					if (rim <= 0.0) {
						continue;
					}
					factor = 1.0 + RIM_LIGHT * rim;
				} else {
					factor = 1.0 - FLOOR * Math.min(z, 2) - BLACK_STEP * Math.max(0, z - 2);
					factor *= 1.0 - TOP_SHADOW * edge(depth, z, u, v - 1);
					factor *= 1.0 - SIDE_SHADOW * edge(depth, z, u - 1, v);

					double lip = Math.min(1.0, (depthAt(depth, u, v - 1) > z ? 1.0 : 0.0) + (depthAt(depth, u - 1, v) > z ? RIM_SIDE : 0.0));
					factor *= 1.0 + RIM_LIGHT * INNER_EDGE * lip;
				}
				out.setRGB(u, v, 0xFF000000 | channel(texel >> 16, mean[0], factor) << 16 | channel(texel >> 8, mean[1], factor) << 8
					| channel(texel, mean[2], factor));
			}
		}
		return out;
	}

	static BufferedImage atSize(BufferedImage image) {
		if (image.getWidth() == SIZE && image.getHeight() == SIZE) {
			return image;
		}
		BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				out.setRGB(x, y, image.getRGB(x * image.getWidth() / SIZE, y * image.getHeight() / SIZE));
			}
		}
		return out;
	}

	private static double luminance(int argb) {
		return (0.299 * ((argb >> 16) & 0xFF) + 0.587 * ((argb >> 8) & 0xFF) + 0.114 * (argb & 0xFF)) / 255.0;
	}

	private static int depthAt(int[] depth, int u, int v) {
		return u < 0 || v < 0 || u >= SIZE || v >= SIZE ? 0 : depth[v * SIZE + u];
	}

	private static double edge(int[] depth, int z, int u, int v) {
		int neighbour = depthAt(depth, u, v);
		return neighbour >= z ? 0.0 : neighbour == 0 ? 1.0 : INNER_EDGE;
	}

	private static int channel(int texelChannel, double mean, double factor) {
		double base = (texelChannel & 0xFF) * (1.0 - SMOOTH) + mean * SMOOTH;
		return (int) Math.max(0.0, Math.min(255.0, Math.floor(base * factor + 0.5)));
	}
}
