package com.alphaver.client.render;

import com.alphaver.block.AVBlocks;
import com.alphaver.block.machine.TileEntityEssenceFountain;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BlockColorEssenceFountain extends BlockColor {

	private static final int NONE = -1;

	@Override
	public int getFallbackColor(int meta, int tintIndex) {
		return NONE;
	}

	@Override
	public int getWorldColor(@NotNull WorldSource source, @NotNull TilePosc tilePos, int tintIndex) {
		if (tilePos.y() <= 1) {
			return NONE;
		}
		TilePos under = tilePos.down(new TilePos());
		Block<?> block = source.getBlockType(under);
		if (block != null && block == AVBlocks.ESSENCE_TRANSFORMER) {
			return 0x33FF33;
		}
		if (block != null && block == AVBlocks.ESSENCE_CLONER) {
			return 0x1AFF66;
		}
		if (source.getTileEntity(under) instanceof TileEntityChest) {
			return 0xFFCC00;
		}
		if (block == Blocks.FURNACE_STONE_IDLE || block == Blocks.FURNACE_STONE_ACTIVE) {
			boolean full = source.getTileEntity(tilePos) instanceof TileEntityEssenceFountain fountain && fountain.isFull();
			return full ? 0xFF0000 : 0x7F0000;
		}
		return NONE;
	}
}
