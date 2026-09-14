package com.alphaver.entity;

import com.alphaver.item.AVItems;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.projectile.ProjectileArrow;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;

public class EntitySpear extends ProjectileArrow {

	private static final int DAMAGE = 4;
	private static final float PLAYER_THROW_SPEED = 3.5F;

	public EntitySpear(World world) {
		super(world);
		this.becomeSpear();
	}

	public EntitySpear(World world, @NotNull Mob owner, boolean pickupable) {
		super(world, owner, pickupable, 0);
		this.becomeSpear();
		if (pickupable) {
			this.setHeading(this.xd, this.yd, this.zd, PLAYER_THROW_SPEED, 1.0F);
		}
	}

	private void becomeSpear() {
		this.damage = DAMAGE;
		this.setSize(0.5F, 0.5F);
		if (AVItems.SPEAR != null) {
			this.stack = new ItemStack(AVItems.SPEAR);
		}
	}
}
