package com.alphaver.item;

import com.alphaver.entity.EntityEssenceShot;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemEssenceRifle extends Item {

	public ItemEssenceRifle(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (AVItems.ESSENCE == null) {
			return selfStack;
		}
		boolean free = !player.getGamemode().hasBlockConsumption();
		if (!free && !player.inventory.consumeInventoryItem(AVItems.ESSENCE.id)) {
			return selfStack;
		}
		if (!world.isClientSide) {
			world.entityJoinedWorld(this.createShot(world, player));
		}
		return selfStack;
	}

	protected Entity createShot(@NotNull World world, @NotNull Player player) {
		return new EntityEssenceShot(world, player);
	}
}
