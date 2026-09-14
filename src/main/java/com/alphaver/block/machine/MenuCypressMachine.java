package com.alphaver.block.machine;

import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public abstract class MenuCypressMachine extends MenuAbstract {

	private final World world;
	private final TilePos tilePos;
	private final Block<?> machine;
	private final int inputCount;
	private final int inventoryStart;

	protected MenuCypressMachine(@NotNull World world, @NotNull TilePosc tilePos, Block<?> machine, int inputCount, int machineSlots) {
		this.world = world;
		this.tilePos = new TilePos(tilePos);
		this.machine = machine;
		this.inputCount = inputCount;
		this.inventoryStart = machineSlots;
	}

	protected void addPlayerInventory(@NotNull ContainerInventory inventory) {
		for (int y = 0; y < 3; y++) {
			for (int x = 0; x < 9; x++) {
				this.addSlot(new Slot(inventory, x + y * 9 + 9, 8 + x * 18, 84 + y * 18));
			}
		}
		for (int i = 0; i < 9; i++) {
			this.addSlot(new Slot(inventory, i, 8 + i * 18, 142));
		}
	}

	protected void returnAll(@NotNull Player player, @NotNull Container container) {
		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);
			container.setItem(i, null);
			this.storeOrDropItem(player, stack);
		}
	}

	@Override
	public boolean stillValid(@NotNull Player player) {
		return this.machine != null
			&& this.world.getBlockType(this.tilePos) == this.machine
			&& player.distanceToSqr(this.tilePos.x + 0.5, this.tilePos.y + 0.5, this.tilePos.z + 0.5) <= 64.0;
	}

	@Override
	public IntList getMoveSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
		int index = slot.index;
		if (index < this.inventoryStart) {
			return index < this.inputCount ? this.getSlots(0, this.inputCount, false) : this.getSlots(index, 1, false);
		}
		if (action == InventoryAction.MOVE_SIMILAR) {
			return index < this.inventoryStart + 36 ? this.getSlots(this.inventoryStart, 36, false) : null;
		}
		if (index < this.inventoryStart + 27) {
			return this.getSlots(this.inventoryStart, 27, false);
		}
		return index < this.inventoryStart + 36 ? this.getSlots(this.inventoryStart + 27, 9, false) : null;
	}

	@Override
	public IntList getTargetSlots(@NotNull InventoryAction action, @NotNull Slot slot, int target, Player player) {
		int index = slot.index;
		if (index >= this.inventoryStart) {
			if (target == 1) {
				return this.getSlots(0, this.inputCount, false);
			}
			if (index < this.inventoryStart + 27) {
				return this.getSlots(this.inventoryStart + 27, 9, false);
			}
			return index < this.inventoryStart + 36 ? this.getSlots(this.inventoryStart, 27, false) : null;
		}
		return this.getSlots(this.inventoryStart, 36, index >= this.inputCount);
	}

	public static class SlotOutput extends Slot {
		public SlotOutput(Container container, int index, int x, int y) {
			super(container, index, x, y);
		}

		@Override
		public boolean mayPlace(ItemStack stack) {
			return false;
		}
	}
}
