package com.alphaver.net;

import com.alphaver.AVConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class AVServerSettings {
	private AVServerSettings() {}

	private static boolean applied;
	private static boolean ownFrail;
	private static String ownNebula;
	private static boolean ownBiomes;
	private static boolean ownDashing;
	@Nullable
	private static String refusal;

	public static synchronized void apply(boolean frail, String nebula, boolean biomes, boolean dashing) {
		if (!applied) {
			ownFrail = AVConfig.FRAIL;
			ownNebula = AVConfig.NEBULA;
			ownBiomes = AVConfig.BIOMES;
			ownDashing = AVConfig.DASHING;
			applied = true;
		}
		AVConfig.FRAIL = frail;
		AVConfig.NEBULA = nebula == null ? ownNebula : nebula.trim().toLowerCase(Locale.ROOT);
		AVConfig.BIOMES = biomes;

		AVConfig.DASHING = ownDashing && dashing;
	}

	public static synchronized void restore() {
		if (applied) {
			AVConfig.FRAIL = ownFrail;
			AVConfig.NEBULA = ownNebula;
			AVConfig.BIOMES = ownBiomes;
			AVConfig.DASHING = ownDashing;
			applied = false;
		}
		refusal = null;
	}

	public static synchronized void refuse(String reason) {
		refusal = reason;
	}

	@Nullable
	public static synchronized String takeRefusal() {
		String reason = refusal;
		refusal = null;
		return reason;
	}
}
