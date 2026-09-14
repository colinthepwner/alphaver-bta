package com.alphaver.client.render;

import com.alphaver.block.AVBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.color.BlockColorDispatcher;

@Environment(EnvType.CLIENT)
public final class AVBlockColors {
	private AVBlockColors() {}

	public static void register(BlockColorDispatcher dispatcher) {
		if (AVBlocks.ESSENCE_FOUNTAIN != null) {
			dispatcher.addDispatch(AVBlocks.ESSENCE_FOUNTAIN, new BlockColorEssenceFountain());
		}
	}
}
