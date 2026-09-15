package com.alphaver.item;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class AVDurability {
	private AVDurability() {}

	private static final int[] ARMOUR_BASE = {11, 16, 15, 13};

	private static volatile Map<Integer, Integer> cypressMax;

	public static int wear(@NotNull ItemStack stack, int damage, @Nullable Entity entity) {
		if (damage <= 0 || !(entity instanceof Player) || !AVWorlds.isAlphaVer(entity.world)) {
			return damage;
		}
		Integer alpha = table().get(stack.itemID);
		if (alpha == null) {
			return damage;
		}
		if (alpha == 0) {
			return 0;
		}
		int max = stack.getMaxDamage();
		if (max <= alpha) {

			return damage;
		}
		long used = Math.max(0, stack.getMetadata());
		long spent = used * alpha / max;
		long after = spent + damage;
		long charged = (after * max + alpha - 1) / alpha;
		return (int) Math.min(Integer.MAX_VALUE, Math.max(1L, charged - used));
	}

	public static int armourWear(@NotNull Player player, int btaWear, int hurt) {
		return AVWorlds.isAlphaVer(player.world) ? Math.max(btaWear, hurt) : btaWear;
	}

	private static Map<Integer, Integer> table() {
		Map<Integer, Integer> table = cypressMax;
		if (table != null) {
			return table;
		}
		table = new HashMap<>();
		put(table, 32, Items.TOOL_SHOVEL_WOOD, Items.TOOL_PICKAXE_WOOD, Items.TOOL_AXE_WOOD, Items.TOOL_SWORD_WOOD,
			Items.TOOL_SHOVEL_GOLD, Items.TOOL_PICKAXE_GOLD, Items.TOOL_AXE_GOLD, Items.TOOL_SWORD_GOLD, Items.TOOL_HOE_WOOD);
		put(table, 64, Items.TOOL_SHOVEL_STONE, Items.TOOL_PICKAXE_STONE, Items.TOOL_AXE_STONE, Items.TOOL_SWORD_STONE,
			Items.TOOL_HOE_STONE, Items.TOOL_HOE_GOLD, Items.TOOL_FIRESTRIKER_IRON);
		put(table, 128, Items.TOOL_SHOVEL_IRON, Items.TOOL_PICKAXE_IRON, Items.TOOL_AXE_IRON, Items.TOOL_SWORD_IRON, Items.TOOL_HOE_IRON);
		put(table, 256, Items.TOOL_HOE_DIAMOND);
		put(table, 1024, Items.TOOL_SHOVEL_DIAMOND, Items.TOOL_PICKAXE_DIAMOND, Items.TOOL_AXE_DIAMOND, Items.TOOL_SWORD_DIAMOND);
		put(table, 0, Items.TOOL_BOW);
		armour(table, 0, Items.ARMOR_HELMET_LEATHER, Items.ARMOR_CHESTPLATE_LEATHER, Items.ARMOR_LEGGINGS_LEATHER, Items.ARMOR_BOOTS_LEATHER);
		armour(table, 1, Items.ARMOR_HELMET_CHAINMAIL, Items.ARMOR_CHESTPLATE_CHAINMAIL, Items.ARMOR_LEGGINGS_CHAINMAIL,
			Items.ARMOR_BOOTS_CHAINMAIL);
		armour(table, 2, Items.ARMOR_HELMET_IRON, Items.ARMOR_CHESTPLATE_IRON, Items.ARMOR_LEGGINGS_IRON, Items.ARMOR_BOOTS_IRON);
		armour(table, 3, Items.ARMOR_HELMET_DIAMOND, Items.ARMOR_CHESTPLATE_DIAMOND, Items.ARMOR_LEGGINGS_DIAMOND, Items.ARMOR_BOOTS_DIAMOND);
		armour(table, 1, Items.ARMOR_HELMET_GOLD, Items.ARMOR_CHESTPLATE_GOLD, Items.ARMOR_LEGGINGS_GOLD, Items.ARMOR_BOOTS_GOLD);
		cypressMax = table;
		return table;
	}

	private static void put(Map<Integer, Integer> table, int max, Item... items) {
		for (Item item : items) {
			if (item != null) {
				table.put(item.id, max);
			}
		}
	}

	private static void armour(Map<Integer, Integer> table, int level, Item helmet, Item chestplate, Item leggings, Item boots) {
		Item[] pieces = {helmet, chestplate, leggings, boots};
		for (int piece = 0; piece < pieces.length; piece++) {
			if (pieces[piece] != null) {
				table.put(pieces[piece].id, ARMOUR_BASE[piece] * 3 << level);
			}
		}
	}
}
