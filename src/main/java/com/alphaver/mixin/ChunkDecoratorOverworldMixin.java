package com.alphaver.mixin;

import com.alphaver.world.travel.AVHubDoors;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.perlin.overworld.ChunkDecoratorOverworld;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ChunkDecoratorOverworld.class, remap = false)
public abstract class ChunkDecoratorOverworldMixin {

	@Shadow
	@Final
	private World world;

	@Inject(method = "decorate", at = @At("TAIL"))
	private void alphaver$hubDoors(Chunk chunk, CallbackInfo ci) {
		try {
			AVHubDoors.scatter(this.world, chunk);
		} catch (RuntimeException e) {

			com.alphaver.AlphaVer.LOGGER.error("Hub Door placement failed at chunk ({}, {})", chunk.pos.x, chunk.pos.z, e);
		}
	}
}
