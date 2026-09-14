package com.alphaver.mixin.client;

import com.alphaver.client.sound.CypressMusic;
import net.minecraft.client.Minecraft;
import net.minecraft.client.sound.MusicCategory;
import net.minecraft.client.sound.SoundEngine;
import net.minecraft.client.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SoundEngine.class, remap = false)
public abstract class SoundEngineMusicMixin {

	@Inject(method = "getMusicTrackFromCategory", at = @At("HEAD"), cancellable = true)
	private void alphaver$cypressTrack(MusicCategory category, CallbackInfoReturnable<SoundEvent> cir) {
		SoundEvent event = CypressMusic.eventFor(Minecraft.getMinecraft().currentWorld);
		if (event != null) {
			cir.setReturnValue(event);
		}
	}
}
