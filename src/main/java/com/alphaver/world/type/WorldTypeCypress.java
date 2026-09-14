package com.alphaver.world.type;

import com.alphaver.AlphaVer;
import com.alphaver.world.CypressEvents;
import com.alphaver.world.biome.BiomeProviderCypress;
import com.alphaver.world.biome.CypressBiomes;
import com.alphaver.world.gen.ChunkGeneratorCypress;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import net.minecraft.core.world.config.season.SeasonConfig;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.season.Seasons;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypes;
import net.minecraft.core.world.type.overworld.WorldTypeOverworld;
import org.jetbrains.annotations.NotNull;

public class WorldTypeCypress extends WorldTypeOverworld {

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static WorldType CYPRESS;

	public WorldTypeCypress(WorldType.Properties properties) {
		super(properties);
	}

	public static void register() {
		CYPRESS = WorldTypes.register(
			AlphaVer.MOD_ID + ":cypress",
			new WorldTypeCypress(
				WorldTypeOverworld.defaultProperties("worldType.alphaver.cypress")
					.fillerBlock(Blocks.STONE)
					.seasonConfig(SeasonConfig.builder().withSingleSeason(Seasons.NULL).build())
					.bounds(0, 127, 64)
					.portalBounds(0, 127)
					.withNoAurora()));

		AlphaVer.LOGGER.info("Registered world type '{}:cypress'.", AlphaVer.MOD_ID);
	}

	@NotNull
	@Override
	public BiomeProvider createBiomeProvider(World world) {
		return new BiomeProviderCypress(world);
	}

	@Override
	public ChunkGenerator createChunkGenerator(World world) {
		AlphaVer.LOGGER.info("Creating Cypress chunk generator for dimension {}.", world.dimension);
		return new ChunkGeneratorCypress(world);
	}

	@NotNull
	@Override
	public Biome[] allBiomes() {
		return CypressBiomes.all();
	}

	@Override
	public int getSkyDarken(World world, long tick, float partialTick) {
		if (!CypressEvents.isNebula(tick)) {
			return super.getSkyDarken(world, tick, partialTick);
		}
		float angle = this.getCelestialAngle(world, tick, partialTick);
		float night = 1.0F - (MathHelper.cos(angle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
		night = MathHelper.clamp(night, 0.0F, 1.0F);
		return 8 + (int) (night * 3.0F);
	}
}
