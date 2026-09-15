package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.commands.CommandGameMode;
import net.minecraft.core.player.gamemode.Gamemode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = CommandGameMode.class, remap = false)
public abstract class CommandGameModeCypressMixin {

	@Inject(method = "lambda$register$0", at = @At("HEAD"))
	@SuppressWarnings("rawtypes")
	private static void alphaver$hiddenFromSelf(CommandContext context, CallbackInfoReturnable<Integer> cir) {
		Player sender = ((CommandSource) context.getSource()).getSender();
		if (sender != null) {

			AVInvites.checkGamemode(List.of(sender), (Gamemode) context.getArgument("gamemode", Gamemode.class));
		}
	}

	@ModifyExpressionValue(method = "lambda$register$1", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/net/command/helpers/EntitySelector;get(Lnet/minecraft/core/net/command/CommandSource;)Ljava/util/List;"))
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static List alphaver$hiddenFromTargets(List targets, CommandContext context) {
		AVInvites.checkGamemode(targets, (Gamemode) context.getArgument("gamemode", Gamemode.class));
		return targets;
	}
}
