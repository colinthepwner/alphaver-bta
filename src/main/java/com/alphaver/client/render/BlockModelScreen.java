package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBdc;

@Environment(EnvType.CLIENT)
public class BlockModelScreen<T extends BlockLogic> extends BlockModelStandard<T> {

	public BlockModelScreen(@NotNull Block<T> block) {
		super(block);
	}

	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		AABBdc bounds = this.block.getBoundsFromState(worldSource, tilePos);
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		tessellator.setLightmapCoord2i(15, 15);
		tessellator.setShade1i(255);
		boolean drew = false;
		if (this.shouldSideBeRendered(worldSource, bounds, new TilePos(x, y - 1, z), Side.BOTTOM)) {
			tessellator.setColorOpaque3f(0.5F, 0.5F, 0.5F);
			renderBlocks.renderBottomFace(tessellator, bounds, tilePos, this.texture(worldSource, tilePos, Side.BOTTOM));
			drew = true;
		}
		if (this.shouldSideBeRendered(worldSource, bounds, new TilePos(x, y + 1, z), Side.TOP)) {
			tessellator.setColorOpaque3f(1.0F, 1.0F, 1.0F);
			renderBlocks.renderTopFace(tessellator, bounds, tilePos, this.texture(worldSource, tilePos, Side.TOP));
			drew = true;
		}

		if (this.shouldSideBeRendered(worldSource, bounds, new TilePos(x, y, z - 1), Side.NORTH)) {
			tessellator.setColorOpaque3f(0.8F, 0.8F, 0.8F);
			renderBlocks.renderNorthFace(tessellator, bounds, tilePos, this.texture(worldSource, tilePos, Side.NORTH));
			drew = true;
		}
		if (this.shouldSideBeRendered(worldSource, bounds, new TilePos(x, y, z + 1), Side.SOUTH)) {
			tessellator.setColorOpaque3f(0.8F, 0.8F, 0.8F);
			renderBlocks.renderSouthFace(tessellator, bounds, tilePos, this.texture(worldSource, tilePos, Side.SOUTH));
			drew = true;
		}
		if (this.shouldSideBeRendered(worldSource, bounds, new TilePos(x - 1, y, z), Side.WEST)) {
			tessellator.setColorOpaque3f(0.6F, 0.6F, 0.6F);
			renderBlocks.renderWestFace(tessellator, bounds, tilePos, this.texture(worldSource, tilePos, Side.WEST));
			drew = true;
		}
		if (this.shouldSideBeRendered(worldSource, bounds, new TilePos(x + 1, y, z), Side.EAST)) {
			tessellator.setColorOpaque3f(0.6F, 0.6F, 0.6F);
			renderBlocks.renderEastFace(tessellator, bounds, tilePos, this.texture(worldSource, tilePos, Side.EAST));
			drew = true;
		}
		return drew;
	}

	private IconCoordinate texture(WorldSource worldSource, TilePosc tilePos, Side side) {
		IconCoordinate override = renderBlocks.overrideBlockTexture;
		return override != null ? override : this.getBlockTexture(worldSource, tilePos, side);
	}
}
