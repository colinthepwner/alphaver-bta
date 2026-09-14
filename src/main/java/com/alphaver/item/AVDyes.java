package com.alphaver.item;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.registry.recipe.RecipeSymbol;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.util.helper.DyeColor;
import org.jetbrains.annotations.Nullable;

public final class AVDyes {
	private AVDyes() {}

	@Nullable
	public static DyeColor colorOf(@Nullable Item item) {
		if (item == null) {
			return null;
		}
		if (item == AVItems.DYE_BLACK) {
			return DyeColor.BLACK;
		}
		if (item == AVItems.DYE_GREEN) {
			return DyeColor.GREEN;
		}
		if (item == AVItems.DYE_BLUE) {
			return DyeColor.BLUE;
		}
		if (item == AVItems.DYE_PINK) {
			return DyeColor.MAGENTA;
		}
		return null;
	}

	@Nullable
	public static ItemStack forDyeing(@Nullable RecipeSymbol input, @Nullable ItemStack stack) {
		if (stack == null) {
			return null;
		}
		DyeColor color = colorOf(stack.getItem());
		if (color == null || (input != null && input.matches(new ItemStack(Blocks.WOOL, 1, 0)))) {
			return stack;
		}
		return new ItemStack(Items.DYE, stack.stackSize, color.itemMeta);
	}
}
