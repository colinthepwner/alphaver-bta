package com.alphaver.world;

import com.alphaver.AVConfig;

public final class CypressEvents {
	private CypressEvents() {}

	public static final long MILESTONE_TICKS = 23000L;

	public static final long NEBULA_EVERY = 10L;

	public static long milestone(long worldTime) {
		return worldTime / MILESTONE_TICKS;
	}

	public static boolean isNebula(long worldTime) {
		switch (AVConfig.NEBULA) {
			case "always":
				return true;
			case "never":
				return false;
			default:
				long m = milestone(worldTime);
				return m > 0 && m % NEBULA_EVERY == 0;
		}
	}

	public static float purplePulse(long worldTime) {
		long x = worldTime % MILESTONE_TICKS;
		if (x >= 22000L) {
			return (1000L - (x - 22000L)) / 1000.0F;
		}
		if (x >= 21000L) {
			return (x - 21000L) / 1000.0F;
		}
		return 0.0F;
	}

	public static final float PULSE_R = 0.58431375F;
	public static final float PULSE_G = 0.0F;
	public static final float PULSE_B = 1.0F;
}
