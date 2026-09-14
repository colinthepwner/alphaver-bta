package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.WeightedRandomLootObject;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = Mob.class, remap = false)
public abstract class MobCypressDropsMixin {

	@Shadow
	protected abstract List<WeightedRandomLootObject> getMobDrops();

	@Redirect(method = "dropDeathItems", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/entity/Mob;getMobDrops()Ljava/util/List;"))
	private List<WeightedRandomLootObject> alphaver$cypressDrops(Mob self) {
		List<WeightedRandomLootObject> drops = this.getMobDrops();
		if (AVWorlds.isZombies(self.world) && self.getClass() == MobZombie.class) {

			return List.of();
		}
		if (!AVWorlds.isCypress(self.world)) {
			return drops;
		}
		Class<?> type = self.getClass();
		if (type == MobZombie.class) {
			return alphaver$upToTwo(new ItemStack(Items.FEATHER_CHICKEN));
		}
		if (type == MobSkeleton.class) {
			return alphaver$upToTwo(new ItemStack(Items.AMMO_ARROW));
		}
		if (type == MobCow.class) {
			return alphaver$upToTwo(new ItemStack(Items.LEATHER));
		}
		if (type == MobPig.class && drops != null) {
			List<WeightedRandomLootObject> alpha = new ArrayList<>(drops.size());
			for (WeightedRandomLootObject drop : drops) {
				ItemStack stack = drop.getDefinedItemStack();
				if (stack != null) {
					alpha.add(new WeightedRandomLootObject(stack.copy(), 0, 2));
				}
			}
			return alpha;
		}
		return drops;
	}

	private static List<WeightedRandomLootObject> alphaver$upToTwo(ItemStack stack) {
		return List.of(new WeightedRandomLootObject(stack, 0, 2));
	}
}
