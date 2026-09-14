package com.alphaver.mixin.client;

import com.alphaver.client.render.AVWaterLook;
import net.minecraft.client.render.block.color.BlockColorWater;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockColorWater.class, remap = false)
public abstract class BlockColorWaterMixin {

	@Inject(method = "getFallbackColor", at = @At("HEAD"), cancellable = true)
	private void alphaver$untintedItem(int meta, int tintIndex, CallbackInfoReturnable<Integer> cir) {
		if (AVWaterLook.alphaWater()) {
			cir.setReturnValue(-1);
		}
	}

	@Inject(
		method = "getWorldColor(Lnet/minecraft/core/world/WorldSource;Lnet/minecraft/core/world/pos/TilePosc;I)I",
		at = @At("HEAD"),
		cancellable = true)
	private void alphaver$untintedWorld(WorldSource source, TilePosc tilePos, int tintIndex, CallbackInfoReturnable<Integer> cir) {
		if (AVWaterLook.alphaWater()) {
			cir.setReturnValue(-1);
		}
	}
}
