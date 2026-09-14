package com.alphaver.entity;

import com.alphaver.AlphaVer;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

public class MobRecruiter extends Mob {

	private static final float VANISH_DISTANCE = 19.0F;
	private static final float STARE_CONE = 10.0F;
	private static final int STARE_LIMIT = 70;
	private static final double SIGHT_LIMIT = 128.0;
	private static final double SIGHT_STEP = 0.1;

	private boolean spawnedThisSession;
	private int stareTicks;

	public MobRecruiter(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "recruiter");
		this.setSize(0.6F, 1.8F);
	}

	public void markSpawnedThisSession() {
		this.spawnedThisSession = true;
	}

	@Override
	public int getMaxHealth() {
		return 20;
	}

	@Override
	public void onLivingUpdate() {
		if (!this.world.isClientSide && !this.watch()) {
			this.remove();
			return;
		}
		super.onLivingUpdate();
	}

	private boolean watch() {
		if (!this.spawnedThisSession) {
			return false;
		}
		Player player = this.world.getClosestPlayerToEntity(this, -1.0);
		if (player == null || this.distanceTo(player) < VANISH_DISTANCE) {
			return false;
		}
		double dx = this.x - player.x;
		double dz = this.z - player.z;
		this.setRot((float) (Math.atan2(dx, -dz) * 180.0 / Math.PI), 0.0F);

		float offAxis = angleDistance(player.yRot, this.yRot) - 180.0F;
		boolean inSight = !this.blockBetween(this.x, this.y + 1.0, this.z, player.x, player.y + 1.0, player.z);
		if (offAxis > -STARE_CONE && offAxis < STARE_CONE && inSight && ++this.stareTicks > STARE_LIMIT) {
			return false;
		}
		return true;
	}

	private static float angleDistance(float a, float b) {
		float d = Math.abs(b - a) % 360.0F;
		return d > 180.0F ? 360.0F - d : d;
	}

	@SuppressWarnings("deprecation")
	private boolean blockBetween(double ax, double ay, double az, double bx, double by, double bz) {
		double dx = bx - ax;
		double dy = by - ay;
		double dz = bz - az;
		double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
		if (length > SIGHT_LIMIT) {
			return false;
		}
		int steps = (int) (length / SIGHT_STEP);
		for (int i = 0; i <= steps; i++) {
			double t = steps == 0 ? 0.0 : (double) i / steps;
			if (this.world.getBlockId((int) (ax + dx * t), (int) (ay + dy * t), (int) (az + dz * t)) != 0) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected void updateAI() {
		this.moveForward = 0.0F;
		this.moveStrafing = 0.0F;
		this.isJumping = false;
	}

	@Override
	public String getLivingSound() {
		return null;
	}

	@Override
	public String getHurtSound() {
		return "random.hurt";
	}

	@Override
	public String getDeathSound() {
		return "random.hurt";
	}
}
