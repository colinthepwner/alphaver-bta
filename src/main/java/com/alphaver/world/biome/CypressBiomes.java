package com.alphaver.world.biome;

import com.alphaver.AlphaVer;
import com.alphaver.world.gen.CypressBiomeKind;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import net.minecraft.core.world.biome.SurfaceProperties;

public final class CypressBiomes {
	private CypressBiomes() {}

	public static Biome PLAINS;
	public static Biome FIELDS;
	public static Biome HIGHWOOD;
	public static Biome MYCON;
	public static Biome CAVES;
	public static Biome LOW_RIVER;

	private static Biome[] all = new Biome[0];

	public static void init() {
		PLAINS = register("plains", 0x7EB05A);
		FIELDS = register("fields", 0x92C26A);
		HIGHWOOD = register("highwood", 0x3E7A3A);
		MYCON = register("mycon", 0x5AA88E);
		CAVES = register("caves", 0x6B6B6B);
		LOW_RIVER = register("low_river", 0x3F6E7A);
		all = new Biome[]{PLAINS, FIELDS, HIGHWOOD, MYCON, CAVES, LOW_RIVER};
		AlphaVer.LOGGER.info("Registered {} Cypress biomes.", all.length);
	}

	private static Biome register(String name, int debugColor) {
		String key = "cypress." + name;
		return Biomes.register(AlphaVer.MOD_ID + ":" + key,
			new BiomeCypress(AlphaVer.MOD_ID + "." + key)
				.withDebugColor(debugColor)
				.withSurfaceProperties(new SurfaceProperties.Builder().withTopBlock(Blocks.GRASS_RETRO).build()));
	}

	public static Biome[] all() {
		return all.clone();
	}

	public static Biome forLayer(CypressBiomeKind.Layer layer, CypressBiomeKind kind) {
		switch (layer) {
			case SURFACE:
				switch (kind) {
					case SURFACE_1:
						return FIELDS;
					case SURFACE_2:
						return HIGHWOOD;
					case MYCON:
						return MYCON;
					default:
						return PLAINS;
				}
			case UNDERGROUND:
				return CAVES;
			case LOW_RIVER:
			default:
				return kind == CypressBiomeKind.MYCON ? MYCON : LOW_RIVER;
		}
	}
}
