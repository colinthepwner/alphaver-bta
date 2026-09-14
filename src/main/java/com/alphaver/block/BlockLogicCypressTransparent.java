package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicTransparent;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicCypressTransparent extends BlockLogicTransparent {
	private final boolean dropsItself;

	public BlockLogicCypressTransparent(@NotNull Block<?> block, @NotNull Material material, boolean dropsItself) {
		super(block, material);
		this.dropsItself = dropsItself;
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		if (dropCause == EnumDropCause.PICK_BLOCK || dropCause == EnumDropCause.SILK_TOUCH) {
			return new ItemStack[]{new ItemStack(this.block)};
		}
		return this.dropsItself ? super.getBreakResult(world, dropCause, data, tileEntity) : null;
	}
}
