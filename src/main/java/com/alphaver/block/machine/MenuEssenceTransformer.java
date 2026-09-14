package com.alphaver.block.machine;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVEssenceValues;
import com.alphaver.item.AVItems;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import net.minecraft.core.player.inventory.container.ContainerSimple;
import net.minecraft.core.player.inventory.slot.Slot;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class MenuEssenceTransformer extends MenuCypressMachine {

	public static final int RESULT_ESSENCE = 0;
	public static final int RESULT_CACHES = 1;

	private static final long CACHE_THRESHOLD = 64L;

	public final ContainerSimple grid = new ContainerSimple("container.alphaver.essence_transformer", 9);
	public final ContainerSimple results = new ContainerSimple("container.alphaver.essence_transformer", 2);

	public MenuEssenceTransformer(@NotNull ContainerInventory inventory, @NotNull World world, @NotNull TilePosc tilePos) {
		super(world, tilePos, AVBlocks.ESSENCE_TRANSFORMER, 9, 11);
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 3; column++) {
				this.addSlot(new Slot(this.grid, column + row * 3, 30 + column * 18, 17 + row * 18));
			}
		}
		this.addSlot(new SlotOutput(this.results, RESULT_CACHES, 102, 35));
		this.addSlot(new SlotOutput(this.results, RESULT_ESSENCE, 138, 35));
		this.addPlayerInventory(inventory);
	}

	public long totalValue() {
		long total = 0L;
		for (int i = 0; i < this.grid.getContainerSize(); i++) {
			total += AVEssenceValues.total(this.grid.getItem(i));
		}
		return total;
	}

	public boolean confirm(@NotNull Player player) {
		if (this.results.getItem(RESULT_ESSENCE) != null || this.results.getItem(RESULT_CACHES) != null
			|| AVItems.ESSENCE == null || AVBlocks.ESSENCE_CACHE == null) {
			return false;
		}
		long essence = this.totalValue();
		if (essence <= 0L) {
			return false;
		}
		for (int i = 0; i < this.grid.getContainerSize(); i++) {
			this.grid.setItem(i, null);
		}

		long caches = 0L;
		if (essence > CACHE_THRESHOLD) {
			caches = essence / 9L;
			essence %= 9L;
		}
		if (caches > 0L) {
			int max = Math.min(this.results.getMaxStackSize(), new ItemStack(AVBlocks.ESSENCE_CACHE).getMaxStackSize());
			int here = (int) Math.min(caches, max);
			this.results.setItem(RESULT_CACHES, new ItemStack(AVBlocks.ESSENCE_CACHE, here));
			for (long rest = caches - here; rest > 0L; rest -= max) {
				this.storeOrDropItem(player, new ItemStack(AVBlocks.ESSENCE_CACHE, (int) Math.min(rest, max)));
			}
		}
		if (essence > 0L) {
			this.results.setItem(RESULT_ESSENCE, new ItemStack(AVItems.ESSENCE, (int) essence));
		}
		this.broadcastChanges();
		return true;
	}

	@Override
	public void onCraftGuiClosed(@NotNull Player player) {
		super.onCraftGuiClosed(player);
		this.returnAll(player, this.grid);
		this.returnAll(player, this.results);
	}
}
