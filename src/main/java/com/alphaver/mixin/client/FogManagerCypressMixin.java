package com.alphaver.mixin.client;

import com.alphaver.client.render.CypressFog;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.CypressEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.FogManager;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.world.weather.Weather;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = FogManager.class, remap = false)
public abstract class FogManagerCypressMixin {

	@Unique
	private static final float BRIGHTNESS_EASE = 0.1F;

	@Redirect(method = "updateFogColor", at = @At(value = "INVOKE", target = "Ljava/lang/Math;pow(DD)D"))
	private double alphaver$cypressSkyPull(double base, double exponent) {
		if (this.alphaver$inAlphaVer()) {
			return CypressFog.skyPullPower(GameSettings.RENDER_DISTANCE.value);
		}
		return Math.pow(base, exponent);
	}

	@Redirect(method = {"updateFogColor", "setupFog"}, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/world/WorldClient;getCurrentWeather()Lnet/minecraft/core/world/weather/Weather;"))
	private Weather alphaver$noWeatherFog(WorldClient world) {
		return AVWorlds.isAlphaVer(world) ? null : world.getCurrentWeather();
	}

	@Redirect(method = "updateFogColor", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL41;glClearColor(FFFF)V"))
	private void alphaver$cypressPulse(float red, float green, float blue, float alpha) {
		FogManager self = (FogManager) (Object) this;
		WorldClient world = self.mc.currentWorld;
		if (world == null || !AVWorlds.isAlphaVer(world)) {
			GL41.glClearColor(red, green, blue, alpha);
			return;
		}
		float mix = CypressEvents.purplePulse(world.getWorldTime()) / 2.0F;
		if (mix > 0.0F) {
			self.fogRed += (CypressEvents.PULSE_R - self.fogRed) * mix;
			self.fogGreen += (CypressEvents.PULSE_G - self.fogGreen) * mix;
			self.fogBlue += (CypressEvents.PULSE_B - self.fogBlue) * mix;
		}
		GL41.glClearColor(self.fogRed, self.fogGreen, self.fogBlue, alpha);
	}

	@Inject(method = "updateBrightness", at = @At("TAIL"))
	private void alphaver$cypressFogBrightness(CallbackInfo ci) {
		FogManager self = (FogManager) (Object) this;
		Minecraft mc = self.mc;
		WorldClient world = mc.currentWorld;
		if (world == null || mc.activeCamera == null || !AVWorlds.isAlphaVer(world)) {
			return;
		}
		float light = mc.fullbright ? 1.0F : world.getLightBrightness(mc.activeCamera.getTilePos());
		float floor = CypressFog.brightnessFloor(GameSettings.RENDER_DISTANCE.value);
		float target = light * (1.0F - floor) + floor;
		self.fogBrightness = self.fogBrightnessOld + (target - self.fogBrightnessOld) * BRIGHTNESS_EASE;
	}

	@Unique
	private boolean alphaver$inAlphaVer() {
		WorldClient world = ((FogManager) (Object) this).mc.currentWorld;
		return world != null && AVWorlds.isAlphaVer(world);
	}
}
