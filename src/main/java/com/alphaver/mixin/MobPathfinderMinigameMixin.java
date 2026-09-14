package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.MobPathfinder;
import net.minecraft.core.entity.monster.MobZombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = MobPathfinder.class, remap = false)
public abstract class MobPathfinderMinigameMixin {

	@ModifyConstant(method = "updateAI()V", constant = @Constant(floatValue = 16.0F))
	private float alphaver$zombiesPathRange(float sightRadius) {
		Object self = this;
		return self instanceof MobZombie zombie && AVWorlds.isZombies(zombie.world) ? 64.0F : sightRadius;
	}
}
