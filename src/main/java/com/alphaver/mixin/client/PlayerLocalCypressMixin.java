package com.alphaver.mixin.client;

import com.alphaver.client.gui.CypressAreaNames;
import com.alphaver.client.movement.CypressDash;
import com.alphaver.client.render.CypressAmbientEffects;
import com.alphaver.world.AVWorlds;
import net.minecraft.client.entity.player.PlayerLocal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerLocal.class, remap = false)
public abstract class PlayerLocalCypressMixin {

	@Inject(method = "onLivingUpdate", at = @At("HEAD"))
	private void alphaver$cypressTick(CallbackInfo ci) {
		PlayerLocal self = (PlayerLocal) (Object) this;
		CypressDash.tick(self);
		CypressAreaNames.track(self);
		CypressAmbientEffects.breathBubbles(self);
	}

	@Inject(method = "getFovModifier", at = @At("HEAD"), cancellable = true)
	private void alphaver$cypressSteadyFov(CallbackInfoReturnable<Float> cir) {
		if (AVWorlds.isAlphaVer(((PlayerLocal) (Object) this).world)) {
			cir.setReturnValue(1.0F);
		}
	}
}
