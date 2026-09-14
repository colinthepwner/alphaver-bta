package com.alphaver.entity;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class MobSpearmaster extends MobMonster {

	private static final float RANGE = 12.0F;
	private static final int COOLDOWN = 25;
	private static final int SUNBURN_TICKS = 150;

	public MobSpearmaster(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "spearmaster");
		if (AVItems.SPEAR != null) {
			this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(AVItems.SPEAR), 0, 2));
		}
		if (AVItems.ESSENCE != null) {
			this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(AVItems.ESSENCE), 12, 17));
		}
	}

	@Override
	public void onLivingUpdate() {
		if (!this.world.isClientSide && this.world.isDaytime()) {
			float brightness = this.calcBrightness(1.0F);
			if (brightness > 0.5F
				&& this.world.canBlockSeeTheSky(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
				&& this.random.nextFloat() * 30.0F < (brightness - 0.4F) * 2.0F) {
				this.remainingFireTicks = SUNBURN_TICKS;
			}
		}
		super.onLivingUpdate();
	}

	@Override
	protected void attackEntity(@NotNull Entity target, float distance) {
		if (distance >= RANGE) {
			return;
		}
		double dx = target.x - this.x;
		double dz = target.z - this.z;
		if (this.attackTime == 0 && !this.world.isClientSide) {
			EntitySpear spear = new EntitySpear(this.world, this, false);
			double dy = target.y - 0.2 - spear.y;
			double arc = Math.sqrt(dx * dx + dz * dz) * 0.2;
			this.world.playSoundAtEntity(null, this, "random.bow", 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
			spear.setHeading(dx, dy + arc, dz, 0.8F, 15.0F);
			this.world.entityJoinedWorld(spear);
			this.attackTime = COOLDOWN;
		}
		this.yRot = (float) (Math.atan2(dz, dx) * 180.0 / Math.PI) - 90.0F;
		this.hasAttacked = true;
	}

	@Override
	public String getLivingSound() {
		return "mob.skeleton";
	}

	@Override
	protected String getHurtSound() {
		return "mob.skeletonhurt";
	}

	@Override
	protected String getDeathSound() {
		return "mob.skeletonhurt";
	}
}
