package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerPickBlockInvitesMixin {

	@Inject(method = "pickBlock(IIIZ)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$onlyInvited(int x, int y, int z, boolean pickFully, CallbackInfo ci) {
		Player player = (Player) (Object) this;
		if (player.world == null || player.getGamemode() != Gamemodes.CREATIVE) {
			return;
		}
		if (!AVInvites.mayPick(player, player.world.getBlock(x, y, z))) {
			ci.cancel();
		}
	}
}
