package com.alphaver.entity;

import com.mojang.nbt.tags.ListTag;
import org.jetbrains.annotations.Nullable;

public interface AVStashData {

	boolean alphaver$hasStash();

	@Nullable
	ListTag alphaver$stashItems();

	int alphaver$stashDimension();

	int alphaver$stashExitX();

	int alphaver$stashExitY();

	int alphaver$stashExitZ();

	int alphaver$stashHealth();

	@Nullable
	String alphaver$stashGamemode();

	void alphaver$setStash(ListTag items, int dimension, int exitX, int exitY, int exitZ, int health, @Nullable String gamemode);

	void alphaver$clearStash();
}
