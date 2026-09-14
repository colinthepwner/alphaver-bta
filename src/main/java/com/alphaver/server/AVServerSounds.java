package com.alphaver.server;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.packet.PacketPlaySoundDirect;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.sound.SoundTypes;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.SERVER)
public final class AVServerSounds {
	private AVServerSounds() {}

	public static void playTo(@NotNull Player player, @NotNull String sound, float volume, float pitch) {
		if (!(player instanceof PlayerServer playerServer) || playerServer.playerNetServerHandler == null) {
			return;
		}
		int id = SoundTypes.getSoundId(sound);
		if (id < 0) {
			return;
		}

		playerServer.playerNetServerHandler.sendPacket(new PacketPlaySoundDirect(id, SoundCategory.ENTITY_SOUNDS,
			playerServer.x, playerServer.y - playerServer.heightOffset, playerServer.z, volume, pitch));
	}
}
