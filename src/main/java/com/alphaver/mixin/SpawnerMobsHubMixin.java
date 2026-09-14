package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.world.SpawnerMobs;
import net.minecraft.core.world.World;
import net.minecraft.core.world.config.spawning.SpawnerConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SpawnerMobs.class, remap = false)
public abstract class SpawnerMobsHubMixin {

	@Inject(method = "performSpawning", at = @At("HEAD"), cancellable = true)
	private static void alphaver$noHubSpawns(World world, SpawnerConfig spawnerConfig, CallbackInfoReturnable<Integer> cir) {
		if (AVWorlds.isHub(world) || AVWorlds.isMinigame(world)) {
			cir.setReturnValue(0);
		}
	}
}
