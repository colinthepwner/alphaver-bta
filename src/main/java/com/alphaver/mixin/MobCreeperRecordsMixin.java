package com.alphaver.mixin;

import com.alphaver.item.AVItems;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.monster.MobCreeper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(value = MobCreeper.class, remap = false)
public abstract class MobCreeperRecordsMixin {

	@ModifyArg(method = "onDeath", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/entity/monster/MobCreeper;dropItem(II)Lnet/minecraft/core/entity/EntityItem;"), index = 0)
	private int alphaver$cypressRecords(int itemId) {
		MobCreeper creeper = (MobCreeper) (Object) this;
		if (!AVWorlds.isCypress(creeper.world) || AVItems.RECORD_LEMURIA == null || AVItems.RECORD_HIDDEN_DEN == null) {
			return itemId;
		}
		return creeper.world.rand.nextInt(2) == 0 ? AVItems.RECORD_LEMURIA.id : AVItems.RECORD_HIDDEN_DEN.id;
	}
}
