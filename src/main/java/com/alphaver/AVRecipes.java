package com.alphaver;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVItems;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.crafting.LookupFuelFurnace;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.ItemBucket;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import turniplabs.halplibe.helper.RecipeBuilder;

public final class AVRecipes {
	private AVRecipes() {}

	private static final String[] GRID_2X2 = {"##", "##"};
	private static final String[] GRID_3X3 = {"###", "###", "###"};
	private static final String[] PLUS = {" # ", "#1#", " # "};
	private static final String[] PILLAR = {"#X#", "X X", "#X#"};

	private static int skipped;

	public static void initNamespace() {
		if (Registries.RECIPES == null) {
			AlphaVer.LOGGER.error("Recipe namespace init ran with no recipe registry; AlphaVer recipes will not survive a "
				+ "multiplayer login.");
			return;
		}
		RecipeBuilder.initNameSpace(AlphaVer.MOD_ID);
	}

	public static void register() {
		RecipeBuilder.initNameSpace(AlphaVer.MOD_ID);
		skipped = 0;

		materials();
		buildingBlocks();
		armour();
		toolsAndDoors();
		machinesAndFlames();
		greenstone();
		dyes();
		smelting();
		fuels();

		if (skipped > 0) {
			AlphaVer.LOGGER.warn("Skipped {} AlphaVer recipe(s) whose ingredients or result failed to register.", skipped);
		}

		Registries.RECIPES.invalidateCaches();
		AlphaVer.LOGGER.info("Registered AlphaVer recipes.");
	}

	private static void materials() {
		shapeless("salt_block_to_granular_salt", stack(AVItems.GRANULAR_SALT, 4), AVBlocks.SALT_BLOCK);
		shapeless("frigid_trunk_to_frigid_bits", stack(AVItems.FRIGID_BITS, 8), AVBlocks.FRIGID_TRUNK);
		shapeless("essence_cache_to_essence", stack(AVItems.ESSENCE, 9), AVBlocks.ESSENCE_CACHE);
		shaped("essence_to_essence_cache", stack(AVBlocks.ESSENCE_CACHE, 1), GRID_3X3, '#', AVItems.ESSENCE);
		shapeless("celestial_flame_to_liquified_flame", stack(AVItems.LIQUIFIED_FLAME, 1), AVBlocks.CELESTIAL_FLAME);
		shaped("mycon_plank_to_mycon_strand", stack(AVItems.MYCON_STRAND, 4), new String[]{"#", "#"}, '#', AVBlocks.PLANKS_MYCON);

		shapeless("flamewood_to_planks", stack(AVBlocks.PLANKS_FLAMEWOOD, 4), AVBlocks.LOG_FLAMEWOOD);

		shapeless("highwood_log_to_planks", stack(AVBlocks.PLANKS_HIGHWOOD, 3), AVBlocks.LOG_HIGHWOOD);
		shapeless("tea_wood_to_planks", stack(AVBlocks.PLANKS_TEA, 4), AVBlocks.LOG_TEA);
		shapeless("mycon_stem_to_planks", stack(AVBlocks.PLANKS_MYCON, 4), AVBlocks.MYCON_STEM);

		shaped("sticks_from_flamewood_planks", stack(Items.STICK, 4), new String[]{"#", "#"}, '#', AVBlocks.PLANKS_FLAMEWOOD);
		shaped("sticks_from_highwood_planks", stack(Items.STICK, 4), new String[]{"#", "#"}, '#', AVBlocks.PLANKS_HIGHWOOD);
		shaped("sticks_from_tea_planks", stack(Items.STICK, 4), new String[]{"#", "#"}, '#', AVBlocks.PLANKS_TEA);

		gemBlock("lace_agate", AVItems.LACE_AGATE, AVBlocks.BLOCK_LACE_AGATE);
		gemBlock("clinohumite", AVItems.CLINOHUMITE, AVBlocks.BLOCK_CLINOHUMITE);
		gemBlock("malachite", AVItems.MALACHITE, AVBlocks.BLOCK_MALACHITE);
		gemBlock("pyrite", AVItems.PYRITE, AVBlocks.BLOCK_PYRITE);

		shaped("the_one_true_book", stack(AVItems.THE_ONE_TRUE_BOOK, 1), new String[]{"12", "34"},
			'1', AVItems.HOURS_LONG_PAST_1, '2', AVItems.HOURS_LONG_PAST_2,
			'3', AVItems.HOURS_LONG_PAST_3, '4', AVItems.HOURS_LONG_PAST_4);
	}

