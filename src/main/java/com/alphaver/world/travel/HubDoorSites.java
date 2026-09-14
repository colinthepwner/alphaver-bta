package com.alphaver.world.travel;

import com.alphaver.AVConfig;
import com.alphaver.world.hub.HubLayout;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public final class HubDoorSites {
	private HubDoorSites() {}

	private static final long SALT = 0x4855424F4F52L;

	public record Site(int x, int z, boolean axisX) {}

	@Nullable
	public static Site site(long seed, int chunkX, int chunkZ) {
		if (AVConfig.HUB_DOOR_CHANCE <= 0) {
			return null;
		}
		Random rand = new Random(seed ^ (chunkX * 341873128712L + chunkZ * 132897987541L) ^ SALT);
		if (rand.nextInt(AVConfig.HUB_DOOR_CHANCE) != 0) {
			return null;
		}
		int x = doorColumn(rand, chunkX);
		int z = doorColumn(rand, chunkZ);
		boolean axisX = rand.nextBoolean();
		if (Math.floorDiv(x, HubLayout.CELL) == 0 && Math.floorDiv(z, HubLayout.CELL) == 0) {
			return null;
		}
		return new Site(x, z, axisX);
	}

	private static int doorColumn(Random rand, int chunk) {
		int min = chunk * 16;
		int first = min + Math.floorMod(HubLayout.DOOR_IN_CELL - min, HubLayout.CELL);
		int count = (min + 15 - first) / HubLayout.CELL + 1;
		return first + HubLayout.CELL * rand.nextInt(count);
	}
}
