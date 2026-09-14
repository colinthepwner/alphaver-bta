package com.alphaver.server;

import com.alphaver.block.machine.MachineKind;
import com.alphaver.block.machine.MenuEssenceCloner;
import com.alphaver.block.machine.MenuEssenceTransformer;
import com.alphaver.block.machine.MenuFreezer;
import com.alphaver.block.machine.TileEntityFreezer;
import com.alphaver.mixin.server.PlayerServerWindowAccessor;
import com.alphaver.net.MessageOpenMachine;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import net.minecraft.server.entity.player.PlayerServer;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Environment(EnvType.SERVER)
public final class AVServerMachines {
	private AVServerMachines() {}

	public static void open(@NotNull Player player, @NotNull MachineKind kind, @NotNull World world, @NotNull TilePosc tilePos) {
		if (!(player instanceof PlayerServer playerServer) || playerServer.playerNetServerHandler == null) {
			return;
		}
		MenuAbstract menu = switch (kind) {
			case FREEZER -> world.getTileEntity(tilePos) instanceof TileEntityFreezer freezer
				? new MenuFreezer(playerServer.inventory, freezer) : null;
			case TRANSFORMER -> new MenuEssenceTransformer(playerServer.inventory, world, tilePos);
			case CLONER -> new MenuEssenceCloner(playerServer.inventory, world, tilePos);
		};
		if (menu == null) {
			return;
		}

		PlayerServerWindowAccessor windows = (PlayerServerWindowAccessor) playerServer;
		windows.alphaver$nextWindowId();
		int windowId = windows.alphaver$getCurrentWindowId();

		NetworkHandler.sendToPlayer(playerServer, new MessageOpenMachine(kind, windowId, tilePos));
		playerServer.containerMenu.onCraftGuiClosed(playerServer);
		playerServer.containerMenu = menu;
		menu.containerId = windowId;
		menu.addSlotListener(playerServer);
	}
}
