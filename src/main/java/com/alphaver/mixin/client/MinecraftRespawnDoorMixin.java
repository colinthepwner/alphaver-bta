package com.alphaver.mixin.client;

import com.alphaver.entity.AVInventoryStash;
import com.alphaver.item.AVMirrorSpawnData;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.travel.AVRespawn;
import com.alphaver.world.travel.AVTravel;
import com.alphaver.world.travel.AVTravelData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftRespawnDoorMixin {

	@Unique
	private static final int MAX_LIFT = 256;

	@Unique
	private boolean alphaver$leaving;

	@Unique
	private int[] alphaver$respawnDoor;

	@Unique
	private int[] alphaver$keptReturn;

	@Unique
	private int[] alphaver$keptMirror;

	@Unique
	private boolean alphaver$keptInvited;

	@Unique
	private Player alphaver$dead;

	@Inject(method = "respawn(ZI)V", at = @At("HEAD"))
	private void alphaver$beforeRespawn(boolean multiplayer, int targetDimension, CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;
		this.alphaver$leaving = false;
		this.alphaver$respawnDoor = null;
		this.alphaver$keptReturn = null;
		this.alphaver$keptMirror = null;
		Player old = mc.thePlayer;
		this.alphaver$dead = old;
		this.alphaver$keptInvited = old instanceof AVTravelData invite && invite.alphaver$invited();
		if (old == null || mc.currentWorld == null) {
			return;
		}
		if (old instanceof AVMirrorSpawnData mirror && mirror.alphaver$hasMirrorSpawn()) {
			this.alphaver$keptMirror = new int[]{mirror.alphaver$mirrorDimension(), mirror.alphaver$mirrorX(),
				mirror.alphaver$mirrorY(), mirror.alphaver$mirrorZ()};
		}

		if (!multiplayer && !mc.currentWorld.isClientSide && targetDimension == Dimension.OVERWORLD.id) {
			this.alphaver$leaving = AVRespawn.leavesDimension(mc.currentWorld, old, true);
			this.alphaver$respawnDoor = AVRespawn.doorFor(mc.currentWorld, old, true);
		}
		if (this.alphaver$respawnDoor == null && old instanceof AVTravelData travel && travel.alphaver$hasReturn()) {
			this.alphaver$keptReturn = new int[]{travel.alphaver$returnX(), travel.alphaver$returnY(), travel.alphaver$returnZ()};
		}
	}

	@Redirect(method = "respawn(ZI)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/type/WorldType;mayRespawn()Z"))
	private boolean alphaver$respawnAtDoorInstead(WorldType type) {
		return !this.alphaver$leaving && type.mayRespawn();
	}

	@Inject(method = "respawn(ZI)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/world/WorldClient;spawnPlayerWithLoadedChunks(Lnet/minecraft/core/entity/player/Player;Z)V"))
	private void alphaver$placeRespawn(boolean multiplayer, int targetDimension, CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;
		int[] door = this.alphaver$respawnDoor;
		int[] keptReturn = this.alphaver$keptReturn;
		int[] keptMirror = this.alphaver$keptMirror;
		boolean keptInvited = this.alphaver$keptInvited;
		this.alphaver$leaving = false;
		this.alphaver$respawnDoor = null;
		this.alphaver$keptReturn = null;
		this.alphaver$keptMirror = null;
		this.alphaver$keptInvited = false;

		Player player = mc.thePlayer;
		Player dead = this.alphaver$dead;
		this.alphaver$dead = null;
		if (player == null) {
			return;
		}

		AVInventoryStash.carryOver(dead, player);

		if (mc.currentWorld != null) {
			player.dimension = mc.currentWorld.dimension.id;
		}
		if (keptMirror != null && player instanceof AVMirrorSpawnData mirror) {
			mirror.alphaver$setMirrorSpawn(keptMirror[0], keptMirror[1], keptMirror[2], keptMirror[3]);
		}
		if (keptReturn != null && player instanceof AVTravelData travel) {
			travel.alphaver$setReturn(keptReturn[0], keptReturn[1], keptReturn[2]);
		}
		if (keptInvited && player instanceof AVTravelData invite) {
			invite.alphaver$setInvited(true);
		}

		World world = mc.currentWorld;

		if (door == null || world == null || AVWorlds.isAlphaVer(world)) {
			return;
		}
		world.getChunkProvider().setCurrentChunkOver(door[0] >> 4, door[2] >> 4);
		int[] spot = AVTravel.besideDoor(world, door[0], door[1], door[2]);
		player.xd = 0.0;
		player.yd = 0.0;
		player.zd = 0.0;
		player.moveTo(spot[0] + 0.5, spot[1] + 0.1, spot[2] + 0.5, player.yRot, 0.0F);
		for (int lift = 0; lift < MAX_LIFT && !world.getCubes(player, player.bb).isEmpty(); lift++) {
			player.setPos(player.x, player.y + 1.0, player.z);
		}
	}
}
