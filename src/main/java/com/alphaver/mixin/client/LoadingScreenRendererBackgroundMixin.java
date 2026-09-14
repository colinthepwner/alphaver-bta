package com.alphaver.mixin.client;

import com.alphaver.client.render.AVLoadingBackgrounds;
import net.minecraft.client.render.LoadingScreenRenderer;
import net.minecraft.core.world.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LoadingScreenRenderer.class, remap = false)
public abstract class LoadingScreenRendererBackgroundMixin {

	@Shadow
	private String backgroundPath;

	@Inject(method = "updateLoadingBackground", at = @At("TAIL"))
	private void alphaver$dimensionBackground(Dimension dimension, CallbackInfo ci) {
		String own = AVLoadingBackgrounds.pathFor(dimension);
		if (own != null) {
			this.backgroundPath = own;
		}
	}
}
