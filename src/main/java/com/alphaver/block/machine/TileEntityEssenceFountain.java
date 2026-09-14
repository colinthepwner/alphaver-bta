package com.alphaver.block.machine;

import com.alphaver.item.AVItems;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.net.packet.Packet;
import net.minecraft.core.net.packet.PacketTileEntityData;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;

import java.util.List;
import java.util.Random;

public class TileEntityEssenceFountain extends TileEntity {

	public static final int CAPACITY = 64;
	private static final int RANDOM_TICK_ODDS = 10;
	private static final int RANDOM_TICK_OUT_OF = 4096;

	public int count;
	private int counter;
	private final Random random = new Random();

	@Override
	public void tick() {
		if (this.worldObj == null || this.worldObj.isClientSide) {
			return;
		}
		boolean changed = this.absorbEssence();
		if (this.count != 0 && this.random.nextInt(RANDOM_TICK_OUT_OF) < RANDOM_TICK_ODDS) {
			changed |= this.evaporate();
		}
		if (changed) {
			this.setChanged();
		}
	}

	public boolean isFull() {
		return this.count >= CAPACITY;
	}

	private boolean absorbEssence() {
		if (this.count >= CAPACITY || AVItems.ESSENCE == null) {
			return false;
		}
		int x = this.tilePos.x;
		int y = this.tilePos.y;
		int z = this.tilePos.z;
		List<EntityItem> items = this.worldObj.getEntitiesWithinAABB(EntityItem.class, new AABBd(x, y, z, x + 1.0, y + 1.0, z + 1.0));
		int before = this.count;
		for (EntityItem entity : items) {
			if (this.count >= CAPACITY) {
				break;
			}
			if (entity.removed || entity.item == null || entity.item.itemID != AVItems.ESSENCE.id
				|| MathHelper.floor(entity.x) != x || MathHelper.floor(entity.y) != y || MathHelper.floor(entity.z) != z) {
				continue;
			}
			int taken = Math.min(CAPACITY - this.count, entity.item.stackSize);
			this.count += taken;
			entity.item.stackSize -= taken;
			if (entity.item.stackSize <= 0) {
				entity.remove();
			}
		}
		if (this.count == before) {
			return false;
		}
		if (before == 0 || this.count == CAPACITY) {
			this.redraw();
		}
		return true;
	}

	private boolean evaporate() {
		this.counter++;
		if (this.counter < 2 || this.random.nextInt(3) != 0) {
			return false;
		}
		this.counter = 0;
		boolean wasFull = this.count == CAPACITY;
		this.count--;
		if (this.count == 0) {
			for (int i = 0; i < 12; i++) {
				this.worldObj.spawnParticle("smoke", this.tilePos.x + this.random.nextFloat(), this.tilePos.y + 0.4,
					this.tilePos.z + this.random.nextFloat(), 0.0, 0.1, 0.0, 0, true);
			}
		}
		if (wasFull || this.count == 0) {
			this.redraw();
		}
		return true;
	}

	private void redraw() {
		this.worldObj.markBlockNeedsUpdate(this.tilePos.x, this.tilePos.y, this.tilePos.z);
	}

	@Override
	public void readAdditionalData(@NotNull CompoundTag tag) {
		this.counter = tag.getShort("counter");
		this.count = Math.max(0, Math.min(CAPACITY, (int) tag.getShort("count")));
	}

	@Override
	public void writeAdditionalData(@NotNull CompoundTag tag) {
		tag.putShort("counter", (short) this.counter);
		tag.putShort("count", (short) this.count);
	}

	@Override
	public Packet getDescriptionPacket() {
		return new PacketTileEntityData(this);
	}
}
