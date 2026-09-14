package com.alphaver.net;

import com.alphaver.entity.EntityEssenceShot;
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

import java.util.function.Function;

public class NetEntryEssenceShot<T extends EntityEssenceShot> implements IVehicleEntry<T>, ITrackedEntry<T> {

	private static final double MAX_PACKET_COMPONENT = 4.0;

	private final Class<T> type;
	private final Function<World, T> factory;

	public NetEntryEssenceShot(Class<T> type, Function<World, T> factory) {
		this.type = type;
		this.factory = factory;
	}

	@NotNull
	@Override
	public Class<T> getAppliedClass() {
		return this.type;
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
	public void onEntityTracked(EntityTracker tracker, EntityTrackerEntry trackerEntry, T shot) {
	}

	@Override
	public Entity getEntity(World world, double x, double y, double z, int metadata, boolean hasVelocity,
	                        double xd, double yd, double zd, Entity owner, @Nullable CompoundTag tag) {
		T shot = this.factory.apply(world);
		shot.setPos(x, y, z);
		return shot;
	}

	@Override
	public PacketAddEntity getSpawnPacket(EntityTrackerEntry trackerEntry, T shot) {
		Mob owner = shot.owner;
		double scale = packetScale(shot);
		return new PacketAddEntity(shot, -1, owner == null ? -1 : owner.id, shot.xd * scale, shot.yd * scale, shot.zd * scale);
	}

	static double packetScale(Entity entity) {
		double largest = Math.max(Math.abs(entity.xd), Math.max(Math.abs(entity.yd), Math.abs(entity.zd)));
		return largest > MAX_PACKET_COMPONENT ? MAX_PACKET_COMPONENT / largest : 1.0;
	}
}
