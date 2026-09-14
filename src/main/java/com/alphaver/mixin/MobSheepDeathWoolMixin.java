package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = MobSheep.class, remap = false)
public abstract class MobSheepDeathWoolMixin {

	@Redirect(method = "dropDeathItems", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/entity/animal/MobSheep;dropItem(Lnet/minecraft/core/item/ItemStack;F)Lnet/minecraft/core/entity/EntityItem;"))
	private EntityItem alphaver$noDeathWool(MobSheep sheep, ItemStack wool, float offset) {
		return AVWorlds.isCypress(sheep.world) ? null : sheep.dropItem(wool, offset);
	}
}
