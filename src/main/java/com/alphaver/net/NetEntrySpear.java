package com.alphaver.net;

import com.alphaver.entity.EntitySpear;
import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.net.entity.EntityTracker;
import net.minecraft.core.net.entity.EntityTrackerEntry;
import net.minecraft.core.net.entity.ITrackedEntry;
import net.minecraft.core.net.entity.IVehicleEntry;
import net.minecraft.core.net.packet.PacketAddEntity;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetEntrySpear implements IVehicleEntry<EntitySpear>, ITrackedEntry<EntitySpear> {

	@NotNull
	@Override
	public Class<EntitySpear> getAppliedClass() {
		return EntitySpear.class;
	}

	@Override
	public int getTrackingDistance() {
		return 64;
	}

	@Override
	public int getMovementPacketDelay() {
		return 20;
	}

	@Override
	public boolean sendMotionUpdates() {
		return false;
	}

	@Override
	public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, EntitySpear spear) {
	}

	@Override
	public Entity getEntity(World world, double x, double y, double z, int metadata, boolean hasVelocity,
	                        double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag) {
		EntitySpear spear = new EntitySpear(world);
		spear.setPos(x, y, z);
		return spear;
	}

	@Override
	public PacketAddEntity getSpawnPacket(EntityTrackerEntry trackerEntry, EntitySpear spear) {
		Mob owner = spear.owner;
		double scale = NetEntryEssenceShot.packetScale(spear);
		return new PacketAddEntity(spear, -1, owner == null ? -1 : owner.id, spear.xd * scale, spear.yd * scale, spear.zd * scale);
	}
}
