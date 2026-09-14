package com.alphaver.client.render;

import com.alphaver.asset.AVAssetSidecar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.client.render.texturepack.TexturePack;

import java.io.File;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class AVTextures {
	private AVTextures() {}

	private static final Set<String> BRIDGED = new HashSet<>();
	private static boolean scanned;

	public static boolean has(String id) {
		ensureScanned();
		if (BRIDGED.contains(id) && packSelected()) {
			return true;
		}
		try {
			return TextureRegistry.hasTexture(id);
		} catch (RuntimeException e) {

			return false;
		}
	}

	public static String block(String name, String fallback) {
		String id = "alphaver:block/" + name;
		return has(id) ? id : fallback;
	}

	private static boolean packSelected() {
		try {
			for (TexturePack pack : Minecraft.getMinecraft().texturePackList.selectedPacks) {
				if (AVAssetSidecar.PACK_NAME.equals(pack.fileName)) {
					return true;
				}
			}
		} catch (Throwable t) {
			return false;
		}
		return false;
	}

	private static void ensureScanned() {
		if (scanned) {
			return;
		}
		File gameDir;
		try {
			gameDir = Minecraft.getMinecraft().getMinecraftDir();
		} catch (Throwable t) {
			return;
		}
		if (gameDir == null) {
			return;
		}
		File root = new File(gameDir, "texturepacks/" + AVAssetSidecar.PACK_NAME + "/assets/alphaver/textures");
		if (!root.isDirectory()) {
			return;
		}
		scanned = true;

		String rootPath = root.getAbsolutePath();
		Deque<File> pending = new ArrayDeque<>();
		pending.push(root);
		while (!pending.isEmpty()) {
			File[] children = pending.pop().listFiles();
			if (children == null) {
				continue;
			}
			for (File child : children) {
				if (child.isDirectory()) {
					pending.push(child);
				} else if (child.getName().toLowerCase(Locale.ROOT).endsWith(".png")) {
					String relative = child.getAbsolutePath().substring(rootPath.length() + 1).replace(File.separatorChar, '/');
					BRIDGED.add("alphaver:" + relative.substring(0, relative.length() - 4));
				}
			}
		}
	}
}
