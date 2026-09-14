package com.alphaver.world.minigame;

import com.alphaver.block.BlockLogicZombiesMachine;
import org.jetbrains.annotations.Nullable;

public enum MinigamePerk {
	HEALTH_BOOST("Berzerkola", 2500, "health_boost"),
	ARMOR("Armor", 1500, "armor"),
	DASH("Dash", 7500, "dash"),
	QUICK_REVIVE("Revive", 600, "quick_revive");

	public final String promptName;
	public final int price;

	public final String icon;

	MinigamePerk(String promptName, int price, String icon) {
		this.promptName = promptName;
		this.price = price;
		this.icon = icon;
	}

	public int bit() {
		return 1 << this.ordinal();
	}

	public boolean in(int mask) {
		return (mask & this.bit()) != 0;
	}

	@Nullable
	public static MinigamePerk sold(@Nullable BlockLogicZombiesMachine.Kind kind) {
		if (kind == null) {
			return null;
		}
		return switch (kind) {
			case PERK_HEALTH_BOOST -> HEALTH_BOOST;
			case PERK_ARMOR -> ARMOR;
			case PERK_DASH -> DASH;
			case PERK_QUICK_REVIVE -> QUICK_REVIVE;
			default -> null;
		};
	}
}
