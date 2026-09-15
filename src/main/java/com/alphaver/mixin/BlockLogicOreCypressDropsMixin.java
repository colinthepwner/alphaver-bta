package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.BlockLogicOreGold;
import net.minecraft.core.block.BlockLogicOreIron;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {BlockLogicOreIron.class, BlockLogicOreGold.class}, remap = false)
public abstract class BlockLogicOreCypressDropsMixin {

	@Inject(method = "getBreakResult(Lnet/minecraft/core/world/World;Lnet/minecraft/core/enums/EnumDropCause;ILnet/minecraft/core/block/entity/TileEntity;)[Lnet/minecraft/core/item/ItemStack;",
		at = @At("HEAD"), cancellable = true)
	private void alphaver$oreDropsItself(World world, EnumDropCause dropCause, int data, TileEntity tileEntity,
	                                     CallbackInfoReturnable<ItemStack[]> cir) {
		if (!AVWorlds.isAlphaVer(world)) {
			return;
		}
		if (dropCause == EnumDropCause.PROPER_TOOL || dropCause == EnumDropCause.EXPLOSION || dropCause == EnumDropCause.PISTON_CRUSH) {
			cir.setReturnValue(new ItemStack[]{new ItemStack(((BlockLogic) (Object) this).block)});
		}
	}
}
