package com.alphaver.world.gen;

public final class AlphaMath {
	private AlphaMath() {}

	private static final float[] SIN_TABLE = new float[65536];

	static {
		for (int i = 0; i < 65536; i++) {
			SIN_TABLE[i] = (float) Math.sin(i * Math.PI * 2.0 / 65536.0);
		}
	}

	public static float sin(float value) {
		return SIN_TABLE[(int) (value * 10430.378F) & 65535];
	}

	public static float cos(float value) {
		return SIN_TABLE[(int) (value * 10430.378F + 16384.0F) & 65535];
	}

	public static int floorDouble(double value) {
		int truncated = (int) value;
		return value < truncated ? truncated - 1 : truncated;
	}
}
