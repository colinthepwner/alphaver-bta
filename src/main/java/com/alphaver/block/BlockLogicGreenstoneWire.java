package com.alphaver.block;

import com.alphaver.item.AVItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.primitives.AABBdc;

import java.util.Random;

public class BlockLogicGreenstoneWire extends BlockLogic {

	public static final String DUST_PARTICLE = "alphaver:greenstone_dust";

	public static final int WEST = 1;
	public static final int EAST = 2;
	public static final int NORTH = 4;
	public static final int SOUTH = 8;

	public static final int CLIMB_SHIFT = 4;

	private static final int[] DX = {-1, 1, 0, 0};
	private static final int[] DZ = {0, 0, -1, 1};

	private boolean wiresProvidePower = true;

	public BlockLogicGreenstoneWire(@NotNull Block<?> block) {
		super(block, Materials.DECORATION);
		this.setBlockBounds(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
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

	@Override
	public boolean canPlaceAt(@NotNull World world, @NotNull TilePosc tilePos) {
		return world.isBlockNormalCube(tilePos.down(new TilePos()));
	}

	@Override
	public void onPlacedByWorld(@NotNull World world, @NotNull TilePosc tilePos) {
		super.onPlacedByWorld(world, tilePos);
		if (world.isClientSide) {
			return;
		}
		this.updateAndPropagate(world, tilePos);
		TilePos query = new TilePos();
		world.notifyBlocksOfNeighborChange(tilePos.up(query), this.block);
		world.notifyBlocksOfNeighborChange(tilePos.down(query), this.block);
		this.notifyWiresAround(world, tilePos);
	}

	@Override
	public void onRemoved(@NotNull World world, @NotNull TilePosc tilePos, int data) {
		super.onRemoved(world, tilePos, data);
		if (world.isClientSide) {
			return;
		}
		TilePos query = new TilePos();
		world.notifyBlocksOfNeighborChange(tilePos.up(query), this.block);
		world.notifyBlocksOfNeighborChange(tilePos.down(query), this.block);
		this.updateAndPropagate(world, tilePos);
		this.notifyWiresAround(world, tilePos);
	}

	@Override
	public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
		if (world.isClientSide) {
			return;
		}
		if (!this.canPlaceAt(world, tilePos)) {
			this.dropWithCause(world, EnumDropCause.WORLD, tilePos, world.getBlockData(tilePos), null, null);
			world.setBlockTypeNotify(tilePos, Blocks.AIR);
		} else {
			this.updateAndPropagate(world, tilePos);
		}
		super.onNeighborChanged(world, tilePos, block);
	}

	private void updateAndPropagate(World world, TilePosc tilePos) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		boolean isWire = world.getBlockType(tilePos) == this.block;
		int old = world.getBlockData(tilePos);
		int strength = 0;

		this.wiresProvidePower = false;
		boolean powered = receivesSignal(world, tilePos);
		this.wiresProvidePower = true;

		TilePos query = new TilePos();
		if (powered) {
			strength = 15;
		} else {
			for (int n = 0; n < 4; n++) {
				int nx = x + DX[n];
				int nz = z + DZ[n];
				strength = this.strongest(world, query.set(nx, y, nz), strength);
				boolean sideSolid = world.isBlockNormalCube(query.set(nx, y, nz));
				if (sideSolid && !world.isBlockNormalCube(query.set(x, y + 1, z))) {
					strength = this.strongest(world, query.set(nx, y + 1, nz), strength);
				} else if (!sideSolid) {
					strength = this.strongest(world, query.set(nx, y - 1, nz), strength);
				}
			}
			strength = strength > 0 ? strength - 1 : 0;
		}

		if (old == strength) {
			return;
		}
		if (isWire) {
			world.setBlockDataNotify(tilePos, strength);
			world.markBlocksDirty(tilePos, tilePos);
		}
		int passed = strength > 0 ? strength - 1 : 0;
		for (int n = 0; n < 4; n++) {
			int nx = x + DX[n];
			int nz = z + DZ[n];
			int ny = world.isBlockNormalCube(query.set(nx, y, nz)) ? y + 1 : y - 1;
			int level = this.strongest(world, query.set(nx, y, nz), -1);
			if (level >= 0 && level != passed) {
				this.updateAndPropagate(world, new TilePos(nx, y, nz));
			}
			level = this.strongest(world, query.set(nx, ny, nz), -1);
			if (level >= 0 && level != passed) {
				this.updateAndPropagate(world, new TilePos(nx, ny, nz));
			}
		}
		if (old == 0 || strength == 0) {
			world.notifyBlocksOfNeighborChange(tilePos, this.block);
			for (int n = 0; n < 4; n++) {
				world.notifyBlocksOfNeighborChange(query.set(x + DX[n], y, z + DZ[n]), this.block);
			}
			world.notifyBlocksOfNeighborChange(query.set(x, y - 1, z), this.block);
			world.notifyBlocksOfNeighborChange(query.set(x, y + 1, z), this.block);
		}
	}

