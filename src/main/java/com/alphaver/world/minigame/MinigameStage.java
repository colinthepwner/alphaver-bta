package com.alphaver.world.minigame;

import com.alphaver.world.minigame.map.CypressMap;
import com.alphaver.world.minigame.map.CypressMapStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum MinigameStage {
	RUINEN_DER_UNTOTEN(MinigameKind.ZOMBIES, 1, "Ruinen der Untoten", CypressMap.RUINEN_DER_UNTOTEN, 22500L),
	METSAN_TALO(MinigameKind.ZOMBIES, 2, "Metsan Talo", CypressMap.METSAN_TALO, 21000L),
	ARENA(MinigameKind.ZOMBIES, 3, "Arena", null, 22500L),
	INTRODUCTION(MinigameKind.FREERUN, 1, "INTRODUCTION", CypressMap.INTRODUCTION, 11879L),
	CONSTRUCT(MinigameKind.FREERUN, 2, "CONSTRUCT", CypressMap.CONSTRUCT, 17287L),
	SKYLINE(MinigameKind.FREERUN, 3, "SKYLINE", CypressMap.SKYLINE, 9776L),
	SORROW(MinigameKind.FREERUN, 4, "SORROW", CypressMap.SORROW, 18469L),
	SHIVER(MinigameKind.FREERUN, 5, "SHIVER", CypressMap.SHIVER, 22516L),
	FINALE(MinigameKind.FREERUN, 6, "FINALE", CypressMap.FINALE, 20000L),
	COURSE(MinigameKind.FREERUN, 7, "COURSE", null, 6000L);

	public static final int CODE_ENTRY = 0;

	public static final int CODE_LOBBY = 14;

	public static final int CODE_EXIT = 15;
	public static final int CODE_SHIFT = 3;
	public static final int CODE_MASK = 15;

	private static final int SIGN_LINE = 15;

	public final MinigameKind kind;
	public final int code;
	public final String title;
	@Nullable
	public final CypressMap map;
	public final long time;

	MinigameStage(MinigameKind kind, int code, String title, @Nullable CypressMap map, long time) {
		this.kind = kind;
		this.code = code;
		this.title = title;
		this.map = map;
		this.time = time;
	}

	public boolean available() {
		return this.map == null || CypressMapStore.ready();
	}

	@NotNull
	public String unavailableReason() {
		if (this.available()) {
			return "";
		}
		return switch (CypressMapStore.state()) {
			case IDLE, CONVERTING -> "(preparing...)";
			default -> "(needs Cypress)";
		};
	}

	public static int doorCode(int data) {
		return data >> CODE_SHIFT & CODE_MASK;
	}

	public static int withDoorCode(int data, int code) {
		return data & ~(CODE_MASK << CODE_SHIFT) | (code & CODE_MASK) << CODE_SHIFT;
	}

	@Nullable
	public static MinigameStage byCode(@NotNull MinigameKind kind, int code) {
		for (MinigameStage stage : values()) {
			if (stage.kind == kind && stage.code == code) {
				return stage;
			}
		}
		return null;
	}

	@NotNull
	public static List<MinigameStage> of(@NotNull MinigameKind kind) {
		List<MinigameStage> stages = new ArrayList<>();
		for (MinigameStage stage : values()) {
			if (stage.kind == kind) {
				stages.add(stage);
			}
		}
		return stages;
	}

	@NotNull
	public String[] signLines() {
		String[] lines = {"", "", "", ""};
		int line = 1;
		StringBuilder current = new StringBuilder();
		for (String word : this.title.split(" ")) {
			if (current.length() > 0 && current.length() + 1 + word.length() > SIGN_LINE) {
				lines[line] = current.toString();
				line = Math.min(2, line + 1);
				current.setLength(0);
			}
			if (current.length() > 0) {
				current.append(' ');
			}
			current.append(word);
		}
		lines[line] = current.length() > SIGN_LINE ? current.substring(0, SIGN_LINE) : current.toString();
		lines[3] = this.unavailableReason();
		return lines;
	}
}
