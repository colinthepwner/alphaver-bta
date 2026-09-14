package com.alphaver.world.minigame.map;

import com.alphaver.AlphaVer;
import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TreeMap;

public final class MapBlockPalette {
	private MapBlockPalette() {}

	private static final int KEEP = -1;

	private static final int LEAVES_PERMANENT = 1;

	private static int[] ids;
	private static int[] data;
	private static final Map<Integer, Integer> UNKNOWN = new TreeMap<>();

	public static synchronized int convert(int alphaId, int alphaData) {
		if (ids == null) {
			build();
		}
		alphaId &= 255;
		alphaData &= 15;
		switch (alphaId) {
			case 0, 51, 52 -> {
				return 0;
			}
			case 64 -> {
				return door(Blocks.DOOR_PLANKS_OAK_BOTTOM, Blocks.DOOR_PLANKS_OAK_TOP, alphaData);
			}
			case 71 -> {
				return door(Blocks.DOOR_IRON_BOTTOM, Blocks.DOOR_IRON_TOP, alphaData);
			}
			case 143 -> {
				return door(AVBlocks.DOOR_FLAMEWOOD_BOTTOM, AVBlocks.DOOR_FLAMEWOOD_TOP, alphaData);
			}
			case 155 -> {
				return door(AVBlocks.DOOR_ICE_BOTTOM, AVBlocks.DOOR_ICE_TOP, alphaData);
			}
			case 224 -> {
				return door(AVBlocks.DOOR_HIGHWOOD_BOTTOM, AVBlocks.DOOR_HIGHWOOD_TOP, alphaData);
			}
			case 225 -> {
				return door(AVBlocks.DOOR_MYCON_BOTTOM, AVBlocks.DOOR_MYCON_TOP, alphaData);
			}
			case 236 -> {
				return door(AVBlocks.DOOR_TEA_BOTTOM, AVBlocks.DOOR_TEA_TOP, alphaData);
			}
			default -> {
			}
		}
		int id = ids[alphaId];
		if (id == 0) {
			UNKNOWN.merge(alphaId, 1, Integer::sum);
			return 0;
		}
		int value = data[alphaId] == KEEP ? alphaData : data[alphaId];
		return id << 8 | (value & 255);
	}

	public static synchronized void reportUnknown(String what) {
		if (!UNKNOWN.isEmpty()) {
			AlphaVer.LOGGER.warn("Converting {}: no block for Cypress ids {} (block count by id); they became air.", what, UNKNOWN);
			UNKNOWN.clear();
		}
	}

	private static int door(@Nullable Block<?> bottom, @Nullable Block<?> top, int alphaData) {
		if (bottom == null || top == null) {
			return 0;
		}
		return ((alphaData & 8) != 0 ? top.id() : bottom.id()) << 8 | (alphaData & 7);
	}

