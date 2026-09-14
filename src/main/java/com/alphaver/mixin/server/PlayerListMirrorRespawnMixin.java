package com.alphaver.mixin.server;

import com.alphaver.entity.AVInventoryStash;
import com.alphaver.item.AVMirrorSpawnData;
import com.alphaver.world.travel.AVRespawn;
import com.alphaver.world.travel.AVTravel;
import com.alphaver.world.travel.AVTravelData;
import net.minecraft.core.world.World;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerList.class, remap = false)
public abstract class PlayerListMirrorRespawnMixin {

	@Unique
	private static final int MAX_LIFT = 256;

	@Inject(method = "recreatePlayerEntity(Lnet/minecraft/server/entity/player/PlayerServer;I)Lnet/minecraft/server/entity/player/PlayerServer;",
		at = @At("TAIL"))
	private void alphaver$respawnPlace(PlayerServer previous, int dimension, CallbackInfoReturnable<PlayerServer> cir) {
		PlayerServer player = cir.getReturnValue();
		if (player == null || previous == null) {
			return;
		}

		AVInventoryStash.carryOver(previous, player);

		if (previous instanceof AVTravelData oldInvite && oldInvite.alphaver$invited()) {
			((AVTravelData) player).alphaver$setInvited(true);
		}
		int[] door = AVRespawn.doorFor(previous.world, previous, false);
		if (previous instanceof AVTravelData oldTravel && oldTravel.alphaver$hasReturn() && door == null) {
			((AVTravelData) player).alphaver$setReturn(oldTravel.alphaver$returnX(), oldTravel.alphaver$returnY(), oldTravel.alphaver$returnZ());
		}
		boolean mirror = previous instanceof AVMirrorSpawnData old && old.alphaver$hasMirrorSpawn();
		if (mirror) {
			AVMirrorSpawnData old = (AVMirrorSpawnData) previous;
			((AVMirrorSpawnData) player).alphaver$setMirrorSpawn(old.alphaver$mirrorDimension(), old.alphaver$mirrorX(),
				old.alphaver$mirrorY(), old.alphaver$mirrorZ());
		}

		World world = player.world;
		if (world == null || player.playerNetServerHandler == null) {
			return;
		}
		if (door != null) {
			int[] spot = AVTravel.besideDoor(world, door[0], door[1], door[2]);
			alphaver$place(player, world, spot[0] + 0.5, spot[1] + 0.1, spot[2] + 0.5);
			return;
		}

		AVMirrorSpawnData spawn = (AVMirrorSpawnData) player;
		if (!mirror || spawn.alphaver$mirrorDimension() != dimension || player.getPlayerSpawnPoint() != null) {
			return;
		}
		AVTravel.loadChunksAround(world, spawn.alphaver$mirrorX(), spawn.alphaver$mirrorZ());
		alphaver$place(player, world, spawn.alphaver$mirrorX() + 0.5, spawn.alphaver$mirrorY() + 0.1, spawn.alphaver$mirrorZ() + 0.5);
	}

	@Unique
	private static void alphaver$place(PlayerServer player, World world, double x, double y, double z) {
		player.setPos(x, y, z);
		for (int lift = 0; lift < MAX_LIFT && !world.getCubes(player, player.bb).isEmpty(); lift++) {
			player.setPos(player.x, player.y + 1.0, player.z);
		}
		player.playerNetServerHandler.teleportAndRotate(player.x, player.y, player.z, player.yRot, player.xRot);
		if (player.mcServer != null && player.mcServer.playerList != null) {
			player.mcServer.playerList.resendChunksAfterTeleport(player);
		}
	}
}
