package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.block.ItemBlock;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;

public class ItemBlockLily<T extends BlockLogic> extends ItemBlock<T> {

	public ItemBlockLily(Block<T> block) {
		super(block);
	}

	@Override
	public boolean interactsWithFluid(ItemStack stack) {
		return true;
	}

	@Override
	public boolean onUseOnBlock(ItemStack stack, World world, Player player, TilePosc tilePos, Side side, double xPlaced,
	                            double yPlaced) {
		if (isStillWater(world, tilePos)) {
			TilePos above = tilePos.up(new TilePos());
			return world.getBlockId(above.x(), above.y(), above.z()) == 0
				&& this.placeWithoutShift(stack, world, player, above, Side.TOP, 0.5, 0.5);
		}
		return super.onUseOnBlock(stack, world, player, tilePos, side, xPlaced, yPlaced);
	}

	private static boolean isStillWater(World world, TilePosc tilePos) {
		Block<?> block = world.getBlockType(tilePos);
		return block == Blocks.FLUID_WATER_STILL || block == Blocks.FLUID_WATER_FLOWING && world.getBlockData(tilePos) == 0;
	}
}
