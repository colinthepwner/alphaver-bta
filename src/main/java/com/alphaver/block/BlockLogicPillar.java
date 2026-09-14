package com.alphaver.block;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicPillar extends BlockLogic {

	public BlockLogicPillar(@NotNull Block<?> block) {
		super(block, Materials.WOOD);
	}

	@Override
	public boolean collidesWithEntity(@NotNull Entity entity, @NotNull World world, @NotNull TilePosc tilePos) {
		return !(entity instanceof MobZombie && AVWorlds.isZombies(world));
	}
}
