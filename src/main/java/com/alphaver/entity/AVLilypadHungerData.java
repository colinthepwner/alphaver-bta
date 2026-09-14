package com.alphaver.entity;

import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public interface AVLilypadHungerData {

	int alphaver$hunger();

	void alphaver$setHunger(int hunger);

	boolean alphaver$hungerSynced();

	int alphaver$sentHungerStage();

	@Nullable
	World alphaver$sentHungerWorld();

	void alphaver$markHungerSent(int stage, @Nullable World world);
}
