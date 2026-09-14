package com.alphaver.mixin;

import com.alphaver.painting.AVPaintings;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemPainting;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ItemPainting.class, remap = false)
public abstract class ItemPaintingCypressMixin {

	@Inject(method = "onUseOnBlock", at = @At("HEAD"), cancellable = true)
	private void alphaver$keepCypressPaintingsHome(ItemStack selfStack, World world, Player player, TilePosc blockPos, Side side,
	                                               double xHit, double yHit, CallbackInfoReturnable<Boolean> cir) {
		if (player != null && !AVPaintings.allowedIn(player.getSelectedArt(), world)) {
			cir.setReturnValue(false);
		}
	}
}
