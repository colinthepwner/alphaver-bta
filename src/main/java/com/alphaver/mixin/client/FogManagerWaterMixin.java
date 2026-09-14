package com.alphaver.mixin.client;

import com.alphaver.client.render.AVWaterLook;
import net.minecraft.client.render.FogManager;
import net.minecraft.client.render.colorizer.Colorizer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = FogManager.class, remap = false)
public abstract class FogManagerWaterMixin {

	@Redirect(
		method = "updateFogColor",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/colorizer/Colorizer;getColor(DD)I"))
	private int alphaver$alphaUnderwaterFog(Colorizer colorizer, double temperature, double humidity) {
		return AVWaterLook.alphaWater() ? AVWaterLook.ALPHA_UNDERWATER_FOG : colorizer.getColor(temperature, humidity);
	}
}
