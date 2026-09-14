package com.alphaver.mixin;

import com.alphaver.item.AVMirrorSpawnData;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMirrorSpawnMixin implements AVMirrorSpawnData {

	@Unique
	private static final String KEY_DIMENSION = "AlphaVerMirrorDimension";
	@Unique
	private static final String KEY_X = "AlphaVerMirrorX";
	@Unique
	private static final String KEY_Y = "AlphaVerMirrorY";
	@Unique
	private static final String KEY_Z = "AlphaVerMirrorZ";

	@Unique
	private boolean alphaver$hasMirrorSpawn;
	@Unique
	private int alphaver$mirrorDimension;
	@Unique
	private int alphaver$mirrorX;
	@Unique
	private int alphaver$mirrorY;
	@Unique
	private int alphaver$mirrorZ;

	@Override
	public boolean alphaver$hasMirrorSpawn() {
		return this.alphaver$hasMirrorSpawn;
	}

	@Override
	public int alphaver$mirrorDimension() {
		return this.alphaver$mirrorDimension;
	}

	@Override
	public int alphaver$mirrorX() {
		return this.alphaver$mirrorX;
	}

	@Override
	public int alphaver$mirrorY() {
		return this.alphaver$mirrorY;
	}

	@Override
	public int alphaver$mirrorZ() {
		return this.alphaver$mirrorZ;
	}

	@Override
	public void alphaver$setMirrorSpawn(int dimension, int x, int y, int z) {
		this.alphaver$hasMirrorSpawn = true;
		this.alphaver$mirrorDimension = dimension;
		this.alphaver$mirrorX = x;
		this.alphaver$mirrorY = y;
		this.alphaver$mirrorZ = z;
	}

	@Override
	public void alphaver$clearMirrorSpawn() {
		this.alphaver$hasMirrorSpawn = false;
	}

	@Inject(method = "setPlayerSpawnPoint(Lnet/minecraft/core/world/pos/TilePos;)V", at = @At("TAIL"))
	private void alphaver$laterBedWins(TilePos spawnCoords, CallbackInfo ci) {
		if (spawnCoords != null) {
			this.alphaver$hasMirrorSpawn = false;
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$saveMirrorSpawn(CompoundTag tag, CallbackInfo ci) {
		if (this.alphaver$hasMirrorSpawn) {
			tag.putInt(KEY_DIMENSION, this.alphaver$mirrorDimension);
			tag.putInt(KEY_X, this.alphaver$mirrorX);
			tag.putInt(KEY_Y, this.alphaver$mirrorY);
			tag.putInt(KEY_Z, this.alphaver$mirrorZ);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$loadMirrorSpawn(CompoundTag tag, CallbackInfo ci) {
		this.alphaver$hasMirrorSpawn = tag.containsKey(KEY_DIMENSION) && tag.containsKey(KEY_X) && tag.containsKey(KEY_Y)
			&& tag.containsKey(KEY_Z);
		if (this.alphaver$hasMirrorSpawn) {
			this.alphaver$mirrorDimension = tag.getInteger(KEY_DIMENSION);
			this.alphaver$mirrorX = tag.getInteger(KEY_X);
			this.alphaver$mirrorY = tag.getInteger(KEY_Y);
			this.alphaver$mirrorZ = tag.getInteger(KEY_Z);
		}
	}
}
