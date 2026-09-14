package com.alphaver.block.machine;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVEssenceValues;
import com.alphaver.item.AVItems;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.container.ContainerSimple;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuEssenceCloner extends MenuCypressMachine {

	private static final int SLOTS = 3;
	private static final int STACK_LIMIT = 64;

	public final ContainerSimple inputs = new ContainerSimple("container.alphaver.essence_cloner", SLOTS);
	public final ContainerSimple outputs = new ContainerSimple("container.alphaver.essence_cloner", SLOTS);

	private final ItemStack[] preview = new ItemStack[SLOTS];

	private final List<ItemStack> extraChange = new ArrayList<>();
	private boolean dealClosed;

	public MenuEssenceCloner(@NotNull ContainerInventory inventory, @NotNull World world, @NotNull TilePosc tilePos) {
		super(world, tilePos, AVBlocks.ESSENCE_CLONER, SLOTS, SLOTS * 2);
		for (int i = 0; i < SLOTS; i++) {
			this.addSlot(new Slot(this.inputs, i, 30 + i * 18, 35));
		}
		for (int i = 0; i < SLOTS; i++) {
			this.addSlot(new SlotOutput(this.outputs, i, 102 + i * 18, 35));
		}
		this.addPlayerInventory(inventory);
	}

	@Override
	public ItemStack clicked(@NotNull InventoryAction action, @Nullable int[] args, @NotNull Player player) {
		ItemStack result = super.clicked(action, args, player);
		this.update(player);
		return result;
	}

	private void update(Player player) {
		if (!this.dealClosed) {
			if (this.outputsMatchPreview()) {
				this.preview();
			} else {
				this.dealClosed = true;
				for (int i = 0; i < SLOTS; i++) {
					this.inputs.setItem(i, null);
				}
				for (ItemStack change : this.extraChange) {
					this.storeOrDropItem(player, change);
				}
				this.extraChange.clear();
				Arrays.fill(this.preview, null);
			}
		}
		if (this.dealClosed && this.outputsEmpty()) {
			this.dealClosed = false;
			this.preview();
		}
		this.broadcastChanges();
	}

	private void preview() {
		this.extraChange.clear();
		for (int i = 0; i < SLOTS; i++) {
			this.outputs.setItem(i, null);
			this.preview[i] = null;
		}
		long paid = 0L;
		ItemStack item = null;
		for (int i = 0; i < SLOTS; i++) {
			ItemStack stack = this.inputs.getItem(i);
			if (stack == null) {
				continue;
			}
			if (isCache(stack)) {
				paid += 9L * stack.stackSize;
			} else if (isEssence(stack)) {
				paid += stack.stackSize;
			} else if (item != null) {
				return;
			} else {
				item = stack;
			}
		}
		if (item == null || paid <= 0L) {
			return;
		}
		long copies = Math.min(paid / AVEssenceValues.of(item), STACK_LIMIT - item.stackSize);
		if (copies <= 0L) {
			return;
		}
		ItemStack cloned = item.copy();
		cloned.stackSize = (int) (item.stackSize + copies);
		this.show(0, cloned);

		int slot = 1;
		for (ItemStack change : change(paid - copies * AVEssenceValues.of(item))) {
			if (slot < SLOTS) {
				this.show(slot++, change);
			} else {
				this.extraChange.add(change);
			}
		}
	}

	private static List<ItemStack> change(long essence) {
		List<ItemStack> out = new ArrayList<>();
		if (essence <= 0L || AVItems.ESSENCE == null || AVBlocks.ESSENCE_CACHE == null) {
			return out;
		}
		if (essence <= STACK_LIMIT) {
			out.add(new ItemStack(AVItems.ESSENCE, (int) essence));
			return out;
		}
		for (long caches = essence / 9L; caches > 0L; caches -= STACK_LIMIT) {
			out.add(new ItemStack(AVBlocks.ESSENCE_CACHE, (int) Math.min(caches, STACK_LIMIT)));
		}
		if (essence % 9L > 0L) {
			out.add(new ItemStack(AVItems.ESSENCE, (int) (essence % 9L)));
		}
		return out;
	}

	private void show(int slot, ItemStack stack) {
		this.outputs.setItem(slot, stack);
		this.preview[slot] = stack.copy();
	}

	private boolean outputsMatchPreview() {
		for (int i = 0; i < SLOTS; i++) {
			ItemStack now = this.outputs.getItem(i);
			ItemStack was = this.preview[i];
			if (now == null || was == null) {
				if (now != was) {
					return false;
				}
			} else if (now.itemID != was.itemID || now.stackSize != was.stackSize || now.getMetadata() != was.getMetadata()) {
				return false;
			}
		}
		return true;
	}

	private boolean outputsEmpty() {
		for (int i = 0; i < SLOTS; i++) {
			if (this.outputs.getItem(i) != null) {
				return false;
			}
		}
		return true;
	}

	private static boolean isCache(ItemStack stack) {
		return AVBlocks.ESSENCE_CACHE != null && stack.itemID == AVBlocks.ESSENCE_CACHE.id();
	}

	private static boolean isEssence(ItemStack stack) {
		return AVItems.ESSENCE != null && stack.itemID == AVItems.ESSENCE.id;
	}

	@Override
	public void onCraftGuiClosed(@NotNull Player player) {
		super.onCraftGuiClosed(player);
		if (this.dealClosed) {
			this.returnAll(player, this.outputs);
			for (ItemStack change : this.extraChange) {
				this.storeOrDropItem(player, change);
			}
		} else {
			this.returnAll(player, this.inputs);
			for (int i = 0; i < SLOTS; i++) {
				this.outputs.setItem(i, null);
			}
		}
		this.extraChange.clear();
		Arrays.fill(this.preview, null);
		this.dealClosed = false;
	}
}
