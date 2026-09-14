package com.alphaver.mixin;

import net.minecraft.core.player.inventory.container.ContainerCrafting;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ContainerCrafting.class, remap = false)
public interface ContainerCraftingAccessor {

	@Accessor("menu")
	MenuAbstract alphaver$getMenu();
}
