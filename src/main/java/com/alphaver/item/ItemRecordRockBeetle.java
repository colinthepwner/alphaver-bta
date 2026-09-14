package com.alphaver.item;

import net.minecraft.core.item.ItemDiscMusic;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemRecordRockBeetle extends ItemDiscMusic {

	private static final String SECRET_NAME = "LA CREATURA MAGNIFICA";

	public ItemRecordRockBeetle(@NotNull String name, @NotNull String namespaceId, int id, @Nullable String recordName) {
		super(name, namespaceId, id, recordName, null);
	}

	@NotNull
	@Override
	public String getTranslatedName(@NotNull ItemStack selfStack) {
		return AVItemHooks.isRightShiftHeld() ? SECRET_NAME : super.getTranslatedName(selfStack);
	}
}
