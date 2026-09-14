package com.alphaver.entity;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;

public class MobSquib extends MobMonster {

	public MobSquib(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "squib");
		this.setSize(1.0F, 2.0F);
		this.attackStrength = 2;
		if (AVItems.ESSENCE != null) {
			this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(AVItems.ESSENCE), 1));
		}
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
}
