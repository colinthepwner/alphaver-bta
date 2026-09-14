package com.alphaver.mixin.client;

import com.alphaver.client.render.AVPackLock;
import net.minecraft.client.gui.options.components.AvailableTexturePackListComponent;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = AvailableTexturePackListComponent.class, remap = false)
public abstract class AvailableTexturePackListHideMixin {

	@Redirect(method = "createTexturePackButtons()V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/texturepack/TexturePackList;availableTexturePacks()Ljava/util/List;"))
	private List<TexturePack> alphaver$hideOutside(TexturePackList list) {
		return AVPackLock.withoutHidden(list.availableTexturePacks());
	}
}