	private static void gemBlock(String name, IItemConvertible gem, IItemConvertible block) {
		shaped(name + "_block", stack(block, 1), GRID_3X3, '#', gem);
		shapeless(name + "_block_to_" + name, stack(gem, 9), block);
	}

	private static void buildingBlocks() {
		shaped("fortified_glass_from_cobblestone", stack(AVBlocks.GLASS_FORTIFIED, 2), new String[]{" # ", "#X#", " # "},
			'#', Blocks.COBBLE_STONE, 'X', Blocks.GLASS);
		shaped("fortified_glass", stack(AVBlocks.GLASS_FORTIFIED, 8), new String[]{"727", "222", "727"},
			'7', Blocks.GLASS, '2', Blocks.OBSIDIAN);
		fortifiedColour("magenta", AVBlocks.GLASS_FORTIFIED_MAGENTA, AVBlocks.CLOTH_MAGENTA);
		fortifiedColour("blue", AVBlocks.GLASS_FORTIFIED_BLUE, AVBlocks.CLOTH_BLUE);
		fortifiedColour("green", AVBlocks.GLASS_FORTIFIED_GREEN, AVBlocks.CLOTH_GREEN);
		fortifiedColour("black", AVBlocks.GLASS_FORTIFIED_BLACK, AVBlocks.CLOTH_BLACK);

		shaped("stone_tile", stack(AVBlocks.STONE_TILE, 4), new String[]{"727", "272", "727"},
			'7', Blocks.BRICK_CLAY, '2', Blocks.COBBLE_STONE);
		shaped("tile", stack(AVBlocks.TILE, 4), new String[]{"7 7", " 7 ", "7 7"}, '7', Blocks.BRICK_CLAY);

		ItemStack whiteWool = new ItemStack(Blocks.WOOL, 1, 0);
		shapeless("cloth_magenta", stack(AVBlocks.CLOTH_MAGENTA, 1), whiteWool, AVItems.DYE_PINK);
		shapeless("cloth_blue", stack(AVBlocks.CLOTH_BLUE, 1), whiteWool, AVItems.DYE_BLUE);
		shapeless("cloth_green", stack(AVBlocks.CLOTH_GREEN, 1), whiteWool, AVItems.DYE_GREEN);
		shapeless("cloth_black", stack(AVBlocks.CLOTH_BLACK, 1), whiteWool, AVItems.DYE_BLACK);

		shaped("empty_bookshelf", stack(AVBlocks.BOOKSHELF_EMPTY, 1), new String[]{"#i#", " i ", "#i#"},
			'#', Blocks.PLANKS_OAK, 'i', Items.STICK);
		shaped("bookshelf", stack(AVBlocks.BOOKSHELF, 1), new String[]{"###", " i ", "###"},
			'#', Items.BOOK, 'i', AVBlocks.BOOKSHELF_EMPTY);

		shaped("lichen_mass", stack(AVBlocks.LICHEN_MASS, 4), GRID_2X2, '#', AVBlocks.LICHEN);
		shaped("lichen_bricks", stack(AVBlocks.BRICK_LICHEN, 4), PLUS, '#', AVBlocks.LICHEN, '1', Blocks.BRICK_CLAY);
		shaped("lichen_cobblestone", stack(Blocks.COBBLE_STONE_MOSSY, 4), PLUS, '#', AVBlocks.LICHEN, '1', Blocks.COBBLE_STONE);

		shaped("wooden_pressure_plate_from_logs", stack(Blocks.PRESSURE_PLATE_PLANKS_OAK, 1), new String[]{"###"}, '#', Blocks.LOG_OAK);

		shaped("low_flame_in_glass", stack(AVBlocks.FLAME_GLASS_LOW, 8), new String[]{"###", "#1#", "###"},
			'#', Blocks.GLASS, '1', AVBlocks.LOW_LILY);

		shaped("salt_bricks", stack(AVBlocks.BRICK_SALT, 4), GRID_2X2, '#', AVItems.GRANULAR_SALT);
		shaped("iron_brick", stack(AVBlocks.BRICK_IRON, 4), PLUS, '#', Items.INGOT_IRON, '1', Blocks.BRICK_CLAY);
		shaped("gold_brick", stack(AVBlocks.BRICK_GOLD, 4), PLUS, '#', Items.INGOT_GOLD, '1', Blocks.BRICK_CLAY);
		shaped("diamond_brick", stack(AVBlocks.BRICK_DIAMOND, 4), PLUS, '#', Items.DIAMOND, '1', Blocks.BRICK_CLAY);
		shaped("obsidian_brick", stack(AVBlocks.BRICK_OBSIDIAN, 4), PLUS, '#', AVItems.OBSIDIAN_INGOT, '1', Blocks.BRICK_CLAY);
		shaped("bismuth_brick", stack(AVBlocks.BRICK_BISMUTH, 4), PLUS, '#', AVItems.BISMUTH_INGOT, '1', Blocks.BRICK_CLAY);
		shaped("bismuth_block", stack(AVBlocks.BLOCK_BISMUTH, 4), GRID_2X2, '#', AVItems.BISMUTH_INGOT);
		shaped("bismuth_pillar", stack(AVBlocks.PILLAR_BISMUTH, 4), new String[]{"#1#", "1 1", "#1#"},
			'#', AVBlocks.BLOCK_BISMUTH, '1', AVItems.BISMUTH_INGOT);

		shaped("pillar", stack(AVBlocks.PILLAR, 4), PILLAR, '#', Blocks.PLANKS_OAK, 'X', Items.STICK);
		shaped("flamewood_pillar", stack(AVBlocks.PILLAR_FLAMEWOOD, 4), PILLAR, '#', AVBlocks.PLANKS_FLAMEWOOD, 'X', Items.STICK);
		shaped("highwood_pillar", stack(AVBlocks.PILLAR_HIGHWOOD, 4), PILLAR, '#', AVBlocks.PLANKS_HIGHWOOD, 'X', Items.STICK);
		shaped("tea_pillar", stack(AVBlocks.PILLAR_TEA, 4), PILLAR, '#', AVBlocks.PLANKS_TEA, 'X', Items.STICK);
		shaped("mycon_pillar", stack(AVBlocks.PILLAR_MYCON, 4), PILLAR, '#', AVBlocks.PLANKS_MYCON, 'X', AVItems.MYCON_STRAND);

		shaped("smooth_limestone", stack(AVBlocks.LIMESTONE_SMOOTH, 4), GRID_2X2, '#', AVBlocks.LIMESTONE);

		shapedInCypress("smooth_stone", stack(AVBlocks.SMOOTH_STONE, 4), GRID_2X2, '#', Blocks.STONE);
		shapedInCypress("wood_slab", stack(Blocks.SLAB_PLANKS_OAK, 3), new String[]{"###"}, '#', Blocks.PLANKS_OAK);

		shaped("cloth_panel", stack(AVBlocks.CLOTH_PANEL, 1), new String[]{"###", "XXX", "###"},
			'#', Blocks.PLANKS_OAK, 'X', whiteWool);

		shaped("dimension_floor", stack(AVBlocks.DIMENSION_FLOOR, 4), new String[]{"72", "27"},
			'7', AVBlocks.DIMENSION_TILE_BLUE, '2', AVBlocks.DIMENSION_TILE_YELLOW);
	}

