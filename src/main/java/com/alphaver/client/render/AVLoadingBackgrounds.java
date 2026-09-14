package com.alphaver.client.render;

import com.alphaver.world.AVDimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.world.Dimension;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class AVLoadingBackgrounds {
	private AVLoadingBackgrounds() {}

	@Nullable
	public static String pathFor(@Nullable Dimension dimension) {
		String name = AVDimensions.loadingBackgroundOf(dimension);
		if (name == null || !AVTextures.has("alphaver:block/" + name)) {
			return null;
		}
		return "/assets/alphaver/textures/block/" + name + ".png";
	}
}
