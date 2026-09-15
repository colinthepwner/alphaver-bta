package com.alphaver.mixin.client;

import com.alphaver.block.BlockLogicAlphaVerDoor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerController.class, remap = false)
public abstract class PlayerControllerDoorGuardMixin {

	@Shadow
	@Final
	protected Minecraft mc;

	@Inject(method = "destroyBlock(Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/util/helper/Side;)Z", at = @At("HEAD"), cancellable = true)
	private void alphaver$keepHubDoors(TilePosc tilePos, Side side, CallbackInfoReturnable<Boolean> cir) {
		World world = this.mc.currentWorld;
		if (world != null && BlockLogicAlphaVerDoor.protectedAt(world, tilePos)) {
			cir.setReturnValue(false);
		}
	}
}
