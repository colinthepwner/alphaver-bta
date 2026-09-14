package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import com.alphaver.world.CypressEnvironment;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherRain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = Weather.class, remap = false)
public abstract class WeatherCypressMixin {

	@Inject(method = "doEnvironmentUpdate", at = @At("HEAD"))
	private void alphaver$cypressEnvironment(World world, Random rand, int x, int z, CallbackInfo ci) {
		if (world.isClientSide || !AVWorlds.isCypress(world)) {
			return;
		}
		CypressEnvironment.tick(world, rand, x, z, (Object) this instanceof WeatherRain);
	}
}
