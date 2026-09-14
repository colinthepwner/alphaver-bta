package com.alphaver.mixin;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.menu.MenuCrafting;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MenuCrafting.class, remap = false)
public abstract class MenuCraftingMixin {

	@Shadow
	@Final
	private World world;

	@Shadow
	@Final
	private TilePos tilePos;

	@Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
	private void alphaver$cypressWorkbenches(Player player, CallbackInfoReturnable<Boolean> cir) {
		Block<?> block = this.world.getBlockType(this.tilePos);
		if (AVBlocks.isWorkbench(block)) {
			cir.setReturnValue(player.distanceToSqr(this.tilePos.x + 0.5, this.tilePos.y + 0.5, this.tilePos.z + 0.5) <= 64.0);
		}
	}
}