	private static void build() {
		ids = new int[256];
		data = new int[256];
		vanilla(1, Blocks.STONE, 0);
		vanilla(2, Blocks.GRASS_RETRO, 0);
		vanilla(3, Blocks.DIRT, 0);
		vanilla(4, Blocks.COBBLE_STONE, 0);
		vanilla(5, Blocks.PLANKS_OAK, 0);
		vanilla(6, Blocks.SAPLING_OAK_RETRO, 0);
		vanilla(7, Blocks.BEDROCK, 0);
		vanilla(8, Blocks.FLUID_WATER_FLOWING, KEEP);
		vanilla(9, Blocks.FLUID_WATER_STILL, KEEP);
		vanilla(10, Blocks.FLUID_LAVA_FLOWING, KEEP);
		vanilla(11, Blocks.FLUID_LAVA_STILL, KEEP);
		vanilla(12, Blocks.SAND, 0);
		vanilla(13, Blocks.GRAVEL, 0);
		vanilla(14, Blocks.ORE_GOLD_STONE, 0);
		vanilla(15, Blocks.ORE_IRON_STONE, 0);
		vanilla(16, Blocks.ORE_COAL_STONE, 0);
		vanilla(17, Blocks.LOG_OAK, 0);
		vanilla(18, Blocks.LEAVES_OAK_RETRO, LEAVES_PERMANENT);
		vanilla(20, Blocks.GLASS, 0);

		vanilla(35, Blocks.WOOL, 0);
		vanilla(37, Blocks.FLOWER_YELLOW, 0);
		vanilla(38, Blocks.FLOWER_RED, 0);
		vanilla(39, Blocks.MUSHROOM_BROWN, 0);
		vanilla(40, Blocks.MUSHROOM_RED, 0);
		vanilla(41, Blocks.BLOCK_GOLD, 0);
		vanilla(42, Blocks.BLOCK_IRON, 0);

		vanilla(43, Blocks.SLAB_STONE_POLISHED, 1);
		vanilla(44, Blocks.SLAB_STONE_POLISHED, 0);
		vanilla(45, Blocks.BRICK_CLAY, 0);
		vanilla(46, Blocks.TNT, 0);
		vanilla(48, Blocks.COBBLE_STONE_MOSSY, 0);
		vanilla(49, Blocks.OBSIDIAN, 0);

		vanilla(50, Blocks.TORCH_COAL, KEEP);
		vanilla(53, Blocks.STAIRS_PLANKS_OAK, KEEP);

		vanilla(54, Blocks.CHEST_LEGACY, 0);
		vanilla(56, Blocks.ORE_DIAMOND_STONE, 0);
		vanilla(57, Blocks.BLOCK_DIAMOND, 0);
		vanilla(58, Blocks.WORKBENCH, 0);
		vanilla(59, Blocks.CROPS_WHEAT, KEEP);
		vanilla(60, Blocks.FARMLAND_DIRT, 0);

		vanilla(61, Blocks.FURNACE_STONE_IDLE, KEEP);
		vanilla(62, Blocks.FURNACE_STONE_ACTIVE, KEEP);
		vanilla(63, Blocks.SIGN_POST_PLANKS_OAK, KEEP);
		vanilla(65, Blocks.LADDER_OAK, KEEP);
		vanilla(66, Blocks.RAIL, KEEP);
		vanilla(67, Blocks.STAIRS_COBBLE_STONE, KEEP);
		vanilla(68, Blocks.SIGN_WALL_PLANKS_OAK, KEEP);
		vanilla(69, Blocks.LEVER_COBBLE_STONE, KEEP);
		vanilla(70, Blocks.PRESSURE_PLATE_STONE, 0);
		vanilla(72, Blocks.PRESSURE_PLATE_PLANKS_OAK, 0);
		vanilla(77, Blocks.BUTTON_STONE, KEEP);
		vanilla(78, Blocks.LAYER_SNOW, 0);
		vanilla(79, Blocks.ICE, 0);
		vanilla(80, Blocks.BLOCK_SNOW, 0);
		vanilla(81, Blocks.CACTUS, 0);
		vanilla(82, Blocks.BLOCK_CLAY, 0);
		vanilla(83, Blocks.SUGARCANE, 0);
		vanilla(84, Blocks.JUKEBOX, 0);
		vanilla(85, Blocks.FENCE_PLANKS_OAK, 0);

		int[] reused = {47, 55, 73, 74, 75, 76};
		for (int alphaId : reused) {
			alphaVer(alphaId);
		}
		for (int alphaId = 86; alphaId < 256; alphaId++) {
			alphaVer(alphaId);
		}

		data[55] = 0;
	}

	private static void vanilla(int alphaId, @Nullable Block<?> block, int fixedData) {
		if (block != null) {
			ids[alphaId] = block.id();
			data[alphaId] = fixedData;
		}
	}

	private static void alphaVer(int alphaId) {
		Block<?> block = Blocks.getBlock(AVBlocks.BASE_ID + alphaId);
		if (block != null) {
			ids[alphaId] = block.id();
			data[alphaId] = KEEP;
		}
	}
}
