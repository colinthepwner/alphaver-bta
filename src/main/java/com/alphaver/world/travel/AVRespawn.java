package com.alphaver.world.travel;

import com.alphaver.item.AVMirrorSpawnData;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public final class AVRespawn {
	private AVRespawn() {}

	@Nullable
	public static int[] doorFor(@Nullable World deathWorld, @Nullable Player player, boolean mirrorCanHold) {
		if (!leavesDimension(deathWorld, player, mirrorCanHold)) {
			return null;
		}
		if (!(player instanceof AVTravelData data) || !data.alphaver$hasReturn()) {
			return null;
		}
		return new int[]{data.alphaver$returnX(), data.alphaver$returnY(), data.alphaver$returnZ()};
	}

	public static boolean leavesDimension(@Nullable World deathWorld, @Nullable Player player, boolean mirrorCanHold) {
		if (deathWorld == null || player == null || !AVWorlds.isAlphaVer(deathWorld)) {
			return false;
		}
		return !(mirrorCanHold && player instanceof AVMirrorSpawnData mirror && mirror.alphaver$hasMirrorSpawn()
			&& mirror.alphaver$mirrorDimension() == deathWorld.dimension.id);
	}
}
