package com.alphaver.client.render;

import com.alphaver.block.BlockLogicGreenstoneWire;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.particle.ParticleDispatcher;
import net.minecraft.client.render.particle.ParticleRedstoneDust;
import net.minecraft.core.world.World;

@Environment(EnvType.CLIENT)
public class ParticleGreenstoneDust extends ParticleRedstoneDust {

	public ParticleGreenstoneDust(World world, double x, double y, double z) {
		super(world, x, y, z, 0);
		this.gCol = (float) (Math.random() * 0.3) + 0.7F;
		this.rCol = (float) (Math.random() * 0.1);
		this.bCol = this.rCol;
	}

	public static void register() {
		ParticleDispatcher.getInstance().addDispatch(BlockLogicGreenstoneWire.DUST_PARTICLE,
			(world, x, y, z, motionX, motionY, motionZ, data) -> new ParticleGreenstoneDust(world, x, y, z));
	}
}
