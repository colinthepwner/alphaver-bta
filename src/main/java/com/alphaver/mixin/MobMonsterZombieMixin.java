package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.MinigameKind;
import com.alphaver.world.minigame.map.CypressMap;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobGiant;
import net.minecraft.core.entity.monster.MobMonster;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.util.helper.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(value = MobMonster.class, remap = false)
public abstract class MobMonsterZombieMixin {

	@ModifyConstant(method = "findPlayerToAttack", constant = @Constant(doubleValue = 16.0))
	private double alphaver$zombieSight(double range) {
		return this.alphaver$isCypressZombie() ? 18.0 : range;
	}

	@Inject(method = "attackEntity", at = @At("HEAD"))
	private void alphaver$zombieLunge(Entity target, float distance, CallbackInfo ci) {
		if (!this.alphaver$isCypressZombie()) {
			return;
		}
		MobMonster self = (MobMonster) (Object) this;
		if (distance >= 2.5F || !(target.bb.maxY > self.bb.minY && target.bb.minY < self.bb.maxY) || !self.onGround) {
			return;
		}
		Random rand = self.world.rand;

		double shiftX = 0.0;
		double shiftZ = 0.0;
		if (AVWorlds.isZombies(self.world)) {
			CypressMap map = CypressMap.atDimensionChunk(MinigameKind.ZOMBIES, MathHelper.floor(self.x) >> 4, MathHelper.floor(self.z) >> 4);
			if (map != null) {
				shiftX = map.offsetX();
				shiftZ = map.offsetZ();
			}
		}
		if (distance > 2.0F && distance < 12.0F && rand.nextInt(50) == 0) {
			alphaver$push(self, target.x - self.x, target.z - self.z, false);
		}
		if (distance > 2.0F && distance < 6.0F && rand.nextInt(35) == 0) {
			alphaver$push(self, target.x - self.x, (target.z - shiftZ) + (self.z - shiftZ), true);
		}
		if (distance > 2.0F && distance < 6.0F && rand.nextInt(35) == 0) {
			alphaver$push(self, (target.x - shiftX) + (self.x - shiftX), target.z - self.z, true);
		}
	}

	private boolean alphaver$isCypressZombie() {
		Object self = this;
		if (!(self instanceof MobZombie) || self instanceof MobGiant) {
			return false;
		}
		MobMonster mob = (MobMonster) self;
		return AVWorlds.isCypress(mob.world) || AVWorlds.isZombies(mob.world);
	}

	private static void alphaver$push(MobMonster mob, double dx, double dz, boolean hop) {
		double length = (float) Math.sqrt(dx * dx + dz * dz);
		if (length == 0.0) {
			return;
		}
		mob.xd = dx / length * 0.5 * 0.8 + mob.xd * 0.2;
		mob.zd = dz / length * 0.5 * 0.8 + mob.zd * 0.2;
		if (hop) {
			mob.yd = 0.2;
		}
	}
}
