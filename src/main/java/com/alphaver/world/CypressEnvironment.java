package com.alphaver.world;

import com.alphaver.AVConfig;
import com.alphaver.world.gen.CypressWorldRules;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class CypressEnvironment {
	private CypressEnvironment() {}

	private static final int FLOOD_BELOW = 66;
	private static final int FLOOD_MAX_LIGHT = 8;
	private static final int CLAY_MAX_LIGHT = 10;

	public static void tick(@NotNull World world, @NotNull Random rand, int x, int z, boolean raining) {
		if (CypressWorldRules.isSandWorld(world.getRandomSeed()) && rand.nextInt(4) == 1) {

			clay(world, rand, (x >> 4 << 4) + rand.nextInt(16), (z >> 4 << 4) + rand.nextInt(16));
		}
		if (raining && AVConfig.RAIN_FLOODING) {
			flood(world, x, z);
		}
	}

	@SuppressWarnings("deprecation")
	private static void clay(World world, Random rand, int x, int z) {
		int y = aboveTopSolidOrLiquid(world, x, z);
		if (y < 1 || y >= world.getHeightBlocks() || blockLight(world, x, y, z) >= CLAY_MAX_LIGHT) {
			return;
		}
		if (world.getBlockId(x, y - 1, z) == Blocks.FLUID_WATER_STILL.id() && world.getBlockMetadata(x, y - 1, z) == 0
			&& rand.nextInt(16) == 0) {
			world.setBlockWithNotify(x, y - 1, z, Blocks.BLOCK_CLAY.id());
		}
	}

	@SuppressWarnings("deprecation")
	private static void flood(World world, int x, int z) {
		int y = aboveTopSolidOrLiquid(world, x, z);
		if (y < 0 || y >= FLOOD_BELOW || y >= world.getHeightBlocks() || blockLight(world, x, y, z) >= FLOOD_MAX_LIGHT) {
			return;
		}
		if (world.getBlockId(x, y, z) == 0) {
			world.setBlockWithNotify(x, y, z, Blocks.FLUID_WATER_STILL.id());
		}
	}

	private static int aboveTopSolidOrLiquid(World world, int x, int z) {
		for (int y = Math.min(world.getHeightValue(x, z) + 1, world.getHeightBlocks() - 1); y > 0; y--) {
			Material below = world.getBlockMaterial(x, y - 1, z);
			if (below.isSolid() || below.isLiquid()) {
				return y;
			}
		}
		return -1;
	}

	private static int blockLight(World world, int x, int y, int z) {
		return world.getSavedLightValue(LightLayer.Block, new TilePos(x, y, z));
	}
}
