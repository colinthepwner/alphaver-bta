package com.alphaver.net;

import com.alphaver.block.machine.MenuEssenceTransformer;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class MessageMachineAction implements NetworkMessage {

	public static final int CONFIRM_TRANSFORM = 0;

	private int windowId;
	private int action;

	public MessageMachineAction() {
	}

	public MessageMachineAction(int windowId, int action) {
		this.windowId = windowId;
		this.action = action;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeInt(this.windowId);
		packet.writeByte(this.action);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.windowId = packet.readInt();
		this.action = packet.readByte();
	}

	@Override
	public void handleServerEnv(@NotNull NetworkContext context) {
		Player player = context.player;
		if (player == null || player.containerMenu == null || player.containerMenu.containerId != this.windowId) {
			return;
		}
		if (this.action == CONFIRM_TRANSFORM && player.containerMenu instanceof MenuEssenceTransformer transformer
			&& transformer.stillValid(player)) {
			transformer.confirm(player);
		}
	}
}
