package com.alphaver.mixin;

import com.alphaver.world.travel.AVCypressHome;
import com.alphaver.world.travel.AVInvites;
import com.alphaver.world.travel.AVTravelData;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerTravelMixin implements AVTravelData {

	@Unique
	private static final String KEY_X = "AlphaVerReturnX";
	@Unique
	private static final String KEY_Y = "AlphaVerReturnY";
	@Unique
	private static final String KEY_Z = "AlphaVerReturnZ";
	@Unique
	private static final String KEY_INVITED = AVInvites.SAVE_KEY;
	@Unique
	private static final String KEY_HOME_X = "AlphaVerHomeX";
	@Unique
	private static final String KEY_HOME_Y = "AlphaVerHomeY";
	@Unique
	private static final String KEY_HOME_Z = "AlphaVerHomeZ";
	@Unique
	private static final String KEY_HOME_TRIP = "AlphaVerHomeTrip";
	@Unique
	private static final String KEY_SPAWN_DIMENSION = "AlphaVerSpawnDimension";

	@Unique
	private boolean alphaver$hasReturn;
	@Unique
	private int alphaver$returnX;
	@Unique
	private int alphaver$returnY;
	@Unique
	private int alphaver$returnZ;
	@Unique
	private boolean alphaver$invited;
	@Unique
	private boolean alphaver$hasHome;
	@Unique
	private int alphaver$homeX;
	@Unique
	private int alphaver$homeY;
	@Unique
	private int alphaver$homeZ;
	@Unique
	private boolean alphaver$homeTripPending;
	@Unique
	private int alphaver$spawnDimension = UNKNOWN_DIMENSION;
	@Unique
	private boolean alphaver$loadedFromSave;

	@Override
	public boolean alphaver$hasReturn() {
		return this.alphaver$hasReturn;
	}

	@Override
	public int alphaver$returnX() {
		return this.alphaver$returnX;
	}

	@Override
	public int alphaver$returnY() {
		return this.alphaver$returnY;
	}

	@Override
	public int alphaver$returnZ() {
		return this.alphaver$returnZ;
	}

	@Override
	public void alphaver$setReturn(int x, int y, int z) {
		this.alphaver$hasReturn = true;
		this.alphaver$returnX = x;
		this.alphaver$returnY = y;
		this.alphaver$returnZ = z;
	}

	@Override
	public void alphaver$clearReturn() {
		this.alphaver$hasReturn = false;
	}

	@Override
	public boolean alphaver$invited() {
		return this.alphaver$invited;
	}

	@Override
	public void alphaver$setInvited(boolean invited) {
		this.alphaver$invited = invited;
	}

	@Override
	public boolean alphaver$hasHome() {
		return this.alphaver$hasHome;
	}

	@Override
	public int alphaver$homeX() {
		return this.alphaver$homeX;
	}

	@Override
	public int alphaver$homeY() {
		return this.alphaver$homeY;
	}

	@Override
	public int alphaver$homeZ() {
		return this.alphaver$homeZ;
	}

	@Override
	public void alphaver$setHome(int x, int y, int z) {
		this.alphaver$hasHome = true;
		this.alphaver$homeX = x;
		this.alphaver$homeY = y;
		this.alphaver$homeZ = z;
	}

	@Override
	public boolean alphaver$homeTripPending() {
		return this.alphaver$homeTripPending;
	}

	@Override
	public void alphaver$setHomeTripPending(boolean pending) {
		this.alphaver$homeTripPending = pending;
	}

	@Override
	public int alphaver$spawnDimension() {
		return this.alphaver$spawnDimension;
	}

	@Override
	public boolean alphaver$loadedFromSave() {
		return this.alphaver$loadedFromSave;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void alphaver$homeTrip(CallbackInfo ci) {
		if (this.alphaver$homeTripPending) {
			AVCypressHome.tick((Player) (Object) this);
		}
	}

	@Inject(method = "setPlayerSpawnPoint(Lnet/minecraft/core/world/pos/TilePos;)V", at = @At("TAIL"))
	private void alphaver$recordSpawnDimension(TilePos spawnCoords, CallbackInfo ci) {
		Player self = (Player) (Object) this;
		this.alphaver$spawnDimension = spawnCoords != null && self.world != null && self.world.dimension != null
			? self.world.dimension.id : UNKNOWN_DIMENSION;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$saveReturn(CompoundTag tag, CallbackInfo ci) {
		if (this.alphaver$hasReturn) {
			tag.putInt(KEY_X, this.alphaver$returnX);
			tag.putInt(KEY_Y, this.alphaver$returnY);
			tag.putInt(KEY_Z, this.alphaver$returnZ);
		}
		if (this.alphaver$invited) {
			tag.putBoolean(KEY_INVITED, true);
		}
		if (this.alphaver$hasHome) {
			tag.putInt(KEY_HOME_X, this.alphaver$homeX);
			tag.putInt(KEY_HOME_Y, this.alphaver$homeY);
			tag.putInt(KEY_HOME_Z, this.alphaver$homeZ);
		}
		if (this.alphaver$homeTripPending) {

			tag.putBoolean(KEY_HOME_TRIP, true);
		}
		if (this.alphaver$spawnDimension != UNKNOWN_DIMENSION && ((Player) (Object) this).getPlayerSpawnPoint() != null) {
			tag.putInt(KEY_SPAWN_DIMENSION, this.alphaver$spawnDimension);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$loadReturn(CompoundTag tag, CallbackInfo ci) {
		this.alphaver$loadedFromSave = true;
		this.alphaver$hasReturn = tag.containsKey(KEY_X) && tag.containsKey(KEY_Y) && tag.containsKey(KEY_Z);
		if (this.alphaver$hasReturn) {
			this.alphaver$returnX = tag.getInteger(KEY_X);
			this.alphaver$returnY = tag.getInteger(KEY_Y);
			this.alphaver$returnZ = tag.getInteger(KEY_Z);
		}

		this.alphaver$invited = tag.containsKey(KEY_INVITED) && tag.getBoolean(KEY_INVITED);
		this.alphaver$hasHome = tag.containsKey(KEY_HOME_X) && tag.containsKey(KEY_HOME_Y) && tag.containsKey(KEY_HOME_Z);
		if (this.alphaver$hasHome) {
			this.alphaver$homeX = tag.getInteger(KEY_HOME_X);
			this.alphaver$homeY = tag.getInteger(KEY_HOME_Y);
			this.alphaver$homeZ = tag.getInteger(KEY_HOME_Z);
		}
		this.alphaver$homeTripPending = tag.containsKey(KEY_HOME_TRIP) && tag.getBoolean(KEY_HOME_TRIP);

		this.alphaver$spawnDimension = tag.containsKey(KEY_SPAWN_DIMENSION) ? tag.getInteger(KEY_SPAWN_DIMENSION) : UNKNOWN_DIMENSION;
	}
}
