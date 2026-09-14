package com.alphaver.world.gen;

import com.alphaver.world.gen.noise.AlphaNoiseOctaves;

import java.util.Random;

public final class CypressBiomeLayers {
	private static final int CACHE_SIZE = 4096;

	private final AlphaNoiseOctaves[] temperature = new AlphaNoiseOctaves[3];
	private final AlphaNoiseOctaves[] humidity = new AlphaNoiseOctaves[3];
	private final AlphaNoiseOctaves myconNoise;
	private final boolean noBiomes;

	private final long[] cacheKeys = new long[CACHE_SIZE];
	private final short[] cacheValues = new short[CACHE_SIZE];

	public CypressBiomeLayers(long seed, boolean noBiomes) {
		Random random = new Random(seed);
		for (int layer = 0; layer < 3; layer++) {
			this.temperature[layer] = new AlphaNoiseOctaves(random, 4);
		}
		for (int layer = 0; layer < 3; layer++) {
			this.humidity[layer] = new AlphaNoiseOctaves(random, 4);
		}
		this.myconNoise = new AlphaNoiseOctaves(random, 6);
		this.noBiomes = noBiomes;
	}

	public AlphaNoiseOctaves myconNoise() {
		return this.myconNoise;
	}

	public CypressBiomeKind classify(int x, int z, CypressBiomeKind.Layer layer) {
		if (this.noBiomes) {
			return CypressBiomeKind.nearest(0.0, 0.0);
		}
		double t = this.temperature[layer.ordinal()].generateNoiseOctaves(x / 32.0, z / 32.0);
		double h = this.humidity[layer.ordinal()].generateNoiseOctaves(x / 32.0, z / 32.0);
		return CypressBiomeKind.nearest(t, h);
	}

	public CypressBiomeKind chunkWinner(int chunkX, int chunkZ, CypressBiomeKind.Layer layer) {
		long key = ((long) chunkX << 32) ^ (chunkZ & 0xFFFFFFFFL);
		int slot = (int) ((key * 0x9E3779B97F4A7C15L) >>> 52) & (CACHE_SIZE - 1);

		synchronized (this.cacheKeys) {
			short packed = this.cacheValues[slot];
			if (packed != 0 && this.cacheKeys[slot] == key) {
				return unpack(packed, layer);
			}
		}

		short packed = (short) (0x4000
			| vote(chunkX, chunkZ, CypressBiomeKind.Layer.SURFACE).ordinal()
			| vote(chunkX, chunkZ, CypressBiomeKind.Layer.UNDERGROUND).ordinal() << 3
			| vote(chunkX, chunkZ, CypressBiomeKind.Layer.LOW_RIVER).ordinal() << 6);

		synchronized (this.cacheKeys) {
			this.cacheKeys[slot] = key;
			this.cacheValues[slot] = packed;
		}
		return unpack(packed, layer);
	}

	private static CypressBiomeKind unpack(short packed, CypressBiomeKind.Layer layer) {
		return CypressBiomeKind.byOrdinal(packed >> (layer.ordinal() * 3) & 7);
	}

	private CypressBiomeKind vote(int chunkX, int chunkZ, CypressBiomeKind.Layer layer) {
		int[] counts = new int[CypressBiomeKind.values().length];
		for (int dx = 0; dx < 16; dx++) {
			for (int dz = 0; dz < 16; dz++) {
				counts[this.classify(chunkX * 16 + dx, chunkZ * 16 + dz, layer).ordinal()]++;
			}
		}

		int best = 0;
		int winner = 0;
		for (int i = 0; i < counts.length; i++) {
			if (counts[i] > best) {
				best = counts[i];
				winner = i;
			}
		}
		return CypressBiomeKind.byOrdinal(winner);
	}
}
