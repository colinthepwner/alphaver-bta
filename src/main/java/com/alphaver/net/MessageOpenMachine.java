package com.alphaver.net;

import com.alphaver.block.machine.MachineKind;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class MessageOpenMachine implements NetworkMessage {

	private int kind;
	private int windowId;
	private int x;
	private int y;
	private int z;

	public MessageOpenMachine() {
	}

	public MessageOpenMachine(@NotNull MachineKind kind, int windowId, @NotNull TilePosc tilePos) {
		this.kind = kind.ordinal();
		this.windowId = windowId;
		this.x = tilePos.x();
		this.y = tilePos.y();
		this.z = tilePos.z();
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.kind);
		packet.writeInt(this.windowId);
		packet.writeInt(this.x);
		packet.writeInt(this.y);
		packet.writeInt(this.z);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.kind = packet.readByte();
		this.windowId = packet.readInt();
		this.x = packet.readInt();
		this.y = packet.readInt();
		this.z = packet.readInt();
	}

	@Override
	public void handleClientEnv(@NotNull NetworkContext context) {
		MachineKind machine = MachineKind.byId(this.kind);
		if (machine != null) {
			AVNetworkHooks.openMachineScreen(machine, this.windowId, this.x, this.y, this.z);
		}
	}
}
