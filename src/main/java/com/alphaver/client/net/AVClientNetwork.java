package com.alphaver.client.net;

import com.alphaver.AlphaVer;
import com.alphaver.block.machine.MachineKind;
import com.alphaver.block.machine.TileEntityFreezer;
import com.alphaver.client.gui.ScreenEssenceCloner;
import com.alphaver.client.gui.ScreenEssenceTransformer;
import com.alphaver.client.gui.ScreenFreezer;
import com.alphaver.net.AVNetworkHooks;
import com.alphaver.net.AVServerSettings;
import com.alphaver.net.MessageHandshake;
import com.alphaver.world.AVDimensions;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.pos.TilePos;

import java.util.Arrays;
import java.util.List;

@Environment(EnvType.CLIENT)
public final class AVClientNetwork {
	private AVClientNetwork() {}

	public static void install() {
		AVNetworkHooks.install(AVClientNetwork::openMachineScreen, AVClientNetwork::receiveHandshake);
	}

	private static void openMachineScreen(MachineKind kind, int windowId, int x, int y, int z) {
		Minecraft mc = Minecraft.getMinecraft();
		PlayerLocal player = mc.thePlayer;
		WorldClient world = mc.currentWorld;
		if (player == null || world == null) {
			return;
		}
		TilePos tilePos = new TilePos(x, y, z);
		switch (kind) {
			case FREEZER -> ScreenFreezer.open(player,
				world.getTileEntity(tilePos) instanceof TileEntityFreezer freezer ? freezer : new TileEntityFreezer());
			case TRANSFORMER -> ScreenEssenceTransformer.open(player, world, tilePos);
			case CLONER -> ScreenEssenceCloner.open(player, world, tilePos);
		}
		if (player.containerMenu != null) {
			player.containerMenu.containerId = windowId;
		}
	}

	private static void receiveHandshake(MessageHandshake server) {
		AVServerSettings.apply(server.frail(), server.nebula(), server.biomes(), server.dashing());

		int[] serverIds = server.dimensionIds();
		int[] clientIds = MessageHandshake.thisSideDimensionIds();
		int[] serverTypes = server.worldTypeIds();
		int[] clientTypes = MessageHandshake.thisSideWorldTypeIds();

		boolean dimensionsDiffer = serverIds.length != clientIds.length;
		for (int i = 0; !dimensionsDiffer && i < serverIds.length; i++) {
			dimensionsDiffer = differs(serverIds[i], clientIds[i]);
		}
		boolean worldTypesDiffer = !Arrays.equals(serverTypes, clientTypes);
		if (!dimensionsDiffer && !worldTypesDiffer) {
			return;
		}

		String serverDescription = describe(serverIds);
		String clientDescription = describe(clientIds);
		String reason = dimensionsDiffer
			? I18n.getInstance().translateKeyAndFormat("disconnect.alphaver.dimension_ids", serverDescription, clientDescription)
			: I18n.getInstance().translateKey("disconnect.alphaver.world_types");
		AlphaVer.LOGGER.error("Refusing to log in: server has {} (world types {}); this game has {} (world types {}).",
			serverDescription, Arrays.toString(serverTypes), clientDescription, Arrays.toString(clientTypes));
		AVServerSettings.refuse(reason);
	}

	private static boolean differs(int serverId, int clientId) {
		return serverId >= 0 && serverId != clientId;
	}

	private static String describe(int[] ids) {
		List<Dimension> dimensions = AVDimensions.all();
		StringBuilder out = new StringBuilder();
		for (int i = 0; i < ids.length; i++) {
			if (i > 0) {
				out.append(i == ids.length - 1 ? " and " : ", ");
			}
			out.append(i < dimensions.size() ? AVDimensions.nameOf(dimensions.get(i)) : "dimension " + (i + 1)).append(' ').append(ids[i]);
		}
		return out.toString();
	}
}
