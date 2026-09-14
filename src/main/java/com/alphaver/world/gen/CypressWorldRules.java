package com.alphaver.world.gen;

import com.alphaver.AVConfig;

import java.time.LocalTime;
import java.util.Random;

public final class CypressWorldRules {
	private CypressWorldRules() {}

	public static boolean isFractured() {
		switch (AVConfig.FRACTURED_WORLD) {
			case "always":
				return true;
			case "never":
				return false;
			default:
				int hour = LocalTime.now().getHour();
				return hour > 22 || hour < 5;
		}
	}

	public static boolean isSandWorld(long seed) {
		switch (AVConfig.SAND_WORLD) {
			case "always":
				return true;
			case "never":
				return false;
			default:
				return new Random(seed ^ 0x6379707265737321L).nextInt(4) == 1;
		}
	}
}
