package com.alphaver.mixin;

import com.alphaver.item.AVDurability;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = ItemStack.class, remap = false)
public abstract class ItemStackCypressDurabilityMixin {

	@ModifyVariable(method = "damageItem(ILnet/minecraft/core/entity/Entity;)V", at = @At("HEAD"), argsOnly = true)
	private int alphaver$cypressWear(int damage, @Local(argsOnly = true) Entity entity) {
		return AVDurability.wear((ItemStack) (Object) this, damage, entity);
	}
}
