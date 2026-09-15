package com.alphaver.entity;

import com.alphaver.AVConfig;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.IArmorItem;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CypressFrail {
	private CypressFrail() {}

	private static final int WEAR_PER_DAMAGE = 16;

	public static boolean active(@Nullable World world, @Nullable Player player) {
		if (world == null || AVWorlds.isMinigame(world)) {
			return false;
		}
		return AVGamemodes.isFrail(player) || AVConfig.FRAIL && AVWorlds.isAlphaVer(world);
	}

	public static boolean hurt(@NotNull Player player, @Nullable Entity attacker, int damage) {
		if (player.heartsFlashTime > player.heartsHalvesLife / 2.0F) {
			return false;
		}
		player.heartsFlashTime = player.heartsHalvesLife;
		if (player.isPlayerSleeping()) {
			player.wakeUpPlayer(true, true);
		}

		int pieces = 0;
		for (HumanArmorShape slot : HumanArmorShape.values()) {
			if (wears(player.getItemInArmorSlot(slot))) {
				pieces++;
			}
		}
		if (pieces == 0) {
			player.world.playSoundAtEntity(null, player, "random.glass", 1.0F, 1.0F);
			player.setHealthRaw(0);
			player.onDeath(attacker);
			return true;
		}

		player.damageArmor(damage * WEAR_PER_DAMAGE / pieces);
		return true;
	}

	private static boolean wears(@Nullable ItemStack stack) {
		return stack != null && stack.getItem() instanceof IArmorItem<?> armor && armor.getArmorMaterial() != null;
	}
}
