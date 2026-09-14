package com.alphaver.world.gen;

import com.alphaver.AVConfig;
import com.alphaver.AlphaVer;
import com.alphaver.block.AVBlocks;
import com.alphaver.world.biome.BiomeProviderCypress;
import com.alphaver.world.gen.city.CypressCities;
import com.alphaver.world.gen.feature.CypressScatter;
import com.alphaver.world.gen.feature.CypressTreeFeature;
import com.alphaver.world.gen.feature.FeatureBigTree;
import com.alphaver.world.gen.feature.FeatureFrozen;
import com.alphaver.world.gen.feature.FeatureLimestonePlatform;
import com.alphaver.world.gen.feature.FeatureLowRiver;
import com.alphaver.world.gen.feature.FeatureMycon;
import com.alphaver.world.gen.feature.FeatureRooms;
import com.alphaver.world.gen.feature.FeatureTree;
import com.alphaver.world.gen.feature.FeatureVein;
import net.minecraft.core.block.BlockLogicFallingBlock;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

@SuppressWarnings("deprecation")
public class ChunkDecoratorCypress implements ChunkDecorator {
	private static final int HEIGHT = CypressTerrain.HEIGHT;

	@NotNull
	private final World world;
	@NotNull
	private final CypressTerrain terrain;
	@NotNull
	private final CypressCities cities;
	private CypressBiomeLayers fallbackLayers;

	public ChunkDecoratorCypress(@NotNull World world, @NotNull CypressTerrain terrain) {
		this.world = world;
		this.terrain = terrain;

		this.cities = new CypressCities(terrain.constructorRandom());
	}

	@Override
	public void decorate(@NotNull Chunk chunk) {
		int chunkX = chunk.pos.x;
		int chunkZ = chunk.pos.z;
		boolean wasInstant = BlockLogicFallingBlock.fallInstantly;
		BlockLogicFallingBlock.fallInstantly = true;
		try {
			this.populate(chunkX, chunkZ);
		} catch (RuntimeException | StackOverflowError e) {

			AlphaVer.LOGGER.error("Cypress decoration failed at chunk ({}, {}); continuing.", chunkX, chunkZ, e);
		} finally {
			BlockLogicFallingBlock.fallInstantly = wasInstant;
		}
	}

	private void populate(int chunkX, int chunkZ) {
		CypressBiomeLayers layers = this.layers();
		CypressBiomeKind surface = layers.chunkWinner(chunkX, chunkZ, CypressBiomeKind.Layer.SURFACE);
		CypressBiomeKind underground = layers.chunkWinner(chunkX, chunkZ, CypressBiomeKind.Layer.UNDERGROUND);
		CypressBiomeKind lowRiver = layers.chunkWinner(chunkX, chunkZ, CypressBiomeKind.Layer.LOW_RIVER);

		int bx = chunkX * 16;
		int bz = chunkZ * 16;
		long seed = this.world.getRandomSeed();
		Random rand = new Random(seed);
		long a = rand.nextLong() / 2L * 2L + 1L;
		long b = rand.nextLong() / 2L * 2L + 1L;
		rand.setSeed(chunkX * a + chunkZ * b ^ seed);

		try {
			this.cities.generate(this.world, chunkX, chunkZ, this.terrain.isSandWorld());
		} catch (RuntimeException e) {

			AlphaVer.LOGGER.error("Cypress city generation failed at chunk ({}, {}); continuing.", chunkX, chunkZ, e);
		}

		if (this.biomeDecorates(surface, rand, chunkX, chunkZ, layers)) {
			this.surface(rand, bx, bz, surface.isHighwoodVariant());
		}

		for (int column = 0; column != 8; column++) {
			for (int row = 0; row != 4; row++) {
				int x = bx + column * 2;
				int y = 5 + rand.nextInt(2);
				int z = bz + row * 4;
				new FeatureLowRiver(2 + rand.nextInt(3), bx, bz).generate(this.world, rand, x, y, z);
				this.biomeDecorates(lowRiver, rand, chunkX, chunkZ, layers);
			}
		}

		if (this.biomeDecorates(underground, rand, chunkX, chunkZ, layers)) {
			this.underground(rand, bx, bz);
		}
	}

