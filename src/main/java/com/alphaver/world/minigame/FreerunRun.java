package com.alphaver.world.minigame;

import net.minecraft.core.entity.player.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

final class FreerunRun {

	private static final Map<String, Long> BEST = new HashMap<>();

	private static final double EYE = 1.62;
	private static final int FULL_AIR = 300;

	final MinigameStage stage;
	final FreerunStages.Stage data;
	private int checkpoint;
	private boolean counting;
	private long startTick;
	private long finalTicks;

	private boolean finished;
	private long ticks;

	private int leaveHints;

	FreerunRun(@NotNull MinigameStage stage) {
		this.stage = stage;
		this.data = FreerunStages.of(stage);
	}

	void start(@NotNull Player player) {
		if (this.stage == MinigameStage.COURSE) {
			FreerunCourse.build(player.world);
		}
		player.setHealthRaw(20);
		player.sendMessage(this.stage.title + ": race to the end. Tab goes back to your last checkpoint, Pause starts over, K leaves "
			+ "(see Controls).");
		this.reset(player);
	}

	void reset(@NotNull Player player) {
		FreerunStages.Loc spawn = this.data.checkpoints[0];
		this.checkpoint = 0;
		this.counting = false;
		this.moveToEye(player, spawn.x1(), spawn.y1(), spawn.z1());
		player.sendMessage("Reset");
	}

	void toLastCheckpoint(@NotNull Player player) {
		player.remainingFireTicks = 0;
		player.xd = 0.0;
		player.yd = 0.0;
		player.zd = 0.0;
		double[] position = this.data.checkpoints[this.checkpoint].returnPosition();
		this.moveToEye(player, position[0], position[1], position[2]);
		player.sendMessage("Restarting last checkpoint");
	}

	private void moveToEye(Player player, double cypressX, double eyeY, double cypressZ) {
		AVMinigames.teleport(player, cypressX + this.data.offsetX, eyeY - EYE, cypressZ + this.data.offsetZ, player.yRot);
	}

	boolean tick(@NotNull String key, @NotNull Player player) {
		this.ticks++;
		player.fallDistance = 0.0F;
		double eyeY = player.bb.minY + EYE;
		double x = player.x - this.data.offsetX;
		double z = player.z - this.data.offsetZ;

		for (FreerunStages.Portal portal : this.data.portals) {
			if (portal.contains(x, eyeY, z)) {
				this.moveToEye(player, portal.toX() + (x - portal.x1()), portal.toY() + (eyeY - portal.y1()), portal.toZ() + (z - portal.z1()));
				return false;
			}
		}

		if (eyeY < this.data.resetY((long) x, (long) z) || player.isInLava() || player.airSupply < 0) {
			player.airSupply = FULL_AIR;
			this.leaveHints++;
			this.toLastCheckpoint(player);
			return false;
		}

		boolean finishedNow = false;
		FreerunStages.Loc[] checkpoints = this.data.checkpoints;
		if (this.checkpoint < checkpoints.length - 1 && checkpoints[this.checkpoint + 1].contains((int) x, (int) eyeY, (int) z)) {
			this.checkpoint++;
			if (this.checkpoint != checkpoints.length - 1) {
				player.sendMessage("Checkpoint " + this.checkpoint + " reached");
			} else {
				this.finish(key, player);
				finishedNow = true;
			}
			if (this.checkpoint == 1) {
				this.counting = true;
				this.startTick = this.ticks;
			}
		}
		return finishedNow;
	}

	int checkpoint() {
		return this.checkpoint;
	}

	int lastFinish() {
		return this.finished ? (int) Math.min(Integer.MAX_VALUE, this.finalTicks) : -1;
	}

	private void finish(String key, Player player) {
		this.counting = false;
		this.finished = true;
		this.leaveHints++;
		this.finalTicks = this.ticks - this.startTick;
		String bestKey = key + "/" + this.stage.name();
		Long best = BEST.get(bestKey);
		boolean record = best == null || this.finalTicks < best;
		if (record) {
			BEST.put(bestKey, this.finalTicks);
		}
		player.sendMessage("Finish! Time " + MinigameHudState.formatTime(this.finalTicks)
			+ (record ? " (best)" : " (best " + MinigameHudState.formatTime(best) + ")") + ". Pause starts over, K leaves.");
	}

	void fillHud(@NotNull MinigameHudState hud) {
		hud.timerRunning = this.counting;
		hud.timerTicks = (int) Math.min(Integer.MAX_VALUE, this.counting ? this.ticks - this.startTick : this.finalTicks);
		hud.checkpoint = this.checkpoint;
		hud.checkpoints = this.data.checkpoints.length - 1;
		hud.leaveHintSerial = this.leaveHints;
	}
}
