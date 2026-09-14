package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.AVMinigames;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.monster.MobZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Mob.class, remap = false)
public abstract class MobMinigameMixin {

	@Inject(method = "onDeath(Lnet/minecraft/core/entity/Entity;)V", at = @At("HEAD"))
	private void alphaver$zombieKilled(Entity killer, CallbackInfo ci) {
		Object self = this;
		if (self instanceof MobZombie zombie && AVWorlds.isZombies(zombie.world)) {
			AVMinigames.zombieDied(zombie, killer);
		}
	}

	@Inject(method = "tryToDespawn()V", at = @At("HEAD"), cancellable = true)
	private void alphaver$gameZombiesStay(CallbackInfo ci) {
		Object self = this;
		if (self instanceof MobZombie zombie && AVWorlds.isZombies(zombie.world) && AVMinigames.keepsZombie(zombie)) {
			ci.cancel();
		}
	}
}
