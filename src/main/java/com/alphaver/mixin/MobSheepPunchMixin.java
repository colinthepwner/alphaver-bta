package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = Mob.class, remap = false)
public abstract class MobSheepPunchMixin {

	@Inject(method = "hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z", at = @At("HEAD"))
	private void alphaver$punchForWool(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		if (!((Object) this instanceof MobSheep sheep) || !(attacker instanceof Mob) || sheep.world == null
			|| sheep.world.isClientSide || sheep.getSheared() || !AVWorlds.isCypress(sheep.world)) {
			return;
		}
		sheep.setSheared(true);
		Random random = sheep.world.rand;
		int count = 1 + random.nextInt(3);
		for (int i = 0; i < count; i++) {
			EntityItem wool = sheep.dropItem(new ItemStack(Blocks.WOOL.id(), 1, sheep.getFleeceColor().blockMeta), 1.0F);
			if (wool != null) {
				wool.yd += random.nextFloat() * 0.05F;
				wool.xd += (random.nextFloat() - random.nextFloat()) * 0.1F;
				wool.zd += (random.nextFloat() - random.nextFloat()) * 0.1F;
			}
		}
	}
}
