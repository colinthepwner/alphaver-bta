package com.alphaver.mixin;

import com.alphaver.AVDimensionRecipes;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = RecipeRegistry.class, remap = false)
public abstract class RecipeRegistryDimensionMixin {

	@Inject(method = "findMatchingCraftingRecipe(Lnet/minecraft/core/player/inventory/container/ContainerCrafting;)Lnet/minecraft/core/data/registry/recipe/entry/RecipeEntryCrafting;",
		at = @At("HEAD"), cancellable = true)
	private void alphaver$recipeByDimension(ContainerCrafting grid, CallbackInfoReturnable<RecipeEntryCrafting<?, ?>> cir) {
		if (!AVDimensionRecipes.any()) {
			return;
		}
		RecipeRegistry registry = (RecipeRegistry) (Object) this;
		List<RecipeEntryCrafting<?, ?>> cypress = AVDimensionRecipes.entries(registry);
		List<RecipeEntryCrafting<?, ?>> blocked = AVDimensionRecipes.blockedInside(registry);
		if (cypress.isEmpty() && blocked.isEmpty()) {
			return;
		}

		if (AVWorlds.isAlphaVer(AVDimensionRecipes.worldOf(grid))) {
			for (RecipeEntryCrafting<?, ?> recipe : cypress) {
				if (recipe.matches(grid)) {
					cir.setReturnValue(recipe);
					return;
				}
			}
			if (blocked.isEmpty()) {
				return;
			}
			for (RecipeEntryCrafting<?, ?> recipe : registry.getAllCraftingRecipes()) {
				if (!AVDimensionRecipes.isAmong(blocked, recipe) && recipe.matches(grid)) {
					cir.setReturnValue(recipe);
					return;
				}
			}
			cir.setReturnValue(null);
			return;
		}

		if (cypress.isEmpty()) {
			return;
		}
		for (RecipeEntryCrafting<?, ?> recipe : registry.getAllCraftingRecipes()) {
			if (!AVDimensionRecipes.isAmong(cypress, recipe) && recipe.matches(grid)) {
				cir.setReturnValue(recipe);
				return;
			}
		}
		cir.setReturnValue(null);
	}
}
