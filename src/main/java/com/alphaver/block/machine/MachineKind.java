package com.alphaver.block.machine;

import org.jetbrains.annotations.Nullable;

public enum MachineKind {
	FREEZER,
	TRANSFORMER,
	CLONER;

	private static final MachineKind[] VALUES = values();

	@Nullable
	public static MachineKind byId(int id) {
		return id >= 0 && id < VALUES.length ? VALUES[id] : null;
	}
}
