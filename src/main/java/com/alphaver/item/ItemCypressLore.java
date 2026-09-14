package com.alphaver.item;

import com.alphaver.net.AVSounds;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemCypressLore extends Item {
	private final String loreKey;
	private final String title;
	private final int toastId;

	public ItemCypressLore(@NotNull String name, @NotNull String namespaceId, int id, @NotNull String title, int toastId) {
		super(name, namespaceId, id);
		this.loreKey = name;
		this.title = title;
		this.toastId = toastId;
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		AVSounds.playFor(player, AVItemHooks.NOTIFICATION_SOUND, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));
		AVItemHooks.showLore(this.loreKey, this.title, this.toastId);
		return selfStack;
	}
}
