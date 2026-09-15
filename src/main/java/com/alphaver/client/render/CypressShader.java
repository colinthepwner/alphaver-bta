package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenPhotoMode;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.shader.Shader;
import net.minecraft.client.render.shader.ShaderHelper;
import net.minecraft.client.render.shader.ShaderProvider;
import net.minecraft.client.render.shader.framebuffer.FrameBuffer;
import net.minecraft.client.render.shader.framebuffer.FrameBufferAttachment;
import net.minecraft.client.render.shader.framebuffer.FrameBufferSingleSample;
import net.minecraft.client.render.shader.framebuffer.FrameBufferTextureSingleSample;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2ic;
import org.lwjgl.opengl.GL41;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Random;

@Environment(EnvType.CLIENT)
public final class CypressShader {
	private CypressShader() {}

	private static final String DIRECTORY = "/assets/alphaver/shaders/";
	private static final String NAME = "cypress/final";

	private static final float NEAR_PLANE = 0.05F;

	private static final double REPORT_SECONDS = 0.008;

	private static final double MIN_FRAME_SECONDS = 0.001;
	private static final double MAX_FRAME_SECONDS = 0.25;

	private static final ShaderProvider SOURCES = name -> {
		try (InputStream in = CypressShader.class.getResourceAsStream(DIRECTORY + name)) {
			if (in == null) {
				AlphaVer.LOGGER.error("Cypress's shader pass is missing {}{}.", DIRECTORY, name);
				return null;
			}
			return new String(in.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException e) {
			AlphaVer.LOGGER.error("Could not read {}{}.", DIRECTORY, name, e);
			return null;
		}
	};

	private static final Shader PROGRAM = new Shader();

	private static boolean attempted;

	@Nullable
	private static FrameBuffer output;
	private static boolean outputLinear;

	private static int sampler;

	private static final Random RANDOM = new Random();

	private static long lastPassNanos;

	@Nullable
	public static FrameBufferAttachment run(@Nullable FrameBuffer world) {
		Minecraft mc = Minecraft.getMinecraft();
		if (world == null || mc.thePlayer == null || !AVWorlds.isCypress(mc.currentWorld)
			|| mc.currentScreen instanceof ScreenPhotoMode) {
			lastPassNanos = 0L;
			return null;
		}
		FrameBuffer source = world.getOutputBuffer();
		FrameBufferAttachment color = source.getAttachment(FrameBuffer.AttachmentType.COLOR);
		FrameBufferAttachment depth = source.getAttachment(FrameBuffer.AttachmentType.DEPTH);
		if (color == null || depth == null || !program()) {
			lastPassNanos = 0L;
			return null;
		}

		Vector2ic size = color.getSize();
		FrameBuffer target = output(size);
		int reads = sampler();

		target.bind();
		GL41.glViewport(0, 0, size.x(), size.y());
		PROGRAM.bind();
		GL41.glActiveTexture(GL41.GL_TEXTURE0);
		color.bindForOutput();
		GL41.glBindSampler(0, reads);
		GL41.glActiveTexture(GL41.GL_TEXTURE1);
		depth.bindForOutput();
		GL41.glBindSampler(1, reads);
		GL41.glActiveTexture(GL41.GL_TEXTURE0);
		PROGRAM.uniformInt("sceneColor", 0);
		PROGRAM.uniformInt("sceneDepth", 1);
		setUniforms(mc);

		ShaderHelper.drawFullscreenRect();

		GL41.glBindSampler(0, 0);
		GL41.glBindSampler(1, 0);
		GL41.glUseProgram(0);
		target.unbind();
		return target.getAttachment(FrameBuffer.AttachmentType.COLOR);
	}

	private static void setUniforms(Minecraft mc) {
		int width = Math.max(1, mc.gameWindow.getWidthPixels());
		int height = Math.max(1, mc.gameWindow.getHeightPixels());
		PROGRAM.uniformFloat("aspectRatio", (float) (width / height));
		PROGRAM.uniformFloat("nearPlane", NEAR_PLANE);
		PROGRAM.uniformFloat("farPlane", mc.worldRenderer.farPlaneDistance * 2.0F);
		PROGRAM.uniformFloat("rand", RANDOM.nextFloat());
		PROGRAM.uniformFloat("lastMouseDist", mouseDistance(mc));
		PROGRAM.uniformFloat("playerPitchRot", mc.thePlayer.xRot);
		float fovDegrees = (int) (GameSettings.FOV.value * 100.0 + 30.0);
		PROGRAM.uniformFloat("fovMod", (fovDegrees - 70.0F) / 80.0F + 0.5F);
	}

	private static float mouseDistance(Minecraft mc) {
		long now = System.nanoTime();
		long previous = lastPassNanos;
		lastPassNanos = now;
		if (mc.currentScreen != null || previous == 0L) {
			return 0.0F;
		}
		double seconds = Math.max(MIN_FRAME_SECONDS, Math.min(MAX_FRAME_SECONDS, (now - previous) / 1.0E9));
		double moved = Math.hypot(mc.mouseInput.deltaX, mc.mouseInput.deltaY);
		return (float) (moved / seconds * REPORT_SECONDS);
	}

	private static boolean program() {
		if (!attempted) {
			attempted = true;
			PROGRAM.compile(SOURCES, NAME);
			if (PROGRAM.isEnabled()) {
				AlphaVer.LOGGER.info("Compiled Cypress's shader pass.");
			} else {
				AlphaVer.LOGGER.warn("Cypress's shader pass did not compile; Cypress renders without it.");
			}
		}
		return PROGRAM.isEnabled();
	}

	private static FrameBuffer output(Vector2ic size) {
		boolean linear = GameSettings.RENDER_SCALE.value.useLinearFiltering;
		if (output != null && (output.isDeleted() || !output.getSize().equals(size) || outputLinear != linear)) {
			if (!output.isDeleted()) {
				output.delete();
			}
			output = null;
		}
		if (output == null) {
			int filter = linear ? GL41.GL_LINEAR : GL41.GL_NEAREST;
			output = new FrameBufferSingleSample(new FrameBufferTextureSingleSample(GL41.GL_RGBA, GL41.GL_COLOR_ATTACHMENT0, size,
				parameters -> {
					parameters.set(GL41.GL_TEXTURE_MIN_FILTER, filter);
					parameters.set(GL41.GL_TEXTURE_MAG_FILTER, filter);
					parameters.set(GL41.GL_TEXTURE_WRAP_S, GL41.GL_CLAMP_TO_EDGE);
					parameters.set(GL41.GL_TEXTURE_WRAP_T, GL41.GL_CLAMP_TO_EDGE);
				}));
			outputLinear = linear;
		}
		return output;
	}

	private static int sampler() {
		if (sampler == 0) {
			sampler = GL41.glGenSamplers();
			GL41.glSamplerParameteri(sampler, GL41.GL_TEXTURE_MIN_FILTER, GL41.GL_LINEAR);
			GL41.glSamplerParameteri(sampler, GL41.GL_TEXTURE_MAG_FILTER, GL41.GL_LINEAR);
			GL41.glSamplerParameteri(sampler, GL41.GL_TEXTURE_WRAP_S, GL41.GL_REPEAT);
			GL41.glSamplerParameteri(sampler, GL41.GL_TEXTURE_WRAP_T, GL41.GL_REPEAT);
		}
		return sampler;
	}

	public static void reload() {
		PROGRAM.delete();
		attempted = false;
	}

	public static void release() {
		if (output != null && !output.isDeleted()) {
			output.delete();
		}
		output = null;
		if (sampler != 0) {
			GL41.glDeleteSamplers(sampler);
			sampler = 0;
		}
	}
}
