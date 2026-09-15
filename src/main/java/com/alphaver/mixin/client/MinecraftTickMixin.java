package com.alphaver.mixin.client;

import com.alphaver.client.render.AVColoredLight;
import com.alphaver.client.render.AVLookController;
import com.alphaver.client.render.CypressAmbientEffects;
import com.alphaver.client.render.CypressHomeLook;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftTickMixin {

	@Inject(method = "runTick", at = @At("HEAD"))
	private void alphaver$dimensionLook(CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;
		AVLookController.tick(mc);
		AVColoredLight.tick(mc);
		CypressAmbientEffects.tick(mc);
		CypressHomeLook.tick(mc);
	}
}
