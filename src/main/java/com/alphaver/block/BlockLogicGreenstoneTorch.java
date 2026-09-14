package com.alphaver.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicTorch;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.WeakHashMap;

public class BlockLogicGreenstoneTorch extends BlockLogicTorch {

	private static final int BURNOUT_TOGGLES = 8;
	private static final long BURNOUT_WINDOW = 100L;

	private static final Map<World, List<long[]>> RECENT_TOGGLES = new WeakHashMap<>();

	private final boolean torchActive;

	public BlockLogicGreenstoneTorch(@NotNull Block<?> block, boolean torchActive) {
		super(block);
		this.torchActive = torchActive;
	}

	@Override
	public int tickDelay() {
		return 2;
	}

	@Override
	public void onPlacedByWorld(@NotNull World world, @NotNull TilePosc tilePos) {
		if (world.getBlockData(tilePos) == 0) {
			super.onPlacedByWorld(world, tilePos);
		}
		world.scheduleBlockUpdate(tilePos, this.block, this.tickDelay());
		if (this.torchActive) {
			world.notifyShellBlocksInRadiusOfNeighborChange(2, tilePos, this.block);
		}
	}

	@Override
	public void onRemoved(@NotNull World world, @NotNull TilePosc tilePos, int data) {
		if (this.torchActive) {
			world.notifyShellBlocksInRadiusOfNeighborChange(2, tilePos, this.block);
		}
	}

	@Override
	public boolean isEmittingSignal(@NotNull WorldSource source, @NotNull TilePosc tilePos, @NotNull Side side) {
		if (!this.torchActive) {
			return false;
		}
		return switch (source.getBlockData(tilePos) & 7) {
			case 1 -> side != Side.EAST;
			case 2 -> side != Side.WEST;
			case 3 -> side != Side.SOUTH;
			case 4 -> side != Side.NORTH;
			case 5 -> side != Side.TOP;
			default -> false;
		};
	}

	@Override
	public boolean isEmittingDirectSignal(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Side side) {
		return side == Side.BOTTOM && this.isEmittingSignal(world, tilePos, side);
	}

	@Override
	public boolean isSignalSource() {
		return true;
	}

	private boolean supportPowered(World world, TilePosc tilePos) {
		TilePos query = new TilePos();
		return switch (world.getBlockData(tilePos) & 7) {
			case 1 -> world.hasSignal(tilePos.west(query), Side.WEST);
			case 2 -> world.hasSignal(tilePos.east(query), Side.EAST);
			case 3 -> world.hasSignal(tilePos.north(query), Side.NORTH);
			case 4 -> world.hasSignal(tilePos.south(query), Side.SOUTH);
			case 5 -> world.hasSignal(tilePos.down(query), Side.BOTTOM);
			default -> false;
		};
	}

	@Override
	public void updateTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand, boolean isRandomTick) {
		super.updateTick(world, tilePos, rand, isRandomTick);
		if (world.isClientSide || world.getBlockType(tilePos) != this.block) {
			return;
		}
		boolean powered = this.supportPowered(world, tilePos);
		List<long[]> toggles = RECENT_TOGGLES.computeIfAbsent(world, w -> new ArrayList<>());
		long now = world.getWorldTime();
		while (!toggles.isEmpty() && now - toggles.get(0)[3] > BURNOUT_WINDOW) {
			toggles.remove(0);
		}
		if (this.torchActive) {
			if (powered) {
				world.setBlockTypeDataNotify(tilePos, AVBlocks.TORCH_GREENSTONE_IDLE, world.getBlockData(tilePos));
				if (burntOut(toggles, tilePos, now, true)) {
					world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, tilePos.x() + 0.5, tilePos.y() + 0.5, tilePos.z() + 0.5,
						"random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
					for (int n = 0; n < 5; n++) {
						world.spawnParticle("smoke", tilePos.x() + rand.nextDouble() * 0.6 + 0.2, tilePos.y() + rand.nextDouble() * 0.6 + 0.2,
							tilePos.z() + rand.nextDouble() * 0.6 + 0.2, 0.0, 0.0, 0.0, 0, false);
					}
				}
			}
		} else if (!powered && !burntOut(toggles, tilePos, now, false)) {
			world.setBlockTypeDataNotify(tilePos, AVBlocks.TORCH_GREENSTONE_ACTIVE, world.getBlockData(tilePos));
		}
	}

	private static boolean burntOut(List<long[]> toggles, TilePosc tilePos, long now, boolean record) {
		if (record) {
			toggles.add(new long[]{tilePos.x(), tilePos.y(), tilePos.z(), now});
		}
		int count = 0;
		for (long[] toggle : toggles) {
			if (toggle[0] == tilePos.x() && toggle[1] == tilePos.y() && toggle[2] == tilePos.z() && ++count >= BURNOUT_TOGGLES) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void onNeighborChanged(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Block<?> block) {
		super.onNeighborChanged(world, tilePos, block);
		if (world.getBlockType(tilePos) == this.block) {
			world.scheduleBlockUpdate(tilePos, this.block, this.tickDelay());
		}
	}

	@Override
	public void onChunkLoad(@NotNull World world, @NotNull TilePosc tilePos) {
		world.scheduleBlockUpdate(tilePos, this.block, this.tickDelay());
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		return new ItemStack[]{new ItemStack(AVBlocks.TORCH_GREENSTONE_ACTIVE)};
	}

	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {
		if (!this.torchActive) {
			return;
		}
		double px = tilePos.x() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
		double py = tilePos.y() + 0.7 + (rand.nextFloat() - 0.5) * 0.2;
		double pz = tilePos.z() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
		double up = 0.22;
		double out = 0.27;
		switch (world.getBlockData(tilePos) & 7) {
			case 1 -> world.spawnParticle(BlockLogicGreenstoneWire.DUST_PARTICLE, px - out, py + up, pz, 0.0, 0.0, 0.0, 0, false);
			case 2 -> world.spawnParticle(BlockLogicGreenstoneWire.DUST_PARTICLE, px + out, py + up, pz, 0.0, 0.0, 0.0, 0, false);
			case 3 -> world.spawnParticle(BlockLogicGreenstoneWire.DUST_PARTICLE, px, py + up, pz - out, 0.0, 0.0, 0.0, 0, false);
			case 4 -> world.spawnParticle(BlockLogicGreenstoneWire.DUST_PARTICLE, px, py + up, pz + out, 0.0, 0.0, 0.0, 0, false);
			default -> world.spawnParticle(BlockLogicGreenstoneWire.DUST_PARTICLE, px, py, pz, 0.0, 0.0, 0.0, 0, false);
		}
	}
}
