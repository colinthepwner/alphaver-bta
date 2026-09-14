package com.alphaver.mixin.client;

import com.alphaver.client.render.AVPackLock;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TexturePackList.class, remap = false)
public abstract class TexturePackListLockMixin {

	@Inject(method = "unsetTexturePack(Lnet/minecraft/client/render/texturepack/TexturePack;)V", at = @At("HEAD"),
		cancellable = true)
	private void alphaver$refuseLockedPack(TexturePack pack, CallbackInfo ci) {
		if (AVPackLock.isLocked(pack)) {
			ci.cancel();
		}
	}

	@Inject(method = {"setTexturePack(Lnet/minecraft/client/render/texturepack/TexturePack;)V",
		"shiftPack(Lnet/minecraft/client/render/texturepack/TexturePack;I)V"}, at = @At("TAIL"))
	private void alphaver$keepLookOnTop(CallbackInfo ci) {
		AVPackLock.keepLookOnTop((TexturePackList) (Object) this);
	}
}
