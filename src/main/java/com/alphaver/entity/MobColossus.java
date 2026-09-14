package com.alphaver.entity;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobGiant;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobColossus extends MobGiant {

	public static final String DORMANT_NAME = "Dormant Giant";

	private static final String TAG_NAME = "BossName";
	private static final String TAG_MAX_HEALTH = "BossMaxHP";

	private static final int SUNBURN_TICKS = 300;

	private static final int DATA_NAME = 20;
	private static final int DATA_MAX_HEALTH = 21;

	private boolean dataDefined;

	public MobColossus(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "colossus");
		this.mobDrops.clear();
		if (AVItems.ESSENCE != null) {
			this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(AVItems.ESSENCE), 36, 47));
		}
	}

	public MobColossus(World world, int milestone, String name) {
		this(world);
		int m = Math.max(milestone, 1);
		int maxHealth = 20 * Math.min(4 * m, 800);
		this.entityData.set(DATA_MAX_HEALTH, maxHealth);
		this.setHealthRaw(maxHealth);
		this.entityData.set(DATA_NAME, name);
	}

	@Override
	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(DATA_NAME, DORMANT_NAME, String.class);
		this.entityData.define(DATA_MAX_HEALTH, -1, Integer.class);
		this.dataDefined = true;
	}

	public String getBossName() {
		String name = this.dataDefined ? this.entityData.getString(DATA_NAME) : null;
		return name == null || name.isEmpty() ? DORMANT_NAME : name;
	}

	private int bossMaxHealth() {
		return this.dataDefined ? this.entityData.getInt(DATA_MAX_HEALTH) : -1;
	}

	@Override
	public int getMaxHealth() {
		int maxHealth = this.bossMaxHealth();
		return maxHealth > 0 ? maxHealth : 20;
	}

	@Override
	public void onLivingUpdate() {
		int fireBefore = this.remainingFireTicks;
		super.onLivingUpdate();
		if (fireBefore <= 0 && this.remainingFireTicks == SUNBURN_TICKS && !this.isInLava()) {

			this.remainingFireTicks = 0;
		}
		if (!this.world.isClientSide && this.bossMaxHealth() == -1) {
			this.entityData.set(DATA_MAX_HEALTH, this.getHealth());
		}
	}

	@Override
	protected void causeFallDamage(float distance) {
	}

	@Override
	public void onDeath(Entity killer) {
		super.onDeath(killer);
		if (!this.world.isClientSide && AVItems.FLAMEBERGE != null && this.random.nextInt(50) > 10) {
			this.dropItem(new ItemStack(AVItems.FLAMEBERGE), 0.0F);
		}
	}

	@Override
	public String getLivingSound() {
		return "alphaver:ext.giantambient";
	}

	@Override
	protected String getHurtSound() {
		return "alphaver:ext.gianthit";
	}

	@Override
	protected String getDeathSound() {
		return "alphaver:ext.giantdead";
	}

	@Override
	public void addAdditionalSaveData(@NotNull CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		tag.putString(TAG_NAME, this.getBossName());
		tag.putInt(TAG_MAX_HEALTH, this.bossMaxHealth());
	}

	@Override
	public void readAdditionalSaveData(@NotNull CompoundTag tag) {
		super.readAdditionalSaveData(tag);
		if (tag.containsKey(TAG_NAME)) {
			this.entityData.set(DATA_NAME, tag.getString(TAG_NAME));
		}
		if (tag.containsKey(TAG_MAX_HEALTH)) {
			this.entityData.set(DATA_MAX_HEALTH, tag.getInteger(TAG_MAX_HEALTH));
		}
	}
}
