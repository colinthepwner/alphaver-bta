package com.alphaver.mixin.client;

import com.alphaver.client.gui.CypressModesMenu;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenCreateWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScreenCreateWorld.class, remap = false)
public abstract class ScreenCreateWorldCypressModesMixin {

	@Inject(method = "<init>(Lnet/minecraft/client/gui/Screen;)V", at = @At("TAIL"))
	private void alphaver$showCypressModesToFinders(Screen parent, CallbackInfo ci) {
		CypressModesMenu.refresh();
	}
}
