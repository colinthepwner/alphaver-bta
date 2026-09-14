package com.alphaver.server;

import net.minecraft.core.entity.player.Player;
import net.minecraft.server.entity.player.PlayerServer;

public final class AVServerTeleport {
	private AVServerTeleport() {}

	public static void teleport(Player player, double x, double feetY, double z, float yaw) {
		if (!(player instanceof PlayerServer server)) {
			player.moveTo(x, feetY, z, yaw, player.xRot);
			return;
		}
		server.setPos(x, feetY, z);
		server.yRot = yaw;
		if (server.playerNetServerHandler != null) {
			server.playerNetServerHandler.teleportAndRotate(x, feetY, z, yaw, server.xRot);
		}
		if (server.mcServer != null && server.mcServer.playerList != null) {
			server.mcServer.playerList.resendChunksAfterTeleport(server);
		}
	}
}
