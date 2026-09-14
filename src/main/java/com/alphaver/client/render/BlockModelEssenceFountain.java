package com.alphaver.client.render;

import com.alphaver.block.machine.TileEntityEssenceFountain;
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
import org.useless.dragonfly.models.block.StaticBlockModel;

@Environment(EnvType.CLIENT)
public class BlockModelEssenceFountain<T extends BlockLogic> extends BlockModelGeneric<T> {

	@NotNull
	private final StaticBlockModel full;
	@NotNull
	private final StaticBlockModel ice;

	public BlockModelEssenceFountain(@NotNull Block<T> block) {
		super(block, BlockModelDispatcher.loadDataModel("alphaver:block/essence_fountain"));
		this.full = BlockModelDispatcher.loadDataModel("alphaver:block/essence_fountain_full").asModel();
		this.ice = BlockModelDispatcher.loadDataModel("alphaver:block/essence_fountain_ice").asModel();
	}

	@NotNull
	@Override
	public StaticBlockModel getModel(@NotNull WorldSource source, @NotNull TilePosc tilePos) {
		if (source.getTileEntity(tilePos) instanceof TileEntityEssenceFountain fountain && fountain.count > 0) {
			return source.getBlockType(tilePos.down(new TilePos())) == Blocks.ICE ? this.ice : this.full;
		}
		return super.getModel(source, tilePos);
	}
}
