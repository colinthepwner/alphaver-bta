package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.world.type.WorldTypeCypress;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.enums.CloudQuality;
import net.minecraft.client.render.worldtype.WorldTypeFXDispatcher;
import net.minecraft.client.render.worldtype.WorldTypeFXOverworld;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public class WorldTypeFXCypress extends WorldTypeFXOverworld {

	private static final float FOG_R = 0xC0 / 255.0F;
	private static final float FOG_G = 0xD8 / 255.0F;
	private static final float FOG_B = 0xFF / 255.0F;

	private static final float FANCY_CLOUD_HEIGHT = 108.0F;

	private static final float FAST_CLOUD_HEIGHT = 120.0F;

	public WorldTypeFXCypress(WorldType worldType) {
		super(worldType);
		this.setHasAurora(false);
	}

	@Override
	public float getCloudHeight(@NotNull World world) {
		return GameSettings.CLOUD_QUALITY.value == CloudQuality.FANCY ? FANCY_CLOUD_HEIGHT : FAST_CLOUD_HEIGHT;
	}

	@Nullable
	@Override
	public float[] getSunriseColor(float timeOfDay, float partialTick) {
		return null;
	}

	@NotNull
	@Override
	public Vector3fc getFogColor(@NotNull World world, double x, double y, double z, float celestialAngle,
	                             float partialTick) {
		float day = MathHelper.clamp(MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F,
			0.0F, 1.0F);
		float r = FOG_R * (day * 0.94F + 0.06F);
		float g = FOG_G * (day * 0.94F + 0.06F);
		float b = FOG_B * (day * 0.91F + 0.09F);
		return new Vector3f(r, g, b);
	}

	public static void register() {
		WorldType worldType = WorldTypeCypress.CYPRESS;
		if (worldType == null) {
			AlphaVer.LOGGER.error("World type FX asked for before the world type exists; Cypress will render "
				+ "with the void dimension's sky.");
			return;
		}
		WorldTypeFXDispatcher dispatcher = WorldTypeFXDispatcher.getInstance();
		if (dispatcher.hasDispatch(worldType)) {
			return;
		}
		dispatcher.addDispatch(new WorldTypeFXCypress(worldType));
		AlphaVer.LOGGER.info("Registered Cypress sky effects.");
	}
}
