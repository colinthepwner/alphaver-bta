package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.block.AVBlocks;
import com.alphaver.mixin.client.DispatcherAccessor;
import net.minecraft.core.block.Blocks;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelEmpty;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.block.model.BlockModelTransparent;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.block.model.generic.BlockModelGenericDoor;
import net.minecraft.client.render.block.model.generic.BlockModelGenericFurnace;
import net.minecraft.client.render.block.model.generic.BlockModelGenericTorch;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.client.render.block.model.generic.BlockModelGenericLeaves;
import net.minecraft.core.block.Block;

@Environment(EnvType.CLIENT)
public final class AVBlockModels {
	private AVBlockModels() {}

	private static final String V_STONE = "minecraft:block/stone";
	private static final String V_COBBLE = "minecraft:block/cobbled_stone";
	private static final String V_LIMESTONE = "minecraft:block/limestone";
	private static final String V_COBBLED_LIMESTONE = "minecraft:block/cobbled_limestone";
	private static final String V_DIRT = "minecraft:block/dirt";
	private static final String V_GLASS = "minecraft:block/glass";
	private static final String V_GLASS_TINTED = "minecraft:block/glass_tinted";
	private static final String V_PLANKS = "minecraft:block/planks/oak";
	private static final String V_WORKBENCH_TOP = "minecraft:block/workbench/top";
	private static final String V_WORKBENCH_SIDE = "minecraft:block/workbench/side";
	private static final String V_ICE = "minecraft:block/ice";
	private static final String V_GLOWSTONE = "minecraft:block/glowstone";
	private static final String V_OBSIDIAN = "minecraft:block/obsidian";
	private static final String V_LOG_SIDE = "minecraft:block/log/oak_side";
	private static final String V_LOG_TOP = "minecraft:block/log/oak_top";
	private static final String V_GRASS_TOP = "minecraft:block/grass_retro/top";
	private static final String V_GRASS_SIDE = "minecraft:block/grass_retro/side";
	private static final String V_SLATE_BRICK = "minecraft:block/brick_slate";
	private static final String V_SLATE_TOP = "minecraft:block/slate_top";
	private static final String V_SLATE_SIDE = "minecraft:block/slate_side";
	private static final String V_POLISHED_TOP = "minecraft:block/polished_stone_top";
	private static final String V_POLISHED_SIDE = "minecraft:block/polished_stone_side";
	private static final String V_ORE_GOLD = "minecraft:block/ore/gold/stone_retro";
	private static final String V_ORE_IRON = "minecraft:block/ore/iron/stone_retro";
	private static final String V_ORE_COAL = "minecraft:block/ore/coal/stone_retro";
	private static final String V_ORE_DIAMOND = "minecraft:block/ore/diamond/stone_retro";
	private static final String V_ORE_REDSTONE = "minecraft:block/ore/redstone/stone_retro";
	private static final String V_ORE_LAPIS = "minecraft:block/ore/lapis/stone";
	private static final String V_DOOR_BOTTOM = "minecraft:block/door/planks/bottom";
	private static final String V_DOOR_TOP = "minecraft:block/door/planks/top";

	private static final String M_SAPLING = "minecraft:block/sapling/oak";
	private static final String M_TORCH = "minecraft:block/torch_coal";
	private static final String M_LEAVES_RETRO = "minecraft:block/leaves/oak_retro";

