package com.alphaver.world.gen.feature;

import com.alphaver.item.AVItems;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class CypressLoot {
	private CypressLoot() {}

	@Nullable
	public static ItemStack pear() {
		return stack(AVItems.PEAR, 1);
	}

	@Nullable
	public static ItemStack teaLeaf(int count) {
		return stack(AVItems.TEA_LEAF, count);
	}

	@Nullable
	public static ItemStack rainConch() {
		return stack(AVItems.RAIN_CONCH, 1);
	}

	@Nullable
	public static ItemStack hoursLongPast(int part) {
		return switch (part) {
			case 1 -> stack(AVItems.HOURS_LONG_PAST_1, 1);
			case 2 -> stack(AVItems.HOURS_LONG_PAST_2, 1);
			case 3 -> stack(AVItems.HOURS_LONG_PAST_3, 1);
			case 4 -> stack(AVItems.HOURS_LONG_PAST_4, 1);
			default -> null;
		};
	}

	@Nullable
	public static ItemStack obsidianPear() {
		return stack(AVItems.OBSIDIAN_PEAR, 1);
	}

	@Nullable
	public static ItemStack sandcastles() {
		return stack(AVItems.RECORD_SANDCASTLES, 1);
	}

	@Nullable
	private static ItemStack stack(@Nullable Item item, int count) {
		return item == null ? null : new ItemStack(item, count);
	}
}
