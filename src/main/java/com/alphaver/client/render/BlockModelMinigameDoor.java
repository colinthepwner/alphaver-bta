package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

@Environment(EnvType.CLIENT)
public class BlockModelMinigameDoor<T extends BlockLogic> extends BlockModelHubDoor<T> {

	private static final String CARVED_MODELS = "alphaver:block/door/icon/carved/";
	private static final String CARVED_TEXTURES = "alphaver:block/door_icon/carved/";

	private final StaticBlockModel flat;
	@Nullable
	private final StaticBlockModel carvedInCypressIron;
	@Nullable
	private final StaticBlockModel carvedInBtaIron;

	public BlockModelMinigameDoor(@NotNull Block<T> block, @NotNull String carvedName, @NotNull String iconModel,
	                              @Nullable String bridgedIconModel, @Nullable String bridgedTexture) {
		super(block);
		boolean bridged = bridgedIconModel != null && bridgedTexture != null && AVTextures.has(bridgedTexture);
		this.flat = BlockModelDispatcher.loadDataModel(bridged ? bridgedIconModel : iconModel).asModel();
		this.carvedInCypressIron = carved(carvedName);
		this.carvedInBtaIron = carved(carvedName + "_bta");
	}

	@Nullable
	private static StaticBlockModel carved(String name) {
		if (!AVTextures.has(CARVED_TEXTURES + name + "_west") || !AVTextures.has(CARVED_TEXTURES + name + "_east")) {
			return null;
		}
		return BlockModelDispatcher.loadDataModel(CARVED_MODELS + name).asModel();
	}

	@NotNull
	@Override
	protected StaticBlockModel icon() {
		StaticBlockModel carved = this.drawsCypressIron() ? this.carvedInCypressIron : this.carvedInBtaIron;
		return carved != null ? carved : this.flat;
	}
}
