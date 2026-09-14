package com.alphaver.client.render;

import com.alphaver.asset.AVAssetSidecar;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class AVPackLock {
	private AVPackLock() {}

	public static boolean inside() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc.thePlayer != null && AVWorlds.isAlphaVer(mc.currentWorld);
	}

	public static boolean isLocked(@Nullable TexturePack pack) {
		if (pack == null || !inside()) {
			return false;
		}
		return AVAssetSidecar.PACK_NAME.equals(pack.fileName) || pack == AVLookController.appliedPack();
	}

	public static boolean hiddenFromMenu(@Nullable TexturePack pack) {
		return pack != null && !inside()
			&& (AVAssetSidecar.PACK_NAME.equals(pack.fileName) || AVAssetSidecar.LOOK_PACK_NAME.equals(pack.fileName));
	}

	public static List<TexturePack> withoutHidden(List<TexturePack> packs) {
		if (packs == null) {
			return null;
		}
		List<TexturePack> shown = null;
		for (int i = 0; i < packs.size(); i++) {
			TexturePack pack = packs.get(i);
			if (hiddenFromMenu(pack)) {
				if (shown == null) {
					shown = new java.util.ArrayList<>(packs.subList(0, i));
				}
			} else if (shown != null) {
				shown.add(pack);
			}
		}
		return shown == null ? packs : shown;
	}

	public static void keepLookOnTop(TexturePackList packs) {
		TexturePack look = AVLookController.appliedPack();
		if (look == null || !inside()) {
			return;
		}
		List<TexturePack> selected = packs.selectedPacks;
		int index = selected.indexOf(look);
		if (index >= 0 && index != selected.size() - 1) {
			selected.remove(index);
			selected.add(look);
		}
	}
}
