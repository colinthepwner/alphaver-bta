package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.data.block.BlockModelData;
import org.useless.dragonfly.models.block.StaticBlockModel;

@Environment(EnvType.CLIENT)
public class BlockModelCypressPlant<T extends BlockLogic> extends BlockModelGeneric<T> {

	@Nullable
	private final StaticBlockModel variant;

	public BlockModelCypressPlant(@NotNull Block<T> block, @NotNull BlockModelData base, @Nullable String variantKey) {
		super(block, base);
		this.variant = variantKey == null ? null : BlockModelDispatcher.loadDataModel(variantKey).asModel();
	}

	@NotNull
	@Override
	public StaticBlockModel getModel(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		if (this.variant != null) {
			int surface = CypressSurfaceVariants.at(source, tilePos.x(), tilePos.z());
			if (surface == CypressSurfaceVariants.FIELDS || surface == CypressSurfaceVariants.HIGHWOOD) {
				return this.variant;
			}
		}
		return super.getModel(source, tilePos);
	}
}
