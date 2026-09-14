package com.alphaver.world.type;

import com.alphaver.AlphaVer;
import com.alphaver.world.biome.CypressBiomes;
import com.alphaver.world.minigame.ChunkGeneratorMinigame;
import com.alphaver.world.minigame.MinigameKind;
import com.alphaver.world.minigame.MinigameLobby;
import com.alphaver.world.minigame.map.CypressMapStore;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.biome.provider.BiomeProviderSingleBiome;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.core.world.type.overworld.WorldTypeOverworld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.ToLongFunction;

public class WorldTypeMinigame extends WorldType {

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static WorldType ZOMBIES;
	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static WorldType FREERUN;

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	@Nullable
	public static volatile ToLongFunction<World> stageTime;

	public static final long ZOMBIES_DEFAULT_TIME = 22500L;

	public static final long FREERUN_DEFAULT_TIME = 6000L;

	private final MinigameKind kind;

	public WorldTypeMinigame(WorldType.Properties properties, MinigameKind kind) {
		super(properties);
		this.kind = kind;
	}

	@NotNull
	public MinigameKind kind() {
		return this.kind;
	}

	public static void register() {
		ZOMBIES = WorldTypes.register(AlphaVer.MOD_ID + ":zombies", new WorldTypeMinigame(properties("worldType.alphaver.zombies"),
			MinigameKind.ZOMBIES));
		FREERUN = WorldTypes.register(AlphaVer.MOD_ID + ":freerun", new WorldTypeMinigame(properties("worldType.alphaver.freerun"),
			MinigameKind.FREERUN));
		AlphaVer.LOGGER.info("Registered world types '{}:zombies' and '{}:freerun'.", AlphaVer.MOD_ID, AlphaVer.MOD_ID);
	}

	private static WorldType.Properties properties(String languageKey) {
		return WorldType.Properties.of(languageKey)
			.brightnessRamp(WorldTypeOverworld.createLightRamp())
			.bounds(0, 127, 0)
			.portalBounds(0, 127)
			.seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.NULL).build())
			.withNoAurora()
			.allowRespawn();
	}

	@NotNull
	@Override
	public BiomeProvider createBiomeProvider(World world) {
		return new BiomeProviderSingleBiome(world, CypressBiomes.PLAINS, 0.5, 0.5, 0.5);
	}

	@NotNull
	@Override
	public Biome[] allBiomes() {
		return new Biome[]{CypressBiomes.PLAINS};
	}

	@Override
	public ChunkGenerator createChunkGenerator(World world) {

		CypressMapStore.prepare();
		return new ChunkGeneratorMinigame(world, this.kind);
	}

	@Override
	public boolean isValidSpawn(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public void getInitialSpawnLocation(World world, @Nullable Biome startingBiome) {
		world.getLevelData().getSpawnPos().set(MinigameLobby.SPAWN_X, MinigameLobby.FLOOR_Y + 1, MinigameLobby.SPAWN_Z);
	}

	@Override
	public void getRespawnLocation(World world) {
		world.getLevelData().getSpawnPos().set(MinigameLobby.SPAWN_X, MinigameLobby.FLOOR_Y + 1, MinigameLobby.SPAWN_Z);
	}

	public long displayTime(World world) {
		ToLongFunction<World> hook = stageTime;
		long time = hook == null ? -1L : hook.applyAsLong(world);
		if (time >= 0L) {
			return time;
		}
		return this.kind == MinigameKind.ZOMBIES ? ZOMBIES_DEFAULT_TIME : FREERUN_DEFAULT_TIME;
	}

	@Override
	public float getCelestialAngle(World world, long tick, float partialTick) {
		return this.getTimeOfDay(world, this.displayTime(world), 0.0F);
	}

	@Override
	public int getSkyDarken(World world, long tick, float partialTick) {
		float angle = this.getCelestialAngle(world, tick, partialTick);
		float night = 1.0F - (MathHelper.cos(angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
		return (int) (MathHelper.clamp(night, 0.0F, 1.0F) * 11.0F);
	}
}
