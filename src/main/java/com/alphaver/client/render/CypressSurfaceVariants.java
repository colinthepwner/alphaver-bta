package com.alphaver.client.render;

import com.alphaver.AVConfig;
import com.alphaver.world.biome.BiomeProviderCypress;
import com.alphaver.world.gen.CypressBiomeKind;
import com.alphaver.world.type.WorldTypeCypress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;

@Environment(EnvType.CLIENT)
public final class CypressSurfaceVariants {
	private CypressSurfaceVariants() {}

	public static final int FIELDS = 1;

	public static final int HIGHWOOD = 2;

	public static int at(WorldSource source, int x, int z) {
		if (AVConfig.VISUALS_1604) {
			return 0;
		}
		if (source == null || source.getWorldType() != WorldTypeCypress.CYPRESS) {
			return 0;
		}
		World world = Minecraft.getMinecraft().currentWorld;
		if (world == null || !(world.getBiomeProvider() instanceof BiomeProviderCypress provider)) {
			return 0;
		}

		CypressBiomeKind kind = provider.layers().surfaceColumn(x, z);
		return kind == null ? 0 : kind.variant;
	}
}
