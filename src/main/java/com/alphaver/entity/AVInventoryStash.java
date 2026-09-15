package com.alphaver.entity;

import com.mojang.nbt.tags.ListTag;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AVInventoryStash {
	private AVInventoryStash() {}

	public static final int HEALTH_FULL = Integer.MAX_VALUE;

	public static boolean take(@NotNull Player player, int dimension, int exitX, int exitY, int exitZ) {
		AVStashData data = (AVStashData) player;
		if (data.alphaver$hasStash()) {
			return false;
		}
		ListTag items = player.inventory.save(new ListTag());
		int health = Math.max(1, Math.min(player.getMaxHealth(), player.getHealth()));

		String gamemode = Registries.GAMEMODES.getKey(player.getGamemode());
		data.alphaver$setStash(items, dimension, exitX, exitY, exitZ, health, gamemode);
		if (gamemode != null && player.getGamemode() != Gamemodes.SURVIVAL) {
			player.setGamemode(Gamemodes.SURVIVAL);
		}
		player.inventory.load(new ListTag());
		return true;
	}

	public static boolean giveBack(@NotNull Player player) {
		AVStashData data = (AVStashData) player;
		ListTag items = data.alphaver$stashItems();
		if (!data.alphaver$hasStash() || items == null) {
			return false;
		}
		restoreGamemode(player, data.alphaver$stashGamemode());
		player.inventory.load(items);
		data.alphaver$clearStash();
		return true;
	}

	private static void restoreGamemode(@NotNull Player player, @Nullable String key) {
		if (key == null) {
			return;
		}
		Gamemode original = Registries.GAMEMODES.getItem(key);
		if (original != null && original != player.getGamemode()) {
			player.setGamemode(original);
		}
	}

	public static void clearGameItems(@NotNull Player player) {
		if (holding(player)) {
			player.inventory.load(new ListTag());
		}
	}

	public static boolean holding(@Nullable Player player) {
		return player instanceof AVStashData data && data.alphaver$hasStash();
	}

	public static int health(@Nullable Player player) {
		return player instanceof AVStashData data && data.alphaver$hasStash() ? data.alphaver$stashHealth() : HEALTH_FULL;
	}

	public static void carryOver(@Nullable Player from, @Nullable Player to) {
		if (!(from instanceof AVStashData old) || !(to instanceof AVStashData fresh) || !old.alphaver$hasStash()
			|| old.alphaver$stashItems() == null) {
			return;
		}
		fresh.alphaver$setStash(old.alphaver$stashItems(), old.alphaver$stashDimension(), old.alphaver$stashExitX(),
			old.alphaver$stashExitY(), old.alphaver$stashExitZ(), old.alphaver$stashHealth(), old.alphaver$stashGamemode());
	}
}
