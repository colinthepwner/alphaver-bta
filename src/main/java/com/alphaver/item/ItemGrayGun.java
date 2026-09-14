package com.alphaver.item;

import com.alphaver.entity.EntityGrayGunShot;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemGrayGun extends ItemEssenceRifle {

	private static final int ZOMBIES_AMMO_PER_SHOT = 10;

	public ItemGrayGun(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (!AVWorlds.isZombies(world) || AVItems.ESSENCE == null || !player.getGamemode().hasBlockConsumption()) {
			return super.onUse(selfStack, world, player);
		}
		if (essenceCarried(player) <= ZOMBIES_AMMO_PER_SHOT) {
			return selfStack;
		}
		for (int i = 0; i < ZOMBIES_AMMO_PER_SHOT; i++) {
			player.inventory.consumeInventoryItem(AVItems.ESSENCE.id);
		}
		if (!world.isClientSide) {
			world.entityJoinedWorld(this.createShot(world, player));
		}
		return selfStack;
	}

	@Override
	protected Entity createShot(@NotNull World world, @NotNull Player player) {
		return new EntityGrayGunShot(world, player);
	}

	private static int essenceCarried(Player player) {
		int count = 0;
		for (ItemStack stack : player.inventory.mainInventory) {
			if (stack != null && stack.itemID == AVItems.ESSENCE.id) {
				count += stack.stackSize;
			}
		}
		return count;
	}
}
