package com.alphaver.client.render;

import com.alphaver.block.BlockLogicAlphaVerDoor;
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
public class BlockModelCypressDoorLower<T extends BlockLogic> extends BlockModelTransparent<T> {

	private static final String CARVED_ICON = "alphaver:block/door/icon/carved/cypress_sapling";
	private static final String CARVED_TEXTURE = "alphaver:block/door_icon/carved/cypress_sapling";
	private static final String SAPLING_ICON = "alphaver:block/door/icon/cypress_sapling";
	private static final String SAPLING_TEXTURE = "alphaver:block/door_icon/cypress_sapling";

	private static final String FALLBACK_ICON = "alphaver:block/door/icon/oak_sapling";

	private final BlockModelGeneric<T> icon;

	public BlockModelCypressDoorLower(@NotNull Block<T> block, boolean renderInside) {
		super(block, renderInside);
		String model = AVTextures.has(CARVED_TEXTURE) ? CARVED_ICON : AVTextures.has(SAPLING_TEXTURE) ? SAPLING_ICON : FALLBACK_ICON;
		this.icon = new BlockModelGeneric<>(block, BlockModelDispatcher.loadDataModel(model));
	}

	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		boolean drawn = super.render(tessellator, worldSource, tilePos);
		int rotation = (worldSource.getBlockData(tilePos) & BlockLogicAlphaVerDoor.AXIS_X) != 0 ? 0 : 1;
		drawn |= this.icon.staticModel.renderAttached(this.icon, tessellator, worldSource, tilePos, 0, rotation, 0, 0.0, 0.0, 0.0, false,
			true, null);
		return drawn;
	}
}
