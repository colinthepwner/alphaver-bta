package com.alphaver.block.machine;

import com.alphaver.AlphaVer;
import net.minecraft.core.block.entity.TileEntityDispatcher;
import net.minecraft.core.util.collection.NamespaceID;

public final class AVTileEntities {
	private AVTileEntities() {}

	public static void register() {
		TileEntityDispatcher.addMapping(TileEntityFreezer.class, NamespaceID.getPermanent(AlphaVer.MOD_ID, "freezer"));
		TileEntityDispatcher.addMapping(TileEntityEssenceFountain.class, NamespaceID.getPermanent(AlphaVer.MOD_ID, "essence_fountain"));
	}
}
