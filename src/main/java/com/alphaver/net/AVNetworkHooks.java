package com.alphaver.net;

import com.alphaver.block.machine.MachineKind;

public final class AVNetworkHooks {
	private AVNetworkHooks() {}

	@FunctionalInterface
	public interface MachineScreenOpener {
		void open(MachineKind kind, int windowId, int x, int y, int z);
	}

	@FunctionalInterface
	public interface HandshakeListener {
		void receive(MessageHandshake message);
	}

	private static volatile MachineScreenOpener machineScreens;
	private static volatile HandshakeListener handshake;

	public static void install(MachineScreenOpener machineScreenOpener, HandshakeListener handshakeListener) {
		machineScreens = machineScreenOpener;
		handshake = handshakeListener;
	}

	static void openMachineScreen(MachineKind kind, int windowId, int x, int y, int z) {
		MachineScreenOpener opener = machineScreens;
		if (opener != null) {
			opener.open(kind, windowId, x, y, z);
		}
	}

	static void receiveHandshake(MessageHandshake message) {
		HandshakeListener listener = handshake;
		if (listener != null) {
			listener.receive(message);
		}
	}
}
