package com.alphaver.mixin.client;

import com.alphaver.client.render.AVPackLock;
import net.minecraft.client.gui.options.components.SelectedTexturePackListComponent;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(value = SelectedTexturePackListComponent.class, remap = false)
public abstract class SelectedTexturePackListHideMixin {

	@Redirect(method = "createTexturePackButtons()V", at = @At(value = "FIELD",
		target = "Lnet/minecraft/client/render/texturepack/TexturePackList;selectedPacks:Ljava/util/List;", opcode = Opcodes.GETFIELD))
	private List<TexturePack> alphaver$hideOutside(TexturePackList list) {
		return AVPackLock.withoutHidden(list.selectedPacks);
	}
}
