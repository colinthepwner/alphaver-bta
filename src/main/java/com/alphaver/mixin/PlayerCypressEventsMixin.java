package com.alphaver.mixin;

import com.alphaver.AlphaVer;
import com.alphaver.entity.AVMobEventData;
import com.alphaver.entity.CypressMobEvents;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerCypressEventsMixin implements AVMobEventData {

	@Unique
	private static final String KEY_OBSERVER_COOLDOWN = "ObvCDTimer";

	@Unique
	private static boolean alphaver$reportedFailure;

	@Unique
	private long alphaver$observerCooldown;
	@Unique
	private boolean alphaver$tracking;
	@Unique
	private long alphaver$blockX;
	@Unique
	private long alphaver$blockZ;
	@Unique
	private long alphaver$areaX;
	@Unique
	private long alphaver$areaZ;

	@Override
	public long alphaver$observerCooldown() {
		return this.alphaver$observerCooldown;
	}

	@Override
	public void alphaver$setObserverCooldown(long ticks) {
		this.alphaver$observerCooldown = ticks;
	}

	@Override
	public void alphaver$addObserverCooldown(long delta) {
		this.alphaver$observerCooldown += delta;
	}

	@Override
	public boolean alphaver$tracking() {
		return this.alphaver$tracking;
	}

	@Override
	public long alphaver$lastBlockX() {
		return this.alphaver$blockX;
	}

	@Override
	public long alphaver$lastBlockZ() {
		return this.alphaver$blockZ;
	}

	@Override
	public long alphaver$lastAreaX() {
		return this.alphaver$areaX;
	}

	@Override
	public long alphaver$lastAreaZ() {
		return this.alphaver$areaZ;
	}

	@Override
	public void alphaver$track(long blockX, long blockZ, long areaX, long areaZ) {
		this.alphaver$tracking = true;
		this.alphaver$blockX = blockX;
		this.alphaver$blockZ = blockZ;
		this.alphaver$areaX = areaX;
		this.alphaver$areaZ = areaZ;
	}

	@Override
	public void alphaver$stopTracking() {
		this.alphaver$tracking = false;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void alphaver$cypressMobEvents(CallbackInfo ci) {
		try {
			CypressMobEvents.tick((Player) (Object) this);
		} catch (RuntimeException e) {
			if (!alphaver$reportedFailure) {
				alphaver$reportedFailure = true;
				AlphaVer.LOGGER.error("Cypress mob events failed; further failures this session are not logged.", e);
			}
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$saveMobEvents(CompoundTag tag, CallbackInfo ci) {
		if (this.alphaver$observerCooldown != 0L) {
			tag.putLong(KEY_OBSERVER_COOLDOWN, this.alphaver$observerCooldown);
		}
	}

	@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
	private void alphaver$loadMobEvents(CompoundTag tag, CallbackInfo ci) {
		this.alphaver$observerCooldown = tag.containsKey(KEY_OBSERVER_COOLDOWN) ? tag.getLong(KEY_OBSERVER_COOLDOWN) : 0L;
	}
}
