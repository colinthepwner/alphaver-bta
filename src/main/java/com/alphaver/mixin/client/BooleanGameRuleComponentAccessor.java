package com.alphaver.mixin.client;

import net.minecraft.client.gui.worldsettings.gamerule.BooleanGameRuleComponent;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = BooleanGameRuleComponent.class, remap = false)
public interface BooleanGameRuleComponentAccessor {

	@Accessor("gameRule")
	GameRuleBoolean alphaver$getGameRule();
}
