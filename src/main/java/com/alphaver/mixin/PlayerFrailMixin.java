package com.alphaver.mixin;

import com.alphaver.entity.CypressFrail;
import com.alphaver.item.ItemFlameberge;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerFrailMixin {

	@Inject(method = "hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z",
		at = @At("HEAD"), cancellable = true)
	private void alphaver$frail(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		Player player = (Player) (Object) this;
		if (damage <= 0 || player.world.isClientSide || !CypressFrail.active(player.world, player)) {
			return;
		}
		if (player.getHealth() <= 0 || player.getGamemode().hasInvulnerablePlayer()) {
			return;
		}
		if (type == DamageType.FIRE && ItemFlameberge.isHeldBy(player)) {
			return;
		}
		cir.setReturnValue(CypressFrail.hurt(player, attacker, damage));
	}
}
