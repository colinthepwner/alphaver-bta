package com.alphaver.mixin;

import com.alphaver.block.CypressLeafDrops;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicLeavesBase;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockLogicLeavesBase.class, remap = false)
public abstract class BlockLogicLeavesBaseCypressMixin {

	@Inject(method = "getBreakResult", at = @At("HEAD"), cancellable = true)
	private void alphaver$cypressLeafDrops(World world, EnumDropCause dropCause, int data, TileEntity tileEntity,
	                                       CallbackInfoReturnable<ItemStack[]> cir) {
		if (((BlockLogic) (Object) this).block != Blocks.LEAVES_OAK_RETRO || !AVWorlds.isCypress(world)) {
			return;
		}
		if (dropCause == EnumDropCause.PICK_BLOCK || dropCause == EnumDropCause.SILK_TOUCH) {
			return;
		}
		cir.setReturnValue(CypressLeafDrops.roll(world.rand, Blocks.SAPLING_OAK, false));
	}
}
