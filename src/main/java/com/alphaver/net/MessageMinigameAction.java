package com.alphaver.net;

import com.alphaver.world.minigame.AVMinigames;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class MessageMinigameAction implements NetworkMessage {

	private int action;

	public MessageMinigameAction() {
	}

	public MessageMinigameAction(int action) {
		this.action = action;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.action);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.action = packet.readByte();
	}

	@Override
	public void handleServerEnv(@NotNull NetworkContext context) {
		Player player = context.player;
		if (player != null) {
			AVMinigames.action(player, this.action);
		}
	}
}
