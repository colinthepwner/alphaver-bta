package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicDoor;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.item.Item;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BlockLogicCypressDoor extends BlockLogicDoor {

	public BlockLogicCypressDoor(@NotNull Block<?> block, @NotNull Material material, boolean isTop, @NotNull Supplier<Item> droppedItem) {
		super(block, material, isTop, false, droppedItem);
	}

	@Override
	public boolean canBePainted() {
		return false;
	}
}
