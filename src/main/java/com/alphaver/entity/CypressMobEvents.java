package com.alphaver.entity;

import com.alphaver.AVConfig;
import com.alphaver.net.AVSounds;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.CypressEvents;
import com.alphaver.world.CypressNames;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

import java.util.Random;

public final class CypressMobEvents {
	private CypressMobEvents() {}

	private static final Random RANDOM = new Random();

	public static final long OBSERVER_COOLDOWN = 5500L;

	public static void tick(Player player) {
		if (!(player instanceof AVMobEventData data)) {
			return;
		}
		if (data.alphaver$observerCooldown() > 0L) {
			data.alphaver$addObserverCooldown(-1L);
		}
		World world = player.world;
		boolean cypress = world != null && AVWorlds.isCypress(world);
		boolean overworld = world != null && world.dimension == Dimension.OVERWORLD
			&& AVConfig.RECRUITERS && AVConfig.OVERWORLD_RECRUITER_CHANCE > 0;
		if (world == null || world.isClientSide || !(cypress || overworld)) {
			data.alphaver$stopTracking();
			return;
		}

		long blockX = (long) player.x;
		long blockZ = (long) player.z;
		long areaX = blockX / CypressNames.AREA_BLOCKS;
		long areaZ = blockZ / CypressNames.AREA_BLOCKS;
		if (!data.alphaver$tracking()) {
			data.alphaver$track(blockX, blockZ, areaX, areaZ);
			return;
		}
		boolean crossedBlock = blockX != data.alphaver$lastBlockX() || blockZ != data.alphaver$lastBlockZ();
		boolean crossedArea = areaX != data.alphaver$lastAreaX() || areaZ != data.alphaver$lastAreaZ();
		data.alphaver$track(blockX, blockZ, areaX, areaZ);

		if (!cypress) {
			if (crossedArea) {
				overworldAreaStep(world, player);
			}
			return;
		}
		if (crossedBlock) {
			observerStep(world, player, data);
		}
		if (crossedArea) {
			areaStep(world, player);
		}
	}

	private static void overworldAreaStep(World world, Player player) {
		if (RANDOM.nextInt(AVConfig.OVERWORLD_RECRUITER_CHANCE) == 0) {
			spawnRecruiter(world, player);
		}
	}

	private static void observerStep(World world, Player player, AVMobEventData data) {
		if (!AVConfig.OBSERVERS || !world.getDifficulty().canHostileMobsSpawn() || !player.getGamemode().hasHostileMobs()
			|| data.alphaver$observerCooldown() > 0L || RANDOM.nextInt(100) >= 5) {
			return;
		}
		long px = (long) player.x;
		long py = (long) player.y;
		long pz = (long) player.z;
		long timeOfDay = world.getWorldTime() % 24000L;
		if (world.getLightBrightness(new TilePos((int) px, (int) py, (int) pz)) >= 0.2F
			|| timeOfDay < 14000L || timeOfDay > 22800L) {
			return;
		}
		MobObserver observer = new MobObserver(world);

		observer.setPos(px, py + 2L, pz);
		world.entityJoinedWorld(observer);

		AVSounds.playFor(player, "alphaver:ext.obvr_spawn", 1.0F, 1.0F);
		data.alphaver$setObserverCooldown(OBSERVER_COOLDOWN);
	}

	private static void areaStep(World world, Player player) {

		if (AVConfig.RECRUITERS && RANDOM.nextInt(100) < 3) {
			spawnRecruiter(world, player);
		}
		if (AVConfig.COLOSSUS && RANDOM.nextInt(100) > 94) {
			cueColossus(world, (int) player.x, (int) player.z);
		}
	}

	@SuppressWarnings("deprecation")
	private static void spawnRecruiter(World world, Player player) {
		MobRecruiter recruiter = new MobRecruiter(world);
		recruiter.markSpawnedThisSession();
		int x = (int) (player.x + (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(60)));
		int y = (int) player.y;
		int z = (int) (player.z + (RANDOM.nextBoolean() ? 1 : -1) * (20 + RANDOM.nextInt(60)));
		while ((world.getBlockId(x, y, z) != 0 || world.getBlockId(x, y + 1, z) != 0) && y < 256) {
			y++;
		}
		while (world.getBlockId(x, y - 1, z) == 0 && y > 0) {
			y--;
		}
		recruiter.setPos(x, y + 2, z);
		world.entityJoinedWorld(recruiter);
	}

	private static void cueColossus(World world, int x, int z) {
		if (colossusAlive(world)) {
			return;
		}
		Random random = new Random();
		int bossX = x + 32 * (random.nextInt(3) - 1);
		int bossZ = z + 32 * (random.nextInt(3) - 1);
		if (bossX == x && bossZ == z) {
			bossZ += 32;
		}
		String name = "Giant of " + CypressNames.area(world.getRandomSeed(), bossX / 32, bossZ / 32);
		MobColossus boss = new MobColossus(world, (int) CypressEvents.milestone(world.getWorldTime()), name);
		boss.moveTo(bossX, 100.0, bossZ, RANDOM.nextFloat() * 360.0F, 0.0F);
		world.entityJoinedWorld(boss);
	}

	private static boolean colossusAlive(World world) {
		for (Entity entity : world.getLoadedEntityList()) {
			if (entity instanceof MobColossus && entity.isAlive() && !entity.removed) {
				return true;
			}
		}
		return false;
	}
}
