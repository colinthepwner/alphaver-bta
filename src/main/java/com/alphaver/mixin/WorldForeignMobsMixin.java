package com.alphaver.mixin;

import com.alphaver.entity.AVForeignMobs;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = World.class, remap = false)
public abstract class WorldForeignMobsMixin {

	@Inject(method = "entityJoinedWorld", at = @At("HEAD"), cancellable = true)
	private void alphaver$refuseForeignMob(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		World world = (World) (Object) this;
		if (AVForeignMobs.keepsOut(world, entity)) {
			AVForeignMobs.report(world, entity, false);
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "updateEntityWithOptionalForce", at = @At("HEAD"), cancellable = true)
	private void alphaver$removeForeignMob(Entity entity, boolean force, CallbackInfo ci) {
		World world = (World) (Object) this;
		if (AVForeignMobs.keepsOut(world, entity)) {
			AVForeignMobs.report(world, entity, true);
			entity.remove();
			ci.cancel();
		}
	}
}
