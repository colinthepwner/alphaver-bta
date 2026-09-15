package com.alphaver.entity;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.GamemodeBuilder;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.player.inventory.menu.MenuInventory;
import org.jetbrains.annotations.Nullable;

public final class AVGamemodes {
	private AVGamemodes() {}

	public static final String CYPRESS_SURVIVAL_ID = "alphaver:gamemode/cypress_survival";
	public static final String CYPRESS_FRAIL_ID = "alphaver:gamemode/cypress_frail";

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static Gamemode CYPRESS_SURVIVAL;
	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static Gamemode CYPRESS_FRAIL;

	public static void register() {
		if (CYPRESS_SURVIVAL != null) {
			return;
		}
		CYPRESS_SURVIVAL = Gamemodes.register(CYPRESS_SURVIVAL_ID, survival("gamemode.alphaver.cypress_survival"));
		CYPRESS_FRAIL = Gamemodes.register(CYPRESS_FRAIL_ID, survival("gamemode.alphaver.cypress_frail"));
	}

	private static Gamemode survival(String languageKey) {
		return new GamemodeBuilder(languageKey, MenuInventory::new)
			.withBlockConsumption()
			.withBlockBreakingAnimation()
			.withToolDurability()
			.withItemDrops()
			.withHostileMobs()
			.build();
	}

	public static boolean startsInCypress(@Nullable Gamemode gamemode) {
		return gamemode != null && (gamemode == CYPRESS_SURVIVAL || gamemode == CYPRESS_FRAIL);
	}

	public static boolean isFrail(@Nullable Player player) {
		return player != null && CYPRESS_FRAIL != null && player.getGamemode() == CYPRESS_FRAIL;
	}
}
