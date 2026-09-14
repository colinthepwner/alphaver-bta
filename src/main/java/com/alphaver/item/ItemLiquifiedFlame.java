package com.alphaver.item;

import com.alphaver.entity.LilypadHunger;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemLiquifiedFlame extends ItemFood {

	public ItemLiquifiedFlame(@NotNull String name, @NotNull String namespaceId, int id, int healAmount, int ticksPerHeal,
	                          int maxStackSize) {
		super(name, namespaceId, id, healAmount, ticksPerHeal, false, maxStackSize);
	}

	@Override
	public @Nullable ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (!LilypadHunger.appliesTo(player)) {
			return super.onUse(selfStack, world, player);
		}

		boolean hurt = player.getHealth() < player.getMaxHealth()
			&& player.getHealth() + player.getTotalHealingRemaining() < player.getMaxHealth();
		if (selfStack.consumeItem(player)) {
			if (hurt) {
				player.eatFood(selfStack);
			}
			LilypadHunger.refill(player);
			world.playSoundAtEntity(
				player,
				player,
				this.getTicksPerHeal(selfStack) >= 10 ? "random.bite_extended" : "random.bite",
				0.5F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F,
				1.1F + (itemRand.nextFloat() - itemRand.nextFloat()) * 0.1F
			);
		}
		return selfStack;
	}
}
