package com.alphaver.mixin.client;

import com.alphaver.client.render.CypressSky;
import com.alphaver.client.render.HubSky;
import com.alphaver.world.AVWorlds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.RenderGlobal;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.world.weather.Weather;
import org.joml.Matrix4f;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = RenderGlobal.class, remap = false)
public abstract class RenderGlobalSkyMixin {

	@Shadow
	private WorldClient world;

	@Shadow
	private double rainbowBrightness;

	@Inject(method = "renderSky", at = @At("HEAD"), cancellable = true)
	private void alphaver$hubSky(float partialTick, CallbackInfo ci) {
		if (HubSky.render(partialTick)) {
			ci.cancel();
		}
	}

	@ModifyConstant(method = "renderSky", constant = @Constant(stringValue = "/assets/minecraft/textures/terrain/sun.png"))
	private String alphaver$sunTexture(String vanilla) {
		return CypressSky.sun(vanilla);
	}

	@ModifyConstant(method = "renderSky", constant = @Constant(stringValue = "/assets/minecraft/textures/terrain/moon.png"))
	private String alphaver$moonTexture(String vanilla) {
		return CypressSky.moon(vanilla);
	}

	@Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lorg/joml/Matrix4f;rotateZ(F)Lorg/joml/Matrix4f;", ordinal = 1))
	private Matrix4f alphaver$cypressSunPath(Matrix4f matrix, float angle) {
		return AVWorlds.isCypress(this.world) ? matrix.rotateX(angle) : matrix.rotateZ(angle);
	}

	@Redirect(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/world/WorldClient;getStarBrightness(F)F"))
	private float alphaver$cypressStars(WorldClient world, float partialTick) {
		float brightness = world.getStarBrightness(partialTick);
		return AVWorlds.isCypress(world) ? CypressSky.starBrightness(brightness) : brightness;
	}

	@Redirect(method = "renderSky", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/world/WorldClient;getCurrentWeather()Lnet/minecraft/core/world/weather/Weather;"))
	private Weather alphaver$noWeatherFade(WorldClient world) {
		return AVWorlds.isAlphaVer(world) ? null : world.getCurrentWeather();
	}

	@Inject(method = "renderSky", at = @At("HEAD"))
	private void alphaver$noRainbowCarriedIn(float partialTick, CallbackInfo ci) {
		if (AVWorlds.isAlphaVer(this.world)) {
			this.rainbowBrightness = 0.0;
		}
	}

	@Redirect(method = "renderSky", at = @At(value = "FIELD", target = "Lnet/minecraft/client/world/WorldClient;rainbowTicks:I",
		opcode = Opcodes.GETFIELD))
	private int alphaver$noRainbow(WorldClient world) {
		return AVWorlds.isAlphaVer(world) ? 0 : world.rainbowTicks;
	}

	@Inject(method = "renderSky", at = @At("TAIL"))
	private void alphaver$cypressVoidPlane(float partialTick, CallbackInfo ci) {
		if (AVWorlds.isCypress(this.world)) {
			CypressSky.renderVoidPlane(Minecraft.getMinecraft(), this.world, partialTick);
		}
	}
}
