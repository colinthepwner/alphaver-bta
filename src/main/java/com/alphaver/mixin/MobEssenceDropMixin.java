package com.alphaver.mixin;

import com.alphaver.entity.AVEntities;
import com.alphaver.item.AVItems;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobGiant;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = Mob.class, remap = false)
public abstract class MobEssenceDropMixin {

	@Inject(method = "dropDeathItems", at = @At("TAIL"))
	private void alphaver$cypressEssence(CallbackInfo ci) {
		Mob mob = (Mob) (Object) this;
		if (!(mob instanceof MobMonster) || AVItems.ESSENCE == null || !(AVWorlds.isCypress(mob.world) || AVWorlds.isZombies(mob.world))
			|| AVEntities.isAlphaVer(mob)) {
			return;
		}
		int count = alphaver$essenceFor(mob, mob.world.rand);
		if (count > 0) {
			mob.dropItem(AVItems.ESSENCE.id, count);
		}
	}

	private static int alphaver$essenceFor(Mob mob, Random rand) {
		if (mob instanceof MobGiant) {
			return 1;
		}
		if (mob instanceof MobZombie) {
			return 4 + rand.nextInt(6);
		}
		if (mob instanceof MobSkeleton) {
			return 12 + rand.nextInt(6);
		}
		if (mob instanceof MobSpider) {
			return 8 + rand.nextInt(4);
		}
		if (mob instanceof MobCreeper) {
			return 10 + rand.nextInt(8);
		}
		return 1;
	}
}