	public static void register(BlockModelDispatcher dispatcher) {

		dispatcher.addDispatch(column(AVBlocks.SALT_BLOCK, "salt_block_top", "salt_block_side", "salt_block_top",
			V_POLISHED_TOP, V_POLISHED_SIDE));
		dispatcher.addDispatch(cross(AVBlocks.WATER_LILY, "water_lily"));
		dispatcher.addDispatch(torch(AVBlocks.CELESTIAL_FLAME, "celestial_flame"));
		dispatcher.addDispatch(all(AVBlocks.LIMESTONE, "limestone", V_LIMESTONE));
		dispatcher.addDispatch(all(AVBlocks.COBBLED_LIMESTONE, "cobbled_limestone", V_COBBLED_LIMESTONE));
		dispatcher.addDispatch(biomeCross(AVBlocks.HYDRANGEA, "hydrangea"));
		dispatcher.addDispatch(biomeCross(AVBlocks.TALLGRASS, "tallgrass"));

		dispatcher.addDispatch(all(AVBlocks.LOG_FLAMEWOOD, "flamewood_log", V_LOG_SIDE));
		dispatcher.addDispatch(leaves(AVBlocks.LEAVES_FLAMEWOOD, "flamewood_leaves"));
		dispatcher.addDispatch(column(AVBlocks.LOG_TEA, "tea_wood_top", "tea_wood_side", "tea_wood_top", V_LOG_TOP, V_LOG_SIDE));
		dispatcher.addDispatch(leaves(AVBlocks.LEAVES_TEA, "tea_bush"));
		dispatcher.addDispatch(column(AVBlocks.LOG_HIGHWOOD, "highwood_log_top", "highwood_log_side", "highwood_log_top",
			V_LOG_TOP, V_LOG_SIDE));
		dispatcher.addDispatch(transparent(AVBlocks.LEAVES_HIGHWOOD, true, "highwood_leaves", V_ICE));
		dispatcher.addDispatch(all(AVBlocks.ROOTS_HIGHWOOD, "highwood_roots", V_LOG_SIDE));
		dispatcher.addDispatch(cross(AVBlocks.FRIGID_TRUNK, "frigid_trunk"));
		dispatcher.addDispatch(transparent(AVBlocks.FRIGID_LEAVES, false, "frigid_leaves", V_ICE));

		dispatcher.addDispatch(cross(AVBlocks.LOW_LILY, "low_lily"));
		dispatcher.addDispatch(cross(AVBlocks.LOW_VINE, "low_vine"));
		dispatcher.addDispatch(all(AVBlocks.LOW_RIVERBED, "low_riverbed", V_COBBLE));
		dispatcher.addDispatch(all(AVBlocks.LOW_WART, "low_wart", V_GLOWSTONE));
		dispatcher.addDispatch(all(AVBlocks.LOW_RIVER_STONE, "low_river_stone", V_STONE));
		dispatcher.addDispatch(all(AVBlocks.LOW_ORE_GOLD, "low_river_gold_ore", V_ORE_GOLD));
		dispatcher.addDispatch(all(AVBlocks.LOW_ORE_IRON, "low_river_iron_ore", V_ORE_IRON));
		dispatcher.addDispatch(all(AVBlocks.LOW_ORE_COAL, "low_river_coal_ore", V_ORE_COAL));
		dispatcher.addDispatch(all(AVBlocks.LOW_ORE_DIAMOND, "low_river_diamond_ore", V_ORE_DIAMOND));
		dispatcher.addDispatch(all(AVBlocks.LOW_ORE_GREENSTONE, "low_river_greenstone_ore", V_ORE_REDSTONE));
		dispatcher.addDispatch(cross(AVBlocks.LICHEN, "lichen"));

		dispatcher.addDispatch(column(AVBlocks.LOW_MYCON, "low_mycon_top", "low_mycon_side", "low_river_stone",
			V_GRASS_TOP, V_GRASS_SIDE));
		dispatcher.addDispatch(column(AVBlocks.MYCON_STEM, "mycon_stem_top", "mycon_stem_side", "mycon_stem_top",
			V_LOG_TOP, V_LOG_SIDE));
		dispatcher.addDispatch(transparent(AVBlocks.MYCON_CAP, false, "mycon_cap", V_ICE));
		dispatcher.addDispatch(transparent(AVBlocks.MYCON_CAP_GLOWING, false, "glowing_mycon_cap", V_GLOWSTONE));

		dispatcher.addDispatch(all(AVBlocks.ORE_BISMUTH, "bismuth_ore", V_ORE_IRON));
		dispatcher.addDispatch(all(AVBlocks.ORE_LACE_AGATE, "lace_agate_ore", V_ORE_LAPIS));
		dispatcher.addDispatch(all(AVBlocks.ORE_CLINOHUMITE, "clinohumite_ore", V_ORE_LAPIS));
		dispatcher.addDispatch(all(AVBlocks.ORE_MALACHITE, "malachite_ore", V_ORE_LAPIS));
		dispatcher.addDispatch(all(AVBlocks.ORE_PYRITE, "pyrite_ore", V_ORE_GOLD));

		dispatcher.addDispatch(new BlockModelGreenstoneWire<>(AVBlocks.GREENSTONE_WIRE));
		dispatcher.addDispatch(all(AVBlocks.ORE_GREENSTONE, "greenstone_ore", V_ORE_REDSTONE));
		dispatcher.addDispatch(all(AVBlocks.ORE_GREENSTONE_GLOWING, "greenstone_ore_glowing", "minecraft:block/ore/redstone/stone_retro_active"));
		dispatcher.addDispatch(greenstoneTorch(AVBlocks.TORCH_GREENSTONE_IDLE, "greenstone_torch_idle", "minecraft:block/torch_redstone_idle"));
		dispatcher.addDispatch(greenstoneTorch(AVBlocks.TORCH_GREENSTONE_ACTIVE, "greenstone_torch_active", "minecraft:block/torch_redstone_active"));

		dispatcher.addDispatch(all(AVBlocks.SMOOTH_STONE, "smooth_stone", V_POLISHED_TOP));
		dispatcher.addDispatch(all(AVBlocks.CRUDE_PILLAR, "crude_pillar", V_COBBLE));
		dispatcher.addDispatch(all(AVBlocks.PILLAR_FLAMEWOOD, "flamewood_pillar", V_LOG_SIDE));
		dispatcher.addDispatch(all(AVBlocks.BRICK_SLATE, "slate_bricks", V_SLATE_BRICK));
		dispatcher.addDispatch(column(AVBlocks.PILLAR_SLATE, "slate_pillar_top", "slate_pillar_side", "slate_pillar_top",
			V_SLATE_TOP, V_SLATE_SIDE));
		dispatcher.addDispatch(transparent(AVBlocks.WIREFRAME, false, "wireframe_block", V_GLASS));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_BLUE, false, "glass_blue", V_GLASS));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_GREEN, false, "glass_green", V_GLASS));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_BLACK, false, "glass_black", V_OBSIDIAN));
		dispatcher.addDispatch(column(AVBlocks.GRASS_PATHWAY, "grass_pathway_top", "grass_pathway_side", "grass_pathway_bottom",
			V_GRASS_TOP, V_GRASS_SIDE));

		dispatcher.addDispatch(transparent(AVBlocks.PLATE_SOLAR, true, "solar_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_DENIAL, true, "denial_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_SWITCH, true, "switch_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_LOOP, true, "loop_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_PART, true, "part_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_TRINITY, true, "trinity_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_ASSOCIATION, true, "association_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_DIALECT, true, "dialect_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_SYLLABLES, true, "syllables_plate", V_GLOWSTONE));
		dispatcher.addDispatch(transparent(AVBlocks.PLATE_MIRRORS, true, "mirrors_plate", V_GLOWSTONE));

		dispatcher.addDispatch(all(AVBlocks.PILLAR, "pillar", V_LOG_SIDE));
		dispatcher.addDispatch(all(AVBlocks.DIMENSION_FLOOR, "dimension_floor", V_POLISHED_TOP));
		dispatcher.addDispatch(all(AVBlocks.DIMENSION_WALL, "dimension_wall", V_SLATE_BRICK));
		dispatcher.addDispatch(all(AVBlocks.DEBUG_BLOCK, "debug_block", V_STONE));
		dispatcher.addDispatch(all(AVBlocks.DIMENSION_TILE_BLUE, "dimension_tile_blue", "minecraft:block/wool/blue"));
		dispatcher.addDispatch(all(AVBlocks.DIMENSION_TILE_YELLOW, "dimension_tile_yellow", "minecraft:block/wool/yellow"));

		dispatcher.addDispatch(new BlockModelScreen<>(AVBlocks.GREENSCREEN).withTextures("alphaver:block/greenscreen"));
		dispatcher.addDispatch(column(AVBlocks.FAKE_GRASS, "fake_grass_top", "fake_grass_side", "fake_grass_bottom",
			V_GRASS_TOP, V_GRASS_SIDE));
		dispatcher.addDispatch(all(AVBlocks.FAKE_DIRT, "fake_dirt", V_DIRT));
		dispatcher.addDispatch(all(AVBlocks.FAKE_STONE, "fake_stone", V_STONE));
		dispatcher.addDispatch(all(AVBlocks.FAKE_SAND, "fake_sand", "minecraft:block/sand"));

		dispatcher.addDispatch(new BlockModelHubDoor<>(AVBlocks.HUB_DOOR));

		dispatcher.addDispatch(new BlockModelCypressDoorLower<>(AVBlocks.CYPRESS_DOOR_LOWER, true)
			.withTextures(AVTextures.block("cypress_door_lower", V_DOOR_BOTTOM)));
		dispatcher.addDispatch(transparent(AVBlocks.CYPRESS_DOOR_UPPER, true, "cypress_door_upper", V_DOOR_TOP));

		dispatcher.addDispatch(new BlockModelMinigameDoor<>(AVBlocks.ZOMBIES_DOOR, "zombies", "alphaver:block/door/icon/zombies",
			"alphaver:block/door/icon/zombies_cypress", "alphaver:block/door_icon/zombies_cypress"));
		dispatcher.addDispatch(new BlockModelMinigameDoor<>(AVBlocks.FREERUN_DOOR, "freerun", "alphaver:block/door/icon/freerun",
			null, null));

		registerCraftedBlocks(dispatcher);
		registerMinigameBlocks(dispatcher);
		registerBiomeGrass(dispatcher);
	}

	private static void registerMinigameBlocks(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(all(AVBlocks.MOJANG_BLOCK_BLUE, "mojang_block_blue", "minecraft:block/wool/blue"));
		dispatcher.addDispatch(new BlockModelEmpty<>(AVBlocks.GHOST_BLOCK));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_MAGENTA, false, "glass_magenta", V_GLASS_TINTED));
		dispatcher.addDispatch(transparent(AVBlocks.GLOWING_CACHE, false, "glowing_cache", V_GLOWSTONE));
		dispatcher.addDispatch(all(AVBlocks.ELDER_DECORATED_STONE, "elder_decorated_stone", V_POLISHED_SIDE));
		dispatcher.addDispatch(all(AVBlocks.ELDER_BRICK, "elder_brick", "minecraft:block/brick_stone"));
		dispatcher.addDispatch(all(AVBlocks.ELDER_PILLAR, "elder_pillar", V_SLATE_SIDE));
		dispatcher.addDispatch(all(AVBlocks.ELDER_SMOOTH_STONE, "elder_smooth_stone", V_POLISHED_TOP));
		dispatcher.addDispatch(all(AVBlocks.GOLD_ELDER_BRICK, "gold_elder_brick", "minecraft:block/brick_gold"));

		vending(dispatcher, AVBlocks.VENDING_HEALTH_BOOST, AVBlocks.VENDING_HEALTH_BOOST_TOP, "health_boost");
		vending(dispatcher, AVBlocks.VENDING_ARMOR, AVBlocks.VENDING_ARMOR_TOP, "armor");
		vending(dispatcher, AVBlocks.VENDING_DASH, AVBlocks.VENDING_DASH_TOP, "dash");
		vending(dispatcher, AVBlocks.VENDING_QUICK_REVIVE, AVBlocks.VENDING_QUICK_REVIVE_TOP, "quick_revive");
		dispatcher.addDispatch(column(AVBlocks.WEAPON_UPGRADER, "weapon_upgrader_top", "weapon_upgrader_side", "weapon_upgrader_top",
			"minecraft:block/block_iron", "minecraft:block/brick_steel"));
		dispatcher.addDispatch(column(AVBlocks.WEAPON_GIVER, "vending_armor_top", "stone_tile", "vending_armor_top",
			"minecraft:block/block_iron", "minecraft:block/brick_stone"));
		dispatcher.addDispatch(transparent(AVBlocks.WIREFRAME_DOOR, false, "wireframe_block", V_GLASS));

		dispatcher.addDispatch(all(AVBlocks.DECORATIVE_BLOCK_1, "decorative_block_1", "minecraft:block/brick_marble"));
		dispatcher.addDispatch(all(AVBlocks.DECORATIVE_BLOCK_2, "decorative_block_2", "minecraft:block/brick_marble"));
		dispatcher.addDispatch(all(AVBlocks.SLATE_BEACON, "slate_beacon", V_GLOWSTONE));
		dispatcher.addDispatch(all(AVBlocks.COAL_BRICK, "coal_brick", V_OBSIDIAN));
	}

	private static void vending(BlockModelDispatcher dispatcher, Block<?> bottom, Block<?> top, String perk) {
		String end = "vending_" + perk + "_top";
		dispatcher.addDispatch(column(bottom, end, "vending_" + perk + "_lower", end, "minecraft:block/block_iron", "minecraft:block/brick_iron"));
		dispatcher.addDispatch(column(top, end, "vending_" + perk + "_upper", end, "minecraft:block/block_iron", "minecraft:block/brick_iron"));
	}

	private static void registerBiomeGrass(BlockModelDispatcher dispatcher) {
		Map<Object, Object> dispatches = ((DispatcherAccessor) (Object) dispatcher).alphaver$getDispatches();
		if (!dispatches.containsKey(Blocks.GRASS_RETRO)) {
			AlphaVer.LOGGER.warn("BTA's retro grass had no model yet when AlphaVer's models loaded; Cypress's biome grass "
				+ "is off for this reload.");
			return;
		}
		boolean fields = AVTextures.has("alphaver:block/grass_fields_top") && AVTextures.has("alphaver:block/grass_fields_side");
		boolean highwood = AVTextures.has("alphaver:block/grass_highwood_top") && AVTextures.has("alphaver:block/grass_highwood_side");
		dispatches.put(Blocks.GRASS_RETRO, new BlockModelCypressGrass<>(Blocks.GRASS_RETRO, fields, highwood));
	}

	private static BlockModelGeneric<?> biomeCross(Block<?> block, String name) {
		if (!AVTextures.has("alphaver:block/" + name)) {
			return cross(block, name);
		}
		String variant = AVTextures.has("alphaver:block/" + name + "_fields") ? "alphaver:block/plant/" + name + "_fields" : null;
		return new BlockModelCypressPlant<>(block, BlockModelDispatcher.loadDataModel("alphaver:block/plant/" + name), variant)
			.render3D(false);
	}

	private static void registerCraftedBlocks(BlockModelDispatcher dispatcher) {
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_FORTIFIED, false, "glass_fortified", V_GLASS));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_FORTIFIED_MAGENTA, false, "glass_fortified_magenta", V_GLASS_TINTED));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_FORTIFIED_BLUE, false, "glass_fortified_blue", V_GLASS_TINTED));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_FORTIFIED_GREEN, false, "glass_fortified_green", V_GLASS_TINTED));
		dispatcher.addDispatch(transparent(AVBlocks.GLASS_FORTIFIED_BLACK, false, "glass_fortified_black", V_GLASS_TINTED));
		dispatcher.addDispatch(transparent(AVBlocks.TILE, false, "tile", "minecraft:block/mesh"));
		dispatcher.addDispatch(all(AVBlocks.STONE_TILE, "stone_tile", "minecraft:block/brick_stone"));

		dispatcher.addDispatch(all(AVBlocks.CLOTH_MAGENTA, "cloth_magenta", "minecraft:block/wool/magenta"));
		dispatcher.addDispatch(all(AVBlocks.CLOTH_BLUE, "cloth_blue", "minecraft:block/wool/blue"));
		dispatcher.addDispatch(all(AVBlocks.CLOTH_GREEN, "cloth_green", "minecraft:block/wool/green"));
		dispatcher.addDispatch(all(AVBlocks.CLOTH_BLACK, "cloth_black", "minecraft:block/wool/black"));

		dispatcher.addDispatch(all(AVBlocks.PLANKS_FLAMEWOOD, "flamewood_planks", "minecraft:block/planks/red"));
		dispatcher.addDispatch(all(AVBlocks.PLANKS_HIGHWOOD, "highwood_planks", "minecraft:block/planks/brown"));
		dispatcher.addDispatch(all(AVBlocks.PLANKS_MYCON, "mycon_planks", "minecraft:block/planks/purple"));
		dispatcher.addDispatch(all(AVBlocks.PLANKS_TEA, "tea_planks", "minecraft:block/planks/green"));
		dispatcher.addDispatch(column(AVBlocks.BOOKSHELF, "bookshelf_top", "bookshelf_side", "bookshelf_top",
			V_PLANKS, "minecraft:block/bookshelf"));
		dispatcher.addDispatch(column(AVBlocks.BOOKSHELF_EMPTY, "bookshelf_top", "bookshelf_empty_side", "bookshelf_top",
			V_PLANKS, V_PLANKS));
		dispatcher.addDispatch(column(AVBlocks.CLOTH_PANEL, "cloth_panel_end", "cloth_panel_side", "cloth_panel_end",
			V_PLANKS, "minecraft:block/wool/white"));

		dispatcher.addDispatch(all(AVBlocks.ESSENCE_CACHE, "essence_cache", "minecraft:block/block_lapis"));
		dispatcher.addDispatch(all(AVBlocks.LIMESTONE_SMOOTH, "smooth_limestone", V_LIMESTONE));
		dispatcher.addDispatch(all(AVBlocks.BRICK_LICHEN, "lichen_bricks", "minecraft:block/brick_polished_stone_mossy"));
		dispatcher.addDispatch(all(AVBlocks.LICHEN_MASS, "lichen_mass", "minecraft:block/cobbled_stone_mossy"));

		dispatcher.addDispatch(transparent(AVBlocks.FLAME_GLASS_SKY, false, "flame_glass_sky", "minecraft:block/lamp/lightblue_active"));
		dispatcher.addDispatch(transparent(AVBlocks.FLAME_GLASS_GOLD, false, "flame_glass_gold", "minecraft:block/lamp/yellow_active"));
		dispatcher.addDispatch(transparent(AVBlocks.FLAME_GLASS_OBSIDIAN, false, "flame_glass_obsidian", "minecraft:block/lamp/purple_active"));
		dispatcher.addDispatch(transparent(AVBlocks.FLAME_GLASS_LOW, false, "flame_glass_low", "minecraft:block/lamp/cyan_active"));

		dispatcher.addDispatch(all(AVBlocks.BRICK_GOLD, "gold_brick", "minecraft:block/brick_gold"));
		dispatcher.addDispatch(all(AVBlocks.BRICK_OBSIDIAN, "obsidian_brick", V_OBSIDIAN));
		dispatcher.addDispatch(all(AVBlocks.BRICK_DIAMOND, "diamond_brick", "minecraft:block/brick_diamond"));
		dispatcher.addDispatch(all(AVBlocks.BRICK_IRON, "iron_brick", "minecraft:block/brick_iron"));

		dispatcher.addDispatch(all(AVBlocks.PILLAR_HIGHWOOD, "highwood_pillar", V_LOG_SIDE));
		dispatcher.addDispatch(all(AVBlocks.PILLAR_TEA, "tea_pillar", V_LOG_SIDE));
		dispatcher.addDispatch(all(AVBlocks.PILLAR_MYCON, "mycon_pillar", V_LOG_SIDE));
		dispatcher.addDispatch(column(AVBlocks.PILLAR_BISMUTH, "bismuth_pillar_top", "bismuth_pillar_side", "bismuth_pillar_top",
			"minecraft:block/pillar_marble/top", "minecraft:block/pillar_marble/side"));

		dispatcher.addDispatch(all(AVBlocks.BRICK_SALT, "salt_bricks", "minecraft:block/brick_marble"));
		dispatcher.addDispatch(all(AVBlocks.BRICK_SNOW, "snow_brick", "minecraft:block/brick_permafrost"));
		dispatcher.addDispatch(all(AVBlocks.BRICK_BISMUTH, "bismuth_brick", "minecraft:block/brick_steel"));
		dispatcher.addDispatch(all(AVBlocks.BLOCK_BISMUTH, "bismuth_block", "minecraft:block/block_iron"));

		dispatcher.addDispatch(transparent(AVBlocks.BLOCK_LACE_AGATE, false, "lace_agate_block", "minecraft:block/block_quartz"));
		dispatcher.addDispatch(transparent(AVBlocks.BLOCK_CLINOHUMITE, false, "clinohumite_block", "minecraft:block/block_gold"));
		dispatcher.addDispatch(transparent(AVBlocks.BLOCK_MALACHITE, false, "malachite_block", "minecraft:block/block_olivine"));
		dispatcher.addDispatch(transparent(AVBlocks.BLOCK_PYRITE, false, "pyrite_block", "minecraft:block/block_gold"));

		door(dispatcher, AVBlocks.DOOR_FLAMEWOOD_BOTTOM, AVBlocks.DOOR_FLAMEWOOD_TOP, "flamewood", "cypress_door");
		door(dispatcher, AVBlocks.DOOR_HIGHWOOD_BOTTOM, AVBlocks.DOOR_HIGHWOOD_TOP, "highwood", "highwood_door");
		door(dispatcher, AVBlocks.DOOR_MYCON_BOTTOM, AVBlocks.DOOR_MYCON_TOP, "mycon", "mycon_door");
		door(dispatcher, AVBlocks.DOOR_TEA_BOTTOM, AVBlocks.DOOR_TEA_TOP, "tea", "tea_door");
		door(dispatcher, AVBlocks.DOOR_ICE_BOTTOM, AVBlocks.DOOR_ICE_TOP, "ice", "ice_door");

		dispatcher.addDispatch(column(AVBlocks.WORKBENCH_MYCON, "mycon_workbench_top", "mycon_workbench_side",
			"mycon_workbench_bottom", V_WORKBENCH_TOP, V_WORKBENCH_SIDE));
		dispatcher.addDispatch(column(AVBlocks.WORKBENCH_HIGHWOOD, "highwood_workbench_top", "highwood_workbench_side",
			"highwood_workbench_bottom", V_WORKBENCH_TOP, V_WORKBENCH_SIDE));
		dispatcher.addDispatch(column(AVBlocks.WORKBENCH_FLAMEWOOD, "flamewood_workbench_top", "flamewood_workbench_side",
			"flamewood_workbench_bottom", V_WORKBENCH_TOP, V_WORKBENCH_SIDE));
		dispatcher.addDispatch(column(AVBlocks.WORKBENCH_TEA, "tea_workbench_top", "tea_workbench_side",
			"tea_workbench_bottom", V_WORKBENCH_TOP, V_WORKBENCH_SIDE));

		freezer(dispatcher, AVBlocks.FREEZER, "freezer", "freezer_front", "minecraft:block/furnace_stone/idle");
		freezer(dispatcher, AVBlocks.FREEZER_LIT, "freezer_lit", "freezer_front_lit", "minecraft:block/furnace_stone/active");

		dispatcher.addDispatch(column(AVBlocks.ESSENCE_TRANSFORMER, "essence_transformer_top", "essence_transformer_side",
			"essence_machine_bottom", V_POLISHED_TOP, V_POLISHED_SIDE));
		dispatcher.addDispatch(column(AVBlocks.ESSENCE_CLONER, "essence_cloner_top", "essence_cloner_side",
			"essence_machine_bottom", V_POLISHED_TOP, V_POLISHED_SIDE));

		dispatcher.addDispatch(new BlockModelEssenceFountain<>(AVBlocks.ESSENCE_FOUNTAIN));

		dispatcher.addDispatch(cross(AVBlocks.LILY_FLAME, "lily_flame"));
		dispatcher.addDispatch(cross(AVBlocks.LILY_GOLD, "lily_gold"));
		dispatcher.addDispatch(cross(AVBlocks.LILY_OBSIDIAN, "lily_obsidian"));
	}

	private static void freezer(BlockModelDispatcher dispatcher, Block<?> block, String model, String front, String fallback) {
		boolean bridged = AVTextures.has("alphaver:block/freezer_top") && AVTextures.has("alphaver:block/freezer_side")
			&& AVTextures.has("alphaver:block/" + front);
		dispatcher.addDispatch(new BlockModelGenericFurnace<>(block, bridged ? "alphaver:block/" + model : fallback));
	}

	private static void door(BlockModelDispatcher dispatcher, Block<? extends BlockLogicDoor> bottom, Block<? extends BlockLogicDoor> top,
	                         String name, String texture) {
		boolean bridged = AVTextures.has("alphaver:block/" + texture + "_lower") && AVTextures.has("alphaver:block/" + texture + "_upper");
		String key = bridged ? "alphaver:block/door/" + name : "minecraft:block/door/planks/oak";
		dispatcher.addDispatch(new BlockModelGenericDoor<>(bottom, key, true));
		dispatcher.addDispatch(new BlockModelGenericDoor<>(top, key, false));
	}

	private static BlockModelStandard<?> all(Block<?> block, String name, String fallback) {
		return new BlockModelStandard<>(block).withTextures(AVTextures.block(name, fallback));
	}

	private static BlockModelStandard<?> column(Block<?> block, String top, String side, String bottom,
	                                           String fallbackTop, String fallbackSide) {
		String topTexture = AVTextures.block(top, fallbackTop);
		String sideTexture = AVTextures.block(side, fallbackSide);
		String bottomTexture = AVTextures.block(bottom, fallbackTop);
		return new BlockModelStandard<>(block).withTextures(topTexture, bottomTexture, sideTexture);
	}

	private static BlockModelStandard<?> transparent(Block<?> block, boolean renderInside, String name, String fallback) {
		return new BlockModelTransparent<>(block, renderInside).withTextures(AVTextures.block(name, fallback));
	}

	private static BlockModelGeneric<?> cross(Block<?> block, String name) {
		String model = AVTextures.has("alphaver:block/" + name) ? "alphaver:block/plant/" + name : M_SAPLING;
		return new BlockModelGeneric<>(block, BlockModelDispatcher.loadDataModel(model)).render3D(false);
	}

	private static BlockModelGeneric<?> torch(Block<?> block, String name) {
		String model = AVTextures.has("alphaver:block/" + name) ? "alphaver:block/" + name : M_TORCH;
		return new BlockModelGeneric<>(block, BlockModelDispatcher.loadDataModel(model)).render3D(false);
	}

	private static BlockModelGeneric<?> greenstoneTorch(Block<?> block, String name, String fallback) {
		String key = AVTextures.has("alphaver:block/" + name) ? "alphaver:block/" + name : fallback;
		return new BlockModelGenericTorch<>(block, key).render3D(false);
	}

	private static BlockModelGenericLeaves<?> leaves(Block<?> block, String name) {
		boolean bridged = AVTextures.has("alphaver:block/" + name) && AVTextures.has("alphaver:block/" + name + "_fast");
		return new BlockModelGenericLeaves<>(block, bridged ? "alphaver:block/leaves/" + name : M_LEAVES_RETRO);
	}
}
