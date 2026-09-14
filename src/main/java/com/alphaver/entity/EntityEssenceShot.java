package com.alphaver.entity;

import com.alphaver.block.AVBlocks;
import com.alphaver.net.AVSounds;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.entity.projectile.Projectile;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public class EntityEssenceShot extends Projectile {

	private static final int DAMAGE = 6;
	private static final float ACCELERATION = 2.97F;
	private static final double MAX_SPEED = 8.0;
	private static final int MAX_TICKS_IN_AIR = 100;
	protected static final String HIT_SOUND = "alphaver:ext.hitmarker";

	public EntityEssenceShot(@NotNull World world) {
		super(world);
		this.setSize(0.5F, 0.5F);
	}

	public EntityEssenceShot(@NotNull World world, @NotNull Mob shooter) {
		super(world, shooter);
		this.setSize(0.5F, 0.5F);
		world.spawnParticle("smoke", this.x, this.y, this.z, 0.0, 0.0, 0.0, 0, true);
	}

	@Override
	protected void initProjectile() {
		super.initProjectile();
		this.damage = DAMAGE;
		this.defaultGravity = 0.0F;
		this.defaultProjectileSpeed = ACCELERATION;
	}

	@Nullable
	@Override
	public HitResult getHitResult() {
		HitResult hit = super.getHitResult();
		if (AVBlocks.WIREFRAME_DOOR == null || !AVWorlds.isZombies(this.world)) {
			return hit;
		}
		int door = AVBlocks.WIREFRAME_DOOR.id();
		double speed = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
		if (speed < 1.0E-6) {
			return hit;
		}
		Vector3dc end = new Vector3d(this.x + this.xd, this.y + this.yd, this.z + this.zd);
		for (int i = 0; i < 8 && hit instanceof HitResult.Tile tile
			&& this.world.getBlockId(tile.tilePos.x(), tile.tilePos.y(), tile.tilePos.z()) == door; i++) {
			Vector3dc at = tile.location;

			double t = Math.min(exitAfter(at.x(), this.xd, tile.tilePos.x()),
				Math.min(exitAfter(at.y(), this.yd, tile.tilePos.y()), exitAfter(at.z(), this.zd, tile.tilePos.z())));
			t += 1.0E-3 / speed;
			if (!(t < 1.0)) {

				return null;
			}
			Vector3dc start = new Vector3d(at.x() + this.xd * t, at.y() + this.yd * t, at.z() + this.zd * t);
			hit = this.world.checkBlockCollisionBetweenPoints(start, end);
		}
		return hit;
	}

	private static double exitAfter(double position, double d, int blockMin) {
		if (d > 0.0) {
			return (blockMin + 1.0 - position) / d;
		}
		if (d < 0.0) {
			return (blockMin - position) / d;
		}
		return Double.POSITIVE_INFINITY;
	}

	@Override
	public void onHit(@NotNull HitResult hitResult) {
		if (!this.world.isClientSide && hitResult instanceof HitResult.Entity hit && hit.entity instanceof Mob) {
			if (this.owner instanceof Player shooter) {
				AVSounds.playFor(shooter, HIT_SOUND, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
			}
			hit.entity.hurt(this.owner, DAMAGE, DamageType.COMBAT);
		}
		this.world.spawnParticle("smoke", this.x, this.y, this.z, 0.0, 0.0, 0.0, 0, true);
		this.remove();
	}

	@Override
	public void afterTick() {
		super.afterTick();
		double speed = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
		if (speed > MAX_SPEED) {
			double scale = MAX_SPEED / speed;
			this.xd *= scale;
			this.yd *= scale;
			this.zd *= scale;
		}
		if (this.ticksInAir > MAX_TICKS_IN_AIR) {
			this.remove();
		}
	}
}
