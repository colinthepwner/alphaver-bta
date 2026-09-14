package com.alphaver.block;

import com.alphaver.item.AVItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BlockLogicGreenstoneOre extends BlockLogic {

	private final boolean glowing;

	public BlockLogicGreenstoneOre(@NotNull Block<?> block, boolean glowing) {
		super(block, Materials.STONE);
		this.glowing = glowing;
	}

	@Override
	public void onAttacked(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @NotNull Side side, double xHit, double yHit) {
		this.glow(world, tilePos);
	}

	@Override
	public void onEntityWalkedOn(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Entity walker) {
		this.glow(world, tilePos);
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @Nullable Player player, @Nullable Side side, double xHit,
	                            double yHit) {
		this.glow(world, tilePos);
		return false;
	}

	private void glow(World world, TilePosc tilePos) {
		this.sparkle(world, tilePos);
		if (!this.glowing && !world.isClientSide) {
			world.setBlockTypeNotify(tilePos, AVBlocks.ORE_GREENSTONE_GLOWING);
		}
	}

	@Override
	public void updateTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand, boolean isRandomTick) {
		if (this.glowing && !world.isClientSide) {
			world.setBlockTypeNotify(tilePos, AVBlocks.ORE_GREENSTONE);
		}
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		return switch (dropCause) {
			case SILK_TOUCH, PICK_BLOCK -> new ItemStack[]{new ItemStack(AVBlocks.ORE_GREENSTONE)};
			case EXPLOSION, PROPER_TOOL, PISTON_CRUSH -> new ItemStack[]{new ItemStack(AVItems.GREENSTONE, 4 + world.rand.nextInt(2))};
			default -> null;
		};
	}

	@Override
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {
		if (this.glowing) {
			this.sparkle(world, tilePos);
		}
	}

	private void sparkle(World world, TilePosc tilePos) {
		Random random = world.rand;
		double lift = 0.0625;
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		TilePos query = new TilePos();
		for (int face = 0; face < 6; face++) {
			double px = x + random.nextFloat();
			double py = y + random.nextFloat();
			double pz = z + random.nextFloat();
			if (face == 0 && !world.isBlockNormalCube(query.set(x, y + 1, z))) {
				py = y + 1 + lift;
			}
			if (face == 1 && !world.isBlockNormalCube(query.set(x, y - 1, z))) {
				py = y - lift;
			}
			if (face == 2 && !world.isBlockNormalCube(query.set(x, y, z + 1))) {
				pz = z + 1 + lift;
			}
			if (face == 3 && !world.isBlockNormalCube(query.set(x, y, z - 1))) {
				pz = z - lift;
			}
			if (face == 4 && !world.isBlockNormalCube(query.set(x + 1, y, z))) {
				px = x + 1 + lift;
			}
			if (face == 5 && !world.isBlockNormalCube(query.set(x - 1, y, z))) {
				px = x - lift;
			}
			if (px < x || px > x + 1 || py < 0.0 || py > y + 1 || pz < z || pz > z + 1) {
				world.spawnParticle(BlockLogicGreenstoneWire.DUST_PARTICLE, px, py, pz, 0.0, 0.0, 0.0, 0, false);
			}
		}
	}
}
