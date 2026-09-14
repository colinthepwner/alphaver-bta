package com.alphaver.entity;

import com.alphaver.AVConfig;
import com.alphaver.net.MessageLilypadHunger;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.Difficulty;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;
import turniplabs.halplibe.helper.network.NetworkHandler;

public final class LilypadHunger {
	private LilypadHunger() {}

	public static final int TICKS_TO_EMPTY = 1200;

	public static final int DAMAGE = 2;

	public static final int TICKS_PER_STAGE = 300;

	public static final int STAGES = 4;

	public static final int HIDDEN = -1;

	private static volatile int receivedStage = HIDDEN;

	public static boolean appliesTo(@NotNull Player player) {
		World world = player.world;
		return AVConfig.LILYPAD_HUNGER && world != null && AVWorlds.isAlphaVer(world) && !AVWorlds.isMinigame(world)
			&& !CypressFrail.active(world) && !player.getGamemode().hasInvulnerablePlayer() && !AVInventoryStash.holding(player);
	}

	public static int stageOf(int hunger) {
		return Math.min(STAGES - 1, Math.max(0, hunger / TICKS_PER_STAGE));
	}

	public static void tick(@NotNull Player player) {
		World world = player.world;
		if (world == null || world.isClientSide) {
			return;
		}
		AVLilypadHungerData data = (AVLilypadHungerData) player;
		if (!appliesTo(player) || !player.isAlive()) {
			data.alphaver$setHunger(0);
			sync(player, data, HIDDEN);
			return;
		}

		if (world.getDifficulty() != Difficulty.PEACEFUL) {
			int hunger = data.alphaver$hunger() + 1;
			if (hunger >= TICKS_TO_EMPTY) {

				data.alphaver$setHunger(0);
				player.hurt(null, DAMAGE, DamageType.GENERIC);
			} else {
				data.alphaver$setHunger(hunger);
			}
		}
		sync(player, data, stageOf(data.alphaver$hunger()));
	}

	public static void refill(@NotNull Player player) {
		World world = player.world;
		if (world == null || world.isClientSide) {
			return;
		}
		AVLilypadHungerData data = (AVLilypadHungerData) player;
		data.alphaver$setHunger(0);
		sync(player, data, appliesTo(player) ? 0 : HIDDEN);
	}

	public static int displayStage(@Nullable Player player) {
		if (player == null || player.world == null) {
			return HIDDEN;
		}
		if (player.world.isClientSide) {

			return AVWorlds.isAlphaVer(player.world) && !AVWorlds.isMinigame(player.world) && !player.getGamemode().hasInvulnerablePlayer()
				? receivedStage : HIDDEN;
		}
		return appliesTo(player) && player.isAlive() ? stageOf(((AVLilypadHungerData) player).alphaver$hunger()) : HIDDEN;
	}

	public static void receive(int stage) {
		receivedStage = stage < HIDDEN || stage >= STAGES ? HIDDEN : stage;
	}

	private static void sync(@NotNull Player player, @NotNull AVLilypadHungerData data, int stage) {
		if (data.alphaver$hungerSynced() && data.alphaver$sentHungerStage() == stage && data.alphaver$sentHungerWorld() == player.world) {
			return;
		}
		data.alphaver$markHungerSent(stage, player.world);

		if (EnvironmentHelper.isMultiplayerServer()) {
			NetworkHandler.sendToPlayer(player, new MessageLilypadHunger(stage));
		}
	}
}
