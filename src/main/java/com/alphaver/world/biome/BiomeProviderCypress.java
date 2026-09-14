package com.alphaver.world.biome;

import com.alphaver.AVConfig;
import com.alphaver.world.gen.CypressBiomeKind;
import com.alphaver.world.gen.CypressBiomeLayers;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.provider.BiomeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public class BiomeProviderCypress extends BiomeProvider {

	public static final int LOW_RIVER_TOP = 8;

	public static final int UNDERGROUND_TOP = 40;

	private final CypressBiomeLayers layers;

	public BiomeProviderCypress(@NotNull World world) {
		super(world);
		this.layers = new CypressBiomeLayers(world.getRandomSeed(), !AVConfig.BIOMES);
	}

	public CypressBiomeLayers layers() {
		return this.layers;
	}

	public static CypressBiomeKind.Layer layerAt(int y) {
		if (y < LOW_RIVER_TOP) {
			return CypressBiomeKind.Layer.LOW_RIVER;
		}
		if (y < UNDERGROUND_TOP) {
			return CypressBiomeKind.Layer.UNDERGROUND;
		}
		return CypressBiomeKind.Layer.SURFACE;
	}

	@Override
	public Biome[] getBiomes(Biome[] biomes, double[] temperatures, double[] humidities, double[] varieties,
	                         int x, int y, int z, int xSize, int ySize, int zSize) {
		if (biomes == null || biomes.length < xSize * ySize * zSize) {
			biomes = new Biome[xSize * ySize * zSize];
		}

		for (int xx = 0; xx < xSize; xx++) {
			for (int zz = 0; zz < zSize; zz++) {
				int chunkX = (x + xx) >> 4;
				int chunkZ = (z + zz) >> 4;
				for (int yy = 0; yy < ySize; yy++) {

					CypressBiomeKind.Layer layer = layerAt(y + yy * 8);
					Biome biome = CypressBiomes.forLayer(layer, this.layers.chunkWinner(chunkX, chunkZ, layer));
					biomes[yy * xSize * zSize + zz * xSize + xx] = biome;
				}
			}
		}

		return biomes;
	}

	@Override
	public double[] getTemperatures(double[] temperatures, int x, int z, int xSize, int zSize) {
		return filled(temperatures, xSize * zSize, 0.5);
	}

	@Override
	public double[] getHumidities(double[] humidities, int x, int z, int xSize, int zSize) {
		return filled(humidities, xSize * zSize, 0.5);
	}

	@Override
	public double[] getVarieties(double[] varieties, int x, int z, int xSize, int zSize) {
		return filled(varieties, xSize * zSize, 0.5);
	}

	@Override
	public double[] getBiomenesses(double[] biomenesses, int x, int y, int z, int xSize, int ySize, int zSize) {
		return filled(biomenesses, xSize * ySize * zSize, 1.0);
	}

	@Override
	public Biome lookupBiome(double temperature, double humidity, double altitude, double variety) {
		return CypressBiomes.PLAINS;
	}

	private static double[] filled(double[] array, int size, double value) {
		if (array == null || array.length < size) {
			array = new double[size];
		}
		Arrays.fill(array, 0, size, value);
		return array;
	}
}
