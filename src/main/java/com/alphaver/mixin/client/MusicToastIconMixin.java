package com.alphaver.mixin.client;

import com.alphaver.client.sound.AVMusicIcons;
import net.minecraft.client.gui.toasts.MusicToast;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MusicToast.class, remap = false)
public abstract class MusicToastIconMixin {

	@Shadow
	@Final
	private String title;

	@Inject(method = "getIcon(J)Lnet/minecraft/core/item/ItemStack;", at = @At("HEAD"), cancellable = true)
	private void alphaver$discIcon(long runtime, CallbackInfoReturnable<ItemStack> cir) {
		ItemStack icon = AVMusicIcons.iconFor(this.title);
		if (icon != null) {
			cir.setReturnValue(icon);
		}
	}
}
