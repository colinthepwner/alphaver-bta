package com.alphaver.mixin.client;

import com.alphaver.net.AVServerSettings;
import net.minecraft.client.gui.ScreenMainMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScreenMainMenu.class, remap = false)
public abstract class ScreenMainMenuSettingsMixin {

	@Inject(method = "init()V", at = @At("TAIL"))
	private void alphaver$restoreOwnSettings(CallbackInfo ci) {
		AVServerSettings.restore();
	}
}
