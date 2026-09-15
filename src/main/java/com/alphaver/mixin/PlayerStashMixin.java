package com.alphaver.mixin;

import com.alphaver.entity.AVInventoryStash;
import com.alphaver.entity.AVStashData;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerStashMixin implements AVStashData {

	@Unique
	private static final String KEY_ITEMS = "AlphaVerStash";
	@Unique
	private static final String KEY_DIMENSION = "AlphaVerStashDimension";
	@Unique
	private static final String KEY_X = "AlphaVerStashX";
	@Unique
	private static final String KEY_Y = "AlphaVerStashY";
	@Unique
	private static final String KEY_Z = "AlphaVerStashZ";
	@Unique
	private static final String KEY_HEALTH = "AlphaVerStashHealth";
	@Unique
	private static final String KEY_GAMEMODE = "AlphaVerStashGamemode";

	@Unique
	private ListTag alphaver$stash;
	@Unique
	private int alphaver$stashDimension;
	@Unique
	private int alphaver$stashX;
	@Unique
	private int alphaver$stashY;
	@Unique
	private int alphaver$stashZ;
	@Unique
	private int alphaver$stashHealth = AVInventoryStash.HEALTH_FULL;
	@Unique
	@Nullable
	private String alphaver$stashGamemode;

	@Override
	public boolean alphaver$hasStash() {
		return this.alphaver$stash != null;
	}

	@Override
	public @Nullable ListTag alphaver$stashItems() {
		return this.alphaver$stash;
	}

	@Override
	public int alphaver$stashDimension() {
		return this.alphaver$stashDimension;
	}

	@Override
	public int alphaver$stashExitX() {
		return this.alphaver$stashX;
	}

	@Override
	public int alphaver$stashExitY() {
		return this.alphaver$stashY;
	}

	@Override
	public int alphaver$stashExitZ() {
		return this.alphaver$stashZ;
	}

	@Override
	public int alphaver$stashHealth() {
		return this.alphaver$stashHealth;
	}

	@Override
	public @Nullable String alphaver$stashGamemode() {
		return this.alphaver$stashGamemode;
	}

	@Override
	public void alphaver$setStash(ListTag items, int dimension, int exitX, int exitY, int exitZ, int health, @Nullable String gamemode) {
		this.alphaver$stash = items;
		this.alphaver$stashDimension = dimension;
		this.alphaver$stashX = exitX;
		this.alphaver$stashY = exitY;
		this.alphaver$stashZ = exitZ;
		this.alphaver$stashHealth = health;
		this.alphaver$stashGamemode = gamemode;
	}

	@Override
	public void alphaver$clearStash() {
		this.alphaver$stash = null;
		this.alphaver$stashGamemode = null;
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$saveStash(CompoundTag tag, CallbackInfo ci) {
		if (this.alphaver$stash != null) {
			tag.put(KEY_ITEMS, this.alphaver$stash);
			tag.putInt(KEY_DIMENSION, this.alphaver$stashDimension);
			tag.putInt(KEY_X, this.alphaver$stashX);
			tag.putInt(KEY_Y, this.alphaver$stashY);
			tag.putInt(KEY_Z, this.alphaver$stashZ);
			tag.putInt(KEY_HEALTH, this.alphaver$stashHealth);
			if (this.alphaver$stashGamemode != null) {
				tag.putString(KEY_GAMEMODE, this.alphaver$stashGamemode);
			}
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$loadStash(CompoundTag tag, CallbackInfo ci) {
		if (tag.containsKey(KEY_ITEMS) && tag.containsKey(KEY_DIMENSION)) {
			this.alphaver$stash = tag.getList(KEY_ITEMS);
			this.alphaver$stashDimension = tag.getInteger(KEY_DIMENSION);
			this.alphaver$stashX = tag.getInteger(KEY_X);
			this.alphaver$stashY = tag.getInteger(KEY_Y);
			this.alphaver$stashZ = tag.getInteger(KEY_Z);

			this.alphaver$stashHealth = tag.containsKey(KEY_HEALTH) ? tag.getInteger(KEY_HEALTH) : AVInventoryStash.HEALTH_FULL;

			this.alphaver$stashGamemode = tag.containsKey(KEY_GAMEMODE) ? tag.getString(KEY_GAMEMODE) : null;
		} else {
			this.alphaver$stash = null;
			this.alphaver$stashGamemode = null;
		}
	}
}
