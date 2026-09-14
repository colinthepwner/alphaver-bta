package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.block.model.BlockModelDispatcher;
import net.minecraft.client.render.block.model.generic.BlockModelGeneric;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.block.StaticBlockModel;

@Environment(EnvType.CLIENT)
public class BlockModelCypressGrass<T extends BlockLogic> extends BlockModelGeneric<T> {

	@Nullable
	private final StaticBlockModel fields;
	@Nullable
	private final StaticBlockModel highwood;
	@NotNull
	private final StaticBlockModel snowy;

	public BlockModelCypressGrass(@NotNull Block<T> block, boolean fieldsArt, boolean highwoodArt) {
		super(block, BlockModelDispatcher.loadDataModel("minecraft:block/grass_retro"));
		this.fields = fieldsArt ? BlockModelDispatcher.loadDataModel("alphaver:block/grass_fields").asModel() : null;
		this.highwood = highwoodArt ? BlockModelDispatcher.loadDataModel("alphaver:block/grass_highwood").asModel() : null;
		this.snowy = BlockModelDispatcher.loadDataModel("minecraft:block/grass_snowy").asModel();
	}

	@NotNull
	@Override
	public StaticBlockModel getModel(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		if (!AVLookController.isApplied()) {
			return super.getModel(source, tilePos);
		}
		int variant = CypressSurfaceVariants.at(source, tilePos.x(), tilePos.z());
		if (variant == 0 && source.getWorldType() != com.alphaver.world.type.WorldTypeCypress.CYPRESS) {
			return super.getModel(source, tilePos);
		}
		Block<?> above = source.getBlockType(tilePos.up(new TilePos()));
		if (above == Blocks.LAYER_SNOW || above == Blocks.BLOCK_SNOW) {
			return this.snowy;
		}
		if (variant == CypressSurfaceVariants.FIELDS && this.fields != null) {
			return this.fields;
		}
		if (variant == CypressSurfaceVariants.HIGHWOOD && this.highwood != null) {
			return this.highwood;
		}
		return super.getModel(source, tilePos);
	}
}
