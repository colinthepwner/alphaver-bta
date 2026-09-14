package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BlockLogicCypressOre extends BlockLogic {
	private final Supplier<? extends IItemConvertible> drop;
	private final int count;
	private final int extra;

	public BlockLogicCypressOre(@NotNull Block<?> block, @NotNull Supplier<? extends IItemConvertible> drop, int count, int extra) {
		super(block, Materials.STONE);
		this.drop = drop;
		this.count = count;
		this.extra = extra;
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		return switch (dropCause) {
			case SILK_TOUCH, PICK_BLOCK -> new ItemStack[]{new ItemStack(this.block)};
			case EXPLOSION, PROPER_TOOL, PISTON_CRUSH -> new ItemStack[]{
				new ItemStack(this.drop.get(), this.count + (this.extra > 0 ? world.rand.nextInt(this.extra) : 0))};
			default -> null;
		};
	}
}
