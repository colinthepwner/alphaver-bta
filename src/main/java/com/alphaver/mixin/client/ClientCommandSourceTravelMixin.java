package com.alphaver.mixin.client;

import com.alphaver.world.travel.AVInvites;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.client.net.command.ClientCommandSource;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.world.Dimension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientCommandSource.class, remap = false)
public abstract class ClientCommandSourceTravelMixin {

	@Unique
	private Dimension alphaver$fromDimension;

	@Inject(method = "movePlayerToDimension", at = @At("HEAD"))
	private void alphaver$rememberOrigin(Player player, int dimension, CallbackInfo ci) {

		AVInvites.checkCommandMove(((CommandSource) (Object) this).getSender(), player, dimension);
		this.alphaver$fromDimension = Dimension.getDimensionList().get(player.dimension);
	}

	@Inject(method = "movePlayerToDimension", at = @At("TAIL"))
	private void alphaver$landLikeADoor(Player player, int dimension, CallbackInfo ci) {
		Dimension from = this.alphaver$fromDimension;
		this.alphaver$fromDimension = null;
		Dimension to = Dimension.getDimensionList().get(dimension);

		if (from == null || to == null || player.world == null || !player.isAlive()) {
			return;
		}
		AVTravel.handle(player.world, player, from, to);
	}
}
