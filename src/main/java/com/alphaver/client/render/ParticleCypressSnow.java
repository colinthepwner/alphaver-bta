package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.world.World;

@Environment(EnvType.CLIENT)
public class ParticleCypressSnow extends Particle {

	public static final String TEXTURE = AlphaVer.MOD_ID + ":particle/snow_";

	public ParticleCypressSnow(World world, double x, double y, double z, double xd, double yd, double zd, int sprite) {
		super(world, x, y, z, xd, yd, zd);
		this.xd = this.xd * 0.01 + xd;
		this.yd = this.yd * -1.01 + yd;
		this.zd = this.zd * 0.01 + zd;
		this.rCol = this.gCol = this.bCol = 1.0F;
		this.noPhysics = true;
		this.lifetime *= 3;
		this.tex = TextureRegistry.getTexture(TEXTURE + Math.max(1, Math.min(3, sprite)));
	}

	@Override
	public void tick() {
		this.cachedLightmapCoord = this.calcLightIndex(1.0F);
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		this.size = Math.max(0.0F, this.size - 0.01F);
		if (this.age++ >= this.lifetime) {
			this.remove();
		}
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.46;
		this.yd *= 0.85;
		this.zd *= 0.46;
	}
}
