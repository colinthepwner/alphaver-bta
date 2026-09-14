package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicFlower;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicCypressPlant extends BlockLogicFlower {

	public enum Soil {
		FLOWER,
		FLOATS,
		LOW_RIVER_STONE
	}

	private final Soil soil;

	public BlockLogicCypressPlant(@NotNull Block<?> block, @NotNull Soil soil) {
		super(block);
		this.soil = soil;
	}

	@Override
	protected boolean mayPlaceOn(@NotNull Block<?> below) {
		switch (this.soil) {
			case FLOATS:
				return true;
			case LOW_RIVER_STONE:
				return below == AVBlocks.LOW_RIVER_STONE || below == AVBlocks.LOW_RIVERBED;
			default:
				return super.mayPlaceOn(below);
		}
	}

	@Override
	public boolean canStay(@NotNull World world, @NotNull TilePosc tilePos) {
		if (this.soil == Soil.FLOWER) {
			return super.canStay(world, tilePos);
		}
		if (this.soil == Soil.FLOATS) {
			return true;
		}
		Block<?> below = world.getBlockType(tilePos.down(new TilePos()));
		return below != null && this.mayPlaceOn(below);
	}
}
