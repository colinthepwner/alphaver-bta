package com.alphaver.world.travel;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVDimensions;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.Dimension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public final class AVInvites {
	private AVInvites() {}

	public static final String REFUSAL = "You were not invited!";

	private static final SimpleCommandExceptionType NOT_INVITED = new SimpleCommandExceptionType(new LiteralMessage(REFUSAL));

	public static boolean enforced() {
		return !FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static boolean invited(@Nullable Player player) {
		return player instanceof AVTravelData data && data.alphaver$invited();
	}

	public static void foundDoor(@Nullable Entity entity) {
		if (entity instanceof Player && entity instanceof AVTravelData data) {
			data.alphaver$setInvited(true);
		}
	}

	public static void checkCommandMove(@Nullable Player sender, @NotNull Player player, int dimension) {
		if (!enforced() || invited(player) || !AVDimensions.isAlphaVer(Dimension.getDimensionList().get(dimension))) {
			return;
		}
		if (sender != player) {
			player.sendMessage(REFUSAL);
		}
		throw sneakyThrow(NOT_INVITED.create());
	}

	public static boolean isAlphaVer(@Nullable ItemStack stack) {
		if (stack == null) {
			return false;
		}
		Item item = stack.getItem();
		return item != null && item.namespaceID != null && AlphaVer.MOD_ID.equals(item.namespaceID.namespace());
	}

	public static boolean isAlphaVer(@Nullable Block<?> block) {
		return block != null && AlphaVer.MOD_ID.equals(block.namespaceId().namespace());
	}

	private static boolean mayCheat(@NotNull Player player) {
		return invited(player) || AVDimensions.isAlphaVer(player.world);
	}

	public static void checkGive(@Nullable Player sender, @NotNull List<? extends Entity> targets, @Nullable ItemStack stack) {
		if (!enforced() || !isAlphaVer(stack)) {
			return;
		}
		for (Entity target : targets) {
			if (target instanceof Player player && !mayCheat(player)) {
				if (sender != player) {
					player.sendMessage(REFUSAL);
				}
				throw sneakyThrow(NOT_INVITED.create());
			}
		}
	}

	public static void checkPlace(@Nullable Player sender, @Nullable Block<?> block) {
		if (!enforced() || sender == null || !isAlphaVer(block) || mayCheat(sender)) {
			return;
		}
		throw sneakyThrow(NOT_INVITED.create());
	}

	public static boolean mayPick(@NotNull Player player, @Nullable Block<?> block) {
		return !enforced() || !isAlphaVer(block) || mayCheat(player);
	}

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws T {
		throw (T) throwable;
	}
}