	private static void fortifiedColour(String colour, IItemConvertible result, IItemConvertible cloth) {
		shaped("fortified_glass_" + colour, stack(result, 8), new String[]{"727", "262", "727"},
			'7', Blocks.GLASS, '2', Blocks.OBSIDIAN, '6', cloth);
	}

	private static void machinesAndFlames() {
		String[] ring = {"###", "#1#", "###"};
		shaped("freezer", stack(AVBlocks.FREEZER, 1), ring, '#', Blocks.COBBLE_STONE, '1', Blocks.BLOCK_SNOW);
		shaped("essence_transformer", stack(AVBlocks.ESSENCE_TRANSFORMER, 1), ring, '#', Blocks.COBBLE_STONE, '1', AVItems.GREENSTONE);
		shaped("essence_cloner", stack(AVBlocks.ESSENCE_CLONER, 1), new String[]{"777", "7X7", "727"},
			'7', Blocks.COBBLE_STONE, 'X', AVItems.GREENSTONE, '2', AVBlocks.ESSENCE_CACHE);
		shaped("hearthen_mirror", stack(AVItems.HEARTHEN_MIRROR, 1), PLUS, '#', Items.INGOT_GOLD, '1', AVBlocks.CELESTIAL_FLAME);
		shaped("essence_fountain", stack(AVBlocks.ESSENCE_FOUNTAIN, 1), new String[]{"#X#", "###"},
			'#', Blocks.COBBLE_STONE, 'X', AVItems.ESSENCE);
		shaped("essence_rifle", stack(AVItems.ESSENCE_RIFLE, 1), new String[]{"#  ", "SEX", " #7"},
			'#', Items.INGOT_IRON, 'S', AVItems.GREENSTONE, 'E', Items.STICK, 'X', AVItems.ESSENCE, '7', Blocks.PLANKS_OAK);
		shaped("sky_flame_in_glass", stack(AVBlocks.FLAME_GLASS_SKY, 8), ring, '#', Blocks.GLASS, '1', AVBlocks.LILY_FLAME);
		shaped("gold_flame_in_glass", stack(AVBlocks.FLAME_GLASS_GOLD, 8), ring, '#', Blocks.GLASS, '1', AVBlocks.LILY_GOLD);
		shaped("obsidian_flame_in_glass", stack(AVBlocks.FLAME_GLASS_OBSIDIAN, 8), ring, '#', Blocks.GLASS, '1', AVBlocks.LILY_OBSIDIAN);
	}

