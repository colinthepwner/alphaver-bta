package com.alphaver.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.HudIngame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HudIngame.class, remap = false)
public abstract class HudIngamePortalOverlayMixin {

	@Shadow
	protected Minecraft mc;

	@Inject(method = "renderPortalOverlay(FII)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$noColourNoOverlay(float strength, int width, int height, CallbackInfo ci) {
		if (this.mc.thePlayer == null || this.mc.thePlayer.portalColor == null) {
			ci.cancel();
		}
	}
}
