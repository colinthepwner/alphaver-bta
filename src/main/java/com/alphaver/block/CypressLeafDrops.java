package com.alphaver.block;

import com.alphaver.item.AVItems;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public final class CypressLeafDrops {
	private CypressLeafDrops() {}

	@Nullable
	public static ItemStack[] roll(@NotNull Random rand, @NotNull IItemConvertible sapling, boolean teaBush) {
		if (rand.nextInt(20) != 0) {
			return null;
		}
		ItemStack drop;
		if (teaBush) {
			drop = rand.nextBoolean() ? new ItemStack(sapling) : stack(AVItems.TEA_LEAF);
		} else if (rand.nextBoolean()) {
			drop = new ItemStack(sapling);
		} else if (rand.nextBoolean()) {
			drop = new ItemStack(Items.FOOD_APPLE);
		} else {
			drop = rand.nextBoolean() ? stack(AVItems.PEAR) : stack(AVItems.RECORD_ROCK_BEETLE);
		}
		return drop == null ? null : new ItemStack[]{drop};
	}

	@Nullable
	private static ItemStack stack(@Nullable Item item) {
		return item == null ? null : new ItemStack(item);
	}
}
