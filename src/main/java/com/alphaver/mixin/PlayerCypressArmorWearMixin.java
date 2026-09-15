package com.alphaver.mixin;

import com.alphaver.item.AVDurability;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerCypressArmorWearMixin {

	@Redirect(method = "damageEntity(ILnet/minecraft/core/util/helper/DamageType;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/player/Player;damageArmor(I)V"))
	private void alphaver$cypressArmorWear(Player self, int btaWear, int hurt, DamageType damageType) {
		self.damageArmor(AVDurability.armourWear(self, btaWear, hurt));
	}
}
