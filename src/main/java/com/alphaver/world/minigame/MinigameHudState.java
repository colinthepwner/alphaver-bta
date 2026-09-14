package com.alphaver.world.minigame;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MinigameHudState {

	public static final int NONE = -1;

	public static final String KEY = "%key%";

	public static final int REVIVE_TICKS = 100;

	public static final int MAX_MATES = 8;

	public int kind = NONE;

	public int stageCode;

	public long stageTime = -1L;

	public int wave;
	public int points;
	public int zombiesLeft;

	public int perks;

	public int downedTicks;

	@NotNull
	public String prompt = "";

	public boolean timerRunning;

	public int timerTicks;
	public int checkpoint;
	public int checkpoints;

	public int leaveHintSerial;

	@NotNull
	public List<Mate> mates = List.of();

	public record Mate(@NotNull String name, int points, int perks, boolean downed, int checkpoint, int finishTicks) {
		public boolean has(@NotNull MinigamePerk perk) {
			return perk.in(this.perks);
		}
	}

	public transient long receivedAtNanos;

	public transient boolean extrapolate;

	public static volatile MinigameHudState local = new MinigameHudState();

	public boolean inZombies() {
		return this.kind == MinigameKind.ZOMBIES.ordinal();
	}

	public boolean inFreerun() {
		return this.kind == MinigameKind.FREERUN.ordinal();
	}

	public boolean inStage() {
		return this.kind != NONE && this.stageCode != 0;
	}

	public boolean has(@NotNull MinigamePerk perk) {
		return perk.in(this.perks);
	}

	public boolean mayDash() {
		return !(this.inZombies() && this.inStage()) || this.has(MinigamePerk.DASH);
	}

	public int displayTimerTicks() {
		if (!this.timerRunning || !this.extrapolate) {
			return this.timerTicks;
		}
		long elapsed = Math.max(0L, System.nanoTime() - this.receivedAtNanos) / 50_000_000L;
		return (int) Math.min(Integer.MAX_VALUE, this.timerTicks + elapsed);
	}

	public boolean differsFrom(@NotNull MinigameHudState other) {
		return this.kind != other.kind || this.stageCode != other.stageCode || this.stageTime != other.stageTime
			|| this.wave != other.wave || this.points != other.points || this.zombiesLeft != other.zombiesLeft
			|| this.perks != other.perks || this.downedTicks != other.downedTicks || !this.prompt.equals(other.prompt)
			|| this.timerRunning != other.timerRunning || !this.timerRunning && this.timerTicks != other.timerTicks
			|| this.checkpoint != other.checkpoint || this.checkpoints != other.checkpoints
			|| this.leaveHintSerial != other.leaveHintSerial || !this.mates.equals(other.mates);
	}

	@NotNull
	public static String formatTime(long ticks) {
		long seconds = Math.max(0L, ticks) / 20L;
		int millis = (int) ((Math.max(0L, ticks) % 20L) / 20.0F * 1000.0F);
		return String.format("%02d:%02d.%03d", seconds / 60L, seconds % 60L, millis);
	}
}
