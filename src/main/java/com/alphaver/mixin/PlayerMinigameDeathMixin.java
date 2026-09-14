package com.alphaver.mixin;

import com.alphaver.entity.AVInventoryStash;
import net.minecraft.core.data.gamerule.GameRule;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMinigameDeathMixin {

	@Redirect(method = "onDeath(Lnet/minecraft/core/entity/Entity;)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/world/World;getGameRuleValue(Lnet/minecraft/core/data/gamerule/GameRule;)Ljava/lang/Object;"))
	private Object alphaver$keepInGames(World world, GameRule<?> rule) {
		if (rule == GameRules.KEEP_INVENTORY && AVInventoryStash.holding((Player) (Object) this)) {
			return Boolean.TRUE;
		}
		return world.getGameRuleValue(rule);
	}
}
