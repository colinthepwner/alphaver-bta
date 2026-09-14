package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.painting.AVPaintings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;

@Environment(EnvType.CLIENT)
public final class AVPaintingTextures {
	private AVPaintingTextures() {}

	private static final String ART_PREFIX = AlphaVer.MOD_ID + ":art/";
	private static final String FALLBACK = "minecraft:art/backing";

	public static void register() {
		int queued = 0;
		for (AVPaintings.Painting painting : AVPaintings.TABLE) {
			if (AVTextures.has(painting.texture())) {
				TextureRegistry.getTexture(painting.texture());
				queued++;
			}
		}
		AlphaVer.LOGGER.info("Queued {} of Cypress's {} paintings for the art atlas.", queued, AVPaintings.TABLE.size());
	}

	public static String resolve(String texture) {
		if (texture != null && texture.startsWith(ART_PREFIX) && !TextureRegistry.hasTexture(texture)) {
			return FALLBACK;
		}
		return texture;
	}
}
