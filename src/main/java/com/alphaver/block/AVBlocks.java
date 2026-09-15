package com.alphaver.block;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVDimensions;
import com.alphaver.world.minigame.MinigameKind;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicFallingBlock;
import net.minecraft.core.block.BlockLogicGlass;
import net.minecraft.core.block.BlockLogicStone;
import net.minecraft.core.block.BlockLogicWorkbench;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.block.tag.BlockTags;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.BlockSounds;
import turniplabs.halplibe.helper.BlockBuilder;

public final class AVBlocks {
	private AVBlocks() {}

	public static final int BASE_ID = 4000;

	public static Block<?> SALT_BLOCK;
	public static Block<?> WATER_LILY;
	public static Block<?> CELESTIAL_FLAME;
	public static Block<?> LILY_FLAME;
	public static Block<?> LILY_GOLD;
	public static Block<?> LILY_OBSIDIAN;
	public static Block<?> GLASS_BLUE;
	public static Block<?> GLASS_GREEN;
	public static Block<?> GLASS_BLACK;
	public static Block<?> LOG_FLAMEWOOD;
	public static Block<?> LEAVES_FLAMEWOOD;
	public static Block<?> LOW_LILY;
	public static Block<?> LOW_VINE;
	public static Block<?> LOW_RIVERBED;
	public static Block<?> LOW_WART;
	public static Block<?> LOW_RIVER_STONE;
	public static Block<?> LOW_ORE_GOLD;
	public static Block<?> LOW_ORE_IRON;
	public static Block<?> LOW_ORE_COAL;
	public static Block<?> LOW_ORE_DIAMOND;
	public static Block<?> LOW_ORE_GREENSTONE;
	public static Block<?> COBBLED_LIMESTONE;
	public static Block<?> LIMESTONE;
	public static Block<?> SMOOTH_STONE;
	public static Block<?> CRUDE_PILLAR;
	public static Block<?> HYDRANGEA;
	public static Block<?> LOG_HIGHWOOD;
	public static Block<?> LEAVES_HIGHWOOD;
	public static Block<?> ROOTS_HIGHWOOD;
	public static Block<?> TALLGRASS;
	public static Block<?> WIREFRAME;
	public static Block<?> LICHEN;
	public static Block<?> PILLAR_FLAMEWOOD;
	public static Block<?> BRICK_SLATE;
	public static Block<?> PILLAR_SLATE;
	public static Block<?> LOW_MYCON;
	public static Block<?> MYCON_STEM;
	public static Block<?> MYCON_CAP;
	public static Block<?> MYCON_CAP_GLOWING;
	public static Block<?> ORE_BISMUTH;
	public static Block<?> LEAVES_TEA;
	public static Block<?> LOG_TEA;
	public static Block<?> ORE_LACE_AGATE;
	public static Block<?> ORE_CLINOHUMITE;
	public static Block<?> ORE_MALACHITE;
	public static Block<?> ORE_PYRITE;
	public static Block<?> GRASS_PATHWAY;
	public static Block<?> FRIGID_TRUNK;
	public static Block<?> FRIGID_LEAVES;
	public static Block<?> PLATE_SOLAR;
	public static Block<?> PLATE_DENIAL;
	public static Block<?> PLATE_SWITCH;
	public static Block<?> PLATE_LOOP;
	public static Block<?> PLATE_PART;
	public static Block<?> PLATE_TRINITY;
	public static Block<?> PLATE_ASSOCIATION;
	public static Block<?> PLATE_DIALECT;
	public static Block<?> PLATE_SYLLABLES;
	public static Block<?> PLATE_MIRRORS;
	public static Block<?> PILLAR;
	public static Block<?> DIMENSION_FLOOR;
	public static Block<?> DIMENSION_WALL;
	public static Block<?> DEBUG_BLOCK;
	public static Block<?> DIMENSION_TILE_BLUE;
	public static Block<?> DIMENSION_TILE_YELLOW;
	public static Block<?> GREENSCREEN;
	public static Block<?> FAKE_GRASS;
	public static Block<?> FAKE_DIRT;
	public static Block<?> FAKE_STONE;
	public static Block<?> FAKE_SAND;

	public static Block<?> MOJANG_BLOCK_BLUE;
	public static Block<?> GHOST_BLOCK;
	public static Block<?> GLASS_MAGENTA;
	public static Block<?> GLOWING_CACHE;
	public static Block<?> ELDER_DECORATED_STONE;
	public static Block<?> ELDER_BRICK;
	public static Block<?> ELDER_PILLAR;
	public static Block<?> ELDER_SMOOTH_STONE;
	public static Block<?> GOLD_ELDER_BRICK;
	public static Block<?> VENDING_HEALTH_BOOST;
	public static Block<?> VENDING_HEALTH_BOOST_TOP;
	public static Block<?> VENDING_ARMOR;
	public static Block<?> VENDING_ARMOR_TOP;
	public static Block<?> VENDING_DASH;
	public static Block<?> VENDING_DASH_TOP;
	public static Block<?> VENDING_QUICK_REVIVE;
	public static Block<?> VENDING_QUICK_REVIVE_TOP;
	public static Block<?> WEAPON_UPGRADER;
	public static Block<?> WIREFRAME_DOOR;
	public static Block<?> WEAPON_GIVER;
	public static Block<?> DECORATIVE_BLOCK_1;
	public static Block<?> DECORATIVE_BLOCK_2;
	public static Block<?> SLATE_BEACON;
	public static Block<?> COAL_BRICK;

	public static Block<?> GREENSTONE_WIRE;
	public static Block<?> ORE_GREENSTONE;
	public static Block<?> ORE_GREENSTONE_GLOWING;
	public static Block<?> TORCH_GREENSTONE_IDLE;
	public static Block<?> TORCH_GREENSTONE_ACTIVE;

