package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.tag.WorldTypeTags;

@Environment(EnvType.CLIENT)
public final class AVWaterLook {
	private AVWaterLook() {}

	public static final int ALPHA_UNDERWATER_FOG = 0x0A0A66;

	public static boolean alphaWater() {
		return AVLookController.isApplied();
	}

	public static boolean hasTag(WorldType type, Tag<WorldType> tag) {
		return type.hasTag(tag) || (tag == WorldTypeTags.RETRO && alphaWater());
	}
}
