package com.alphaver.mixin.server;

import com.alphaver.world.travel.AVInvites;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.Dimension;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.command.ServerCommandSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ServerCommandSource.class, remap = false)
public abstract class ServerCommandSourceTravelMixin {

	@Unique
	private Dimension alphaver$fromDimension;

	@Inject(method = "movePlayerToDimension(Lnet/minecraft/core/entity/player/Player;I)V", at = @At("HEAD"))
	private void alphaver$rememberOrigin(Player player, int dimension, CallbackInfo ci) {

		AVInvites.checkCommandMove(((CommandSource) (Object) this).getSender(), player, dimension);
		this.alphaver$fromDimension = Dimension.getDimensionList().get(player.dimension);
	}

	@Inject(method = "movePlayerToDimension(Lnet/minecraft/core/entity/player/Player;I)V", at = @At("TAIL"))
	private void alphaver$landLikeADoor(Player player, int dimension, CallbackInfo ci) {
		Dimension from = this.alphaver$fromDimension;
		this.alphaver$fromDimension = null;
		Dimension to = Dimension.getDimensionList().get(dimension);
		if (from == null || to == null || player.world == null || !player.isAlive()) {
			return;
		}
		if (AVTravel.handle(player.world, player, from, to) && player instanceof PlayerServer playerServer
			&& playerServer.playerNetServerHandler != null) {
			playerServer.playerNetServerHandler.teleportAndRotate(playerServer.x, playerServer.y, playerServer.z,
				playerServer.yRot, playerServer.xRot);
		}
	}
}
