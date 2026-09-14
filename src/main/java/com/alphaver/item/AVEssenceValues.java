package com.alphaver.item;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public final class AVEssenceValues {
	private AVEssenceValues() {}

	public static final int DEFAULT = 1;

	private static final int[][] CYPRESS_BLOCKS = {
		{90, 16}, {91, 8}, {92, 8}, {93, 8}, {94, 8}, {95, 8}, {96, 8}, {98, 8}, {99, 8}, {100, 8},
		{101, 8}, {102, 8}, {103, 8}, {104, 8}, {105, 8}, {106, 8}, {107, 8}, {108, 8}, {109, 16}, {110, 16},
		{111, 16}, {112, 16}, {115, 8}, {116, 6}, {117, 16}, {118, 24}, {119, 32}, {121, 16}, {122, 16}, {123, 16},
		{124, 16}, {125, 24}, {126, 24}, {127, 24}, {128, 24}, {129, 4}, {130, 8}, {131, 4}, {132, 16}, {133, 8},
		{134, 4}, {135, 11}, {137, 32}, {138, 24}, {139, 16}, {140, 48}, {141, 32}, {142, 32}, {143, 8}, {144, 8},
		{145, 14}, {149, 9}, {150, 32}, {152, 2}, {153, 32}, {154, 32}, {155, 8}, {156, 16}, {157, 16}, {158, 16},
		{159, 8}, {160, 2}, {161, 2}, {162, 4}, {163, 2}, {164, 8}, {166, 8}, {167, 8}, {168, 8}, {169, 8},
		{173, 5}, {174, 60}, {175, 60}, {176, 60}, {177, 60}, {178, 60}, {179, 60}, {180, 60}, {181, 60}, {182, 8},
		{183, 16}, {184, 16}, {185, 32}, {186, 32}, {187, 60}, {188, 5}, {189, 32767}, {190, 32}, {191, 32}, {192, 32},
		{195, 32}, {196, 32}, {197, 32}, {198, 72}, {199, 64}, {200, 32}, {201, 12}, {202, 12}, {203, 4}, {204, 8},
		{205, 8}, {206, 22}, {207, 22}, {208, 22}, {209, 22}, {210, 22}, {211, 22}, {212, 22}, {213, 22}, {214, 22},
		{215, 22}, {217, 4}, {218, 8}, {220, 8}, {221, 8}, {222, 16}, {223, 11}, {224, 4}, {225, 8}, {226, 11},
		{227, 11}, {228, 11}, {229, 24}, {230, 64}, {231, 64}, {232, 64}, {234, 8}, {235, 8}, {236, 8}, {237, 8},
		{238, 8}, {239, 8}, {240, 16}, {241, 32}, {242, 16}, {243, 64}, {244, 64}, {245, 182}, {246, 182}, {247, 182},
		{248, 182}, {249, 256}, {250, 256}, {251, 256}, {252, 256}, {253, 4}, {254, 8}, {255, 8},
	};

	private static final int[][] CYPRESS_ITEMS = {
		{90, 128}, {91, 128}, {92, 128}, {93, 128}, {94, 128}, {95, 128}, {96, 128}, {97, 128}, {98, 98}, {99, 98},
		{100, 8}, {101, 8}, {102, 8}, {103, 8}, {104, 16}, {106, 64}, {107, 32}, {108, 8}, {111, 22}, {113, 14},
		{114, 2}, {115, 77}, {116, 8}, {117, 8}, {118, 16}, {119, 16}, {120, 16}, {121, 16}, {122, 36}, {123, 8},
		{124, 16}, {125, 16}, {126, 16}, {127, 16}, {128, 8}, {129, 8}, {130, 128}, {131, 8}, {132, 32}, {133, 16},
		{134, 519}, {135, 815}, {136, 639}, {137, 372}, {138, 64}, {139, 128}, {140, 12}, {141, 7}, {142, 17}, {143, 8},
		{144, 64}, {145, 64}, {146, 64}, {147, 64}, {148, 17}, {2003, 128}, {2004, 128}, {2005, 128}, {2006, 8}, {2007, 128},
		{2008, 128}, {2009, 128},
	};

	private static volatile Map<Integer, Integer> table;

	public static int of(@Nullable ItemStack stack) {
		return stack == null ? 0 : of(stack.itemID);
	}

	public static int of(int itemId) {
		return table().getOrDefault(itemId, DEFAULT);
	}

	public static long total(@Nullable ItemStack stack) {
		return stack == null ? 0L : (long) of(stack.itemID) * stack.stackSize;
	}

	private static Map<Integer, Integer> table() {
		Map<Integer, Integer> built = table;
		if (built == null) {
			synchronized (AVEssenceValues.class) {
				if (table == null) {
					table = build();
				}
				built = table;
			}
		}
		return built;
	}

	private static Map<Integer, Integer> build() {
		Map<Integer, Integer> map = new HashMap<>();

		block(map, Blocks.STONE, 2);
		block(map, Blocks.PLANKS_OAK, 2);
		block(map, Blocks.SAPLING_OAK, 4);
		block(map, Blocks.SAPLING_OAK_RETRO, 4);
		block(map, Blocks.GRAVEL, 2);
		block(map, Blocks.ORE_GOLD_STONE, 32);
		block(map, Blocks.ORE_IRON_STONE, 16);
		block(map, Blocks.ORE_COAL_STONE, 8);
		block(map, Blocks.LOG_OAK, 4);
		block(map, Blocks.GLASS, 4);
		block(map, Blocks.WOOL, 16);
		block(map, Blocks.FLOWER_YELLOW, 4);
		block(map, Blocks.FLOWER_RED, 4);
		block(map, Blocks.MUSHROOM_BROWN, 10);
		block(map, Blocks.MUSHROOM_RED, 10);
		block(map, Blocks.BLOCK_GOLD, 432);

		block(map, Blocks.BLOCK_IRON, 180);

		block(map, Blocks.SLAB_STONE_POLISHED, 8);
		block(map, Blocks.BRICK_CLAY, 16);
		block(map, Blocks.TNT, 20);
		block(map, Blocks.BOOKSHELF_PLANKS_OAK, 8);
		block(map, Blocks.COBBLE_STONE_MOSSY, 16);
		block(map, Blocks.OBSIDIAN, 24);
		block(map, Blocks.TORCH_COAL, 2);
		block(map, Blocks.FIRE, 2);
		block(map, Blocks.STAIRS_PLANKS_OAK, 2);
		block(map, Blocks.CHEST_PLANKS_OAK, 4);
		block(map, Blocks.CHEST_LEGACY, 4);
		block(map, Blocks.ORE_DIAMOND_STONE, 28);
		block(map, Blocks.BLOCK_DIAMOND, 504);
		block(map, Blocks.WORKBENCH, 8);
		block(map, Blocks.CROPS_WHEAT, 8);
		block(map, Blocks.FARMLAND_DIRT, 9);
		block(map, Blocks.FURNACE_STONE_IDLE, 8);
		block(map, Blocks.FURNACE_STONE_ACTIVE, 8);
		block(map, Blocks.DOOR_PLANKS_OAK_BOTTOM, 4);
		block(map, Blocks.DOOR_PLANKS_OAK_TOP, 4);
		block(map, Blocks.RAIL, 11);
		block(map, Blocks.STAIRS_COBBLE_STONE, 12);
		block(map, Blocks.LEVER_COBBLE_STONE, 11);
		block(map, Blocks.PRESSURE_PLATE_STONE, 11);
		block(map, Blocks.DOOR_IRON_BOTTOM, 32);
		block(map, Blocks.DOOR_IRON_TOP, 32);
		block(map, Blocks.ORE_REDSTONE_STONE, 12);
		block(map, Blocks.ORE_REDSTONE_GLOWING_STONE, 12);
		block(map, Blocks.TORCH_REDSTONE_IDLE, 8);
		block(map, Blocks.TORCH_REDSTONE_ACTIVE, 8);

		block(map, AVBlocks.ORE_GREENSTONE, 12);
		block(map, AVBlocks.ORE_GREENSTONE_GLOWING, 12);
		block(map, AVBlocks.TORCH_GREENSTONE_IDLE, 8);
		block(map, AVBlocks.TORCH_GREENSTONE_ACTIVE, 8);
		block(map, Blocks.BUTTON_STONE, 2);
		block(map, Blocks.LAYER_SNOW, 4);
		block(map, Blocks.ICE, 4);
		block(map, Blocks.BLOCK_SNOW, 2);
		block(map, Blocks.BLOCK_CLAY, 8);
		block(map, Blocks.SUGARCANE, 4);
		block(map, Blocks.JUKEBOX, 32);

		item(map, Items.TOOL_SHOVEL_IRON, 16);
		item(map, Items.TOOL_PICKAXE_IRON, 16);
		item(map, Items.TOOL_AXE_IRON, 16);
		item(map, Items.TOOL_FIRESTRIKER_IRON, 8);
		item(map, Items.FOOD_APPLE, 2);
		item(map, Items.TOOL_BOW, 8);
		item(map, Items.AMMO_ARROW, 2);
		item(map, Items.COAL, 2);
		item(map, Items.DIAMOND, 32);
		item(map, Items.INGOT_IRON, 16);
		item(map, Items.INGOT_GOLD, 32);
		item(map, Items.TOOL_SWORD_IRON, 16);
		item(map, Items.TOOL_SWORD_WOOD, 8);
		item(map, Items.TOOL_SHOVEL_WOOD, 8);
		item(map, Items.TOOL_PICKAXE_WOOD, 8);
		item(map, Items.TOOL_AXE_WOOD, 8);
		item(map, Items.TOOL_SWORD_STONE, 10);
		item(map, Items.TOOL_SHOVEL_STONE, 10);
		item(map, Items.TOOL_PICKAXE_STONE, 10);
		item(map, Items.TOOL_AXE_STONE, 10);
		item(map, Items.TOOL_SWORD_DIAMOND, 32);
		item(map, Items.TOOL_SHOVEL_DIAMOND, 32);
		item(map, Items.TOOL_PICKAXE_DIAMOND, 32);
		item(map, Items.TOOL_AXE_DIAMOND, 32);
		item(map, Items.STICK, 2);
		item(map, Items.BOWL, 2);
		item(map, Items.FOOD_STEW_MUSHROOM, 12);
		item(map, Items.TOOL_SWORD_GOLD, 24);
		item(map, Items.TOOL_SHOVEL_GOLD, 24);
		item(map, Items.TOOL_PICKAXE_GOLD, 24);
		item(map, Items.TOOL_AXE_GOLD, 24);
		item(map, Items.STRING, 12);
		item(map, Items.FEATHER_CHICKEN, 4);
		item(map, Items.GUNPOWDER, 4);
		item(map, Items.TOOL_HOE_WOOD, 4);
		item(map, Items.TOOL_HOE_STONE, 8);
		item(map, Items.TOOL_HOE_IRON, 12);
		item(map, Items.TOOL_HOE_DIAMOND, 16);
		item(map, Items.TOOL_HOE_GOLD, 8);
		item(map, Items.WHEAT, 4);
		item(map, Items.FOOD_BREAD, 8);
		item(map, Items.ARMOR_HELMET_LEATHER, 4);
		item(map, Items.ARMOR_CHESTPLATE_LEATHER, 4);
		item(map, Items.ARMOR_LEGGINGS_LEATHER, 4);
		item(map, Items.ARMOR_BOOTS_LEATHER, 4);
		item(map, Items.ARMOR_HELMET_CHAINMAIL, 8);
		item(map, Items.ARMOR_CHESTPLATE_CHAINMAIL, 8);
		item(map, Items.ARMOR_LEGGINGS_CHAINMAIL, 8);
		item(map, Items.ARMOR_BOOTS_CHAINMAIL, 8);
		item(map, Items.ARMOR_HELMET_IRON, 16);
		item(map, Items.ARMOR_CHESTPLATE_IRON, 16);
		item(map, Items.ARMOR_LEGGINGS_IRON, 16);
		item(map, Items.ARMOR_BOOTS_IRON, 16);
		item(map, Items.ARMOR_HELMET_DIAMOND, 32);
		item(map, Items.ARMOR_CHESTPLATE_DIAMOND, 32);
		item(map, Items.ARMOR_LEGGINGS_DIAMOND, 32);
		item(map, Items.ARMOR_BOOTS_DIAMOND, 32);
		item(map, Items.ARMOR_HELMET_GOLD, 32);
		item(map, Items.ARMOR_CHESTPLATE_GOLD, 16);
		item(map, Items.ARMOR_LEGGINGS_GOLD, 16);
		item(map, Items.ARMOR_BOOTS_GOLD, 16);
		item(map, Items.FLINT, 12);
		item(map, Items.FOOD_PORKCHOP_RAW, 8);
		item(map, Items.FOOD_PORKCHOP_COOKED, 16);
		item(map, Items.PAINTING, 4);
		item(map, Items.FOOD_APPLE_GOLD, 90);
		item(map, Items.DOOR_OAK, 4);
		item(map, Items.BUCKET_IRON, 16);
		item(map, Items.MINECART, 32);
		item(map, Items.SADDLE, 16);
		item(map, Items.DOOR_IRON, 16);
		item(map, Items.DUST_REDSTONE, 3);
		item(map, AVItems.GREENSTONE, 3);
		item(map, Items.BOAT, 8);
		item(map, Items.LEATHER, 2);
		item(map, Items.BRICK_CLAY, 4);
		item(map, Items.CLAY, 4);
		item(map, Items.SUGARCANE, 4);
		item(map, Items.PAPER, 5);
		item(map, Items.BOOK, 16);
		item(map, Items.SLIMEBALL, 10);
		item(map, Items.MINECART_CHEST, 16);
		item(map, Items.MINECART_FURNACE, 16);
		item(map, Items.EGG_CHICKEN, 2);
		item(map, Items.TOOL_COMPASS, 16);
		item(map, Items.RECORD_13, 128);
		item(map, Items.RECORD_CAT, 128);

		for (int[] row : CYPRESS_BLOCKS) {
			map.put(AVBlocks.BASE_ID + row[0], row[1]);
		}
		for (int[] row : CYPRESS_ITEMS) {
			map.put(AVItems.BASE_ID + row[0], row[1]);
		}
		return map;
	}

	private static void block(Map<Integer, Integer> map, @Nullable Block<?> block, int value) {
		if (block != null) {
			map.put(block.id(), value);
		}
	}

	private static void item(Map<Integer, Integer> map, @Nullable Item item, int value) {
		if (item != null) {
			map.put(item.id, value);
		}
	}
}
