package com.alphaver.block.machine;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicEssenceMachine extends BlockLogic {

	public enum Kind {
		TRANSFORMER,
		CLONER
	}

	private final Kind kind;

	public BlockLogicEssenceMachine(@NotNull Block<?> block, @NotNull Kind kind) {
		super(block, Materials.GRASS);
		this.kind = kind;
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side,
	                            double xHit, double yHit) {
		if (!world.isClientSide) {
			AVScreens.open(player, this.kind == Kind.TRANSFORMER ? MachineKind.TRANSFORMER : MachineKind.CLONER, world, tilePos);
		}
		return true;
	}
}
