package com.alphaver.net;

import com.alphaver.entity.LilypadHunger;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

public class MessageLilypadHunger implements NetworkMessage {

	private int stage;

	public MessageLilypadHunger() {
	}

	public MessageLilypadHunger(int stage) {
		this.stage = stage;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.stage);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {

		this.stage = packet.readByte();
	}

	@Override
	public void handleClientEnv(@NotNull NetworkContext context) {
		LilypadHunger.receive(this.stage);
	}
}
