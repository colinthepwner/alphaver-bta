package com.alphaver.mixin.server;

import com.alphaver.block.BlockLogicAlphaVerDoor;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.server.world.ServerPlayerController;
import net.minecraft.server.world.WorldServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ServerPlayerController.class, remap = false)
public abstract class ServerPlayerControllerDoorGuardMixin {

	@Shadow
	private WorldServer thisWorld;

	@Inject(method = "mineBlock(IIILnet/minecraft/core/util/helper/Side;)Z", at = @At("HEAD"), cancellable = true)
	private void alphaver$keepHubDoors(int x, int y, int z, Side side, CallbackInfoReturnable<Boolean> cir) {
		if (this.thisWorld != null && BlockLogicAlphaVerDoor.protectedAt(this.thisWorld, new TilePos(x, y, z))) {
			cir.setReturnValue(false);
		}
	}
}
