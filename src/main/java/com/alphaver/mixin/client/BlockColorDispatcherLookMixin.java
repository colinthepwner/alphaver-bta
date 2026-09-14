package com.alphaver.mixin.client;

import com.alphaver.client.render.AVLookColors;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockColorDispatcher.class, remap = false)
public abstract class BlockColorDispatcherLookMixin {

	@Inject(method = "reload", at = @At("TAIL"))
	private void alphaver$lookColors(CallbackInfo ci) {
		AVLookColors.wrap((BlockColorDispatcher) (Object) this);
	}
}