	public static Block<?> GLASS_FORTIFIED;
	public static Block<?> GLASS_FORTIFIED_MAGENTA;
	public static Block<?> GLASS_FORTIFIED_BLUE;
	public static Block<?> GLASS_FORTIFIED_GREEN;
	public static Block<?> GLASS_FORTIFIED_BLACK;
	public static Block<?> STONE_TILE;
	public static Block<?> TILE;
	public static Block<?> CLOTH_MAGENTA;
	public static Block<?> CLOTH_BLUE;
	public static Block<?> CLOTH_GREEN;
	public static Block<?> CLOTH_BLACK;
	public static Block<?> PLANKS_FLAMEWOOD;
	public static Block<?> PLANKS_HIGHWOOD;
	public static Block<?> PLANKS_MYCON;
	public static Block<?> PLANKS_TEA;
	public static Block<?> ESSENCE_CACHE;
	public static Block<?> LIMESTONE_SMOOTH;
	public static Block<?> BOOKSHELF;
	public static Block<?> BOOKSHELF_EMPTY;
	public static Block<?> CLOTH_PANEL;
	public static Block<?> BRICK_LICHEN;
	public static Block<?> LICHEN_MASS;
	public static Block<?> FLAME_GLASS_SKY;
	public static Block<?> FLAME_GLASS_GOLD;
	public static Block<?> FLAME_GLASS_OBSIDIAN;
	public static Block<?> FLAME_GLASS_LOW;
	public static Block<?> BRICK_GOLD;
	public static Block<?> BRICK_OBSIDIAN;
	public static Block<?> BRICK_DIAMOND;
	public static Block<?> BRICK_IRON;
	public static Block<?> PILLAR_HIGHWOOD;
	public static Block<?> PILLAR_TEA;
	public static Block<?> PILLAR_MYCON;
	public static Block<?> PILLAR_BISMUTH;
	public static Block<?> BRICK_SALT;
	public static Block<?> BRICK_SNOW;
	public static Block<?> BRICK_BISMUTH;
	public static Block<?> BLOCK_BISMUTH;
	public static Block<?> BLOCK_LACE_AGATE;
	public static Block<?> BLOCK_CLINOHUMITE;
	public static Block<?> BLOCK_MALACHITE;
	public static Block<?> BLOCK_PYRITE;

	public static Block<BlockLogicCypressDoor> DOOR_FLAMEWOOD_BOTTOM;
	public static Block<BlockLogicCypressDoor> DOOR_FLAMEWOOD_TOP;
	public static Block<BlockLogicCypressDoor> DOOR_HIGHWOOD_BOTTOM;
	public static Block<BlockLogicCypressDoor> DOOR_HIGHWOOD_TOP;
	public static Block<BlockLogicCypressDoor> DOOR_MYCON_BOTTOM;
	public static Block<BlockLogicCypressDoor> DOOR_MYCON_TOP;
	public static Block<BlockLogicCypressDoor> DOOR_TEA_BOTTOM;
	public static Block<BlockLogicCypressDoor> DOOR_TEA_TOP;
	public static Block<BlockLogicCypressDoor> DOOR_ICE_BOTTOM;
	public static Block<BlockLogicCypressDoor> DOOR_ICE_TOP;
	public static Block<?> WORKBENCH_MYCON;
	public static Block<?> WORKBENCH_HIGHWOOD;
	public static Block<?> WORKBENCH_FLAMEWOOD;
	public static Block<?> WORKBENCH_TEA;

	public static Block<?> FREEZER;
	public static Block<?> FREEZER_LIT;
	public static Block<?> ESSENCE_TRANSFORMER;
	public static Block<?> ESSENCE_CLONER;
	public static Block<?> ESSENCE_FOUNTAIN;

	public static Block<BlockLogicAlphaVerDoor> HUB_DOOR;
	public static Block<BlockLogicAlphaVerDoor> CYPRESS_DOOR_LOWER;
	public static Block<BlockLogicAlphaVerDoor> CYPRESS_DOOR_UPPER;

	public static Block<BlockLogicMinigameDoor> ZOMBIES_DOOR;
	public static Block<BlockLogicMinigameDoor> FREERUN_DOOR;

	private static int id(int cypressId) {
		return BASE_ID + cypressId;
	}

	public static void registerMiningLevels() {
		var levels = net.minecraft.core.item.tool.ItemToolPickaxe.miningLevels;
		levels.put(ORE_LACE_AGATE, 2);
		levels.put(ORE_CLINOHUMITE, 2);
		levels.put(ORE_MALACHITE, 2);
		levels.put(ORE_PYRITE, 2);
		levels.put(LOW_ORE_DIAMOND, 2);
		levels.put(LOW_ORE_GOLD, 2);
		levels.put(LOW_ORE_GREENSTONE, 2);
		levels.put(ORE_GREENSTONE, 2);
		levels.put(ORE_GREENSTONE_GLOWING, 2);
		levels.put(LOW_ORE_IRON, 1);
	}

