package com.alphaver.mixin;

import com.alphaver.item.AVDyes;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryDyeing;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = RecipeEntryDyeing.class, remap = false)
public abstract class RecipeEntryDyeingCypressDyesMixin {

	@Redirect(method = {"matches", "getCraftingResult"},
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/core/player/inventory/container/ContainerCrafting;getItem(I)Lnet/minecraft/core/item/ItemStack;"))
	private ItemStack alphaver$cypressDyeAsDye(ContainerCrafting grid, int slot) {
		return AVDyes.forDyeing(((RecipeEntryDyeing) (Object) this).inputSymbol, grid.getItem(slot));
	}
}
