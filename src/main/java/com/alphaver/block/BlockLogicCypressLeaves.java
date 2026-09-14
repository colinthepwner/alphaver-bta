package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockLogicCypressLeaves extends BlockLogicLeavesBase {

	public enum Kind {
		FLAMEWOOD,
		TEA
	}

	private final Kind kind;

	public BlockLogicCypressLeaves(@NotNull Block<?> block, @NotNull Kind kind) {
		super(block, Materials.LEAVES, Blocks.SAPLING_OAK);
		this.kind = kind;
	}

	@Override
	public void updateTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand, boolean isRandomTick) {

	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		if (dropCause == EnumDropCause.PICK_BLOCK || dropCause == EnumDropCause.SILK_TOUCH) {
			return new ItemStack[]{new ItemStack(this.block)};
		}
		return CypressLeafDrops.roll(world.rand, Blocks.SAPLING_OAK, this.kind == Kind.TEA);
	}
}
