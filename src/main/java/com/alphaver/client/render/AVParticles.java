package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.block.BlockLogicAlphaVerDoor;
import com.alphaver.block.machine.BlockLogicFreezer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.particle.ParticleDispatcher;
import net.minecraft.client.render.particle.ParticleSnowShovel;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;

@Environment(EnvType.CLIENT)
public final class AVParticles {
	private AVParticles() {}

	public static final String LEAF = AlphaVer.MOD_ID + ":leaf";
	public static final String SNOW = AlphaVer.MOD_ID + ":snow";

	public static void register() {
		ParticleDispatcher dispatcher = ParticleDispatcher.getInstance();
		dispatcher.addDispatch(LEAF, (world, x, y, z, motionX, motionY, motionZ, data) ->
			new ParticleCypressLeaf(world, x, y, z, motionX, motionY, motionZ));
		dispatcher.addDispatch(SNOW, (world, x, y, z, motionX, motionY, motionZ, data) ->
			new ParticleCypressSnow(world, x, y, z, motionX, motionY, motionZ, data));
		dispatcher.addDispatch(BlockLogicFreezer.SNOW_PARTICLE, (world, x, y, z, motionX, motionY, motionZ, data) ->
			TextureRegistry.hasTexture(ParticleCypressSnowflake.TEXTURE + 1)
				? new ParticleCypressSnowflake(world, x, y, z, motionX, motionY, motionZ, data)
				: new ParticleSnowShovel(world, x, y, z, motionX, motionY, motionZ));
		dispatcher.addDispatch(BlockLogicAlphaVerDoor.SMOKE_PARTICLE, (world, x, y, z, motionX, motionY, motionZ, data) ->
			new ParticleDoorSmoke(world, x, y, z, motionX, motionY, motionZ, data));
		AlphaVer.LOGGER.info("Registered Cypress's particles.");
	}

	public static void registerTextures() {
		try {
			queue(ParticleCypressLeaf.TEXTURE);
			for (int sprite = 1; sprite <= 3; sprite++) {
				queue(ParticleCypressSnow.TEXTURE + sprite);
				queue(ParticleCypressSnowflake.TEXTURE + sprite);
			}
		} catch (RuntimeException e) {

			AlphaVer.LOGGER.warn("Could not queue Cypress's particle sprites: {}", e.toString());
		}
	}

	public static boolean hasSnowArt() {
		return TextureRegistry.hasTexture(ParticleCypressSnow.TEXTURE + 1);
	}

	private static void queue(String id) {
		if (TextureRegistry.hasSourceFile(id)) {
			TextureRegistry.getTexture(id);
		}
	}
}
