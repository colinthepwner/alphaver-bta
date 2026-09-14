package com.alphaver.entity;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobObserver extends MobMonster {

	private static final int STAGE_TICKS = 55;
	private static final int HOSTILE_AFTER = 550;
	private static final float TOO_CLOSE = 4.0F;
	private static final double ACQUIRE_RANGE = 64.0;
	private static final float PATH_RANGE = 64.0F;

	private static final String TAG_TIMER = "ObservationTimer";
	private static final String TAG_FIRST_ATTACK = "CommencedAttack";
	private static final String TAG_HAS_ANCHOR = "HasLLoc";
	private static final String TAG_ANCHOR_X = "llocx";
	private static final String TAG_ANCHOR_Y = "llocy";
	private static final String TAG_ANCHOR_Z = "llocz";

	private Entity stalkingTarget;
	private long timer;
	private int stage;
	private boolean firstAttack = true;
	private boolean hasAnchor;
	private long anchorX;
	private long anchorY;
	private long anchorZ;

	public MobObserver(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "observer");
		this.setSize(1.0F, 2.0F);
		this.moveSpeed = 3.0F;

		if (AVItems.OBSERVER_FUR != null) {
			this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(AVItems.OBSERVER_FUR), 0, 2));
		}
		if (AVItems.ESSENCE != null) {
			this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(AVItems.ESSENCE), 19, 23));
		}
	}

	@Override
	public void onLivingUpdate() {
		boolean stalking = false;
		if (!this.world.isClientSide) {
			stalking = this.stalk();
		}
		super.onLivingUpdate();
		if (stalking) {

			this.faceTarget();
		}
	}

	private boolean stalk() {
		if (this.stalkingTarget != null && (this.stalkingTarget.removed || !this.stalkingTarget.isAlive())) {
			this.stalkingTarget = null;
		}
		if (this.stalkingTarget == null) {
			this.stalkingTarget = this.world.getClosestPlayerToEntity(this, ACQUIRE_RANGE);
			this.timer = 0L;
			this.firstAttack = true;
			return false;
		}

		boolean watching = this.timer <= HOSTILE_AFTER;
		if (!watching) {
			this.target = this.stalkingTarget;
		} else {
			this.target = null;
			this.timer++;
			if (this.timer > HOSTILE_AFTER) {
				this.world.playSoundAtEntity(null, this, "alphaver:ext.obvr_attack", 1.0F, 1.0F);
			}
			if (this.distanceTo(this.stalkingTarget) < TOO_CLOSE) {
				this.timer += STAGE_TICKS;

				this.relocate(8 * (11 - this.stage));
			}
			int previousStage = this.stage;
			this.stage = 1 + (int) (this.timer / STAGE_TICKS);
			if (previousStage != this.stage) {
				this.relocate(5 + 3 * (11 - this.stage));
			}
			if (this.hasAnchor) {
				this.setPos(this.anchorX, this.anchorY, this.anchorZ);
			}
		}
		this.faceTarget();
		return watching;
	}

	private void faceTarget() {
		if (this.stalkingTarget == null) {
			return;
		}
		double dx = this.x - this.stalkingTarget.x;
		double dz = this.z - this.stalkingTarget.z;
		this.setRot((float) (Math.atan2(dx, -dz) * 180.0 / Math.PI), 0.0F);
	}

	private void relocate(int radius) {
		Entity anchor = this.stalkingTarget != null ? this.stalkingTarget : this;
		int anchorBlockX = (int) anchor.x;
		int x = anchorBlockX + radius * (this.random.nextInt(3) - 1);
		int z = (int) anchor.z + radius * (this.random.nextInt(3) - 1);

		if (x == anchorBlockX && z == anchor.z) {
			z += 8;
		}
		int y = (int) anchor.y;
		while (this.notFullBlock(x, y, z) && y != 0) {
			y--;
		}
		while (!this.notFullBlock(x, y, z) && y != 128) {
			y++;
		}
		this.hasAnchor = true;
		this.anchorX = x;
		this.anchorY = y;
		this.anchorZ = z;
		this.setPos(x, y, z);
	}

	@SuppressWarnings("deprecation")
	private boolean notFullBlock(int x, int y, int z) {
		int id = this.world.getBlockId(x, y, z);
		if (id == 0) {
			return true;
		}
		if (id == Blocks.FLUID_WATER_STILL.id() || id == Blocks.FLUID_LAVA_STILL.id()) {
			return false;
		}
		return !Blocks.solid[id];
	}

	@Override
	protected void updateAI() {
		if (this.target != null && this.target.isAlive() && (this.pathToEntity == null || this.random.nextInt(20) == 0)) {
			this.pathToEntity = this.world.getPathToEntity(this, this.target, PATH_RANGE);
		}
		super.updateAI();
	}

	@Override
	protected Entity findPlayerToAttack() {

		return null;
	}

	@Override
	protected void attackEntity(@NotNull Entity entity, float distance) {
		this.attackStrength = this.firstAttack ? 0 : 2;
		this.firstAttack = false;
		super.attackEntity(entity, distance);
	}

	@Override
	public boolean hurt(Entity attacker, int damage, DamageType type) {
		if (attacker instanceof Mob && attacker != this.stalkingTarget) {
			this.stalkingTarget = attacker;
			this.timer = HOSTILE_AFTER;
		}
		boolean wasAlive = this.getHealth() > 0;
		boolean hurt = super.hurt(attacker, damage, type);
		if (wasAlive && this.getHealth() <= 0 && attacker instanceof Player && attacker instanceof AVMobEventData data) {
			data.alphaver$addObserverCooldown(-2000L);
		}
		return hurt;
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
