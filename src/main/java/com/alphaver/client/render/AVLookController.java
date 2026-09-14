package com.alphaver.client.render;

import com.alphaver.AVConfig;
import com.alphaver.AlphaVer;
import com.alphaver.asset.AVAssetSidecar;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class AVLookController {
	private AVLookController() {}

	private static boolean inside;

	private static TexturePack applied;

	private static TexturePack borrowed;
	private static boolean failedOnce;

	public static boolean isApplied() {
		return applied != null;
	}

	@Nullable
	public static TexturePack appliedPack() {
		return applied;
	}

	public static void tick(Minecraft mc) {
		boolean now = mc.thePlayer != null && AVWorlds.isAlphaVer(mc.currentWorld);
		if (now == inside) {
			return;
		}
		inside = now;
		try {
			if (now) {
				apply(mc);
			} else {
				revert(mc);
			}
		} catch (Throwable t) {

			if (!failedOnce) {
				failedOnce = true;
				AlphaVer.LOGGER.error("Could not switch Cypress's look {}", now ? "on" : "off", t);
			}
		}
	}

	private static void apply(Minecraft mc) {
		TexturePackList packs = mc.texturePackList;
		boolean changed = false;

		TexturePack assets = find(packs, AVAssetSidecar.PACK_NAME);
		if (assets != null && !packs.selectedPacks.contains(assets)) {

			assets.readZipFile();
			packs.selectedPacks.add(0, assets);
			borrowed = assets;
			changed = true;
			AlphaVer.LOGGER.info("'{}' switched back on for this visit.", AVAssetSidecar.PACK_NAME);
		}

		TexturePack look = AVConfig.RETEXTURE ? find(packs, AVAssetSidecar.LOOK_PACK_NAME) : null;
		if (look != null) {

			if (!packs.selectedPacks.remove(look)) {
				look.readZipFile();
			}
			packs.selectedPacks.add(look);
			applied = look;
			changed = true;
			AlphaVer.LOGGER.info("Cypress's look switched on.");
		}

		if (changed) {
			packs.refresh();
		}
	}

	private static void revert(Minecraft mc) {
		TexturePackList packs = mc.texturePackList;
		String saved = GameSettings.SKIN.value;
		boolean changed = false;
		boolean resave = false;

		if (applied != null) {
			TexturePack look = applied;
			applied = null;
			packs.selectedPacks.remove(look);
			look.closeTexturePackFile();
			changed = true;
			resave = saved != null && saved.contains(AVAssetSidecar.LOOK_PACK_NAME);
			AlphaVer.LOGGER.info("Cypress's look switched off.");
		}

		if (borrowed != null) {
			TexturePack assets = borrowed;
			borrowed = null;
			packs.selectedPacks.remove(assets);
			assets.closeTexturePackFile();
			changed = true;
			resave |= saved != null && saved.contains(AVAssetSidecar.PACK_NAME);
			AlphaVer.LOGGER.info("'{}' switched off again, as it was before the visit.", AVAssetSidecar.PACK_NAME);
		}

		if (!changed) {
			return;
		}

		if (resave) {
			packs.savePacksToSettings();
		}
		packs.refresh();
	}

	@Nullable
	private static TexturePack find(TexturePackList packs, String fileName) {
		for (TexturePack pack : packs.availableTexturePacks()) {
			if (fileName.equals(pack.fileName)) {
				return pack;
			}
		}
		return null;
	}
}
