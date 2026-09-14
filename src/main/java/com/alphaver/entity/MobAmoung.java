package com.alphaver.entity;

import com.alphaver.AlphaVer;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.animal.MobAnimal;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class MobAmoung extends MobAnimal {

	public MobAmoung(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "amoung");
		this.setSize(1.0F, 1.0F);
		this.mobDrops.add(new WeightedRandomLootObject(new ItemStack(Items.FOOD_PORKCHOP_RAW), 0, 2));
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}

	@Override
	public boolean interact(@NotNull Player player) {
		return false;
	}

	@Override
	protected float getBlockPathWeight(@NotNull TilePosc blockPos) {
		if (!this.world.isBlockLoaded(blockPos)) {
			return 0.0F;
		}
		Block<?> below = this.world.getBlockType(blockPos.down(new TilePos()));
		if (below == Blocks.GRASS || below == Blocks.GRASS_RETRO) {
			return 10.0F;
		}
		return this.world.getLightBrightness(blockPos) - 0.5F;
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
