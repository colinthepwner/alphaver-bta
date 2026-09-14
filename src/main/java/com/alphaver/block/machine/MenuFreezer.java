package com.alphaver.block.machine;

import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuFurnace;
import net.minecraft.core.player.inventory.slot.Slot;

public class MenuFreezer extends MenuFurnace {

	public MenuFreezer(ContainerInventory inventory, TileEntityFreezer freezer) {
		super(inventory, freezer);
		move(0, 16, 35);
		move(1, 60, 35);
	}

	private void move(int index, int x, int y) {
		Slot slot = this.slots.get(index);
		slot.x = x;
		slot.y = y;
	}
}
