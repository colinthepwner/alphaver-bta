package com.alphaver.mixin;

import com.alphaver.block.machine.EssenceFountainItems;
import net.minecraft.core.entity.EntityItem;
import net.minecraft.core.util.helper.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityItem.class, remap = false)
public abstract class EntityItemFountainMixin {

	@Unique
	private int alphaver$cellX = Integer.MIN_VALUE;
	@Unique
	private int alphaver$cellY = Integer.MIN_VALUE;
	@Unique
	private int alphaver$cellZ = Integer.MIN_VALUE;
	@Unique
	private int alphaver$fountainY = EssenceFountainItems.NO_FOUNTAIN;
	@Unique
	private int alphaver$liftStrength;

	@Inject(method = "tick", at = @At("HEAD"))
	private void alphaver$fountainBefore(CallbackInfo ci) {
		EntityItem self = (EntityItem) (Object) this;
		this.alphaver$liftStrength = 0;
		if (self.item == null || self.removed) {
			return;
		}
		int x = MathHelper.floor(self.x);
		int y = MathHelper.floor(self.y);
		int z = MathHelper.floor(self.z);
		boolean entered = x != this.alphaver$cellX || y != this.alphaver$cellY || z != this.alphaver$cellZ;
		if (entered) {
			this.alphaver$cellX = x;
			this.alphaver$cellY = y;
			this.alphaver$cellZ = z;
			this.alphaver$fountainY = EssenceFountainItems.findFountainBelow(self.world, x, y, z);
		}
		if (this.alphaver$fountainY == EssenceFountainItems.NO_FOUNTAIN) {
			return;
		}
		this.alphaver$liftStrength = EssenceFountainItems.apply(self, x, y, z, this.alphaver$fountainY, entered);
		if (this.alphaver$liftStrength > 0) {
			self.yd += EssenceFountainItems.LIFT + EssenceFountainItems.ITEM_GRAVITY;
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void alphaver$fountainAfter(CallbackInfo ci) {
		if (this.alphaver$liftStrength > 0) {
			((EntityItem) (Object) this).yd = this.alphaver$liftStrength * EssenceFountainItems.STRENGTH_SPEED;
		}
	}
}
