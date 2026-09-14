package com.alphaver.client.render;

import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;

@Environment(EnvType.CLIENT)
public final class HubSky {
	private HubSky() {}

	public static final String PARALLAX = "/assets/alphaver/textures/environment/hub_parallax.png";

	private static final float HALF = 100.0F;

	public static boolean render(float partialTick) {
		Minecraft mc = Minecraft.getMinecraft();
		if (!AVWorlds.isHub(mc.currentWorld)) {
			return false;
		}
		if (!CypressSky.available(mc, PARALLAX)) {
			return true;
		}

		GLRenderer.pushFrame();
		GLRenderer.getFogState().disable();
		GLRenderer.setDepthMask(false);
		GLRenderer.setAlphaTest(0.0F);
		mc.textureManager.loadTexture(PARALLAX).bind();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		TessellatorGeneral tessellator = GLRenderer.getTessellator();

		for (int face = 0; face < 6; face++) {
			GLRenderer.pushFrame();
			switch (face) {
				case 1 -> GLRenderer.modelM4f().rotateX((float) Math.toRadians(90.0));
				case 2 -> GLRenderer.modelM4f().rotateX((float) Math.toRadians(-90.0));
				case 3 -> GLRenderer.modelM4f().rotateX((float) Math.toRadians(180.0));
				case 4 -> GLRenderer.modelM4f().rotateZ((float) Math.toRadians(90.0));
				case 5 -> GLRenderer.modelM4f().rotateZ((float) Math.toRadians(-90.0));
				default -> {
				}
			}

			tessellator.startDrawingQuads();
			tessellator.addVertexWithUV(-HALF, HALF, -HALF, 0.0, 0.0);
			tessellator.addVertexWithUV(HALF, HALF, -HALF, 1.0, 0.0);
			tessellator.addVertexWithUV(HALF, HALF, HALF, 1.0, 1.0);
			tessellator.addVertexWithUV(-HALF, HALF, HALF, 0.0, 1.0);
			tessellator.draw();
			GLRenderer.popFrame();
		}

		GLRenderer.setDepthMask(true);
		GLRenderer.popFrame();
		return true;
	}
}
