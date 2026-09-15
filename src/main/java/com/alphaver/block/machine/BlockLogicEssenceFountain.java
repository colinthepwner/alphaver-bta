package com.alphaver.block.machine;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class BlockLogicEssenceFountain extends BlockLogic {

	private static final double HEIGHT = 0.25;

	public BlockLogicEssenceFountain(@NotNull Block<?> block) {
		super(block, Materials.STONE);
		block.withEntity(TileEntityEssenceFountain::new);
		this.setBlockBounds(0.0, 0.0, 0.0, 1.0, HEIGHT, 1.0);
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
	public void animationTick(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Random rand) {
		if (!(world.getTileEntity(tilePos) instanceof TileEntityEssenceFountain fountain) || fountain.count == 0) {
			return;
		}
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		if (rand.nextInt(2) == 0) {
			world.spawnParticle("item", x + rand.nextFloat(), y + 0.4, z + rand.nextFloat(), 0.0, 0.0, 0.0,
				Items.AMMO_SNOWBALL.id, false);
		}
		if (rand.nextInt(3) == 0) {

			int top = Math.min(128, y + fountain.count);
			for (int cellY = y + 1; cellY < top; cellY++) {
				if (!world.isAirBlock(x, cellY, z)) {
					break;
				}
				if (rand.nextInt(6) == 0) {
					world.spawnParticle("splash", x + rand.nextFloat(), cellY + rand.nextFloat(), z + rand.nextFloat(),
						0.0, 0.0, 0.0, world.dimension.id, false);
				}
			}
		}
	}
}