	private int strongest(World world, TilePosc tilePos, int current) {
		if (world.getBlockType(tilePos) != this.block) {
			return current;
		}
		return Math.max(world.getBlockData(tilePos), current);
	}

	private static boolean receivesSignal(World world, TilePosc tilePos) {
		TilePos query = new TilePos();
		for (Side side : Side.sides) {
			tilePos.add(side.direction(), query);
			if (world.getBlockType(query) == Blocks.WIRE_REDSTONE) {
				continue;
			}
			if (world.hasSignal(query, side)) {
				return true;
			}
		}
		return false;
	}

	private void notifyWiresAround(World world, TilePosc tilePos) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		TilePos query = new TilePos();
		for (int n = 0; n < 4; n++) {
			int nx = x + DX[n];
			int nz = z + DZ[n];
			this.notifyAroundWire(world, nx, y, nz);
			this.notifyAroundWire(world, nx, world.isBlockNormalCube(query.set(nx, y, nz)) ? y + 1 : y - 1, nz);
		}
	}

	private void notifyAroundWire(World world, int x, int y, int z) {
		TilePos pos = new TilePos(x, y, z);
		if (world.getBlockType(pos) != this.block) {
			return;
		}
		world.notifyBlocksOfNeighborChange(pos, this.block);
		TilePos query = new TilePos();
		for (int n = 0; n < 4; n++) {
			world.notifyBlocksOfNeighborChange(query.set(x + DX[n], y, z + DZ[n]), this.block);
		}
		world.notifyBlocksOfNeighborChange(query.set(x, y - 1, z), this.block);
		world.notifyBlocksOfNeighborChange(query.set(x, y + 1, z), this.block);
	}

	@Override
	public boolean isSignalSource() {
		return this.wiresProvidePower;
	}

	@Override
	public boolean isEmittingDirectSignal(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side) {
		return this.wiresProvidePower && this.isEmittingSignal(world, tilePos, side);
	}

	@Override
	public boolean isEmittingSignal(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
		if (!this.wiresProvidePower || source.getBlockData(tilePos) == 0) {
			return false;
		}
		if (source.getBlockType(tilePos.add(side.opposite().direction(), new TilePos())) == Blocks.WIRE_REDSTONE) {
			return false;
		}
		if (side == Side.TOP) {
			return true;
		}
		if (side == Side.BOTTOM || side == Side.NONE) {
			return false;
		}
		int all = connections(source, tilePos);
		int links = all | all >> CLIMB_SHIFT;
		boolean west = (links & WEST) != 0;
		boolean east = (links & EAST) != 0;
		boolean north = (links & NORTH) != 0;
		boolean south = (links & SOUTH) != 0;
		if (!west && !east && !north && !south) {
			return true;
		}
		return switch (side) {
			case NORTH -> north && !west && !east;
			case SOUTH -> south && !west && !east;
			case WEST -> west && !north && !south;
			case EAST -> east && !north && !south;
			default -> false;
		};
	}

	public static int connections(WorldSource source, TilePosc tilePos) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		TilePos query = new TilePos();
		int links = 0;
		boolean coveredAbove = source.isBlockNormalCube(query.set(x, y + 1, z));
		for (int n = 0; n < 4; n++) {
			int nx = x + DX[n];
			int nz = z + DZ[n];
			int bit = 1 << n;
			boolean sideSolid = source.isBlockNormalCube(query.set(nx, y, nz));
			if (joins(source, query.set(nx, y, nz)) || !sideSolid && joins(source, query.set(nx, y - 1, nz))) {
				links |= bit;
			}
			if (!coveredAbove && sideSolid && joins(source, query.set(nx, y + 1, nz))) {
				links |= bit << CLIMB_SHIFT;
			}
		}
		return links;
	}

	private static boolean joins(WorldSource source, TilePosc tilePos) {
		Block<?> block = source.getBlockType(tilePos);
		if (block == null || block == Blocks.AIR || block == Blocks.WIRE_REDSTONE) {
			return false;
		}
		return block == AVBlocks.GREENSTONE_WIRE || block.isSignalSource();
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(AVItems.GREENSTONE)};
	}

	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {
		if (world.getBlockData(tilePos) > 0) {
			double px = tilePos.x() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
			double py = tilePos.y() + 0.0625;
			double pz = tilePos.z() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
			world.spawnParticle(DUST_PARTICLE, px, py, pz, 0.0, 0.0, 0.0, 0, false);
		}
	}
}
