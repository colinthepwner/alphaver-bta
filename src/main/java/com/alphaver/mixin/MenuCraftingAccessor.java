package com.alphaver.mixin;

import net.minecraft.core.player.inventory.menu.MenuCrafting;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = MenuCrafting.class, remap = false)
public interface MenuCraftingAccessor {

	@Accessor("world")
	World alphaver$getWorld();
}
