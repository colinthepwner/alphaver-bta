package com.alphaver.world.travel;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVMirrorSpawnData;
import com.alphaver.world.AVDimensions;
import com.alphaver.world.AVGameRules;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class AVCypressHome {
	private AVCypressHome() {}

	public static final int NONE = Integer.MIN_VALUE;

	public static boolean active(@Nullable World world) {
		return AVGameRules.startInCypress(world) && AVDimensions.CYPRESS != null && AVBlocks.CYPRESS_DOOR_LOWER != null;
	}

	public static void sendHome(@Nullable Player player) {
		if (player instanceof AVTravelData data) {
			data.alphaver$setHomeTripPending(true);
		}
	}

	public static void tick(@NotNull Player player) {
		if (!(player instanceof AVTravelData data) || !data.alphaver$homeTripPending()) {
			return;
		}
		World world = player.world;
		if (world == null || world.isClientSide) {
			return;
		}
		if (AVDimensions.CYPRESS == null || AVBlocks.CYPRESS_DOOR_LOWER == null || world.dimension == AVDimensions.CYPRESS) {

			data.alphaver$setHomeTripPending(false);
			return;
		}
		if (!player.isAlive() || player.getHealth() <= 0) {
			return;
		}
		player.timeUntilPortal = 0;
		player.timeInPortal = 1.0F;
		player.handlePortal(AVBlocks.CYPRESS_DOOR_LOWER.id(), null);
	}

	public static int respawnDimension(@Nullable World deathWorld, @Nullable Player player) {
		if (player == null || !active(deathWorld)) {
			return NONE;
		}
		if (player.getPlayerSpawnPoint() != null && player instanceof AVTravelData data) {
			int bed = data.alphaver$spawnDimension();
			Dimension dimension = bed == AVTravelData.UNKNOWN_DIMENSION ? Dimension.OVERWORLD : Dimension.getDimensionList().get(bed);
			if (usable(dimension)) {
				return dimension.id;
			}
		}
		if (player instanceof AVMirrorSpawnData mirror && mirror.alphaver$hasMirrorSpawn()) {
			Dimension dimension = Dimension.getDimensionList().get(mirror.alphaver$mirrorDimension());
			if (usable(dimension)) {
				return dimension.id;
			}
		}
		return AVDimensions.CYPRESS.id;
	}

	private static boolean usable(@Nullable Dimension dimension) {
		return dimension != null && dimension != AVDimensions.ZOMBIES && dimension != AVDimensions.FREERUN;
	}

	@NotNull
	public static int[] landingInCypress(@NotNull World cypress, @NotNull Player player) {
		if (player instanceof AVMirrorSpawnData mirror && mirror.alphaver$hasMirrorSpawn()
			&& mirror.alphaver$mirrorDimension() == cypress.dimension.id) {
			AVTravel.loadChunksAround(cypress, mirror.alphaver$mirrorX(), mirror.alphaver$mirrorZ());
			return new int[]{mirror.alphaver$mirrorX(), mirror.alphaver$mirrorY(), mirror.alphaver$mirrorZ()};
		}
		AVTravelData data = (AVTravelData) player;
		if (data.alphaver$hasHome()) {
			return AVTravel.besideDoor(cypress, data.alphaver$homeX(), data.alphaver$homeY(), data.alphaver$homeZ());
		}
		TilePos spawn = cypress.getSpawnPoint();
		int[] spot = AVTravel.cypressLanding(cypress, spawn.x, spawn.z, false);
		data.alphaver$setHome(spot[0], spot[1], spot[2]);
		return spot;
	}

	public static void afterRespawnOutside(@NotNull World world, @NotNull Player player) {
		if (!active(world) || AVWorlds.isCypress(world) || player.getPlayerSpawnPoint() != null) {
			return;
		}
		if (player instanceof AVMirrorSpawnData mirror && mirror.alphaver$hasMirrorSpawn()
			&& mirror.alphaver$mirrorDimension() == world.dimension.id) {
			return;
		}
		sendHome(player);
	}

	public static void carryOver(@Nullable Player from, @Nullable Player to) {
		if (!(from instanceof AVTravelData old) || !(to instanceof AVTravelData fresh)) {
			return;
		}
		if (old.alphaver$hasHome()) {
			fresh.alphaver$setHome(old.alphaver$homeX(), old.alphaver$homeY(), old.alphaver$homeZ());
		}
		if (old.alphaver$homeTripPending()) {
			fresh.alphaver$setHomeTripPending(true);
		}
	}
}