	private static void greenstone() {
		shaped("greenstone_torch", stack(AVBlocks.TORCH_GREENSTONE_ACTIVE, 1), new String[]{"X", "#"},
			'X', AVItems.GREENSTONE, '#', Items.STICK);
		shaped("compass_from_greenstone", stack(Items.TOOL_COMPASS, 1), PLUS, '#', Items.INGOT_IRON, '1', AVItems.GREENSTONE);
	}

	private static void dyes() {
		smelt("hydrangea_to_pink_dye", AVBlocks.HYDRANGEA, stack(AVItems.DYE_PINK, 1));
		AVDimensionRecipes.blockInside("minecraft", "flower_red_to_dye");
		AVDimensionRecipes.blockInside("minecraft", "flower_yellow_to_dye");
	}

	private static void armour() {
		shaped("ragged_helm", stack(Items.ARMOR_HELMET_CHAINMAIL, 1), new String[]{"XXX", "X X"}, 'X', AVItems.OBSERVER_FUR);
		shaped("ragged_suit", stack(Items.ARMOR_CHESTPLATE_CHAINMAIL, 1), new String[]{"X X", "XXX", "XXX"}, 'X', AVItems.OBSERVER_FUR);
		shaped("ragged_leggings", stack(Items.ARMOR_LEGGINGS_CHAINMAIL, 1), new String[]{"XXX", "X X", "X X"}, 'X', AVItems.OBSERVER_FUR);
		shaped("ragged_boots", stack(Items.ARMOR_BOOTS_CHAINMAIL, 1), new String[]{"X X", "X X"}, 'X', AVItems.OBSERVER_FUR);
	}

