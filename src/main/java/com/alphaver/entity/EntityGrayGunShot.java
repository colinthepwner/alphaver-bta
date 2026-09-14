package com.alphaver.entity;

import com.alphaver.net.AVSounds;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.IArmorWearing;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.util.phys.HitResult;
import net.minecraft.core.world.Explosion;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3dc;
import org.joml.primitives.AABBd;

import java.util.ArrayList;
import java.util.List;

public class EntityGrayGunShot extends EntityEssenceShot {

	private static final float POWER = 3.0F;
	private static final int BLAST_DAMAGE = 18;
	private static final int PUFFS = 20;

	public EntityGrayGunShot(@NotNull World world) {
		super(world);
	}

	public EntityGrayGunShot(@NotNull World world, @NotNull Mob shooter) {
		super(world, shooter);
	}

	@Override
	public void onHit(@NotNull HitResult hitResult) {
		if (!this.world.isClientSide) {
			if (hitResult instanceof HitResult.Entity hit && hit.entity instanceof Mob && this.owner instanceof Player shooter) {
				AVSounds.playFor(shooter, HIT_SOUND, 1.0F, 1.0F / (this.random.nextFloat() * 0.4F + 0.8F));
			}
			Blast blast = new Blast(this.world, this, this.x, this.y, this.z);
			blast.explode();

			blast.addEffects(false);
		}
		for (int i = 0; i < PUFFS; i++) {
			this.world.spawnParticle("explode", this.x + this.random.nextDouble() * 8.0 - 4.0, this.y + this.random.nextDouble() * 2.0,
				this.z + this.random.nextDouble() * 8.0 - 4.0, 0.0, 0.1, 0.0, 0, true);
			this.world.spawnParticle("smoke", this.x + this.random.nextDouble() * 8.0 - 4.0, this.y + this.random.nextDouble() * 2.0,
				this.z + this.random.nextDouble() * 8.0 - 4.0, 0.0, 0.1, 0.0, 0, true);
		}
		this.remove();
	}

	private static final class Blast extends Explosion {

		Blast(@NotNull World world, @NotNull Entity exploder, double x, double y, double z) {
			super(world, exploder, x, y, z, POWER);
			this.destroyBlocks = false;
		}

		@Override
		protected void calculateBlocksToDestroy() {

		}

		@Override
		protected void damageEntities() {
			float reach = this.explosionSize * 2.0F;
			AABBd box = new AABBd(
				MathHelper.floor(this.explosionX - reach - 1.0), MathHelper.floor(this.explosionY - reach - 1.0),
				MathHelper.floor(this.explosionZ - reach - 1.0), MathHelper.floor(this.explosionX + reach + 1.0),
				MathHelper.floor(this.explosionY + reach + 1.0), MathHelper.floor(this.explosionZ + reach + 1.0));
			List<Entity> inReach = new ArrayList<>(this.world.getEntitiesWithinAABBExcludingEntity(this.exploder, box));
			Vector3dc centre = new Vector3d(this.explosionX, this.explosionY, this.explosionZ);

			for (Entity entity : inReach) {
				double distance = entity.distanceTo(this.explosionX, this.explosionY, this.explosionZ) / reach;
				if (distance > 1.0) {
					continue;
				}
				double dx = entity.x - this.explosionX;
				double dy = entity.y - this.explosionY;
				double dz = entity.z - this.explosionZ;
				double length = MathHelper.sqrt(dx * dx + dy * dy + dz * dz);
				if (length > 0.0) {
					dx /= length;
					dy /= length;
					dz /= length;
				}
				double exposure = (1.0 - distance) * this.world.getSeenPercent(centre, entity.bb);

				entity.hurt(this.exploder, entity instanceof Player ? BLAST_DAMAGE / 3 : BLAST_DAMAGE, DamageType.BLAST);
				double fling = exposure * 2.0;
				if (entity instanceof IArmorWearing armoured) {
					fling *= 1.0F - armoured.getTotalProtectionAmount(DamageType.BLAST) / 2.0F;
				}
				if (!entity.hasNoPhysics()) {
					entity.fling(dx * fling, dy * fling, dz * fling, 1.0F);
				}
			}
		}
	}
}
