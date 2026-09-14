package com.alphaver.mixin.client;

import com.alphaver.client.MinigameClient;
import com.alphaver.client.movement.CypressDash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.PlayerInput;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerInput.class, remap = false)
public abstract class PlayerInputDashMixin {

	@Shadow
	@Final
	public Minecraft mc;

	@Inject(method = "keyEvent", at = @At("HEAD"))
	private void alphaver$dashKeys(int keyCode, int mouseCode, boolean pressed, CallbackInfo ci) {
		CypressDash.onKey(this.mc, keyCode, mouseCode, pressed);
		MinigameClient.onKey(this.mc, keyCode, mouseCode, pressed);
	}
}
