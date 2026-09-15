package com.alphaver.mixin.client;

import com.alphaver.AVConfig;
import com.alphaver.client.render.AVLookController;
import net.minecraft.client.render.texture.stitcher.AtlasStitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = AtlasStitcher.class, remap = false)
public abstract class AtlasStitcherCypressFilterMixin {

	@ModifyArg(method = "init", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/texture/Texture;setupTexture(Ljava/awt/image/BufferedImage;ZZZ)V"), index = 1)
	private boolean alphaver$cypressTextureFilter(boolean blur) {
		return blur || AVConfig.TEXTURE_FILTER && AVLookController.isApplied();
	}
}
