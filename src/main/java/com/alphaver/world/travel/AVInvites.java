package com.alphaver.world.travel;

import com.alphaver.world.AVDimensions;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

	@SuppressWarnings("unchecked")
	private static <T extends Throwable> RuntimeException sneakyThrow(Throwable throwable) throws T {
		throw (T) throwable;
	}
}
