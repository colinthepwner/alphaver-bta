package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.util.helper.DyeColor;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.PortalHandler;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PortalHandler.class, remap = false)
public abstract class PortalHandlerMixin {

	@Inject(method = "teleportEntity", at = @At("HEAD"), cancellable = true)
	private void alphaver$doorTravel(World world, Entity entity, @Nullable DyeColor portalColor, Dimension oldDim,
	                                 Dimension newDim, CallbackInfo ci) {
		if (AVTravel.handle(world, entity, oldDim, newDim)) {
			AVInvites.foundDoor(entity);
			ci.cancel();
		}
	}
}
