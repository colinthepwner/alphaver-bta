package com.alphaver.item;

import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;

public class ItemHearthenMirror extends Item {

	private static final long MESSAGE_MS = 4000L;

	private static final int TOAST_ID = 139;

	public ItemHearthenMirror(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (!world.isClientSide) {
			int x = (int) player.x;
			int y = (int) player.y;
			int z = (int) player.z;
			if (EnvironmentHelper.isMultiplayerServer()) {

				player.setPlayerSpawnPoint(null);
				((AVMirrorSpawnData) player).alphaver$setMirrorSpawn(player.dimension, x, y, z);
			} else {
				world.getLevelData().getSpawnPos().set(x, y, z);

				((AVMirrorSpawnData) player).alphaver$setMirrorSpawn(player.dimension, x, y, z);
			}
		}
		AVItemHooks.showMessage(I18n.getInstance().translateKey("message.alphaver.hearthen_mirror.spawn_set"), TOAST_ID, MESSAGE_MS);
		return selfStack;
	}
}
