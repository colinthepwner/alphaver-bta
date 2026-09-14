package com.alphaver.net;

import com.alphaver.AVConfig;
import com.alphaver.world.AVDimensions;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.type.WorldType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.util.List;

public class MessageHandshake implements NetworkMessage {

	private static final int MAX_DIMENSIONS = 256;

	private int[] dimensionIds = new int[0];

	private int[] worldTypeIds = new int[0];
	private boolean frail;
	private String nebula = "milestones";
	private boolean biomes = true;
	private boolean dashing = true;

	public MessageHandshake() {
	}

	@NotNull
	public static MessageHandshake describeThisSide() {
		MessageHandshake message = new MessageHandshake();
		message.dimensionIds = thisSideDimensionIds();
		message.worldTypeIds = thisSideWorldTypeIds();
		message.frail = AVConfig.FRAIL;
		message.nebula = AVConfig.NEBULA;
		message.biomes = AVConfig.BIOMES;
		message.dashing = AVConfig.DASHING;
		return message;
	}

	@NotNull
	public static int[] thisSideDimensionIds() {
		List<Dimension> dimensions = AVDimensions.all();
		int[] ids = new int[dimensions.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = dimensionId(dimensions.get(i));
		}
		return ids;
	}

	@NotNull
	public static int[] thisSideWorldTypeIds() {
		List<Dimension> dimensions = AVDimensions.all();
		int[] ids = new int[dimensions.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = worldTypeId(dimensions.get(i).defaultWorldType);
		}
		return ids;
	}

	public static int dimensionId(@Nullable Dimension dimension) {
		return AVDimensions.isRegistered(dimension) ? dimension.id : -1;
	}

	public static int worldTypeId(@Nullable WorldType worldType) {
		return worldType == null ? -1 : Registries.WORLD_TYPES.getNumericIdOfItem(worldType);
	}

	@NotNull
	public int[] dimensionIds() {
		return this.dimensionIds.clone();
	}

	@NotNull
	public int[] worldTypeIds() {
		return this.worldTypeIds.clone();
	}

	public boolean frail() {
		return this.frail;
	}

	public String nebula() {
		return this.nebula;
	}

	public boolean biomes() {
		return this.biomes;
	}

	public boolean dashing() {
		return this.dashing;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeBoolean(this.frail);
		packet.writeString(this.nebula);
		packet.writeBoolean(this.biomes);
		packet.writeBoolean(this.dashing);
		writeInts(packet, this.dimensionIds);
		writeInts(packet, this.worldTypeIds);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.frail = packet.readBoolean();
		this.nebula = packet.readString();
		this.biomes = packet.readBoolean();
		this.dashing = packet.readBoolean();

		int[] ids = readInts(packet);
		int[] types = ids == null ? null : readInts(packet);
		this.dimensionIds = ids == null ? new int[0] : ids;
		this.worldTypeIds = types == null ? new int[0] : types;
	}

	private static void writeInts(UniversalPacket packet, int[] values) {
		packet.writeInt(values.length);
		for (int value : values) {
			packet.writeInt(value);
		}
	}

	@Nullable
	private static int[] readInts(UniversalPacket packet) {
		int length = packet.readInt();
		if (length < 0 || length > MAX_DIMENSIONS) {
			return null;
		}
		int[] values = new int[length];
		for (int i = 0; i < length; i++) {
			values[i] = packet.readInt();
		}
		return values;
	}

	@Override
	public void handleClientEnv(@NotNull NetworkContext context) {
		AVNetworkHooks.receiveHandshake(this);
	}
}
