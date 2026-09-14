package com.alphaver.world;

import com.alphaver.world.type.WorldTypeCypress;
import com.alphaver.world.type.WorldTypeHub;
import com.alphaver.world.type.WorldTypeMinigame;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public final class AVWorlds {
	private AVWorlds() {}

	public static boolean isCypress(@Nullable World world) {
		return world != null && world.getWorldType() instanceof WorldTypeCypress;
	}

	public static boolean isHub(@Nullable World world) {
		return world != null && world.getWorldType() instanceof WorldTypeHub;
	}

	public static boolean isZombies(@Nullable World world) {
		return world != null && world.getWorldType() == WorldTypeMinigame.ZOMBIES && WorldTypeMinigame.ZOMBIES != null;
	}

	public static boolean isFreerun(@Nullable World world) {
		return world != null && world.getWorldType() == WorldTypeMinigame.FREERUN && WorldTypeMinigame.FREERUN != null;
	}

	public static boolean isMinigame(@Nullable World world) {
		return world != null && world.getWorldType() instanceof WorldTypeMinigame;
	}

	public static boolean isAlphaVer(@Nullable World world) {
		return isCypress(world) || isHub(world) || isMinigame(world);
	}
}
