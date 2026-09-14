package com.alphaver.client.render;

import com.alphaver.AVConfig;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.CypressEvents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.Shaders;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.world.World;
import org.joml.Vector3fc;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class CypressSky {
	private CypressSky() {}

	public static final String SUN = "/assets/alphaver/textures/environment/sun.png";
	public static final String SUN_NEBULA = "/assets/alphaver/textures/environment/sun_nebula.png";
	public static final String MOON = "/assets/alphaver/textures/environment/moon.png";
	public static final String MOON_NEBULA = "/assets/alphaver/textures/environment/moon_nebula.png";

	private static final long RECHECK_MILLIS = 2000L;
	private static final Map<String, Boolean> AVAILABLE = new HashMap<>();
	private static long checkedAt;

	public static final int SKY_DAY_STANDARD = 0x88BBFF;

	public static final int SKY_DAY_1604 = 0xC7DEFC;

	public static final int SKY_DAY_NEBULA = 0x001B40;

	public static int daySkyColor(long worldTime) {
		if (CypressEvents.isNebula(worldTime)) {
			return SKY_DAY_NEBULA;
		}
		return AVConfig.VISUALS_1604 ? SKY_DAY_1604 : SKY_DAY_STANDARD;
	}

	private static final float STAR_SCALE = 0.70710677F;

	public static float starBrightness(float btaBrightness) {
		return (float) Math.sqrt(Math.max(0.0F, btaBrightness)) * STAR_SCALE;
	}

	private static final double PLANE_Y = -16.0;
	private static final int PLANE_CELL = 64;
	private static final int PLANE_REACH = 384;

	public static void renderVoidPlane(Minecraft mc, WorldClient world, float partialTick) {
		Vector3fc sky = world.getSkyColor(mc.activeCamera, partialTick);
		GLRenderer.pushFrame();
		mc.worldRenderer.fogManager.setupFog(-1, mc.worldRenderer.farPlaneDistance, partialTick, GLRenderer.getFogState());
		GLRenderer.setShader(Shaders.COLOR_WORLD);
		GLRenderer.setDepthMask(false);
		GLRenderer.setColor3f(sky.x() * 0.2F + 0.04F, sky.y() * 0.2F + 0.04F, sky.z() * 0.6F + 0.1F);
		TessellatorGeneral tessellator = GLRenderer.getTessellator();
		tessellator.startDrawingQuads();
		for (int x = -PLANE_REACH; x <= PLANE_REACH; x += PLANE_CELL) {
			for (int z = -PLANE_REACH; z <= PLANE_REACH; z += PLANE_CELL) {
				tessellator.addVertex(x + PLANE_CELL, PLANE_Y, z);
				tessellator.addVertex(x, PLANE_Y, z);
				tessellator.addVertex(x, PLANE_Y, z + PLANE_CELL);
				tessellator.addVertex(x + PLANE_CELL, PLANE_Y, z + PLANE_CELL);

				tessellator.addVertex(x + PLANE_CELL, PLANE_Y, z + PLANE_CELL);
				tessellator.addVertex(x, PLANE_Y, z + PLANE_CELL);
				tessellator.addVertex(x, PLANE_Y, z);
				tessellator.addVertex(x + PLANE_CELL, PLANE_Y, z);
			}
		}
		tessellator.draw();
		GLRenderer.setDepthMask(true);
		GLRenderer.popFrame();
	}

	public static String sun(String vanilla) {
		return pick(vanilla, SUN, SUN_NEBULA);
	}

	public static String moon(String vanilla) {
		return pick(vanilla, MOON, MOON_NEBULA);
	}

	private static String pick(String vanilla, String plain, String nebula) {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc.currentWorld;
		if (!AVWorlds.isCypress(world)) {
			return vanilla;
		}
		if (CypressEvents.isNebula(world.getWorldTime()) && available(mc, nebula)) {
			return nebula;
		}
		return available(mc, plain) ? plain : vanilla;
	}

	static boolean available(Minecraft mc, String path) {
		long now = System.currentTimeMillis();
		if (now - checkedAt > RECHECK_MILLIS) {
			AVAILABLE.clear();
			checkedAt = now;
		}
		Boolean known = AVAILABLE.get(path);
		if (known == null) {
			known = mc.texturePackList != null && mc.texturePackList.anyHasFile(path);
			AVAILABLE.put(path, known);
		}
		return known;
	}
}
