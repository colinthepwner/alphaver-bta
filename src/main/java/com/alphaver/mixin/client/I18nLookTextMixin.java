package com.alphaver.mixin.client;

import com.alphaver.client.render.AVLookText;
import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = I18n.class, remap = false)
public abstract class I18nLookTextMixin {

	@Inject(method = "translateKey", at = @At("HEAD"), cancellable = true)
	private void alphaver$lookText(String key, CallbackInfoReturnable<String> cir) {
		if (key == null) {
			return;
		}
		String text = AVLookText.override(key);
		if (text != null) {
			cir.setReturnValue(text);
		}
	}
}
