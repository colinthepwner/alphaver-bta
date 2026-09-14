package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicFrigidTrunk extends BlockLogic {

	public BlockLogicFrigidTrunk(@NotNull Block<?> block) {
		super(block, Materials.WOOD);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean canPlaceAt(@NotNull World world, @NotNull TilePosc tilePos) {
		return super.canPlaceAt(world, tilePos) && world.getBlockType(tilePos.down(new TilePos())) == Blocks.BLOCK_SNOW;
	}
}
