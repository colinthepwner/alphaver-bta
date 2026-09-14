package com.alphaver.mixin;

import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerSelectedArtMixin {

	@ModifyArg(method = "getSelectedArt", at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), index = 0)
	private int alphaver$unsignedArtIndex(int index) {
		return index & 0xFF;
	}
}