	private static void toolsAndDoors() {
		toolSet("obsidian", AVItems.OBSIDIAN_INGOT, Items.STICK, AVItems.OBSIDIAN_SWORD, AVItems.OBSIDIAN_SHOVEL,
			AVItems.OBSIDIAN_PICKAXE, AVItems.OBSIDIAN_AXE, AVItems.OBSIDIAN_HOE);
		toolSet("mycon", AVBlocks.PLANKS_MYCON, AVItems.MYCON_STRAND, AVItems.MYCON_SWORD, AVItems.MYCON_SHOVEL,
			AVItems.MYCON_PICKAXE, AVItems.MYCON_AXE, AVItems.MYCON_HOE);

		shaped("obsidian_helm", stack(AVItems.OBSIDIAN_HELM, 1), new String[]{"###", "# #"}, '#', AVItems.OBSIDIAN_INGOT);
		shaped("obsidian_chestplate", stack(AVItems.OBSIDIAN_CHESTPLATE, 1), new String[]{"# #", "###", "###"}, '#', AVItems.OBSIDIAN_INGOT);
		shaped("obsidian_leggings", stack(AVItems.OBSIDIAN_LEGGINGS, 1), new String[]{"###", "# #", "# #"}, '#', AVItems.OBSIDIAN_INGOT);
		shaped("obsidian_boots", stack(AVItems.OBSIDIAN_BOOTS, 1), new String[]{"# #", "# #"}, '#', AVItems.OBSIDIAN_INGOT);

		shaped("spear", stack(AVItems.SPEAR, 1), new String[]{"#", "#", "#"}, '#', Items.INGOT_IRON);

		String[] door = {"##", "##", "##"};
		shaped("door_flamewood", stack(AVItems.DOOR_FLAMEWOOD, 1), door, '#', AVBlocks.PLANKS_FLAMEWOOD);
		shaped("door_highwood", stack(AVItems.DOOR_HIGHWOOD, 1), door, '#', AVBlocks.PLANKS_HIGHWOOD);
		shaped("door_mycon", stack(AVItems.DOOR_MYCON, 1), door, '#', AVBlocks.PLANKS_MYCON);
		shaped("door_ice", stack(AVItems.DOOR_ICE, 1), door, '#', Blocks.ICE);

		shaped("workbench_flamewood", stack(AVBlocks.WORKBENCH_FLAMEWOOD, 1), GRID_2X2, '#', AVBlocks.PLANKS_FLAMEWOOD);
		shaped("workbench_highwood", stack(AVBlocks.WORKBENCH_HIGHWOOD, 1), GRID_2X2, '#', AVBlocks.PLANKS_HIGHWOOD);
		shaped("workbench_mycon", stack(AVBlocks.WORKBENCH_MYCON, 1), GRID_2X2, '#', AVBlocks.PLANKS_MYCON);

		ItemStack waterBucket = ItemBucket.createRecipeInput(Items.BUCKET_IRON, ItemBucket.STATE_WATER);

		if (AVItems.TEA_BUCKET == null || AVItems.TEA_LEAF == null) {
			skipped++;
		} else {
			RecipeBuilder.Shaped(AlphaVer.MOD_ID).setShape(PLUS)
				.addInput('#', AVItems.TEA_LEAF)
				.addInput('1', waterBucket)
				.setConsumeContainer(true)
				.create("tea_bucket", stack(AVItems.TEA_BUCKET, 1));
		}
	}

	private static void toolSet(String material, IItemConvertible head, IItemConvertible handle, IItemConvertible sword,
	                            IItemConvertible shovel, IItemConvertible pickaxe, IItemConvertible axe, IItemConvertible hoe) {
		shaped(material + "_sword", stack(sword, 1), new String[]{"#", "#", "$"}, '#', head, '$', handle);
		shaped(material + "_shovel", stack(shovel, 1), new String[]{"#", "$", "$"}, '#', head, '$', handle);
		shaped(material + "_pickaxe", stack(pickaxe, 1), new String[]{"###", " $ ", " $ "}, '#', head, '$', handle);
		shaped(material + "_axe", stack(axe, 1), new String[]{"##", "#$", " $"}, '#', head, '$', handle);
		shaped(material + "_hoe", stack(hoe, 1), new String[]{"##", " $", " $"}, '#', head, '$', handle);
	}

