package com.alphaver.mixin;

import com.alphaver.world.minigame.AVMinigames;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Block.class, remap = false)
public abstract class BlockMinigameMixin {

	@Inject(method = "getStrength(Lnet/minecraft/core/world/World;Lnet/minecraft/core/world/pos/TilePosc;Lnet/minecraft/core/util/helper/Side;Lnet/minecraft/core/entity/player/Player;)F",
		at = @At("HEAD"), cancellable = true)
	private void alphaver$noDiggingInGames(World world, TilePosc tilePos, Side side, Player player, CallbackInfoReturnable<Float> cir) {
		if (AVMinigames.isPlaying(player)) {
			cir.setReturnValue(0.0F);
		}
	}
}
