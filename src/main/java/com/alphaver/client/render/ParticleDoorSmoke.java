package com.alphaver.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.particle.ParticleSmoke;
import net.minecraft.core.world.World;

@Environment(EnvType.CLIENT)
public class ParticleDoorSmoke extends ParticleSmoke {

	private static final float[][] SHADES = {
		{0x44 / 255.0F, 0x7C / 255.0F, 0x4D / 255.0F},
		{0x6B / 255.0F, 0xA9 / 255.0F, 0x74 / 255.0F}
	};
	private static final float SCALE = 1.5F;

	public ParticleDoorSmoke(World world, double x, double y, double z, double motionX, double motionY, double motionZ, int data) {

		super(world, x, y, z, motionX, motionY, motionZ, SCALE, 0);
		float[] shade = SHADES[Math.floorMod(data, SHADES.length)];
		float light = 0.88F + (float) Math.random() * 0.12F;
		this.rCol = shade[0] * light;
		this.gCol = shade[1] * light;
		this.bCol = shade[2] * light;
	}
}
