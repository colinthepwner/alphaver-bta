package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.tessellator.TessellatorGeneral;
import net.minecraft.core.entity.projectile.Projectile;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;

import java.util.function.ToIntFunction;

@Environment(EnvType.CLIENT)
public class EntityRendererAlphaArrow<T extends Projectile> extends EntityRenderer<T> {

	private static final String ARROW_SHEET = "/assets/minecraft/textures/entity/arrows.png";

	private final String texture;
	private final String bridgedId;
	private final boolean fullBright;
	private final ToIntFunction<T> shake;

	public EntityRendererAlphaArrow(String texture, String bridgedId, boolean fullBright, ToIntFunction<T> shake) {
		this.texture = texture;
		this.bridgedId = bridgedId;
		this.fullBright = fullBright;
		this.shake = shake;
	}

	@Override
	public void render(@NotNull TessellatorGeneral tessellator, @NotNull T projectile, double x, double y, double z, float yaw, float partialTick) {
		int prime = 900;
		if (projectile.ticksInGround + partialTick > prime) {
			int pastPrime = projectile.ticksInGround - prime;
			double toDeath = 1.0 - (pastPrime + partialTick) * 4.0 / 1200.0;
			if (Math.cos((pastPrime + partialTick) * Math.PI / (2.5 + 5.5 * toDeath)) > 0.0) {
				return;
			}
		}

		boolean own = AVTextures.has(this.bridgedId);
		this.bindTexture(own ? this.texture : ARROW_SHEET);
		float imgWidth = own ? 32.0F : 16.0F;
		float imgHeight = own ? 32.0F : 64.0F;

		GLRenderer.pushFrame();
		if (this.fullBright) {
			GLRenderer.globalSetLightEnabled(false);
			GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GLRenderer.setLightmapCoord2i(15, 15);
		}
		GLRenderer.modelM4f().translate((float) x, (float) y, (float) z);
		GLRenderer.modelM4f().rotateY(org.joml.Math.toRadians(org.joml.Math.lerp(projectile.yRotO, projectile.yRot, partialTick) - 90.0F));
		GLRenderer.modelM4f().rotateZ(org.joml.Math.toRadians(org.joml.Math.lerp(projectile.xRotO, projectile.xRot, partialTick)));
		float bodyMinU = 0.0F;
		float bodyMaxU = 16.0F / imgWidth;
		float bodyMinV = 0.0F;
		float bodyMaxV = 5.0F / imgHeight;
		float tailMinU = 0.0F;
		float tailMaxU = 5.0F / imgWidth;
		float tailMinV = 5.0F / imgHeight;
		float tailMaxV = 10.0F / imgHeight;
		float scale = 0.05625F;
		float shakeAmount = this.shake.applyAsInt(projectile) - partialTick;
		if (shakeAmount > 0.0F) {
			float shakeAngle = -MathHelper.sin(shakeAmount * 3.0F) * shakeAmount;
			GLRenderer.modelM4f().rotateZ(org.joml.Math.toRadians(shakeAngle));
		}

		GLRenderer.modelM4f().rotateX(org.joml.Math.toRadians(45.0F));
		GLRenderer.modelM4f().scale(scale, scale, scale);
		GLRenderer.modelM4f().translate(-4.0F, 0.0F, 0.0F);
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(-7.0, -2.0, -2.0, tailMinU, tailMinV);
		tessellator.addVertexWithUV(-7.0, -2.0, 2.0, tailMaxU, tailMinV);
		tessellator.addVertexWithUV(-7.0, 2.0, 2.0, tailMaxU, tailMaxV);
		tessellator.addVertexWithUV(-7.0, 2.0, -2.0, tailMinU, tailMaxV);
		tessellator.addVertexWithUV(-7.0, 2.0, -2.0, tailMinU, tailMinV);
		tessellator.addVertexWithUV(-7.0, 2.0, 2.0, tailMaxU, tailMinV);
		tessellator.addVertexWithUV(-7.0, -2.0, 2.0, tailMaxU, tailMaxV);
		tessellator.addVertexWithUV(-7.0, -2.0, -2.0, tailMinU, tailMaxV);
		tessellator.addVertexWithUV(-8.0, -2.0, 0.0, bodyMinU, bodyMinV);
		tessellator.addVertexWithUV(-8.0, 2.0, 0.0, bodyMinU, bodyMaxV);
		tessellator.addVertexWithUV(8.0, 2.0, 0.0, bodyMaxU, bodyMaxV);
		tessellator.addVertexWithUV(8.0, -2.0, 0.0, bodyMaxU, bodyMinV);
		tessellator.addVertexWithUV(-8.0, -2.0, 0.0, bodyMinU, bodyMinV);
		tessellator.addVertexWithUV(8.0, -2.0, 0.0, bodyMaxU, bodyMinV);
		tessellator.addVertexWithUV(8.0, 2.0, 0.0, bodyMaxU, bodyMaxV);
		tessellator.addVertexWithUV(-8.0, 2.0, 0.0, bodyMinU, bodyMaxV);
		tessellator.addVertexWithUV(-8.0, 0.0, -2.0, bodyMinU, bodyMinV);
		tessellator.addVertexWithUV(-8.0, 0.0, 2.0, bodyMinU, bodyMaxV);
		tessellator.addVertexWithUV(8.0, 0.0, 2.0, bodyMaxU, bodyMaxV);
		tessellator.addVertexWithUV(8.0, 0.0, -2.0, bodyMaxU, bodyMinV);
		tessellator.addVertexWithUV(-8.0, 0.0, -2.0, bodyMinU, bodyMinV);
		tessellator.addVertexWithUV(8.0, 0.0, -2.0, bodyMaxU, bodyMinV);
		tessellator.addVertexWithUV(8.0, 0.0, 2.0, bodyMaxU, bodyMaxV);
		tessellator.addVertexWithUV(-8.0, 0.0, 2.0, bodyMinU, bodyMaxV);
		tessellator.draw();
		GLRenderer.popFrame();
		if (this.fullBright) {
			GLRenderer.globalSetLightEnabled(true);
		}
	}
}
