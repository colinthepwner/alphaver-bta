package com.alphaver.net;

import com.alphaver.world.minigame.MinigameHudState;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.util.ArrayList;
import java.util.List;

public class MessageMinigameState implements NetworkMessage {

	private MinigameHudState state = new MinigameHudState();

	public MessageMinigameState() {
	}

	public MessageMinigameState(@NotNull MinigameHudState state) {
		this.state = state;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		MinigameHudState s = this.state;

		packet.writeByte(s.kind);
		packet.writeByte(s.stageCode);
		packet.writeInt((int) s.stageTime);
		packet.writeInt(s.wave);
		packet.writeInt(s.points);
		packet.writeInt(s.zombiesLeft);
		packet.writeByte(s.perks);
		packet.writeByte(s.downedTicks);
		packet.writeString(s.prompt);
		packet.writeBoolean(s.timerRunning);
		packet.writeInt(s.timerTicks);
		packet.writeByte(s.checkpoint);
		packet.writeByte(s.checkpoints);
		packet.writeInt(s.leaveHintSerial);
		packet.writeInt(s.nextWaveTicks);
		int mates = Math.min(s.mates.size(), MinigameHudState.MAX_MATES);
		packet.writeByte(mates);
		for (int i = 0; i < mates; i++) {
			MinigameHudState.Mate mate = s.mates.get(i);
			packet.writeString(mate.name());
			packet.writeInt(mate.points());
			packet.writeByte(mate.perks());
			packet.writeBoolean(mate.downed());
			packet.writeByte(mate.checkpoint());
			packet.writeInt(mate.finishTicks());
		}
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		MinigameHudState s = new MinigameHudState();
		s.kind = packet.readByte();
		s.stageCode = packet.readByte();
		s.stageTime = packet.readInt();
		s.wave = packet.readInt();
		s.points = packet.readInt();
		s.zombiesLeft = packet.readInt();
		s.perks = packet.readByte();
		s.downedTicks = packet.readByte();
		String prompt = packet.readString();
		s.prompt = prompt == null ? "" : prompt;
		s.timerRunning = packet.readBoolean();
		s.timerTicks = packet.readInt();
		s.checkpoint = packet.readByte();
		s.checkpoints = packet.readByte();
		s.leaveHintSerial = packet.readInt();
		s.nextWaveTicks = packet.readInt();
		int count = Math.max(0, Math.min(packet.readByte(), MinigameHudState.MAX_MATES));
		List<MinigameHudState.Mate> mates = new ArrayList<>(count);
		for (int i = 0; i < count; i++) {
			String name = packet.readString();
			int points = packet.readInt();
			int perks = packet.readByte();
			boolean downed = packet.readBoolean();
			int checkpoint = packet.readByte();
			int finishTicks = packet.readInt();
			mates.add(new MinigameHudState.Mate(name == null ? "" : name, points, perks, downed, checkpoint, finishTicks));
		}
		s.mates = List.copyOf(mates);
		this.state = s;
	}

	@Override
	public void handleClientEnv(@NotNull NetworkContext context) {
		MinigameHudState s = this.state;
		s.receivedAtNanos = System.nanoTime();
		s.extrapolate = true;
		MinigameHudState.local = s;
	}
}
