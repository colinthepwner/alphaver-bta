package com.alphaver.item;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class ItemEraser extends ItemToolPickaxe {

	private static final float STRENGTH = 1.0E6F;
	private static final int DAMAGE = Short.MAX_VALUE;

	public ItemEraser(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id, new ToolMaterial().setDurability(0).setEfficiency(STRENGTH, STRENGTH).setMiningLevel(4));
	}

	@Override
	public boolean canHarvestBlock(@NotNull ItemStack selfStack, @NotNull Mob mob, @NotNull Block<?> block) {
		return true;
	}

	@Override
	public float getStrVsBlock(@NotNull ItemStack selfStack, @NotNull Block<?> block) {
		return STRENGTH;
	}

	@Override
	public int getDamageVsEntity(@NotNull ItemStack selfStack, @NotNull Entity entity) {
		return DAMAGE;
	}

	@Override
	public boolean hitEntity(@NotNull ItemStack selfStack, @NotNull Mob target, @NotNull Mob attacker) {
		return true;
	}

	@Override
	public boolean onBlockDestroyed(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Mob mob,
	                                @NotNull Block<?> removedBlock, @NotNull TilePosc blockPos, @NotNull Side side) {
		return true;
	}
}
