package com.alphaver.mixin;

import com.alphaver.item.ItemEraser;
import com.alphaver.item.ItemObsidianPickaxe;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockLogic.class, remap = false)
public abstract class BlockLogicObsidianPickaxeMixin {

	@Shadow
	@Final
	public Block<?> block;

	@Inject(method = "getStrength", at = @At("HEAD"), cancellable = true)
	private void alphaver$elderstone(World world, TilePosc tilePos, Side side, Player player, CallbackInfoReturnable<Float> cir) {
		if (this.block != Blocks.BEDROCK || !AVWorlds.isCypress(world)) {
			return;
		}
		ItemStack held = player.inventory.getCurrentItem();
		if (held == null) {
			return;
		}
		if (held.getItem() instanceof ItemEraser) {

			cir.setReturnValue(1.0F);
			return;
		}
		if (!(held.getItem() instanceof ItemObsidianPickaxe)) {
			return;
		}
		float strength = 1.0F;
		if (player.isUnderAcidOrWater()) {
			strength /= 4.0F;
		}
		if (!player.onGround && !player.canClimb() && player.vehicle == null) {
			strength /= 4.0F;
		}
		cir.setReturnValue(strength / 2.0F / 30.0F);
	}
}
