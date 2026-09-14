package com.alphaver.world.minigame.map;

import com.alphaver.world.minigame.MinigameKind;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum CypressMap {
	INTRODUCTION(MinigameKind.FREERUN, 0, "freerun/WorldFreerun0", 9, 42, 21, 55),
	CONSTRUCT(MinigameKind.FREERUN, 1, "freerun/WorldFreerun1", -14, -6, 0, 11),
	SKYLINE(MinigameKind.FREERUN, 2, "freerun/WorldFreerun2", -54, -65, -31, -44),
	SORROW(MinigameKind.FREERUN, 3, "freerun/WorldFreerun3", -22, -10, -5, 6),
	SHIVER(MinigameKind.FREERUN, 4, "freerun/WorldFreerun4", -5, -10, 7, 20),
	FINALE(MinigameKind.FREERUN, 5, "freerun/WorldFreerun5", 0, -24, 13, -3),
	RUINEN_DER_UNTOTEN(MinigameKind.ZOMBIES, 0, "zombies/WorldZM1", -27, -3, -6, 16),
	METSAN_TALO(MinigameKind.ZOMBIES, 1, "zombies/WorldZM2", -13, -11, -1, 2);

	public static final int FIRST_SLOT_CHUNK_X = 64;

	public static final int SLOT_SPACING = 64;

	public static final int SLOT_CHUNK_Z = 0;

	public final MinigameKind kind;
	public final int slot;

	public final String save;

	public final int cropMinChunkX;
	public final int cropMinChunkZ;
	public final int cropMaxChunkX;
	public final int cropMaxChunkZ;

	CypressMap(MinigameKind kind, int slot, String save, int cropMinChunkX, int cropMinChunkZ, int cropMaxChunkX, int cropMaxChunkZ) {
		this.kind = kind;
		this.slot = slot;
		this.save = save;
		this.cropMinChunkX = cropMinChunkX;
		this.cropMinChunkZ = cropMinChunkZ;
		this.cropMaxChunkX = cropMaxChunkX;
		this.cropMaxChunkZ = cropMaxChunkZ;
	}

	public int slotChunkX() {
		return FIRST_SLOT_CHUNK_X + this.slot * SLOT_SPACING;
	}

	public int offsetX() {
		return (this.slotChunkX() - this.cropMinChunkX) * 16;
	}

	public int offsetZ() {
		return (SLOT_CHUNK_Z - this.cropMinChunkZ) * 16;
	}

	public int x(int cypressX) {
		return cypressX + this.offsetX();
	}

	public int z(int cypressZ) {
		return cypressZ + this.offsetZ();
	}

	public double cypressX(double x) {
		return x - this.offsetX();
	}

	public double cypressZ(double z) {
		return z - this.offsetZ();
	}

	public boolean containsCypressChunk(int chunkX, int chunkZ) {
		return chunkX >= this.cropMinChunkX && chunkX <= this.cropMaxChunkX && chunkZ >= this.cropMinChunkZ && chunkZ <= this.cropMaxChunkZ;
	}

	public int cypressChunkX(int chunkX) {
		return chunkX - this.slotChunkX() + this.cropMinChunkX;
	}

	public int cypressChunkZ(int chunkZ) {
		return chunkZ - SLOT_CHUNK_Z + this.cropMinChunkZ;
	}

	public boolean containsBlock(double x, double z) {
		int chunkX = Math.floorDiv((int) Math.floor(x), 16);
		int chunkZ = Math.floorDiv((int) Math.floor(z), 16);
		return this.containsCypressChunk(this.cypressChunkX(chunkX), this.cypressChunkZ(chunkZ));
	}

	@Nullable
	public static CypressMap atDimensionChunk(@NotNull MinigameKind kind, int chunkX, int chunkZ) {
		for (CypressMap map : values()) {
			if (map.kind == kind && map.containsCypressChunk(map.cypressChunkX(chunkX), map.cypressChunkZ(chunkZ))) {
				return map;
			}
		}
		return null;
	}

	@Nullable
	public static CypressMap ofEntry(@NotNull String entryName) {
		for (CypressMap map : values()) {
			if (entryName.startsWith(map.save + "/")) {
				return map;
			}
		}
		return null;
	}
}
