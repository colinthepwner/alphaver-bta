package com.alphaver.world.type;

import com.alphaver.AlphaVer;
import com.alphaver.world.biome.CypressBiomes;
import com.alphaver.world.hub.ChunkGeneratorHub;
import com.alphaver.world.hub.HubLayout;
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

public class WorldTypeHub extends WorldType {

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static WorldType HUB;

	public WorldTypeHub(WorldType.Properties properties) {
		super(properties);
	}

	public static void register() {
		HUB = WorldTypes.register(
			AlphaVer.MOD_ID + ":hub",
			new WorldTypeHub(
				WorldType.Properties.of("worldType.alphaver.hub")
					.brightnessRamp(WorldTypeOverworld.createLightRamp())
					.bounds(0, 127, 0)
					.portalBounds(0, 127)
					.seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.NULL).build())
					.withNoAurora()
					.allowRespawn()));
		AlphaVer.LOGGER.info("Registered world type '{}:hub'.", AlphaVer.MOD_ID);
	}

	@NotNull
	@Override
	public BiomeProvider createBiomeProvider(World world) {
		return new BiomeProviderSingleBiome(world, CypressBiomes.CAVES, 0.5, 0.5, 0.5);
	}

	@NotNull
	@Override
	public Biome[] allBiomes() {
		return new Biome[]{CypressBiomes.CAVES};
	}

	@Override
	public ChunkGenerator createChunkGenerator(World world) {
		return new ChunkGeneratorHub(world);
	}

	@Override
	public boolean isValidSpawn(World world, int x, int y, int z) {
		return true;
	}

	@Override
	public void getInitialSpawnLocation(World world, @Nullable Biome startingBiome) {
		world.getLevelData().getSpawnPos().set(HubLayout.SPAWN_X, HubLayout.FLOOR_Y + 1, HubLayout.SPAWN_Z);
	}

	@Override
	public void getRespawnLocation(World world) {
		world.getLevelData().getSpawnPos().set(HubLayout.SPAWN_X, HubLayout.FLOOR_Y + 1, HubLayout.SPAWN_Z);
	}

	@Override
	public float getCelestialAngle(World world, long tick, float partialTick) {
		return this.getTimeOfDay(world, tick, partialTick);
	}

	@Override
	public int getSkyDarken(World world, long tick, float partialTick) {
		float angle = this.getCelestialAngle(world, tick, partialTick);
		float night = 1.0F - (MathHelper.cos(angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
		return (int) (MathHelper.clamp(night, 0.0F, 1.0F) * 11.0F);
	}
}
