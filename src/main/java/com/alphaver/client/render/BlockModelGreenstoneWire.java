package com.alphaver.client.render;

import com.alphaver.block.AVBlocks;
import com.alphaver.block.BlockLogicGreenstoneWire;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelStandard;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BlockModelGreenstoneWire<T extends BlockLogic> extends BlockModelStandard<T> {

	private static final double LIFT = 1.0 / 32.0;

	private static final double STUB = 0.3125;

	private static final String BTA_CROSS = "minecraft:block/wire_redstone_cross";
	private static final String BTA_STRAIGHT = "minecraft:block/wire_redstone_straight";

	private final boolean bridged;
	private final IconCoordinate cross;
	private final IconCoordinate straight;
	private final IconCoordinate crossPowered;
	private final IconCoordinate straightPowered;

	public BlockModelGreenstoneWire(@NotNull Block<T> block) {
		super(block);
		this.bridged = AVTextures.has("alphaver:block/greenstone_wire_cross") && AVTextures.has("alphaver:block/greenstone_wire_straight")
			&& AVTextures.has("alphaver:block/greenstone_wire_cross_powered") && AVTextures.has("alphaver:block/greenstone_wire_straight_powered");
		this.cross = TextureRegistry.getTexture(this.bridged ? "alphaver:block/greenstone_wire_cross" : BTA_CROSS);
		this.straight = TextureRegistry.getTexture(this.bridged ? "alphaver:block/greenstone_wire_straight" : BTA_STRAIGHT);
		this.crossPowered = TextureRegistry.getTexture(this.bridged ? "alphaver:block/greenstone_wire_cross_powered" : BTA_CROSS);
		this.straightPowered = TextureRegistry.getTexture(this.bridged ? "alphaver:block/greenstone_wire_straight_powered" : BTA_STRAIGHT);
		this.setAllTextures(this.bridged ? "alphaver:block/greenstone_wire_cross" : BTA_CROSS);
	}

	@Override
	public boolean render(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos) {
		int x = tilePos.x();
		int y = tilePos.y();
		int z = tilePos.z();
		boolean powered = (worldSource.getBlockData(tilePos) & 15) > 0;
		IconCoordinate crossIcon = powered ? this.crossPowered : this.cross;
		IconCoordinate straightIcon = powered ? this.straightPowered : this.straight;
		IconCoordinate override = renderBlocks.overrideBlockTexture;
		if (override != null) {
			crossIcon = override;
			straightIcon = override;
		}

		tessellator.setLightmapCoord2i(worldSource.getSavedLightValue(LightLayer.Block, tilePos),
			worldSource.getSavedLightValue(LightLayer.Sky, tilePos));
		if (this.bridged || override != null) {
			tessellator.setColorOpaque3f(1.0F, 1.0F, 1.0F);
		} else if (powered) {
			tessellator.setColorOpaque3f(4 / 255.0F, 174 / 255.0F, 12 / 255.0F);
		} else {
			tessellator.setColorOpaque3f(16 / 255.0F, 55 / 255.0F, 7 / 255.0F);
		}

		int links = BlockLogicGreenstoneWire.connections(worldSource, tilePos);
		int joins = links | links >> BlockLogicGreenstoneWire.CLIMB_SHIFT;
		boolean west = (joins & BlockLogicGreenstoneWire.WEST) != 0;
		boolean east = (joins & BlockLogicGreenstoneWire.EAST) != 0;
		boolean north = (joins & BlockLogicGreenstoneWire.NORTH) != 0;
		boolean south = (joins & BlockLogicGreenstoneWire.SOUTH) != 0;
		double top = y + LIFT;

		if ((west || east) && !north && !south) {
			face(tessellator, x, x + 1, z, z + 1, top, straightIcon.getIconUMin(), straightIcon.getIconVMin(),
				straightIcon.getIconUMax(), straightIcon.getIconVMax(), false);
		} else if ((north || south) && !west && !east) {
			face(tessellator, x, x + 1, z, z + 1, top, straightIcon.getIconUMin(), straightIcon.getIconVMin(),
				straightIcon.getIconUMax(), straightIcon.getIconVMax(), true);
		} else if (!west && !east && !north && !south) {
			face(tessellator, x, x + 1, z, z + 1, top, crossIcon.getIconUMin(), crossIcon.getIconVMin(),
				crossIcon.getIconUMax(), crossIcon.getIconVMax(), false);
		} else {
			double w = west ? 0.0 : STUB;
			double e = east ? 0.0 : STUB;
			double n = north ? 0.0 : STUB;
			double s = south ? 0.0 : STUB;
			face(tessellator, x + w, x + 1 - e, z + n, z + 1 - s, top, crossIcon.getSubIconU(w), crossIcon.getSubIconV(n),
				crossIcon.getSubIconU(1.0 - e), crossIcon.getSubIconV(1.0 - s), false);
		}

		TilePos query = new TilePos();
		int climbs = links >> BlockLogicGreenstoneWire.CLIMB_SHIFT;
		if ((climbs & BlockLogicGreenstoneWire.WEST) != 0 && isWire(worldSource, query.set(x - 1, y + 1, z))) {
			climb(tessellator, x + LIFT, y, z, z + 1, true, straightIcon);
		}
		if ((climbs & BlockLogicGreenstoneWire.EAST) != 0 && isWire(worldSource, query.set(x + 1, y + 1, z))) {
			climb(tessellator, x + 1 - LIFT, y, z, z + 1, true, straightIcon);
		}
		if ((climbs & BlockLogicGreenstoneWire.NORTH) != 0 && isWire(worldSource, query.set(x, y + 1, z - 1))) {
			climb(tessellator, z + LIFT, y, x, x + 1, false, straightIcon);
		}
		if ((climbs & BlockLogicGreenstoneWire.SOUTH) != 0 && isWire(worldSource, query.set(x, y + 1, z + 1))) {
			climb(tessellator, z + 1 - LIFT, y, x, x + 1, false, straightIcon);
		}
		return true;
	}

	@Override
	public boolean shouldItemRender3d() {
		return false;
	}

	private static boolean isWire(WorldSource worldSource, TilePosc tilePos) {
		return worldSource.getBlockType(tilePos) == AVBlocks.GREENSTONE_WIRE;
	}

	private static void climb(TessellatorGeneral t, double at, int y, double a0, double a1, boolean alongZ, IconCoordinate icon) {
		double u0 = icon.getIconUMin();
		double u1 = icon.getIconUMax();
		double v0 = icon.getIconVMin();
		double v1 = icon.getIconVMax();
		double yb = y;
		double yt = y + 1;
		if (alongZ) {
			t.addVertexWithUV(at, yt, a1, u1, v1);
			t.addVertexWithUV(at, yb, a1, u0, v1);
			t.addVertexWithUV(at, yb, a0, u0, v0);
			t.addVertexWithUV(at, yt, a0, u1, v0);
			t.addVertexWithUV(at, yt, a0, u1, v0);
			t.addVertexWithUV(at, yb, a0, u0, v0);
			t.addVertexWithUV(at, yb, a1, u0, v1);
			t.addVertexWithUV(at, yt, a1, u1, v1);
		} else {
			t.addVertexWithUV(a1, yt, at, u1, v1);
			t.addVertexWithUV(a1, yb, at, u0, v1);
			t.addVertexWithUV(a0, yb, at, u0, v0);
			t.addVertexWithUV(a0, yt, at, u1, v0);
			t.addVertexWithUV(a0, yt, at, u1, v0);
			t.addVertexWithUV(a0, yb, at, u0, v0);
			t.addVertexWithUV(a1, yb, at, u0, v1);
			t.addVertexWithUV(a1, yt, at, u1, v1);
		}
	}

	private static void face(TessellatorGeneral t, double x0, double x1, double z0, double z1, double y,
	                         double u0, double v0, double u1, double v1, boolean rotated) {
		if (rotated) {
			t.addVertexWithUV(x1, y, z1, u1, v0);
			t.addVertexWithUV(x1, y, z0, u0, v0);
			t.addVertexWithUV(x0, y, z0, u0, v1);
			t.addVertexWithUV(x0, y, z1, u1, v1);
			t.addVertexWithUV(x1, y, z1, u1, v0);
			t.addVertexWithUV(x0, y, z1, u1, v1);
			t.addVertexWithUV(x0, y, z0, u0, v1);
			t.addVertexWithUV(x1, y, z0, u0, v0);
			return;
		}
		t.addVertexWithUV(x1, y, z1, u1, v1);
		t.addVertexWithUV(x1, y, z0, u1, v0);
		t.addVertexWithUV(x0, y, z0, u0, v0);
		t.addVertexWithUV(x0, y, z1, u0, v1);
		t.addVertexWithUV(x1, y, z1, u1, v1);
		t.addVertexWithUV(x0, y, z1, u0, v1);
		t.addVertexWithUV(x0, y, z0, u0, v0);
		t.addVertexWithUV(x1, y, z0, u1, v0);
	}
}
