package com.alphaver.mixin.client;

import com.alphaver.client.render.AVColoredLight;
import net.minecraft.client.render.LightmapHelper;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = LightmapHelper.class, remap = false)
public abstract class LightmapHelperColoredLightMixin {

	@Shadow
	@Final
	private int[] lightmapData;

	@Inject(method = "generateGrayLightmap(Lnet/minecraft/core/world/World;F)V", at = @At("TAIL"))
	private void alphaver$colourGray(World world, float partialTicks, CallbackInfo ci) {
		this.alphaver$colour(world);
	}

	@Inject(method = "generateColorizedLightmap(Lnet/minecraft/core/world/World;F)V", at = @At("TAIL"))
	private void alphaver$colourColorized(World world, float partialTicks, CallbackInfo ci) {
		this.alphaver$colour(world);
	}

	@Inject(method = "generateCustomLightmap(Lnet/minecraft/core/world/World;)V", at = @At("TAIL"))
	private void alphaver$colourCustom(World world, CallbackInfo ci) {
		this.alphaver$colour(world);
	}

	@Unique
	private void alphaver$colour(World world) {
		if (AVColoredLight.appliesTo(world)) {
			AVColoredLight.tint(this.lightmapData);
		}
	}
}
