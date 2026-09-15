package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.tessellator.TessellatorParticle;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.helper.LightIndexHelper;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ParticleCypressSnowflake extends Particle {

	public static final String TEXTURE = AlphaVer.MOD_ID + ":particle/snowflake_";

	private final float startSize;

	public ParticleCypressSnowflake(World world, double x, double y, double z, double xd, double yd, double zd, int sprite) {
		super(world, x, y, z, xd, yd, zd);
		this.xd = this.xd * 0.01 + xd;
		this.yd = this.yd * 0.01 + yd;
		this.zd = this.zd * 0.01 + zd;
		this.startSize = this.size;
		this.rCol = this.gCol = this.bCol = 1.0F;
		this.noPhysics = false;
		this.lifetime += 2;
		this.tex = TextureRegistry.getTexture(TEXTURE + Math.max(1, Math.min(3, sprite)));
	}

	@Override
	public void render(@NotNull TessellatorParticle tessellator, float partialTick) {
		float aged = (this.age + partialTick) / this.lifetime;
		this.size = this.startSize * (1.0F - aged * aged * 0.5F);
		super.render(tessellator, partialTick);
	}

	@Override
	public byte calcLightIndex(float partialTick) {
		byte around = super.calcLightIndex(partialTick);
		float aged = MathHelper.clamp((this.age + partialTick) / this.lifetime, 0.0F, 1.0F);
		int block = LightIndexHelper.blockLightFromIndex(around);
		int glow = Math.round(block * aged + 15.0F * (1.0F - aged));
		return LightIndexHelper.setBlockLight(around, Math.max(block, Math.min(15, glow)));
	}

	@Override
	public void tick() {
		this.cachedLightmapCoord = this.calcLightIndex(1.0F);
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		}
		this.move(this.xd, this.yd, this.zd);
		this.xd *= 0.96;
		this.yd *= 0.96;
		this.zd *= 0.96;
		if (this.onGround) {
			this.xd *= 0.7;
			this.zd *= 0.7;
		}
	}
}
