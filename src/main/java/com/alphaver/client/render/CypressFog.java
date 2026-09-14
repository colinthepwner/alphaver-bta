package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class CypressFog {
	private CypressFog() {}

	private static double log2(int chunks) {
		return Math.log(Math.max(1, chunks)) / Math.log(2.0);
	}

	public static double skyPullPower(int chunks) {
		double d = Math.max(1.0, Math.min(5.0, log2(chunks)));
		return Math.pow(1.0 / d, 0.25);
	}

	public static float brightnessFloor(int chunks) {
		return (float) Math.max(0.0, Math.min(1.0, (log2(chunks) - 1.0) / 3.0));
	}
}
