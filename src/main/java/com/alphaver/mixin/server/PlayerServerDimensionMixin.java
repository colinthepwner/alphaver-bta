package com.alphaver.mixin.server;

import com.alphaver.world.AVDimensions;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.world.ServerPlayerController;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(value = PlayerServer.class, remap = false)
public abstract class PlayerServerDimensionMixin {

	@Inject(method = "<init>(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/core/world/World;Ljava/lang/String;Ljava/util/UUID;Lnet/minecraft/server/world/ServerPlayerController;)V",
		at = @At("TAIL"))
	private void alphaver$dimensionOfWorld(MinecraftServer server, World world, String username, UUID uuid, ServerPlayerController controller,
	                                       CallbackInfo ci) {
		if (AVDimensions.isAlphaVer(world)) {
			((Player) (Object) this).dimension = world.dimension.id;
		}
	}
}
