package com.alphaver;

import com.alphaver.mixin.ContainerCraftingAccessor;
import com.alphaver.mixin.MenuCraftingAccessor;
import net.minecraft.core.data.registry.recipe.RecipeEntryBase;
import net.minecraft.core.data.registry.recipe.RecipeGroup;
import net.minecraft.core.data.registry.recipe.RecipeNamespace;
import net.minecraft.core.data.registry.recipe.RecipeRegistry;
import net.minecraft.core.data.registry.recipe.entry.RecipeEntryCrafting;
import net.minecraft.core.player.inventory.container.ContainerCrafting;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.menu.MenuCrafting;
import net.minecraft.core.player.inventory.menu.MenuInventory;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class AVDimensionRecipes {
	private AVDimensionRecipes() {}

	private static final String GROUP = "workbench";

	private static final Set<String> NAMES = new LinkedHashSet<>();

	private static final Set<String> BLOCKED_INSIDE = new LinkedHashSet<>();

	static void add(String name) {
		NAMES.add(name);
	}

	static void blockInside(String namespace, String name) {
		BLOCKED_INSIDE.add(namespace + ":" + name);
	}

	public static boolean any() {
		return !NAMES.isEmpty() || !BLOCKED_INSIDE.isEmpty();
	}

	public static List<RecipeEntryCrafting<?, ?>> entries(RecipeRegistry registry) {
		List<RecipeEntryCrafting<?, ?>> found = new ArrayList<>(NAMES.size());
		for (String name : NAMES) {
			RecipeEntryCrafting<?, ?> recipe = lookup(registry, AlphaVer.MOD_ID, name);
			if (recipe != null) {
				found.add(recipe);
			}
		}
		return found;
	}

	public static List<RecipeEntryCrafting<?, ?>> blockedInside(RecipeRegistry registry) {
		List<RecipeEntryCrafting<?, ?>> found = new ArrayList<>(BLOCKED_INSIDE.size());
		for (String key : BLOCKED_INSIDE) {
			int colon = key.indexOf(':');
			RecipeEntryCrafting<?, ?> recipe = lookup(registry, key.substring(0, colon), key.substring(colon + 1));
			if (recipe != null) {
				found.add(recipe);
			}
		}
		return found;
	}

	@Nullable
	private static RecipeEntryCrafting<?, ?> lookup(RecipeRegistry registry, String namespaceId, String name) {
		RecipeNamespace namespace = registry.getItem(namespaceId);
		if (namespace == null) {
			return null;
		}
		RecipeGroup<? extends RecipeEntryBase<?, ?, ?>> group = namespace.getItem(GROUP);
		if (group == null) {
			return null;
		}
		return group.getItem(name) instanceof RecipeEntryCrafting<?, ?> recipe ? recipe : null;
	}

	public static boolean isAmong(List<RecipeEntryCrafting<?, ?>> recipes, RecipeEntryCrafting<?, ?> recipe) {
		for (RecipeEntryCrafting<?, ?> candidate : recipes) {
			if (candidate == recipe) {
				return true;
			}
		}
		return false;
	}

	@Nullable
	public static World worldOf(@Nullable ContainerCrafting grid) {
		if (grid == null) {
			return null;
		}
		MenuAbstract menu = ((ContainerCraftingAccessor) grid).alphaver$getMenu();
		if (menu instanceof MenuCrafting crafting) {
			return ((MenuCraftingAccessor) crafting).alphaver$getWorld();
		}
		if (menu instanceof MenuInventory inventory && inventory.inventory != null && inventory.inventory.player != null) {
			return inventory.inventory.player.world;
		}
		return null;
	}
}
