package com.alphaver.mixin.client;

import com.alphaver.entity.AVGamemodes;
import com.alphaver.world.AVGameRules;
import com.alphaver.world.travel.AVCypressHome;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.settings.WorldConfiguration;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftCreateWorldCypressMixin {

	@Inject(method = "createAndStartWorld", at = @At("HEAD"))
	private void alphaver$markCypressStart(WorldConfiguration worldConfiguration, CallbackInfo ci) {
		if (AVGameRules.START_IN_CYPRESS != null && AVGamemodes.startsInCypress(worldConfiguration.getGamemode())) {
			worldConfiguration.getGameRules().setValue(AVGameRules.START_IN_CYPRESS, true);
		}
	}

	@Inject(method = "createAndStartWorld", at = @At("TAIL"))
	private void alphaver$startInCypress(WorldConfiguration worldConfiguration, CallbackInfo ci) {
		Minecraft mc = (Minecraft) (Object) this;
		if (mc.thePlayer != null && AVCypressHome.active(mc.currentWorld)) {
			AVCypressHome.sendHome(mc.thePlayer);
		}
	}
}
