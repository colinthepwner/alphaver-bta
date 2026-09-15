package com.alphaver.client.render;

import com.alphaver.world.AVGameRules;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.World;

@Environment(EnvType.CLIENT)
public final class CypressHomeLook {
	private CypressHomeLook() {}

	private static volatile boolean serverValue;

	private static World lastWorld;
	private static boolean lastValue;

	public static boolean woodenCypressDoors() {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc == null ? null : mc.currentWorld;
		if (world == null) {
			return false;
		}
		return world.isClientSide ? serverValue : AVGameRules.startInCypress(world);
	}

	public static void rulesReceived(Minecraft mc) {
		boolean value = AVGameRules.startInCypress(mc.currentWorld);
		if (value != serverValue) {
			serverValue = value;
			redraw(mc);
		}
	}

	public static void tick(Minecraft mc) {
		World world = mc.currentWorld;
		if (world == null || world.isClientSide) {
			lastWorld = null;
			return;
		}
		boolean value = AVGameRules.startInCypress(world);
		if (world == lastWorld && value != lastValue && AVWorlds.isHub(world)) {
			redraw(mc);
		}
		lastWorld = world;
		lastValue = value;
	}

	private static void redraw(Minecraft mc) {
		if (mc.renderGlobal != null && mc.currentWorld != null) {
			mc.renderGlobal.allChanged();
		}
	}
}
