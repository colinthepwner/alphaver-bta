package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

public class BlockLogicCypressPlate extends BlockLogic {

	private static final double THICKNESS = 0.125;

	public BlockLogicCypressPlate(@NotNull Block<?> block) {
		super(block, Materials.DECORATION);
	}

	public static int wallMeta(int attachDx, int attachDz) {
		if (attachDz > 0) {
			return 2;
		}
		if (attachDz < 0) {
			return 3;
		}
		return attachDx > 0 ? 4 : 5;
	}

	@NotNull
	@Override
	public AABBdc getBoundsFromState(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		double t = THICKNESS;
		return switch (source.getBlockData(tilePos) & 7) {
			case 2 -> new AABBd(0.0, 0.0, 1.0 - t, 1.0, 1.0, 1.0);
			case 3 -> new AABBd(0.0, 0.0, 0.0, 1.0, 1.0, t);
			case 4 -> new AABBd(1.0 - t, 0.0, 0.0, 1.0, 1.0, 1.0);
			case 5 -> new AABBd(0.0, 0.0, 0.0, t, 1.0, 1.0);
			case 6 -> new AABBd(0.0, 1.0 - t, 0.0, 1.0, 1.0, 1.0);
			default -> new AABBd(0.0, 0.0, 0.0, 1.0, t, 1.0);
		};
	}

	@Nullable
	@Override
	public AABBdc getCollisionAABB(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		return null;
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean isCubeShaped() {
		return false;
	}
}
