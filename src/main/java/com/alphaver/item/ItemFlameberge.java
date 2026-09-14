package com.alphaver.item;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class ItemFlameberge extends ItemToolSword {

	private static final int[] RING = {3, 0, 3, 1, 2, 2, 1, 3, 0, 3, -1, 3, -2, 2, -3, 1, -3, 0, -3, -1, -2, -2, -1, -3, 0, -3,
		1, -3, 2, -2, 3, -1};

	private static final int SEARCH_STEPS = 6;

	static final ToolMaterial MATERIAL = new ToolMaterial().setDurability(128).setEfficiency(6.0F, 8.0F).setMiningLevel(2);

	public ItemFlameberge(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id, MATERIAL);
	}

	public static boolean isHeldBy(@NotNull Player player) {
		if (AVItems.FLAMEBERGE == null) {
			return false;
		}
		ItemStack held = player.inventory.getCurrentItem();
		return held != null && held.getItem() == AVItems.FLAMEBERGE;
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (world.isClientSide) {
			return selfStack;
		}
		int x = (int) player.x;
		int y = (int) player.y;
		int z = (int) player.z;
		world.playSoundAtEntity(null, player, "alphaver:ext.useflame", 0.4F, world.rand.nextFloat() * 0.3F + 0.7F);
		for (int i = 0; i < RING.length / 2; i++) {
			ignite(world, x + RING[i * 2], y, z + RING[i * 2 + 1]);
		}
		return selfStack;
	}

	@SuppressWarnings("deprecation")
	private static void ignite(World world, int x, int y, int z) {
		for (int step = 0; step < SEARCH_STEPS; step++) {
			if (firable(world.getBlockId(x, y, z))) {
				if (!firable(world.getBlockId(x, y - 1, z))) {
					world.setBlockWithNotify(x, y, z, Blocks.FIRE.id());
					return;
				}
				y--;
			} else {
				y++;
			}
		}
	}

	private static boolean firable(int id) {
		return id == 0 || id == Blocks.LAYER_SNOW.id();
	}
}