	private boolean biomeDecorates(CypressBiomeKind kind, Random rand, int chunkX, int chunkZ, CypressBiomeLayers layers) {
		if (kind != CypressBiomeKind.MYCON) {
			return true;
		}
		FeatureMycon.generate(this.world, rand, chunkX, chunkZ, layers.myconNoise());
		return false;
	}

	private void surface(Random rand, int bx, int bz, boolean highwood) {

		int snowLayer = Blocks.LAYER_SNOW.id();
		scan:
		for (int i = 0; i < 16; i++) {
			for (int j = 0; j < 16; j++) {
				for (int y = 127; y > 97; y--) {
					if (rand.nextInt(70) == 0 && this.world.getBlockId(bx + i, y, bz + j) == snowLayer) {
						this.world.setBlock(bx + i, y + 1, bz + j, Blocks.ICE.id());
						break scan;
					}
				}
			}
		}

		CypressTreeFeature tree = highwood && rand.nextInt(40) == 0 ? new FeatureTree(true) : new FeatureTree(null);
		if (rand.nextInt(10) == 0) {
			tree = highwood && rand.nextInt(50) == 0 ? new FeatureBigTree(true) : new FeatureBigTree(null);
		}
		for (int i = 0; i < 3; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int z = bz + rand.nextInt(16) + 8;
			tree.setScale(1.0, 1.0, 1.0);
			tree.generate(this.world, rand, x, this.world.getHeightValue(x, z), z);
		}
		for (int i = 0; i < 3; i++) {
			int x = bx + rand.nextInt(8) + 8;
			int z = bz + rand.nextInt(8) + 8;
			FeatureFrozen.frigidHighwood(this.world, rand, x, this.world.getHeightValue(x, z), z);
		}

		for (int i = 0; i < 2; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, Blocks.FLOWER_RED);
		}
		for (int i = 0; i < 100; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, AVBlocks.TALLGRASS);
		}
		if (rand.nextInt(2) == 0) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, Blocks.FLOWER_YELLOW);
		}
		if (rand.nextInt(2) == 0) {

			int x = bx + rand.nextInt(8) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(8) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, AVBlocks.HYDRANGEA);
		}
		if (rand.nextInt(4) == 0) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, Blocks.MUSHROOM_BROWN);
		}
		if (rand.nextInt(8) == 0) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, Blocks.MUSHROOM_RED);
		}

		rand.nextInt(10);

		if (FeatureLimestonePlatform.isChunkWaterOnly(this.world, bx, bz)) {
			for (int i = 0; i != 50; i++) {
				if (rand.nextInt(8) == 0) {
					int x = bx + rand.nextInt(16) + 8;
					int y = 50 + rand.nextInt(16);
					int z = bz + rand.nextInt(16) + 8;
					FeatureLimestonePlatform.generate(this.world, rand, x, y, z);
				}
			}
		}
		for (int i = 0; i < 4; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.saltPillars(this.world, rand, x, y, z);
		}
		for (int i = 0; i < 5; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.onWater(this.world, rand, x, y, z, AVBlocks.WATER_LILY);
		}
		for (int i = 0; i < 5; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(10);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.plants(this.world, rand, x, y, z, AVBlocks.LICHEN);
		}
		for (int i = 0; i < 16; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.sunkenMetal(this.world, rand, x, y, z);
		}
		for (int i = 0; i < 3; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.celestialFlames(this.world, rand, x, y, z);
		}

		for (int i = 0; i < 10; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.reeds(this.world, rand, x, y, z);
		}
		{
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.cacti(this.world, rand, x, y, z);
		}
		for (int i = 0; i < 50; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(rand.nextInt(120) + 8);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.spring(this.world, x, y, z, Blocks.FLUID_WATER_FLOWING);
		}
		for (int i = 0; i < 20; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(rand.nextInt(rand.nextInt(112) + 8) + 8);
			int z = bz + rand.nextInt(16) + 8;
			CypressScatter.spring(this.world, x, y, z, Blocks.FLUID_LAVA_FLOWING);
		}

	}

	private void underground(Random rand, int bx, int bz) {

		rand.nextDouble();

		for (int i = 0; i < 8; i++) {
			int x = bx + rand.nextInt(16) + 8;
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16) + 8;
			FeatureRooms.dungeon(this.world, rand, x, y, z);
		}
		{
			int x = bx + rand.nextInt(64) + 8;
			int y = rand.nextInt(5) + 55;
			int z = bz + rand.nextInt(64) + 8;
			FeatureRooms.floodedVault(this.world, rand, x, y, z);
		}
		for (int i = 0; i < 10; i++) {
			int x = bx + rand.nextInt(16);
			int y = rand.nextInt(HEIGHT);
			int z = bz + rand.nextInt(16);
			FeatureVein.clay(this.world, rand, x, y, z, 32);
		}

		this.veins(rand, bx, bz, 20, HEIGHT, Blocks.DIRT.id(), 0, 32);
		this.veins(rand, bx, bz, 10, HEIGHT, Blocks.GRAVEL.id(), 0, 32);
		this.veins(rand, bx, bz, 20, HEIGHT, Blocks.ORE_COAL_STONE.id(), AVBlocks.LOW_ORE_COAL.id(), 16);
		this.veins(rand, bx, bz, 20, 64, Blocks.ORE_IRON_STONE.id(), AVBlocks.LOW_ORE_IRON.id(), 8);
		this.veins(rand, bx, bz, 2, 32, Blocks.ORE_GOLD_STONE.id(), AVBlocks.LOW_ORE_GOLD.id(), 8);
		this.veins(rand, bx, bz, 8, 16, AVBlocks.ORE_GREENSTONE.id(), AVBlocks.LOW_ORE_GREENSTONE.id(), 7);
		this.veins(rand, bx, bz, 4, 16, Blocks.ORE_DIAMOND_STONE.id(), AVBlocks.LOW_ORE_DIAMOND.id(), 7);
		this.veins(rand, bx, bz, 4, 10, AVBlocks.ORE_LACE_AGATE.id(), AVBlocks.ORE_LACE_AGATE.id(), 3);
		this.veins(rand, bx, bz, 4, 10, AVBlocks.ORE_CLINOHUMITE.id(), AVBlocks.ORE_CLINOHUMITE.id(), 3);
		this.veins(rand, bx, bz, 4, 10, AVBlocks.ORE_MALACHITE.id(), AVBlocks.ORE_MALACHITE.id(), 3);
		this.veins(rand, bx, bz, 4, 10, AVBlocks.ORE_PYRITE.id(), AVBlocks.ORE_PYRITE.id(), 3);
		this.veins(rand, bx, bz, 4, 16, AVBlocks.ORE_BISMUTH.id(), AVBlocks.ORE_BISMUTH.id(), 7);
	}

	private void veins(Random rand, int bx, int bz, int count, int maxY, int id, int lowId, int size) {
		for (int i = 0; i < count; i++) {
			int x = bx + rand.nextInt(16);
			int y = rand.nextInt(maxY);
			int z = bz + rand.nextInt(16);
			FeatureVein.ore(this.world, rand, x, y, z, id, lowId, size);
		}
	}

	private CypressBiomeLayers layers() {
		if (this.world.getBiomeProvider() instanceof BiomeProviderCypress provider) {
			return provider.layers();
		}
		if (this.fallbackLayers == null) {
			this.fallbackLayers = new CypressBiomeLayers(this.world.getRandomSeed(), !AVConfig.BIOMES);
		}
		return this.fallbackLayers;
	}
}
