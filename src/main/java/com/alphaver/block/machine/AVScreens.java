package com.alphaver.block.machine;

import com.alphaver.AlphaVer;
import com.alphaver.server.AVServerMachines;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;

public final class AVScreens {
	private AVScreens() {}

	@FunctionalInterface
	public interface FreezerOpener {
		void open(@NotNull Player player, @NotNull TileEntityFreezer freezer);
	}

	@FunctionalInterface
	public interface MachineOpener {
		void open(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos);
	}

	private static volatile FreezerOpener freezer;
	private static volatile MachineOpener transformer;
	private static volatile MachineOpener cloner;
	private static boolean warned;

	public static void setFreezerOpener(FreezerOpener opener) {
		freezer = opener;
	}

	public static void setTransformerOpener(MachineOpener opener) {
		transformer = opener;
	}

	public static void setClonerOpener(MachineOpener opener) {
		cloner = opener;
	}

	public static void open(@NotNull Player player, @NotNull MachineKind kind, @NotNull World world, @NotNull TilePosc tilePos) {
		if (EnvironmentHelper.isMultiplayerServer()) {
			AVServerMachines.open(player, kind, world, tilePos);
			return;
		}
		switch (kind) {
			case FREEZER -> {
				if (world.getTileEntity(tilePos) instanceof TileEntityFreezer tileEntity) {
					openFreezer(player, tileEntity);
				}
			}
			case TRANSFORMER -> openTransformer(player, world, tilePos);
			case CLONER -> openCloner(player, world, tilePos);
		}
	}

	private static void openFreezer(@NotNull Player player, @NotNull TileEntityFreezer tileEntity) {
		FreezerOpener opener = freezer;
		if (opener == null) {
			warnOnce();
			return;
		}
		opener.open(player, tileEntity);
	}

	private static void openTransformer(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos) {
		MachineOpener opener = transformer;
		if (opener == null) {
			warnOnce();
			return;
		}
		opener.open(player, world, tilePos);
	}

	private static void openCloner(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos) {
		MachineOpener opener = cloner;
		if (opener == null) {
			warnOnce();
			return;
		}
		opener.open(player, world, tilePos);
	}

	private static void warnOnce() {
		if (!warned) {
			warned = true;
			AlphaVer.LOGGER.warn("A Cypress machine was used in single player with no screen installed to open; the client "
				+ "entrypoint did not run.");
		}
	}
}
