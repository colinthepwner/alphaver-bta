package com.alphaver.block.machine;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicRotatable;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Direction;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockLogicFreezer extends BlockLogicRotatable {

	public static final String SNOW_PARTICLE = "alphaver:snowflake";

	private final boolean working;

	public BlockLogicFreezer(@NotNull Block<?> block, boolean working) {
		super(block, Materials.STONE);
		this.working = working;
		block.withEntity(TileEntityFreezer::new);
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		if (AVBlocks.FREEZER == null) {
			return null;
		}
		return switch (dropCause) {
			case PICK_BLOCK, EXPLOSION, PROPER_TOOL, SILK_TOUCH, PISTON_CRUSH -> new ItemStack[]{new ItemStack(AVBlocks.FREEZER)};
			default -> null;
		};
	}

	@Override
	public void onPlacedByWorld(@NotNull World world, @NotNull TilePosc tilePos) {
		if (!this.working) {
			faceAwayFromWall(world, tilePos);
		}
	}

	@Override
	public void onPlacedByMob(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, @NotNull Mob mob, double xHit, double yHit) {
		faceAwayFromWall(world, tilePos);
	}

	@Override
	public void onPlacedOnSide(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side, double xHit, double yHit) {
		faceAwayFromWall(world, tilePos);
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side,
	                            double xHit, double yHit) {
		if (!world.isClientSide && world.getTileEntity(tilePos) instanceof TileEntityFreezer) {
			AVScreens.open(player, MachineKind.FREEZER, world, tilePos);
		}
		return true;
	}

	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {
		if (!this.working) {
			return;
		}
		double x = tilePos.x() + 0.5;
		double y = tilePos.y() + rand.nextFloat() * 6.0F / 16.0F;
		double z = tilePos.z() + 0.5;
		double out = 0.52;
		double along = rand.nextFloat() * 0.6F - 0.3F;

		int flake = rand.nextInt(3) + 1;
		switch (getDirectionFromMeta(world.getBlockData(tilePos))) {
			case WEST -> world.spawnParticle(SNOW_PARTICLE, x - out, y, z + along, 0.0, -0.01, 0.0, flake, false);
			case EAST -> world.spawnParticle(SNOW_PARTICLE, x + out, y, z + along, 0.0, -0.01, 0.0, flake, false);
			case NORTH -> world.spawnParticle(SNOW_PARTICLE, x + along, y, z - out, 0.0, -0.01, 0.0, flake, false);
			case SOUTH -> world.spawnParticle(SNOW_PARTICLE, x + along, y, z + out, 0.0, -0.01, 0.0, flake, false);
			default -> {
			}
		}
	}

	public static void setWorking(@NotNull World world, @NotNull TilePosc tilePos, boolean working) {
		Block<?> target = working ? AVBlocks.FREEZER_LIT : AVBlocks.FREEZER;
		if (target == null || world.getBlockType(tilePos) == target || !(world.getTileEntity(tilePos) instanceof TileEntityFreezer)) {
			return;
		}
		int meta = world.getBlockData(tilePos);
		world.setBlockTypeDataRaw(tilePos, target, meta);
		world.notifyBlockChange(tilePos, target);
	}

	private static void faceAwayFromWall(@NotNull World world, @NotNull TilePosc tilePos) {
		if (world.isClientSide) {
			return;
		}
		TilePos query = new TilePos();
		boolean north = world.getBlockType(tilePos.north(query)).solid();
		boolean south = world.getBlockType(tilePos.south(query)).solid();
		boolean west = world.getBlockType(tilePos.west(query)).solid();
		boolean east = world.getBlockType(tilePos.east(query)).solid();
		Direction facing = Direction.SOUTH;
		if (south && !north) {
			facing = Direction.NORTH;
		}
		if (west && !east) {
			facing = Direction.EAST;
		}
		if (east && !west) {
			facing = Direction.WEST;
		}
		world.setBlockDataNotify(tilePos, setDirection(world.getBlockData(tilePos), facing));
	}
}
