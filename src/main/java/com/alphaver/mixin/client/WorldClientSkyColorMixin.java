package com.alphaver.mixin.client;

import com.alphaver.client.render.CypressSky;
import com.alphaver.world.AVWorlds;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldClient.class, remap = false)
public abstract class WorldClientSkyColorMixin {

	@ModifyVariable(method = "getSkyColor", at = @At("STORE"), ordinal = 0)
	private int alphaver$cypressSkyColor(int biomeSkyColor) {
		World world = (World) (Object) this;
		return AVWorlds.isAlphaVer(world) ? CypressSky.daySkyColor(world.getWorldTime()) : biomeSkyColor;
	}

	@Redirect(method = {"getSkyColor", "getDimensionColor"}, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/world/WorldClient;getCurrentWeather()Lnet/minecraft/core/world/weather/Weather;"))
	private Weather alphaver$noWeatherTint(WorldClient world) {
		return AVWorlds.isAlphaVer(world) ? null : world.getCurrentWeather();
	}
}
