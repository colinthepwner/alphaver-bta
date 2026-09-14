package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.color.BlockColor;
import net.minecraft.client.render.block.color.BlockColorDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public final class AVLookColors {
	private AVLookColors() {}

	private static final int NO_TINT = -1;

	public static void wrap(@NotNull BlockColorDispatcher dispatcher) {
		untinted(dispatcher, Blocks.GRASS);
		untinted(dispatcher, Blocks.TALLGRASS);
		untinted(dispatcher, Blocks.TALLGRASS_FERN);
		untinted(dispatcher, Blocks.LEAVES_OAK);
		untinted(dispatcher, Blocks.LAYER_LEAVES_OAK);
	}

	private static void untinted(BlockColorDispatcher dispatcher, Block<?> block) {
		if (block == null || !dispatcher.hasDispatch(block)) {
			return;
		}
		BlockColor original = dispatcher.getDispatch(block);
		if (!(original instanceof Untinted)) {
			dispatcher.addDispatch(block, new Untinted(original));
		}
	}

	private static final class Untinted extends BlockColor {
		private final BlockColor bta;

		Untinted(BlockColor bta) {
			this.bta = bta;
		}

		@Override
		public int getFallbackColor(int meta, int tintIndex) {
			return AVLookController.isApplied() ? NO_TINT : this.bta.getFallbackColor(meta, tintIndex);
		}

		@Override
		public int getWorldColor(@NotNull WorldSource source, @NotNull TilePosc tilePos, int tintIndex) {
			return AVLookController.isApplied() ? NO_TINT : this.bta.getWorldColor(source, tilePos, tintIndex);
		}
	}
}
