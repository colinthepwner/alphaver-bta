package com.alphaver.mixin.client;

import com.alphaver.client.render.AVDownloadedTextures;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.lwjgl.opengl.GL41;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TexturePackList.class, remap = false)
public abstract class TexturePackListSkinsMixin {

	@Inject(method = "refresh", at = @At("HEAD"))
	private void alphaver$unbindBeforeRefresh(CallbackInfo ci) {
		GL41.glActiveTexture(GL41.GL_TEXTURE0);
		GL41.glBindTexture(GL41.GL_TEXTURE_2D, 0);
	}

	@Inject(method = "refresh", at = @At("TAIL"))
	private void alphaver$reuploadDownloadedTextures(CallbackInfo ci) {
		AVDownloadedTextures.reupload(Minecraft.getMinecraft());
	}
}
