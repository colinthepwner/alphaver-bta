package com.alphaver.client.render;

import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;

@Environment(EnvType.CLIENT)
public final class AVFlatLighting {
	private AVFlatLighting() {}

	public static Object read(OptionBoolean option) {
		if (option == GameSettings.AMBIENT_OCCLUSION && forced()) {
			return Boolean.FALSE;
		}
		return option.value;
	}

	private static boolean forced() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc != null && AVWorlds.isAlphaVer(mc.currentWorld);
	}
}
