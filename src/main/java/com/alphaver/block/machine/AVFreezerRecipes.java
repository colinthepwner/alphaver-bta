package com.alphaver.block.machine;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVItems;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class AVFreezerRecipes {
	private AVFreezerRecipes() {}

	public static final int FREEZE_TICKS = 200;
	public static final int COOLANT_ESSENCE_CACHE = 450;
	public static final int COOLANT_FRIGID_BITS = 55;

	private static volatile Map<Integer, ItemStack> table;

	@Nullable
	public static ItemStack result(@Nullable ItemStack input) {
		if (input == null) {
			return null;
		}
		if (isWaterBucket(input)) {
			return new ItemStack(Blocks.ICE);
		}
		ItemStack result = table().get(input.itemID);
		return result == null ? null : result.copy();
	}

	public static int coolantTicks(@Nullable ItemStack coolant) {
		if (coolant == null) {
			return 0;
		}
		if (AVBlocks.ESSENCE_CACHE != null && coolant.itemID == AVBlocks.ESSENCE_CACHE.id()) {
			return COOLANT_ESSENCE_CACHE;
		}
		if (AVItems.FRIGID_BITS != null && coolant.itemID == AVItems.FRIGID_BITS.id) {
			return COOLANT_FRIGID_BITS;
		}
		return 0;
	}

	public static void consumeInput(ItemStack[] slots, int slot) {
		ItemStack input = slots[slot];
		if (input == null) {
			return;
		}
		if (isWaterBucket(input)) {
			int charges = ItemBucket.getCharges(input) - 1;
			ItemBucket.setCharges(input, charges);
			if (charges <= 0) {
				ItemBucket.setState(input, ItemBucket.STATE_EMPTY);
			}
			return;
		}
		input.stackSize--;
		if (input.stackSize <= 0) {
			slots[slot] = null;
		}
	}

	static boolean isWaterBucket(ItemStack stack) {
		return stack.getItem() instanceof ItemBucket
			&& ItemBucket.STATE_WATER.equals(ItemBucket.getState(stack))
			&& ItemBucket.getCharges(stack) > 0;
	}

	private static Map<Integer, ItemStack> table() {
		Map<Integer, ItemStack> built = table;
		if (built == null) {
			synchronized (AVFreezerRecipes.class) {
				if (table == null) {
					table = build();
				}
				built = table;
			}
		}
		return built;
	}

	private static Map<Integer, ItemStack> build() {
		Map<Integer, ItemStack> map = new HashMap<>();
		if (AVBlocks.BRICK_SNOW != null) {
			map.put(Blocks.BLOCK_SNOW.id(), new ItemStack(AVBlocks.BRICK_SNOW));
		}
		if (AVItems.CANDY_ICE != null) {
			map.put(Items.FOOD_APPLE.id, new ItemStack(AVItems.CANDY_ICE));
		}
		map.put(Blocks.DIRT.id(), new ItemStack(Blocks.BLOCK_SNOW));
		if (AVItems.LIQUIFIED_FLAME != null && AVBlocks.CELESTIAL_FLAME != null) {
			map.put(AVItems.LIQUIFIED_FLAME.id, new ItemStack(AVBlocks.CELESTIAL_FLAME));
		}
		map.put(Items.SLIMEBALL.id, new ItemStack(Items.AMMO_SNOWBALL));
		return map;
	}
}
