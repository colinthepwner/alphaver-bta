package com.alphaver.mixin;

import com.alphaver.world.AVDimensions;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MinecraftServer.class, remap = false)
public abstract class MinecraftServerDimensionMixin {

	@Inject(
		method = "startServer",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/Dimension;init()V", shift = At.Shift.AFTER))
	private void alphaver$registerDimensions(CallbackInfoReturnable<Boolean> cir) {
		AVDimensions.register();
	}
}
