package com.alphaver.mixin.client;

import com.alphaver.client.render.AVPackLock;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.render.texturepack.ManifestBase;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.core.lang.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "net.minecraft.client.gui.options.components.SelectedTexturePackListComponent$TexturePackButton",
	remap = false)
public abstract class SelectedTexturePackButtonLockMixin {

	@Shadow @Final private ButtonElement button;
	@Shadow @Final public TexturePack texturePack;

	@Inject(method = "setupButton(IIIII)V", at = @At("TAIL"))
	private void alphaver$greyOutLockedRemove(int x, int y, int width, int mouseX, int mouseY, CallbackInfo ci) {
		this.button.enabled = !AVPackLock.isLocked(this.texturePack);
	}

	@Redirect(method = "render(Lnet/minecraft/client/gui/options/components/SelectedTexturePackListComponent;IIIII)V",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/client/render/texturepack/ManifestBase;getDescriptionLine2()Ljava/lang/String;"))
	private String alphaver$explainLock(ManifestBase manifest) {
		if (AVPackLock.isLocked(this.texturePack)) {
			return I18n.getInstance().translateKey("gui.alphaver.pack_locked");
		}
		return manifest.getDescriptionLine2();
	}
}
