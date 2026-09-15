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

	static final String WOOD = "minecraft:block/door/planks/oak";

	private static final double CENTRE = 0.5 - 3.0 / 32.0;

	private final BlockModelGeneric<T> icon;
	private final BlockModelGeneric<T> wood;

	public BlockModelCypressDoorLower(@NotNull Block<T> block, boolean renderInside) {
		super(block, renderInside);
		String model = AVTextures.has(CARVED_TEXTURE) ? CARVED_ICON : AVTextures.has(SAPLING_TEXTURE) ? SAPLING_ICON : FALLBACK_ICON;
		this.icon = new BlockModelGeneric<>(block, BlockModelDispatcher.loadDataModel(model));
		this.wood = new BlockModelGeneric<>(block, BlockModelDispatcher.loadDataModel(WOOD + "/bottom_right_open"));
	}

	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		int data = worldSource.getBlockData(tilePos);
		if (CypressHomeLook.woodenCypressDoors()) {
			return drawWood(this.wood, tessellator, worldSource, tilePos, data);
		}
		boolean drawn = super.render(tessellator, worldSource, tilePos);
		int rotation = (data & BlockLogicAlphaVerDoor.AXIS_X) != 0 ? 0 : 1;
		drawn |= this.icon.staticModel.renderAttached(this.icon, tessellator, worldSource, tilePos, 0, rotation, 0, 0.0, 0.0, 0.0, false,
			true, null);
		return drawn;
	}

	static <T extends BlockLogic> boolean drawWood(BlockModelGeneric<T> wood, TessellatorGeneral tessellator, WorldSource worldSource,
	                                               TilePosc tilePos, int data) {
		boolean axisX = (data & BlockLogicAlphaVerDoor.AXIS_X) != 0;
		return wood.staticModel.renderAttached(wood, tessellator, worldSource, tilePos, 0, axisX ? 0 : 3, 0,
			axisX ? CENTRE : 0.0, 0.0, axisX ? 0.0 : CENTRE, false, false, null);
	}
}
