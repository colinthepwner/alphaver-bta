package com.alphaver.item;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import org.jetbrains.annotations.NotNull;

public class ItemObsidianPickaxe extends ItemToolPickaxe {

	public ItemObsidianPickaxe(@NotNull String name, @NotNull String namespaceId, int id, @NotNull ToolMaterial material) {
		super(name, namespaceId, id, material);
	}

	@Override
	public boolean canHarvestBlock(@NotNull ItemStack selfStack, @NotNull Mob mob, @NotNull Block<?> block) {
		if (block == Blocks.BEDROCK) {
			return AVWorlds.isCypress(mob.world);
		}
		return super.canHarvestBlock(selfStack, mob, block);
	}
}
