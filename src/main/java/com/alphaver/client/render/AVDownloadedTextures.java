package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.TextureDownloaded;

import java.awt.image.BufferedImage;

@Environment(EnvType.CLIENT)
public final class AVDownloadedTextures {
	private AVDownloadedTextures() {}

	public static void reupload(Minecraft mc) {
		if (mc == null || mc.textureManager == null) {
			return;
		}
		for (TextureDownloaded texture : mc.textureManager.downloadedTextures.values()) {
			BufferedImage image = texture.image;

			if (image != null && texture.isGenerated()) {
				texture.setupTexture(image, false, false, false);
			}
		}
	}
}
