package com.alphaver.entity;

import com.alphaver.AlphaVer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.gamemode.Gamemodes;
import net.minecraft.core.util.helper.DamageType;
import net.minecraft.core.world.World;

public class MobCatbomb extends Mob {

	public MobCatbomb(World world) {
		super(world);
		this.setTextureIdentifier(AlphaVer.MOD_ID, "catbomb");
		this.setSize(0.5F, 0.3F);
	}

	@Override
	public int getMaxHealth() {
		return 10;
	}

	@Override
	public void onLivingUpdate() {

	}

	@Override
	public boolean hurt(Entity attacker, int damage, DamageType type) {
		if (attacker instanceof Player player && player.getGamemode() == Gamemodes.CREATIVE && !this.world.isClientSide) {
			this.remove();
		}
		return true;
	}

	@Override
	public String getLivingSound() {
		return null;
	}

	@Override
	public String getHurtSound() {
		return "mob.skeletonhurt";
	}

	@Override
	public String getDeathSound() {
		return "mob.skeletonhurt";
	}
}
