package com.alphaver.block.machine;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVEssenceValues;
import com.alphaver.item.AVItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.player.inventory.container.Container;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

public final class EssenceFountainItems {
	private EssenceFountainItems() {}

	public static final int NO_FOUNTAIN = Integer.MIN_VALUE;
	public static final double LIFT = 0.2;
	public static final double ITEM_GRAVITY = 0.04;
	public static final double STRENGTH_SPEED = 0.015;
	private static final double ICE_KICK = 2.3;
	private static final int ESSENCE_STACK = 64;

	public static int findFountainBelow(World world, int x, int y, int z) {
		if (AVBlocks.ESSENCE_FOUNTAIN == null) {
			return NO_FOUNTAIN;
		}
		TilePos query = new TilePos();
		for (int cellY = y; cellY >= 0; cellY--) {
			query.set(x, cellY, z);
			Block<?> block = world.getBlockType(query);
			if (block == null || block == Blocks.AIR) {
				continue;
			}
			return block == AVBlocks.ESSENCE_FOUNTAIN ? cellY : NO_FOUNTAIN;
		}
		return NO_FOUNTAIN;
	}

	public static int apply(EntityItem entity, int x, int y, int z, int fountainY, boolean enteredCell) {
		World world = entity.world;
		TilePos fountainPos = new TilePos(x, fountainY, z);
		if (!(world.getTileEntity(fountainPos) instanceof TileEntityEssenceFountain fountain) || fountain.count <= 0) {
			return 0;
		}
		TilePos underPos = new TilePos(x, fountainY - 1, z);
		Block<?> under = world.getBlockType(underPos);
		boolean inBasin = y == fountainY;
		boolean once = enteredCell && inBasin && !world.isClientSide;

		if (under != null && under == AVBlocks.ESSENCE_TRANSFORMER) {
			if (once && fountain.isFull()) {
				transform(entity);
			}
			return 0;
		}
		if (under != null && under == AVBlocks.ESSENCE_CLONER) {
			if (once && fountain.isFull()) {
				compress(entity);
			}
			return 0;
		}
		TileEntity underEntity = world.getTileEntity(underPos);
		if (underEntity instanceof TileEntityChest chest) {
			if (once && fountain.isFull()) {
				deposit(entity, chest);
			}
			return 0;
		}
		if (under == Blocks.ICE) {
			if (enteredCell && inBasin) {
				entity.xd *= ICE_KICK;
				entity.zd *= ICE_KICK;
			}
			return 0;
		}
		if (under == Blocks.FURNACE_STONE_IDLE || under == Blocks.FURNACE_STONE_ACTIVE) {
			return 0;
		}
		return fountain.count - (y - fountainY);
	}

	private static void transform(EntityItem entity) {
		ItemStack stack = entity.item;
		if (AVItems.ESSENCE == null || stack == null || stack.itemID == AVItems.ESSENCE.id) {
			return;
		}
		long worth = AVEssenceValues.total(stack);
		while (worth > ESSENCE_STACK) {
			spawnBeside(entity, new ItemStack(AVItems.ESSENCE, ESSENCE_STACK));
			worth -= ESSENCE_STACK;
		}
		entity.item = new ItemStack(AVItems.ESSENCE, (int) Math.max(1L, worth));
	}

	private static void compress(EntityItem entity) {
		ItemStack stack = entity.item;
		if (AVItems.ESSENCE == null || AVBlocks.ESSENCE_CACHE == null || stack == null
			|| stack.itemID != AVItems.ESSENCE.id || stack.stackSize < 9) {
			return;
		}
		int caches = stack.stackSize / 9;
		int left = stack.stackSize % 9;
		if (left == 0) {
			entity.item = new ItemStack(AVBlocks.ESSENCE_CACHE, caches);
		} else {
			spawnBeside(entity, new ItemStack(AVBlocks.ESSENCE_CACHE, caches));
			stack.stackSize = left;
		}
	}

	private static void deposit(EntityItem entity, Container chest) {
		ItemStack stack = entity.item;
		if (stack == null) {
			return;
		}
		int limit = Math.min(chest.getMaxStackSize(), stack.getMaxStackSize());
		for (int i = 0; i < chest.getContainerSize() && stack.stackSize > 0; i++) {
			ItemStack slot = chest.getItem(i);
			if (slot != null && slot.canStackWith(stack) && slot.stackSize < limit) {
				int moved = Math.min(limit - slot.stackSize, stack.stackSize);
				slot.stackSize += moved;
				stack.stackSize -= moved;
			}
		}
		for (int i = 0; i < chest.getContainerSize() && stack.stackSize > 0; i++) {
			if (chest.getItem(i) == null) {
				ItemStack placed = stack.copy();
				placed.stackSize = Math.min(limit, stack.stackSize);
				chest.setItem(i, placed);
				stack.stackSize -= placed.stackSize;
			}
		}
		chest.setChanged();
		if (stack.stackSize <= 0) {
			entity.remove();
		}
	}

	private static void spawnBeside(EntityItem entity, ItemStack stack) {
		EntityItem copy = new EntityItem(entity.world, entity.x, entity.y, entity.z, stack);
		copy.xd = entity.xd;
		copy.yd = entity.yd;
		copy.zd = entity.zd;
		copy.pickupDelay = entity.pickupDelay;
		entity.world.entityJoinedWorld(copy);
	}
}