	@SuppressWarnings("unchecked")
	public static void register() {
		BlockBuilder base = new BlockBuilder(AlphaVer.MOD_ID);

		BlockBuilder rock = base.clone()
			.setHardness(1.5F)
			.setResistance(10.0F)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		BlockBuilder ore = base.clone()
			.setHardness(3.0F)
			.setResistance(5.0F)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		BlockBuilder wood = base.clone()
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.MINEABLE_BY_AXE);
		BlockBuilder plant = base.clone()
			.setHardness(0.0F)
			.setResistance(0.0F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.BROKEN_BY_FLUIDS);
		BlockBuilder leaves = base.clone()
			.setHardness(0.2F)
			.setResistance(0.33F)
			.setLightOpacity(1)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.MINEABLE_BY_SHEARS);
		BlockBuilder glass = base.clone()
			.setHardness(0.1F)
			.setResistance(0.17F)
			.setLightOpacity(0)
			.setBlockSound(AVBlockSounds.GLASS);

		SALT_BLOCK = base.clone()
			.setHardness(0.6F)
			.setResistance(1.0F)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("block.salt", id(114), b -> new BlockLogic(b, Materials.GRASS));

		WATER_LILY = plant.clone()
			.setLuminance(14)
			.setBlockItem(b -> new ItemBlockLily<>(b))
			.build("lily.water", id(115), BlockLogicGlowingFlower::new);
		CELESTIAL_FLAME = plant.clone()
			.setLuminance(14)
			.setBlockSound(BlockSounds.STONE)
			.build("flame.celestial", id(116), b -> new BlockLogicCypressPlant(b, BlockLogicCypressPlant.Soil.FLOATS));

		LILY_FLAME = plant.clone()
			.setLuminance(14)
			.setBlockItem(b -> new ItemBlockLily<>(b))
			.build("lily.flame", id(117), b -> new BlockLogicInfusedLily(b, 2));
		LILY_GOLD = plant.clone()
			.setLuminance(14)
			.setBlockItem(b -> new ItemBlockLily<>(b))
			.build("lily.gold", id(118), b -> new BlockLogicInfusedLily(b, 4));
		LILY_OBSIDIAN = plant.clone()
			.setLuminance(14)
			.setBlockItem(b -> new ItemBlockLily<>(b))
			.build("lily.obsidian", id(119), b -> new BlockLogicInfusedLily(b, 7));
		COBBLED_LIMESTONE = rock.clone()
			.build("cobble.limestone", id(144), b -> new BlockLogic(b, Materials.STONE));
		LIMESTONE = rock.clone()
			.build("limestone", id(145), b -> new BlockLogicStone(b, COBBLED_LIMESTONE, Materials.STONE));
		HYDRANGEA = plant.clone()
			.build("flower.hydrangea", id(160), b -> new BlockLogicCypressPlant(b, BlockLogicCypressPlant.Soil.FLOWER));

		TALLGRASS = plant.clone()
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PLACE_OVERWRITES, BlockTags.SHEARS_DO_SILK_TOUCH)
			.build("tallgrass", id(165), BlockLogicCypressGrass::new);

		LOG_FLAMEWOOD = wood.clone()
			.setHardness(1.5F)
			.setResistance(10.0F)
			.build("log.flamewood", id(129), b -> new BlockLogic(b, Materials.WOOD));
		LEAVES_FLAMEWOOD = leaves.clone()
			.build("leaves.flamewood", id(131), b -> new BlockLogicCypressLeaves(b, BlockLogicCypressLeaves.Kind.FLAMEWOOD));
		LOG_HIGHWOOD = wood.clone()
			.setHardness(5.0F)
			.setResistance(20.0F)
			.build("log.highwood", id(161), b -> new BlockLogic(b, Materials.WOOD));
		LEAVES_HIGHWOOD = base.clone()
			.setHardness(0.4F)
			.setResistance(0.67F)
			.setLightOpacity(1)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.MINEABLE_BY_SHEARS)
			.build("leaves.highwood", id(162), b -> new BlockLogicCypressTransparent(b, Materials.LEAVES, false));
		ROOTS_HIGHWOOD = wood.clone()
			.setHardness(1.6F)
			.setResistance(20.0F)
			.build("roots.highwood", id(163), b -> new BlockLogic(b, Materials.WOOD));
		LEAVES_TEA = leaves.clone()
			.build("leaves.tea", id(233), b -> new BlockLogicCypressLeaves(b, BlockLogicCypressLeaves.Kind.TEA));
		LOG_TEA = wood.clone()
			.setHardness(2.0F)
			.setResistance(20.0F)
			.build("log.tea", id(234), b -> new BlockLogic(b, Materials.WOOD));
		FRIGID_TRUNK = base.clone()
			.setHardness(1.5F)
			.setResistance(2.0F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("trunk.frigid", id(254), BlockLogicFrigidTrunk::new);
		FRIGID_LEAVES = glass.clone()
			.build("leaves.frigid", id(255), b -> new BlockLogicCypressTransparent(b, Materials.LEAVES, false));

		LOW_LILY = plant.clone()
			.setLuminance(11)
			.setBlockItem(b -> new ItemBlockLily<>(b))
			.build("lily.low", id(132), BlockLogicGlowingFlower::new);
		LOW_VINE = plant.clone()
			.setLuminance(12)
			.build("vine.low", id(133), BlockLogicGlowingFlower::new);
		LOW_RIVERBED = rock.clone()
			.build("riverbed.low", id(134), b -> new BlockLogic(b, Materials.STONE));
		LOW_WART = base.clone()
			.setHardness(1.0F)
			.setResistance(1.67F)
			.setLuminance(13)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("wart.low", id(135), b -> new BlockLogicCypressTransparent(b, Materials.STONE, true));
		LOW_RIVER_STONE = rock.clone()
			.build("stone.low_river", id(136), b -> new BlockLogicStone(b, Blocks.COBBLE_STONE, Materials.STONE));
		LOW_ORE_GOLD = ore.clone()
			.build("ore.gold.low_river", id(137), b -> new BlockLogic(b, Materials.STONE));
		LOW_ORE_IRON = ore.clone()
			.build("ore.iron.low_river", id(138), b -> new BlockLogic(b, Materials.STONE));
		LOW_ORE_COAL = ore.clone()
			.build("ore.coal.low_river", id(139), b -> new BlockLogicCypressOre(b, () -> Items.COAL, 1, 0));
		LOW_ORE_DIAMOND = ore.clone()
			.build("ore.diamond.low_river", id(140), b -> new BlockLogicCypressOre(b, () -> Items.DIAMOND, 1, 0));

		LOW_ORE_GREENSTONE = ore.clone()
			.build("ore.greenstone.low_river", id(141), b -> new BlockLogicCypressOre(b, () -> com.alphaver.item.AVItems.GREENSTONE, 4, 2));
		LICHEN = plant.clone()
			.build("lichen", id(182), b -> new BlockLogicCypressPlant(b, BlockLogicCypressPlant.Soil.LOW_RIVER_STONE));

		LOW_MYCON = base.clone()
			.setHardness(0.6F)
			.setResistance(1.0F)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("mycon.low", id(217), b -> new BlockLogic(b, Materials.GRASS));
		MYCON_STEM = wood.clone()
			.setHardness(5.0F)
			.setResistance(20.0F)
			.build("mycon.stem", id(218), b -> new BlockLogic(b, Materials.WOOD));
		MYCON_CAP = base.clone()
			.setHardness(1.0F)
			.setResistance(1.67F)
			.setLightOpacity(4)
			.setBlockSound(BlockSounds.CLOTH)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("mycon.cap", id(220), b -> new BlockLogicCypressTransparent(b, Materials.ICE, false));
		MYCON_CAP_GLOWING = base.clone()
			.setHardness(1.0F)
			.setResistance(1.67F)
			.setLightOpacity(4)
			.setLuminance(9)
			.setBlockSound(BlockSounds.CLOTH)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("mycon.cap.glowing", id(221), b -> new BlockLogicCypressTransparent(b, Materials.ICE, false));

		ORE_BISMUTH = ore.clone().build("ore.bismuth", id(229), b -> new BlockLogic(b, Materials.STONE));

		ORE_LACE_AGATE = ore.clone().build("ore.lace_agate", id(245),
			b -> new BlockLogicCypressOre(b, () -> com.alphaver.item.AVItems.LACE_AGATE, 1, 0));
		ORE_CLINOHUMITE = ore.clone().build("ore.clinohumite", id(246),
			b -> new BlockLogicCypressOre(b, () -> com.alphaver.item.AVItems.CLINOHUMITE, 1, 0));
		ORE_MALACHITE = ore.clone().build("ore.malachite", id(247),
			b -> new BlockLogicCypressOre(b, () -> com.alphaver.item.AVItems.MALACHITE, 1, 0));
		ORE_PYRITE = ore.clone().build("ore.pyrite", id(248),
			b -> new BlockLogicCypressOre(b, () -> com.alphaver.item.AVItems.PYRITE, 1, 0));

		GREENSTONE_WIRE = base.clone()
			.setHardness(0.0F)
			.setResistance(0.0F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PREVENT_MOB_SPAWNS)
			.build("wire.greenstone", id(55), b -> new BlockLogicGreenstoneWire(b));
		ORE_GREENSTONE = ore.clone()
			.build("ore.greenstone", id(73), b -> new BlockLogicGreenstoneOre(b, false));
		ORE_GREENSTONE_GLOWING = ore.clone()
			.setLuminance(9)
			.setTicking(true)
			.build("ore.greenstone.glowing", id(74), b -> new BlockLogicGreenstoneOre(b, true));
		BlockBuilder greenstoneTorch = base.clone()
			.setHardness(0.0F)
			.setResistance(0.0F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.BROKEN_BY_FLUIDS, BlockTags.PREVENT_MOB_SPAWNS);
		TORCH_GREENSTONE_IDLE = greenstoneTorch.clone()
			.build("torch.greenstone.idle", id(75), b -> new BlockLogicGreenstoneTorch(b, false));
		TORCH_GREENSTONE_ACTIVE = greenstoneTorch.clone()
			.setLuminance(7)
			.build("torch.greenstone.active", id(76), b -> new BlockLogicGreenstoneTorch(b, true));

		SMOOTH_STONE = rock.clone().build("stone.smooth", id(157), b -> new BlockLogic(b, Materials.STONE));
		CRUDE_PILLAR = rock.clone().build("pillar.crude", id(159), b -> new BlockLogic(b, Materials.STONE));

		PILLAR_FLAMEWOOD = rock.clone()
			.setBlockSound(BlockSounds.WOOD)
			.build("pillar.flamewood", id(201), b -> new BlockLogic(b, Materials.STONE));
		BRICK_SLATE = rock.clone().build("brick.slate", id(203), b -> new BlockLogic(b, Materials.STONE));
		PILLAR_SLATE = rock.clone().build("pillar.slate", id(205), b -> new BlockLogic(b, Materials.STONE));
		WIREFRAME = base.clone()
			.setHardness(1.5F)
			.setResistance(2.5F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("wireframe", id(173), b -> new BlockLogicCypressTransparent(b, Materials.IRON, true));
		GLASS_BLUE = glass.clone().build("glass.blue", id(122), b -> new BlockLogicGlass(b, Materials.GLASS));
		GLASS_GREEN = glass.clone().build("glass.green", id(123), b -> new BlockLogicGlass(b, Materials.GLASS));
		GLASS_BLACK = glass.clone().build("glass.black", id(124), b -> new BlockLogicGlass(b, Materials.GLASS));
		GRASS_PATHWAY = base.clone()
			.setHardness(0.6F)
			.setResistance(1.0F)
			.setBlockSound(BlockSounds.METAL)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("pathway.grass", id(253), b -> new BlockLogic(b, Materials.GRASS));

		BlockBuilder plate = base.clone()
			.setHardness(0.0F)
			.setResistance(0.0F)
			.setLuminance(13)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.STONE);
		PLATE_SOLAR = plate.clone().build("plate.solar", id(206), BlockLogicCypressPlate::new);
		PLATE_DENIAL = plate.clone().build("plate.denial", id(207), BlockLogicCypressPlate::new);
		PLATE_SWITCH = plate.clone().build("plate.switch", id(208), BlockLogicCypressPlate::new);
		PLATE_LOOP = plate.clone().build("plate.loop", id(209), BlockLogicCypressPlate::new);
		PLATE_PART = plate.clone().build("plate.part", id(210), BlockLogicCypressPlate::new);
		PLATE_TRINITY = plate.clone().build("plate.trinity", id(211), BlockLogicCypressPlate::new);
		PLATE_ASSOCIATION = plate.clone().build("plate.association", id(212), BlockLogicCypressPlate::new);
		PLATE_DIALECT = plate.clone().build("plate.dialect", id(213), BlockLogicCypressPlate::new);
		PLATE_SYLLABLES = plate.clone().build("plate.syllables", id(214), BlockLogicCypressPlate::new);
		PLATE_MIRRORS = plate.clone().build("plate.mirrors", id(215), BlockLogicCypressPlate::new);

		PILLAR = wood.clone()
			.setHardness(2.0F)
			.setResistance(5.0F)
			.build("pillar", id(91), BlockLogicPillar::new);
		DIMENSION_FLOOR = rock.clone().build("dimension.floor", id(95), b -> new BlockLogic(b, Materials.STONE));
		DIMENSION_WALL = rock.clone().build("dimension.wall", id(96), b -> new BlockLogic(b, Materials.STONE));
		DEBUG_BLOCK = rock.clone()
			.setHardness(0.2F)
			.setBlockSound(AVBlockSounds.SPECIAL)
			.build("debug", id(97), b -> new BlockLogic(b, Materials.STONE));

		DIMENSION_TILE_BLUE = rock.clone().build("dimension.tile.blue", id(98), b -> new BlockLogic(b, Materials.STONE));
		DIMENSION_TILE_YELLOW = rock.clone().build("dimension.tile.yellow", id(99), b -> new BlockLogic(b, Materials.STONE));

		GREENSCREEN = rock.clone()
			.build("greenscreen", BASE_ID + 264, b -> new BlockLogicCypressNoDrop(b, Materials.STONE));

		FAKE_GRASS = base.clone()
			.setHardness(0.6F)
			.setResistance(1.0F)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("fake.grass", id(100), b -> new BlockLogic(b, Materials.GRASS));
		FAKE_DIRT = rock.clone()
			.setHardness(0.6F)
			.setBlockSound(BlockSounds.GRASS)
			.build("fake.dirt", id(106), b -> new BlockLogic(b, Materials.STONE));
		FAKE_STONE = rock.clone().build("fake.stone", id(107), b -> new BlockLogic(b, Materials.STONE));
		FAKE_SAND = base.clone()
			.setHardness(0.5F)
			.setResistance(0.83F)
			.setBlockSound(BlockSounds.SAND)
			.setTags(BlockTags.MINEABLE_BY_SHOVEL)
			.build("fake.sand", id(108), b -> new BlockLogicFallingBlock(b, Materials.SAND));

		registerCraftedBlocks(base, rock, wood, glass);
		registerMinigameBlocks(base, rock, glass);

		AlphaVer.LOGGER.info("Registered Cypress's blocks.");
	}

	private static void registerMinigameBlocks(BlockBuilder base, BlockBuilder rock, BlockBuilder glass) {
		MOJANG_BLOCK_BLUE = rock.clone().build("block.mojang.blue", id(101), b -> new BlockLogic(b, Materials.STONE));
		GHOST_BLOCK = base.clone()
			.setHardness(0.8F)
			.setResistance(0.8F)
			.setLightOpacity(0)
			.setBlockSound(AVBlockSounds.SPECIAL)
			.build("ghost", id(104), BlockLogicGhostBlock::new);
		GLASS_MAGENTA = glass.clone().build("glass.magenta", id(121), b -> new BlockLogicGlass(b, Materials.GLASS));

		GLOWING_CACHE = base.clone()
			.setHardness(1.0F)
			.setResistance(1.67F)
			.setLuminance(14)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.GRASS)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("cache.glowing", id(148), b -> new BlockLogicCypressTransparent(b, Materials.STONE, true));

		BlockBuilder elder = rock.clone().setBlockSound(AVBlockSounds.ELDER);
		ELDER_DECORATED_STONE = elder.clone().build("elder.decorated", id(166), b -> new BlockLogic(b, Materials.STONE));
		ELDER_BRICK = elder.clone().build("elder.brick", id(167), b -> new BlockLogic(b, Materials.STONE));
		ELDER_PILLAR = elder.clone().build("elder.pillar", id(168), b -> new BlockLogic(b, Materials.STONE));
		ELDER_SMOOTH_STONE = elder.clone().build("elder.smooth", id(169), b -> new BlockLogic(b, Materials.STONE));

		GOLD_ELDER_BRICK = elder.clone()
			.setLuminance(13)
			.setLightOpacity(0)
			.build("elder.brick.gold", id(185), b -> new BlockLogic(b, Materials.STONE));

		BlockBuilder machine = base.clone()
			.setHardness(8000.0F)
			.setResistance(8000.0F)
			.setBlockSound(BlockSounds.METAL)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		VENDING_HEALTH_BOOST = machine.clone().build("vending.health_boost", id(174),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.PERK_HEALTH_BOOST));
		VENDING_HEALTH_BOOST_TOP = machine.clone().build("vending.health_boost.top", id(175),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.MACHINE_TOP));
		VENDING_ARMOR = machine.clone().build("vending.armor", id(176),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.PERK_ARMOR));
		VENDING_ARMOR_TOP = machine.clone().build("vending.armor.top", id(177),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.MACHINE_TOP));
		VENDING_DASH = machine.clone().build("vending.dash", id(178),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.PERK_DASH));
		VENDING_DASH_TOP = machine.clone().build("vending.dash.top", id(179),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.MACHINE_TOP));
		VENDING_QUICK_REVIVE = machine.clone().build("vending.quick_revive", id(180),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.PERK_QUICK_REVIVE));
		VENDING_QUICK_REVIVE_TOP = machine.clone().build("vending.quick_revive.top", id(181),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.MACHINE_TOP));
		WEAPON_UPGRADER = machine.clone().build("weapon_upgrader", id(187),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.UPGRADER));
		WEAPON_GIVER = machine.clone().build("weapon_giver", id(189),
			b -> new BlockLogicZombiesMachine(b, BlockLogicZombiesMachine.Kind.GIVER));
		WIREFRAME_DOOR = base.clone()
			.setHardness(2.0F)
			.setResistance(2.0F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.METAL)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("wireframe.door", id(188), BlockLogicWireframeDoor::new);

		BlockBuilder decorative = rock.clone()
			.setHardness(2.0F)
			.setBlockSound(AVBlockSounds.SPECIAL);
		DECORATIVE_BLOCK_1 = decorative.clone().build("decorative.1", id(195), b -> new BlockLogic(b, Materials.STONE));
		DECORATIVE_BLOCK_2 = decorative.clone().build("decorative.2", id(196), b -> new BlockLogic(b, Materials.STONE));
		SLATE_BEACON = rock.clone()
			.setLuminance(15)
			.setBlockSound(BlockSounds.METAL)
			.build("beacon.slate", id(204), b -> new BlockLogic(b, Materials.STONE));
		COAL_BRICK = rock.clone().build("brick.coal", id(240), b -> new BlockLogic(b, Materials.STONE));
	}

	private static void registerCraftedBlocks(BlockBuilder base, BlockBuilder rock, BlockBuilder wood, BlockBuilder glass) {
		GLASS_FORTIFIED = glass.clone()
			.setHardness(0.7F)
			.setResistance(1.17F)
			.build("glass.fortified", id(90), b -> new BlockLogicGlass(b, Materials.GLASS));

		GLASS_FORTIFIED_MAGENTA = glass.clone().build("glass.fortified.magenta", id(125), b -> new BlockLogicGlass(b, Materials.GLASS));
		GLASS_FORTIFIED_BLUE = glass.clone().build("glass.fortified.blue", id(126), b -> new BlockLogicGlass(b, Materials.GLASS));
		GLASS_FORTIFIED_GREEN = glass.clone().build("glass.fortified.green", id(127), b -> new BlockLogicGlass(b, Materials.GLASS));
		GLASS_FORTIFIED_BLACK = glass.clone().build("glass.fortified.black", id(128), b -> new BlockLogicGlass(b, Materials.GLASS));

		TILE = glass.clone()
			.setHardness(1.5F)
			.setResistance(2.5F)
			.build("tile", id(94), b -> new BlockLogicGlass(b, Materials.GLASS));
		STONE_TILE = rock.clone().build("tile.stone", id(92), b -> new BlockLogic(b, Materials.STONE));

		BlockBuilder cloth = base.clone()
			.setHardness(0.8F)
			.setResistance(1.33F)
			.setBlockSound(BlockSounds.CLOTH)
			.setTags(BlockTags.MINEABLE_BY_SHEARS);
		CLOTH_MAGENTA = cloth.clone().build("cloth.magenta", id(109), b -> new BlockLogic(b, Materials.CLOTH));
		CLOTH_BLUE = cloth.clone().build("cloth.blue", id(110), b -> new BlockLogic(b, Materials.CLOTH));
		CLOTH_GREEN = cloth.clone().build("cloth.green", id(111), b -> new BlockLogic(b, Materials.CLOTH));
		CLOTH_BLACK = cloth.clone().build("cloth.black", id(112), b -> new BlockLogic(b, Materials.CLOTH));

		BlockBuilder planks = wood.clone()
			.setHardness(1.5F)
			.setResistance(10.0F);
		PLANKS_FLAMEWOOD = planks.clone().build("planks.flamewood", id(130), b -> new BlockLogic(b, Materials.WOOD));
		PLANKS_HIGHWOOD = planks.clone()
			.setHardness(2.0F)
			.setResistance(5.0F)
			.build("planks.highwood", id(164), b -> new BlockLogic(b, Materials.WOOD));
		PLANKS_MYCON = planks.clone().build("planks.mycon", id(223), b -> new BlockLogic(b, Materials.WOOD));
		PLANKS_TEA = planks.clone().build("planks.tea", id(235), b -> new BlockLogic(b, Materials.WOOD));
		BOOKSHELF = wood.clone()
			.setHardness(1.5F)
			.setResistance(2.5F)
			.build("bookshelf", id(170), b -> new BlockLogicCypressNoDrop(b, Materials.WOOD));
		BOOKSHELF_EMPTY = wood.clone()
			.setHardness(1.5F)
			.setResistance(2.5F)
			.build("bookshelf.empty", id(171), b -> new BlockLogicCypressNoDrop(b, Materials.WOOD));

		CLOTH_PANEL = wood.clone()
			.setHardness(1.5F)
			.setResistance(2.5F)
			.build("cloth.panel", id(47), b -> new BlockLogicCypressNoDrop(b, Materials.WOOD));

		ESSENCE_CACHE = rock.clone()
			.setBlockSound(BlockSounds.METAL)
			.build("essence.cache", id(149), b -> new BlockLogic(b, Materials.STONE));
		LIMESTONE_SMOOTH = rock.clone().build("limestone.smooth", id(158), b -> new BlockLogic(b, Materials.STONE));
		BRICK_LICHEN = rock.clone()
			.setHardness(2.0F)
			.build("brick.lichen", id(183), b -> new BlockLogic(b, Materials.STONE));
		LICHEN_MASS = base.clone()
			.setHardness(2.0F)
			.setResistance(10.0F)
			.setBlockSound(BlockSounds.GRASS)
			.build("lichen.mass", id(184), b -> new BlockLogic(b, Materials.GRASS));

		BlockBuilder flameGlass = base.clone()
			.setHardness(1.0F)
			.setResistance(1.67F)
			.setLuminance(14)
			.setLightOpacity(0)
			.setBlockSound(AVBlockSounds.GLASS)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		FLAME_GLASS_SKY = flameGlass.clone().build("flame_glass.sky", id(186), b -> new BlockLogicCypressTransparent(b, Materials.STONE, true));
		FLAME_GLASS_GOLD = flameGlass.clone().build("flame_glass.gold", id(190), b -> new BlockLogicCypressTransparent(b, Materials.STONE, true));
		FLAME_GLASS_OBSIDIAN = flameGlass.clone().build("flame_glass.obsidian", id(191), b -> new BlockLogicCypressTransparent(b, Materials.STONE, true));
		FLAME_GLASS_LOW = flameGlass.clone().build("flame_glass.low", id(192), b -> new BlockLogicCypressTransparent(b, Materials.STONE, true));

		BlockBuilder metalBrick = rock.clone().setBlockSound(BlockSounds.METAL);
		BRICK_GOLD = metalBrick.clone().build("brick.gold", id(197), b -> new BlockLogic(b, Materials.STONE));
		BRICK_OBSIDIAN = metalBrick.clone().build("brick.obsidian", id(198), b -> new BlockLogic(b, Materials.STONE));
		BRICK_DIAMOND = metalBrick.clone().build("brick.diamond", id(199), b -> new BlockLogic(b, Materials.STONE));
		BRICK_IRON = metalBrick.clone().build("brick.iron", id(200), b -> new BlockLogic(b, Materials.STONE));

		BlockBuilder woodPillar = rock.clone().setBlockSound(BlockSounds.WOOD);
		PILLAR_HIGHWOOD = woodPillar.clone().build("pillar.highwood", id(202), b -> new BlockLogic(b, Materials.STONE));
		PILLAR_TEA = woodPillar.clone().build("pillar.tea", id(237), b -> new BlockLogic(b, Materials.STONE));
		PILLAR_MYCON = woodPillar.clone().build("pillar.mycon", id(239), b -> new BlockLogic(b, Materials.STONE));

		BRICK_SALT = rock.clone().build("brick.salt", id(222), b -> new BlockLogic(b, Materials.STONE));
		BLOCK_BISMUTH = rock.clone().build("block.bismuth", id(230), b -> new BlockLogic(b, Materials.STONE));

		PILLAR_BISMUTH = rock.clone().build("pillar.bismuth", id(231), b -> new BlockLogic(b, Materials.GRASS));
		BRICK_BISMUTH = rock.clone().build("brick.bismuth", id(232), b -> new BlockLogic(b, Materials.STONE));

		BRICK_SNOW = rock.clone()
			.setBlockSound(BlockSounds.CLOTH)
			.build("brick.snow", id(242), b -> new BlockLogic(b, Materials.STONE));

		BlockBuilder gemBlock = base.clone()
			.setHardness(3.0F)
			.setResistance(5.0F)
			.setLuminance(7)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.METAL)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		BLOCK_LACE_AGATE = gemBlock.clone().build("block.lace_agate", id(249), b -> new BlockLogicCypressTransparent(b, Materials.IRON, true));
		BLOCK_CLINOHUMITE = gemBlock.clone().build("block.clinohumite", id(250), b -> new BlockLogicCypressTransparent(b, Materials.IRON, true));
		BLOCK_MALACHITE = gemBlock.clone().build("block.malachite", id(251), b -> new BlockLogicCypressTransparent(b, Materials.IRON, true));
		BLOCK_PYRITE = gemBlock.clone().build("block.pyrite", id(252), b -> new BlockLogicCypressTransparent(b, Materials.IRON, true));

		registerDoorsAndWorkbenches(base, wood);
	}

	@SuppressWarnings("unchecked")
	private static void registerDoorsAndWorkbenches(BlockBuilder base, BlockBuilder wood) {
		BlockBuilder door = base.clone()
			.setHardness(3.0F)
			.setResistance(5.0F)
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.NOT_IN_CREATIVE_MENU, BlockTags.MINEABLE_BY_AXE);
		DOOR_FLAMEWOOD_BOTTOM = door.clone().build("door.flamewood.bottom", id(143),
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, false, () -> com.alphaver.item.AVItems.DOOR_FLAMEWOOD));
		DOOR_FLAMEWOOD_TOP = door.clone().build("door.flamewood.top", BASE_ID + 259,
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, true, () -> com.alphaver.item.AVItems.DOOR_FLAMEWOOD));
		DOOR_HIGHWOOD_BOTTOM = door.clone().build("door.highwood.bottom", id(224),
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, false, () -> com.alphaver.item.AVItems.DOOR_HIGHWOOD));
		DOOR_HIGHWOOD_TOP = door.clone().build("door.highwood.top", BASE_ID + 260,
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, true, () -> com.alphaver.item.AVItems.DOOR_HIGHWOOD));
		DOOR_MYCON_BOTTOM = door.clone().build("door.mycon.bottom", id(225),
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, false, () -> com.alphaver.item.AVItems.DOOR_MYCON));
		DOOR_MYCON_TOP = door.clone().build("door.mycon.top", BASE_ID + 261,
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, true, () -> com.alphaver.item.AVItems.DOOR_MYCON));
		DOOR_TEA_BOTTOM = door.clone().build("door.tea.bottom", id(236),
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, false, () -> com.alphaver.item.AVItems.DOOR_TEA));
		DOOR_TEA_TOP = door.clone().build("door.tea.top", BASE_ID + 262,
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, true, () -> com.alphaver.item.AVItems.DOOR_TEA));
		BlockBuilder iceDoor = door.clone()
			.setHardness(2.2F)
			.setResistance(0.5F)
			.setBlockSound(AVBlockSounds.GLASS);
		DOOR_ICE_BOTTOM = iceDoor.clone().build("door.ice.bottom", id(155),
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, false, () -> com.alphaver.item.AVItems.DOOR_ICE));
		DOOR_ICE_TOP = iceDoor.clone().build("door.ice.top", BASE_ID + 263,
			b -> new BlockLogicCypressDoor(b, Materials.WOOD, true, () -> com.alphaver.item.AVItems.DOOR_ICE));

		BlockBuilder workbench = wood.clone()
			.setHardness(1.5F)
			.setResistance(10.0F);
		WORKBENCH_MYCON = workbench.clone().build("workbench.mycon", id(226), BlockLogicWorkbench::new);
		WORKBENCH_HIGHWOOD = workbench.clone().build("workbench.highwood", id(227), BlockLogicWorkbench::new);
		WORKBENCH_FLAMEWOOD = workbench.clone().build("workbench.flamewood", id(228), BlockLogicWorkbench::new);

		WORKBENCH_TEA = workbench.clone().build("workbench.tea", id(238), BlockLogicWorkbench::new);

		registerMachines(base);
	}

	private static void registerMachines(BlockBuilder base) {
		BlockBuilder freezer = base.clone()
			.setHardness(3.5F)
			.setResistance(5.83F)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		FREEZER = freezer.clone()
			.build("freezer", id(243), b -> new com.alphaver.block.machine.BlockLogicFreezer(b, false));
		FREEZER_LIT = freezer.clone()
			.setTags(BlockTags.MINEABLE_BY_PICKAXE, BlockTags.NOT_IN_CREATIVE_MENU)
			.build("freezer.lit", id(244), b -> new com.alphaver.block.machine.BlockLogicFreezer(b, true));

		BlockBuilder essenceMachine = base.clone()
			.setHardness(1.5F)
			.setResistance(10.0F)
			.setBlockSound(BlockSounds.METAL)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE);
		ESSENCE_TRANSFORMER = essenceMachine.clone().build("essence.transformer", id(150),
			b -> new com.alphaver.block.machine.BlockLogicEssenceMachine(b, com.alphaver.block.machine.BlockLogicEssenceMachine.Kind.TRANSFORMER));
		ESSENCE_CLONER = essenceMachine.clone().build("essence.cloner", id(154),
			b -> new com.alphaver.block.machine.BlockLogicEssenceMachine(b, com.alphaver.block.machine.BlockLogicEssenceMachine.Kind.CLONER));

		ESSENCE_FOUNTAIN = base.clone()
			.setHardness(0.5F)
			.setResistance(0.5F)
			.setLuminance(7)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.STONE)
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("essence.fountain", id(156), com.alphaver.block.machine.BlockLogicEssenceFountain::new);
	}

	@SuppressWarnings("unchecked")
	public static void hideFromCreativeMenu() {
		int hidden = 0;
		for (Block<?> block : Blocks.blocksList) {
			if (block != null && AlphaVer.MOD_ID.equals(block.namespaceId().namespace())) {
				block.withTags(BlockTags.NOT_IN_CREATIVE_MENU);
				hidden++;
			}
		}
		AlphaVer.LOGGER.info("Hid {} AlphaVer blocks from the creative inventory.", hidden);
	}

	public static boolean isWorkbench(Block<?> block) {
		return block != null
			&& (block == WORKBENCH_MYCON || block == WORKBENCH_HIGHWOOD || block == WORKBENCH_FLAMEWOOD || block == WORKBENCH_TEA);
	}

	@SuppressWarnings("unchecked")
	public static void registerDoors() {

		BlockBuilder door = new BlockBuilder(AlphaVer.MOD_ID)
			.setHardness(-1.0F)
			.setResistance(6000000.0F)
			.setLightOpacity(0)
			.setBlockSound(BlockSounds.METAL);

		HUB_DOOR = door.clone()
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("door.hub", BASE_ID + 256,
				b -> new BlockLogicAlphaVerDoor(b, AVDimensions.HUB, BlockLogicAlphaVerDoor.Half.BOTH));
		CYPRESS_DOOR_LOWER = door.clone()
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.MINEABLE_BY_AXE)
			.build("door.cypress.lower", BASE_ID + 257,
				b -> new BlockLogicAlphaVerDoor(b, AVDimensions.CYPRESS, BlockLogicAlphaVerDoor.Half.LOWER));
		CYPRESS_DOOR_UPPER = door.clone()
			.setBlockSound(BlockSounds.WOOD)
			.setTags(BlockTags.MINEABLE_BY_AXE)
			.build("door.cypress.upper", BASE_ID + 258,
				b -> new BlockLogicAlphaVerDoor(b, AVDimensions.CYPRESS, BlockLogicAlphaVerDoor.Half.UPPER));

		ZOMBIES_DOOR = door.clone()
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("door.zombies", BASE_ID + 265, b -> new BlockLogicMinigameDoor(b, MinigameKind.ZOMBIES));
		FREERUN_DOOR = door.clone()
			.setTags(BlockTags.MINEABLE_BY_PICKAXE)
			.build("door.freerun", BASE_ID + 266, b -> new BlockLogicMinigameDoor(b, MinigameKind.FREERUN));

		AlphaVer.LOGGER.info("Registered the Hub, Cypress and minigame doors.");
	}
}
