package com.alphaver.client.gui;

import com.alphaver.world.AVWorlds;
import com.alphaver.world.CypressNames;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;

@Environment(EnvType.CLIENT)
public final class CypressAreaNames {
	private CypressAreaNames() {}

	public static final int AREA_BLOCKS = CypressNames.AREA_BLOCKS;
	public static final long FADE_MILLIS = 5000L;
	public static final float MIN_ALPHA = 0.3F;

	private static String current;
	private static long shownAt;
	private static long trackedX = Long.MIN_VALUE;
	private static long trackedZ = Long.MIN_VALUE;

	public static String name(long seed, int areaX, int areaZ) {
		return CypressNames.area(seed, areaX, areaZ);
	}

	public static void track(PlayerLocal player) {
		Minecraft mc = Minecraft.getMinecraft();
		if (player != mc.thePlayer) {
			return;
		}
		if (!AVWorlds.isCypress(player.world)) {
			current = null;
			trackedX = Long.MIN_VALUE;
			trackedZ = Long.MIN_VALUE;
			return;
		}
		long areaX = (long) player.x / AREA_BLOCKS;
		long areaZ = (long) player.z / AREA_BLOCKS;
		if (areaX == trackedX && areaZ == trackedZ) {
			return;
		}
		trackedX = areaX;
		trackedZ = areaZ;
		current = name(player.world.getRandomSeed(), (int) (player.x / AREA_BLOCKS), (int) (player.z / AREA_BLOCKS));
		shownAt = System.currentTimeMillis();
	}

	public static String current() {
		return current;
	}

	public static int alpha() {
		float faded = 1.0F - Math.min(System.currentTimeMillis() - shownAt, FADE_MILLIS) / (float) FADE_MILLIS;
		return (int) (255.0F * Math.max(faded, MIN_ALPHA));
	}
}
