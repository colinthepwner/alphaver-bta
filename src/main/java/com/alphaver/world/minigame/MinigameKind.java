package com.alphaver.world.minigame;

import com.alphaver.world.AVDimensions;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public enum MinigameKind {
	ZOMBIES("Zombies"),
	FREERUN("Freerun");

	public final String title;

	MinigameKind(String title) {
		this.title = title;
	}

	@Nullable
	public Dimension dimension() {
		return this == ZOMBIES ? AVDimensions.ZOMBIES : AVDimensions.FREERUN;
	}

	@Nullable
	public static MinigameKind ofWorld(@Nullable World world) {
		if (AVWorlds.isZombies(world)) {
			return ZOMBIES;
		}
		if (AVWorlds.isFreerun(world)) {
			return FREERUN;
		}
		return null;
	}

	@Nullable
	public static MinigameKind byOrdinal(int ordinal) {
		MinigameKind[] kinds = values();
		return ordinal >= 0 && ordinal < kinds.length ? kinds[ordinal] : null;
	}
}
