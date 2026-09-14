package com.alphaver.block;

import com.alphaver.world.AVDimensions;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;
import org.joml.primitives.AABBdc;

import java.util.Random;

public class BlockLogicAlphaVerDoor extends BlockLogicPortal {

	public static final int AXIS_X = 1;
	public static final int UPPER = 2;

	public static final int FAR = 4;

	private static final double THICKNESS = 3.0 / 16.0;

	public enum Half {

		BOTH,
		LOWER,
		UPPER
	}

	private final Half half;

	public BlockLogicAlphaVerDoor(@NotNull Block<?> block, @NotNull Dimension target, @NotNull Half half) {

		super(block, target, Blocks.STONE, Blocks.AIR);
		this.half = half;
	}

	@NotNull
	@Override
	public AABBdc getBoundsFromState(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		int data = source.getBlockData(tilePos);
		double min;
		double max;
		if (this.half == Half.BOTH) {

			boolean far = (data & FAR) != 0;
			min = far ? 1.0 - THICKNESS : 0.0;
			max = far ? 1.0 : THICKNESS;
		} else {
			min = 0.5 - THICKNESS / 2.0;
			max = 0.5 + THICKNESS / 2.0;
		}
		if ((data & AXIS_X) != 0) {
			return new AABBd(min, 0.0, 0.0, max, 1.0, 1.0);
		}
		return new AABBd(0.0, 0.0, min, 1.0, 1.0, max);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
		boolean upper = this.isUpper(world.getBlockData(tilePos));
		TilePos other = upper ? tilePos.down(new TilePos()) : tilePos.up(new TilePos());
		if (world.getBlockType(other) != this.partner(upper)) {
			world.setBlockTypeNotify(tilePos, Blocks.AIR);
		}
	}

	@Override
	public void onEntityCollision(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Entity entity) {
		if (!(entity instanceof Player player)) {
			return;
		}
		if (player.timeUntilPortal > 0) {

			player.handlePortal(this.block.id(), null);
			return;
		}
		if (world.dimension == Dimension.OVERWORLD && this.targetDimension == AVDimensions.HUB) {
			int data = world.getBlockData(tilePos);
			int lowerY = this.isUpper(data) ? tilePos.y() - 1 : tilePos.y();
			AVTravel.recordReturn(player, tilePos.x(), lowerY, tilePos.z(), (data & AXIS_X) != 0);
		}
		player.timeInPortal = 1.0F;
		player.handlePortal(this.block.id(), null);
	}

	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {

	}

	@Override
	public boolean canBePainted() {
		return false;
	}

	@Override
	public void setColor(@NotNull World world, @NotNull TilePosc tilePos, @NotNull DyeColor color) {

	}

	@Override
	public void removeDye(@NotNull World world, @NotNull TilePosc tilePos) {

	}

	private boolean isUpper(int data) {
		return switch (this.half) {
			case LOWER -> false;
			case UPPER -> true;
			default -> (data & UPPER) != 0;
		};
	}

	private Block<?> partner(boolean upper) {
		return switch (this.half) {
			case LOWER -> AVBlocks.CYPRESS_DOOR_UPPER;
			case UPPER -> AVBlocks.CYPRESS_DOOR_LOWER;
			default -> this.block;
		};
	}
}
