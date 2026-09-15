package com.alphaver.world.gen;

import com.alphaver.AVConfig;

import java.time.LocalDateTime;
import java.util.Random;

public final class CypressWorldRules {
	private CypressWorldRules() {}

	public static boolean isFractured(long seed) {
		switch (AVConfig.FRACTURED_WORLD) {
			case "always":
				return true;
			case "never":
				return false;
			default:
				LocalDateTime now = LocalDateTime.now();
				int hour = now.getHour();
				if (hour <= 22 && hour >= 5) {
					return false;
				}
				long night = (hour < 5 ? now.toLocalDate().minusDays(1) : now.toLocalDate()).toEpochDay();
				NightAnswer memo = lastNight;
				if (memo != null && memo.seed() == seed && memo.night() == night) {
					return memo.fractured();
				}
				boolean fractured = (mix(seed ^ night * 0x9E3779B97F4A7C15L ^ FRACTURE_SALT) & 1L) == 0L;
				lastNight = new NightAnswer(seed, night, fractured);
				return fractured;
		}
	}

	private static final long FRACTURE_SALT = 0x6672616374757265L;

	private static long mix(long z) {
		z = (z ^ (z >>> 30)) * 0xBF58476D1CE4E5B9L;
		z = (z ^ (z >>> 27)) * 0x94D049BB133111EBL;
		return z ^ (z >>> 31);
	}

	private record NightAnswer(long seed, long night, boolean fractured) {}

	private static volatile NightAnswer lastNight;

	public static boolean isSandWorld(long seed) {
		switch (AVConfig.SAND_WORLD) {
			case "always":
				return true;
			case "never":
				return false;
			default:

				SandAnswer memo = lastSand;
				if (memo != null && memo.seed() == seed) {
					return memo.sand();
				}
				boolean sand = new Random(seed ^ 0x6379707265737321L).nextInt(4) == 1;
				lastSand = new SandAnswer(seed, sand);
				return sand;
		}
	}

	private record SandAnswer(long seed, boolean sand) {}

	private static volatile SandAnswer lastSand;
}
