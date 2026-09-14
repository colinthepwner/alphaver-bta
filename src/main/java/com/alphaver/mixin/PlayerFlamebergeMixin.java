package com.alphaver.mixin;

import com.alphaver.item.ItemFlameberge;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerFlamebergeMixin {

	@Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
	private void alphaver$flamebergeFireproof(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		if (type == DamageType.FIRE && ItemFlameberge.isHeldBy((Player) (Object) this)) {
			cir.setReturnValue(false);
		}
	}
}
