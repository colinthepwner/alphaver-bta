package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerMiningCypressMixin {

	@ModifyConstant(method = "getCurrentPlayerStrVsBlock", constant = @Constant(floatValue = 5.0F))
	private float alphaver$cypressMiningPenalty(float divisor) {
		return AVWorlds.isCypress(((Player) (Object) this).world) ? 4.0F : divisor;
	}
}
