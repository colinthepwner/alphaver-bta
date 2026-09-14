package com.alphaver.net;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkMessage;
import turniplabs.halplibe.helper.network.UniversalPacket;

import java.util.Map;
import java.util.WeakHashMap;

public class MessageDashSound implements NetworkMessage {

	public static final int DASH = 0;
	public static final int BOOST = 1;

	private static final long MIN_MILLIS_BETWEEN = 250L;
	private static final Map<Player, Long> LAST_PLAYED = new WeakHashMap<>();

	private int sound;

	public MessageDashSound() {
	}

	public MessageDashSound(int sound) {
		this.sound = sound;
	}

	@Override
	public void encodeToUniversalPacket(@NotNull UniversalPacket packet) {
		packet.writeByte(this.sound);
	}

	@Override
	public void decodeFromUniversalPacket(@NotNull UniversalPacket packet) {
		this.sound = packet.readByte();
	}

	@Override
	public void handleServerEnv(@NotNull NetworkContext context) {
		Player player = context.player;
		if (player == null || player.world == null || !AVWorlds.isAlphaVer(player.world)) {
			return;
		}
		String event;
		float volume;
		if (this.sound == DASH) {
			event = AVSounds.DASH;
			volume = AVSounds.DASH_VOLUME;
		} else if (this.sound == BOOST) {
			event = AVSounds.BOOST;
			volume = AVSounds.BOOST_VOLUME;
		} else {
			return;
		}
		long now = System.currentTimeMillis();
		synchronized (LAST_PLAYED) {
			Long last = LAST_PLAYED.get(player);
			if (last != null && now - last < MIN_MILLIS_BETWEEN) {
				return;
			}
			LAST_PLAYED.put(player, now);
		}
		player.world.playSoundAtEntity(player, player, event, volume, 1.0F);
	}
}
