package com.alphaver.mixin.server;

import com.alphaver.world.travel.AVCypressHome;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.handler.PacketHandlerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = PacketHandlerServer.class, remap = false)
public abstract class PacketHandlerServerRespawnHomeMixin {

	@ModifyArg(method = "handleRespawn", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/server/net/PlayerList;recreatePlayerEntity(Lnet/minecraft/server/entity/player/PlayerServer;I)Lnet/minecraft/server/entity/player/PlayerServer;"),
		index = 1)
	private int alphaver$respawnHome(PlayerServer player, int dimension) {
		int home = player == null ? AVCypressHome.NONE : AVCypressHome.respawnDimension(player.world, player);
		return home != AVCypressHome.NONE ? home : dimension;
	}
}
