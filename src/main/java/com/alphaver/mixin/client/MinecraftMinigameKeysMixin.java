package com.alphaver.mixin.client;

import com.alphaver.client.MinigameClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftMinigameKeysMixin {

	@Inject(method = "checkBoundInputs(Lnet/minecraft/client/input/InputDevice;)Z", at = @At("HEAD"), cancellable = true)
	private void alphaver$zombiesBuyKey(InputDevice currentInputDevice, CallbackInfoReturnable<Boolean> cir) {
		if (GameSettings.KEY_INVENTORY.isPressEvent(currentInputDevice)
			&& MinigameClient.buyInsteadOfInventory((Minecraft) (Object) this)) {
			cir.setReturnValue(true);
		}
	}
}
