package com.alphaver.mixin;

import com.alphaver.block.AVRetroItems;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = BlockLogic.class, remap = false)
public abstract class BlockLogicRetroDropsMixin {

	@Redirect(
		method = "dropWithCause(Lnet/minecraft/core/world/World;Lnet/minecraft/core/enums/EnumDropCause;Lnet/minecraft/core/world/pos/TilePosc;ILnet/minecraft/core/block/entity/TileEntity;Lnet/minecraft/core/entity/player/Player;)V",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/core/block/BlockLogic;getBreakResult(Lnet/minecraft/core/world/World;Lnet/minecraft/core/enums/EnumDropCause;Lnet/minecraft/core/world/pos/TilePosc;ILnet/minecraft/core/block/entity/TileEntity;)[Lnet/minecraft/core/item/ItemStack;"))
	private ItemStack[] alphaver$noRetroDrops(BlockLogic logic, World world, EnumDropCause dropCause, TilePosc tilePos, int data,
	                                          TileEntity tileEntity) {
		return AVRetroItems.regularInside(world, logic.getBreakResult(world, dropCause, tilePos, data, tileEntity));
	}
}
