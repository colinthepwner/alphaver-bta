package com.alphaver.entity;

import com.alphaver.AlphaVer;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobSaltSven extends MobMonster {

	private static final int STING_AFTER = 550;
	private static final double ACQUIRE_RANGE = 64.0;

	private static final String TAG_TIMER = "ObservationTimer";
	private static final String TAG_FIRST_ATTACK = "CommencedAttack";
	private static final String TAG_HAS_ANCHOR = "HasLLoc";
	private static final String TAG_ANCHOR_X = "llocx";
	private static final String TAG_ANCHOR_Y = "llocy";
	private static final String TAG_ANCHOR_Z = "llocz";

	private Entity watching;
	private long timer;
	private boolean firstAttack = true;
	private boolean hasAnchor;
	private long anchorX;
	private long anchorY;
	private long anchorZ;

	public MobSaltSven(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "salt_sven");
		this.setSize(1.0F, 2.0F);
		this.moveSpeed = 3.0F;
	}

	@Override
	public void onLivingUpdate() {
		if (!this.world.isClientSide) {
			this.watch();
		}
		super.onLivingUpdate();
		this.face();
	}

	private void watch() {
		if (this.watching != null && (this.watching.removed || !this.watching.isAlive())) {
			this.watching = null;
		}
		if (this.watching == null) {
			this.watching = this.world.getClosestPlayerToEntity(this, ACQUIRE_RANGE);
			this.timer = 0L;
			this.firstAttack = true;
			return;
		}
		this.target = null;
		this.timer++;
		if (this.timer > STING_AFTER) {
			this.world.playSoundAtEntity(null, this, "alphaver:ext.obvr_attack", 1.0F, 1.0F);
		}
		if (this.hasAnchor) {
			this.setPos(this.anchorX, this.anchorY, this.anchorZ);
		}
		this.face();
	}

	private void face() {
		if (this.watching == null) {
			return;
		}
		double dx = this.x - this.watching.x;
		double dz = this.z - this.watching.z;
		this.setRot((float) (Math.atan2(dx, -dz) * 180.0 / Math.PI), 0.0F);
	}

	@Override
	protected void updateAI() {
		this.moveForward = 0.0F;
		this.moveStrafing = 0.0F;
		this.isJumping = false;
	}

	@Override
	protected Entity findPlayerToAttack() {
		return null;
	}

	@Override
	protected boolean canDespawn() {
		return false;
	}

	@Override
	public String getLivingSound() {
		return null;
	}

	@Override
	protected String getHurtSound() {
		return "mob.skeletonhurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.skeletonhurt";
	}

	@Override
	protected float getSoundVolume() {
		return 0.4F;
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putLong(TAG_TIMER, this.timer);
		tag.putBoolean(TAG_FIRST_ATTACK, this.firstAttack);
		tag.putBoolean(TAG_HAS_ANCHOR, this.hasAnchor);
		if (this.hasAnchor) {
			tag.putLong(TAG_ANCHOR_X, this.anchorX);
			tag.putLong(TAG_ANCHOR_Y, this.anchorY);
			tag.putLong(TAG_ANCHOR_Z, this.anchorZ);
		}
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		this.timer = tag.getLong(TAG_TIMER);
		this.firstAttack = tag.getBoolean(TAG_FIRST_ATTACK);
		this.hasAnchor = tag.getBoolean(TAG_HAS_ANCHOR);
		if (this.hasAnchor) {
			this.anchorX = tag.getLong(TAG_ANCHOR_X);
			this.anchorY = tag.getLong(TAG_ANCHOR_Y);
			this.anchorZ = tag.getLong(TAG_ANCHOR_Z);
		}
	}
}
