package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import com.alphaver.world.travel.AVTravelData;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.player.Player;
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
	private static final String KEY_INVITED = "AlphaVerInvited";

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
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$loadReturn(CompoundTag tag, CallbackInfo ci) {
		this.alphaver$hasReturn = tag.containsKey(KEY_X) && tag.containsKey(KEY_Y) && tag.containsKey(KEY_Z);
		if (this.alphaver$hasReturn) {
			this.alphaver$returnX = tag.getInteger(KEY_X);
			this.alphaver$returnY = tag.getInteger(KEY_Y);
			this.alphaver$returnZ = tag.getInteger(KEY_Z);
		}

		this.alphaver$invited = tag.containsKey(KEY_INVITED) && tag.getBoolean(KEY_INVITED);
	}
}
