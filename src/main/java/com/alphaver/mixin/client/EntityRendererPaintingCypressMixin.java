package com.alphaver.mixin.client;

import com.alphaver.client.render.AVPaintingTextures;
import net.minecraft.client.render.entity.EntityRendererPainting;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = EntityRendererPainting.class, remap = false)
public abstract class EntityRendererPaintingCypressMixin {

	@Redirect(method = "render(Lnet/minecraft/client/render/tessellator/TessellatorGeneral;Lnet/minecraft/core/entity/EntityPainting;DDDFF)V",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/texture/stitcher/TextureRegistry;getTexture(Ljava/lang/String;)Lnet/minecraft/client/render/texture/stitcher/IconCoordinate;"))
	private IconCoordinate alphaver$paintingTexture(String texture) {
		return TextureRegistry.getTexture(AVPaintingTextures.resolve(texture));
	}
}
