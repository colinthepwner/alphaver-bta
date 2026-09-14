package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.world.type.WorldTypeHub;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.worldtype.WorldTypeFX;
import net.minecraft.client.render.worldtype.WorldTypeFXDispatcher;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Environment(EnvType.CLIENT)
public class WorldTypeFXHub extends WorldTypeFX {

	private static final float FOG_R = 0.0F;
	private static final float FOG_G = 6.0F / 255.0F;
	private static final float FOG_B = 31.0F / 255.0F;

	public WorldTypeFXHub(WorldType worldType) {
		super(worldType);
		this.setHasClouds(false);
		this.setHasAurora(false);
	}

	@Nullable
	@Override
	public float[] getSunriseColor(float timeOfDay, float partialTick) {
		return null;
	}

	@NotNull
	@Override
	public Vector3fc getFogColor(@NotNull World world, double x, double y, double z, float celestialAngle, float partialTick) {
		float day = MathHelper.clamp(MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F, 0.0F, 1.0F);
		float r = FOG_R * (day * 0.94F + 0.06F);
		float g = FOG_G * (day * 0.94F + 0.06F);
		float b = FOG_B * (day * 0.91F + 0.09F);
		return new Vector3f(r, g, b);
	}

	public static void register() {
		WorldType worldType = WorldTypeHub.HUB;
		if (worldType == null) {
			AlphaVer.LOGGER.error("Hub sky effects asked for before the world type exists.");
			return;
		}
		WorldTypeFXDispatcher dispatcher = WorldTypeFXDispatcher.getInstance();
		if (dispatcher.hasDispatch(worldType)) {
			return;
		}
		dispatcher.addDispatch(new WorldTypeFXHub(worldType));
		AlphaVer.LOGGER.info("Registered Hub sky effects.");
	}
}
