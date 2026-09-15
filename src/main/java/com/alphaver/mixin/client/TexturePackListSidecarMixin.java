package com.alphaver.mixin.client;

import com.alphaver.AlphaVer;
import com.alphaver.asset.AVAssetSidecar;
import com.alphaver.client.gui.CypressFrailHud;
import com.alphaver.client.gui.HudComponentLilypadHunger;
import com.alphaver.client.gui.HudComponentZombies;
import com.alphaver.client.render.AVParticles;
import com.alphaver.client.render.AVPaintingTextures;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = TexturePackList.class, remap = false)
public abstract class TexturePackListSidecarMixin {

	@Inject(method = "init", at = @At("TAIL"))
	private void alphaver$runSidecar(CallbackInfo ci) {
		try {

			AlphaVer.LOGGER.info("Asset sidecar: starting.");
			File gameDir = AVAssetSidecar.gameDir();
			if (gameDir == null) {
				return;
			}
			File packDir = AVAssetSidecar.run(gameDir);

			AVAssetSidecar.enablePacks((TexturePackList) (Object) this, packDir);
			AVAssetSidecar.logSummary();

			AVPaintingTextures.register();
			CypressFrailHud.register();
			HudComponentLilypadHunger.registerIcons();
			HudComponentZombies.registerIcons();

			AVParticles.registerTextures();
		} catch (Throwable t) {

			AlphaVer.LOGGER.error("Asset sidecar failed; AlphaVer will run without Cypress's art.", t);
		}
	}
}
