package com.alphaver.mixin.client;

import com.alphaver.client.sound.CypressMusic;
import net.minecraft.client.sound.MusicCategory;
import net.minecraft.client.sound.MusicCategoryManager;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MusicCategoryManager.class, remap = false)
public abstract class MusicCategoryManagerMixin {

	@Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
	private static void alphaver$musicEverywhere(Player player, CallbackInfoReturnable<MusicCategory> cir) {
		if (player != null && CypressMusic.eventFor(player.world) != null) {
			cir.setReturnValue(MusicCategory.OVERWORLD);
		}
	}
}
