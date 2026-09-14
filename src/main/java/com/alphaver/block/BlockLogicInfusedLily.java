package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicInfusedLily extends BlockLogicCypressPlant {

	private static final int FIRE_TICKS = 300;

	private final int power;

	public BlockLogicInfusedLily(@NotNull Block<?> block, int power) {
		super(block, Soil.FLOATS);
		this.power = power;
	}

	@Override
	public void onEntityCollision(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Entity entity) {
		if (!world.isClientSide && entity instanceof MobMonster monster) {
			monster.hurt(null, 2 * this.power, DamageType.COMBAT);
			monster.remainingFireTicks = FIRE_TICKS;
		}
	}
}
