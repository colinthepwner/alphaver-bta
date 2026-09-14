package com.alphaver.block;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public final class AVRetroItems {
	private AVRetroItems() {}

	@Nullable
	public static ItemStack[] regularInside(@Nullable World world, @Nullable ItemStack[] drops) {
		if (drops == null || world == null || !AVWorlds.isAlphaVer(world)) {
			return drops;
		}
		ItemStack[] out = drops;
		for (int i = 0; i < drops.length; i++) {
			ItemStack stack = drops[i];
			Block<?> regular = stack == null ? null : regularOf(stack.itemID);
			if (regular == null) {
				continue;
			}
			if (out == drops) {
				out = drops.clone();
			}
			out[i] = new ItemStack(regular, stack.stackSize, stack.getMetadata());
		}
		return out;
	}

	@Nullable
	private static Block<?> regularOf(int itemId) {
		if (itemId == Blocks.GRASS_RETRO.id()) {
			return Blocks.GRASS;
		}
		if (itemId == Blocks.LEAVES_OAK_RETRO.id()) {
			return Blocks.LEAVES_OAK;
		}
		if (itemId == Blocks.SAPLING_OAK_RETRO.id()) {
			return Blocks.SAPLING_OAK;
		}
		return null;
	}
}
