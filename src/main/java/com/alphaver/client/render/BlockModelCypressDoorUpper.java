package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.BlockModelTransparent;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BlockModelCypressDoorUpper<T extends BlockLogic> extends BlockModelTransparent<T> {

	private final BlockModelGeneric<T> wood;

	public BlockModelCypressDoorUpper(@NotNull Block<T> block, boolean renderInside) {
		super(block, renderInside);
		this.wood = new BlockModelGeneric<>(block, BlockModelDispatcher.loadDataModel(BlockModelCypressDoorLower.WOOD + "/top_right_open"));
	}

	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		if (CypressHomeLook.woodenCypressDoors()) {
			return BlockModelCypressDoorLower.drawWood(this.wood, tessellator, worldSource, tilePos, worldSource.getBlockData(tilePos));
		}
		return super.render(tessellator, worldSource, tilePos);
	}
}
