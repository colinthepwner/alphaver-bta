package com.alphaver.mixin;

import com.alphaver.AlphaVer;
import com.alphaver.world.minigame.AVMinigames;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.DamageType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMinigameMixin {

	@Unique
	private static boolean alphaver$reportedMinigameFailure;

	@Inject(method = "tick", at = @At("TAIL"))
	private void alphaver$minigameTick(CallbackInfo ci) {
		try {
			AVMinigames.tick((Player) (Object) this);
		} catch (RuntimeException e) {
			if (!alphaver$reportedMinigameFailure) {
				alphaver$reportedMinigameFailure = true;
				AlphaVer.LOGGER.error("Minigame tick failed; further failures this session are not logged.", e);
			}
		}
	}

	@Inject(method = "onDeath(Lnet/minecraft/core/entity/Entity;)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$knockedOut(Entity killer, CallbackInfo ci) {
		if (AVMinigames.knockedOut((Player) (Object) this)) {
			ci.cancel();
		}
	}

	@Inject(method = "hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z", at = @At("HEAD"),
		cancellable = true)
	private void alphaver$untouchableInGames(Entity attacker, int damage, DamageType type, CallbackInfoReturnable<Boolean> cir) {
		if (AVMinigames.adjustDamage((Player) (Object) this, damage) < 0) {
			cir.setReturnValue(false);
		}
	}

	@ModifyVariable(method = "hurt(Lnet/minecraft/core/entity/Entity;ILnet/minecraft/core/util/helper/DamageType;)Z", at = @At("HEAD"),
		argsOnly = true, ordinal = 0)
	private int alphaver$berzerkola(int damage) {
		int adjusted = AVMinigames.adjustDamage((Player) (Object) this, damage);
		return adjusted < 0 ? damage : adjusted;
	}

	@Inject(method = "dropCurrentItem(Z)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$noDroppingInGames(boolean dropFullStack, CallbackInfo ci) {
		if (AVMinigames.isPlaying((Player) (Object) this)) {
			ci.cancel();
		}
	}

	@Inject(method = "dropPlayerItem(Lnet/minecraft/core/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$noThrowingInGames(ItemStack stack, CallbackInfo ci) {
		if (AVMinigames.isPlaying((Player) (Object) this)) {
			ci.cancel();
		}
	}
}
