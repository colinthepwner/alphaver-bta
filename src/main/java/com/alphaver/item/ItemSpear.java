package com.alphaver.item;

import com.alphaver.entity.EntitySpear;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemSpear extends Item {

	public ItemSpear(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		selfStack.consumeItem(player);
		world.playSoundAtEntity(player, player, "random.bow", 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
		if (!world.isClientSide) {
			world.entityJoinedWorld(new EntitySpear(world, player, true));
		}
		return selfStack;
	}
}
