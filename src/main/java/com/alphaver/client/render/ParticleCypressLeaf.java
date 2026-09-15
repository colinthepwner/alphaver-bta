package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.particle.Particle;
import net.minecraft.client.render.tessellator.TessellatorParticle;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ParticleCypressLeaf extends Particle {

	public static final String TEXTURE = AlphaVer.MOD_ID + ":particle/leaf";
	private static final String STAND_IN = "minecraft:block/leaves/oak_fancy";

	private final boolean standIn;

	public ParticleCypressLeaf(World world, double x, double y, double z, double xd, double yd, double zd) {
		super(world, x, y, z, xd, yd, zd);
		this.xd = this.xd * 0.01 + xd;
		this.yd = this.yd * 0.01 + yd;
		this.zd = this.zd * 0.01 + zd;
		this.rCol = this.gCol = this.bCol = 1.0F;
		this.noPhysics = true;
		this.lifetime *= 2;
		this.standIn = !TextureRegistry.hasTexture(TEXTURE);
		this.tex = TextureRegistry.getTexture(this.standIn ? STAND_IN : TEXTURE);
		if (this.standIn) {

			this.rCol = 0x5B / 255.0F;
			this.gCol = 0x9A / 255.0F;
			this.bCol = 0x66 / 255.0F;
		}
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
	}

	@Override
	public void render(@NotNull TessellatorParticle tessellator, float partialTick) {
		if (!this.standIn || this.tex == null) {
			super.render(tessellator, partialTick);
			return;
		}

		tessellator.setLightmapCoord1i(this.getLightIndex(partialTick));
		tessellator.setColorOpaque3f(this.rCol, this.gCol, this.bCol);
		tessellator.addParticle(MathHelper.lerp(this.xo, this.x, (double) partialTick), MathHelper.lerp(this.yo, this.y, (double) partialTick),
			MathHelper.lerp(this.zo, this.z, (double) partialTick), this.size * 0.1F,
			this.tex.getIconUMin() + this.tex.getIconUSize() * this.uo / 4.0, this.tex.getIconVMin() + this.tex.getIconVSize() * this.vo / 4.0,
			this.tex.getIconUSize() * 0.25, this.tex.getIconVSize() * 0.25);
	}
}
