package com.alphaver.client.render;

import com.alphaver.block.BlockLogicAlphaVerDoor;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

@Environment(EnvType.CLIENT)
public class BlockModelHubDoor<T extends BlockLogic> extends BlockModelGeneric<T> {

	private static final String IRON = "minecraft:block/door/iron";
	private static final String OWN = "alphaver:block/door/hub";

	private final StaticBlockModel ironBottom;
	private final StaticBlockModel ironTop;
	@Nullable
	private final StaticBlockModel ownBottom;
	@Nullable
	private final StaticBlockModel ownTop;

	public BlockModelHubDoor(@NotNull Block<T> block) {
		super(block, BlockModelDispatcher.loadDataModel(IRON + "/bottom_right_open"));
		this.ironBottom = this.staticModel;
		this.ironTop = BlockModelDispatcher.loadDataModel(IRON + "/top_right_open").asModel();
		boolean bridged = AVTextures.has("alphaver:block/hub_door_lower") && AVTextures.has("alphaver:block/hub_door_upper");
		this.ownBottom = bridged ? BlockModelDispatcher.loadDataModel(OWN + "/bottom_right_open").asModel() : null;
		this.ownTop = bridged ? BlockModelDispatcher.loadDataModel(OWN + "/top_right_open").asModel() : null;
	}

	@Override
	public boolean renderAttached(@NotNull TessellatorGeneral tessellator, @NotNull WorldSource worldSource, @NotNull TilePosc tilePos,
	                              boolean cullFaces, @Nullable IconCoordinate overrideTexture) {
		int data = worldSource.getBlockData(tilePos);
		boolean drawn = this.getModelFromData(data)
			.renderAttached(this, tessellator, worldSource, tilePos, 0, rotationY(data), 0, 0.0, 0.0, 0.0, false, cullFaces, overrideTexture);
		StaticBlockModel icon = (data & BlockLogicAlphaVerDoor.UPPER) == 0 ? this.icon() : null;
		if (icon != null) {

			drawn |= icon.renderAttached(this, tessellator, worldSource, tilePos, 0, rotationY(data), 0, 0.0, 0.0, 0.0, false, cullFaces,
				overrideTexture);
		}
		return drawn;
	}

	@Nullable
	protected StaticBlockModel icon() {
		return null;
	}

	protected boolean drawsCypressIron() {
		return inAlphaVerWorld() ? AVLookController.isApplied() : this.ownBottom != null && this.ownTop != null;
	}

	@NotNull
	@Override
	public StaticBlockModel getModelFromData(int data) {
		boolean upper = (data & BlockLogicAlphaVerDoor.UPPER) != 0;
		if (this.ownBottom != null && this.ownTop != null && !inAlphaVerWorld()) {
			return upper ? this.ownTop : this.ownBottom;
		}
		return upper ? this.ironTop : this.ironBottom;
	}

	protected static int rotationY(int data) {
		boolean far = (data & BlockLogicAlphaVerDoor.FAR) != 0;
		if ((data & BlockLogicAlphaVerDoor.AXIS_X) != 0) {
			return far ? 2 : 0;
		}
		return far ? 1 : 3;
	}

	private static boolean inAlphaVerWorld() {
		Minecraft mc = Minecraft.getMinecraft();
		return mc != null && AVWorlds.isAlphaVer(mc.currentWorld);
	}
}