	private static void smelting() {
		smelt("bismuth_ore_to_bismuth_ingot", AVBlocks.ORE_BISMUTH, stack(AVItems.BISMUTH_INGOT, 1));
		smelt("obsidian_to_obsidian_ingot", Blocks.OBSIDIAN, stack(AVItems.OBSIDIAN_INGOT, 1));
		smelt("coal_to_black_dye", Items.COAL, stack(AVItems.DYE_BLACK, 1));
		smelt("greenstone_to_green_dye", AVItems.GREENSTONE, stack(AVItems.DYE_GREEN, 1));
		smelt("rose_to_blue_dye", Blocks.FLOWER_RED, stack(AVItems.DYE_BLUE, 1));
		smelt("brown_mushroom_to_pink_dye", Blocks.MUSHROOM_BROWN, stack(AVItems.DYE_PINK, 1));
		smelt("red_mushroom_to_fryshroom", Blocks.MUSHROOM_RED, stack(AVItems.FRYSHROOM, 1));
		smelt("low_river_gold_ore_to_gold", AVBlocks.LOW_ORE_GOLD, stack(Items.INGOT_GOLD, 1));
		smelt("low_river_iron_ore_to_iron", AVBlocks.LOW_ORE_IRON, stack(Items.INGOT_IRON, 1));
		smelt("cobbled_limestone_to_limestone", AVBlocks.COBBLED_LIMESTONE, stack(AVBlocks.LIMESTONE, 1));
	}

	private static void fuels() {
		fuel(AVItems.ESSENCE, 50);
		fuel(AVBlocks.ESSENCE_CACHE, 450);
		IItemConvertible[] wooden = {AVBlocks.PILLAR, AVBlocks.LOG_FLAMEWOOD, AVBlocks.PLANKS_FLAMEWOOD, AVBlocks.LOG_HIGHWOOD,
			AVBlocks.ROOTS_HIGHWOOD, AVBlocks.PLANKS_HIGHWOOD, AVBlocks.MYCON_STEM, AVBlocks.PLANKS_MYCON, AVBlocks.LOG_TEA,
			AVBlocks.PLANKS_TEA, AVBlocks.BOOKSHELF, AVBlocks.BOOKSHELF_EMPTY, AVBlocks.FRIGID_TRUNK, AVBlocks.CLOTH_PANEL};
		for (IItemConvertible wood : wooden) {
			fuel(wood, 300);
		}
	}

	private static void fuel(IItemConvertible thing, int ticks) {
		if (thing == null) {
			skipped++;
			return;
		}
		LookupFuelFurnace.instance.addFuelEntry(thing.asItem().id, ticks);
	}

	private static ItemStack stack(IItemConvertible item, int count) {
		return item == null ? null : new ItemStack(item, count);
	}

	private static boolean shaped(String name, ItemStack result, String[] shape, Object... keys) {
		if (result == null || hasNull(keys)) {
			skipped++;
			return false;
		}
		var builder = RecipeBuilder.Shaped(AlphaVer.MOD_ID).setShape(shape);
		for (int i = 0; i + 1 < keys.length; i += 2) {
			char symbol = (Character) keys[i];
			builder = keys[i + 1] instanceof ItemStack stack
				? builder.addInput(symbol, stack)
				: builder.addInput(symbol, (IItemConvertible) keys[i + 1]);
		}
		builder.create(name, result);
		return true;
	}

	private static void shapedInCypress(String name, ItemStack result, String[] shape, Object... keys) {
		if (shaped(name, result, shape, keys)) {
			AVDimensionRecipes.add(name);
		}
	}

	private static boolean shapeless(String name, ItemStack result, Object... inputs) {
		if (result == null || hasNull(inputs)) {
			skipped++;
			return false;
		}
		var builder = RecipeBuilder.Shapeless(AlphaVer.MOD_ID);
		for (Object input : inputs) {
			builder = input instanceof ItemStack stack ? builder.addInput(stack) : builder.addInput((IItemConvertible) input);
		}
		builder.create(name, result);
		return true;
	}

	private static void smelt(String name, IItemConvertible input, ItemStack result) {
		if (input == null || result == null) {
			skipped++;
			return;
		}
		RecipeBuilder.Furnace(AlphaVer.MOD_ID).setInput(input).create(name, result);
	}

	private static boolean hasNull(Object[] values) {
		for (Object value : values) {
			if (value == null) {
				return true;
			}
		}
		return false;
	}
}
