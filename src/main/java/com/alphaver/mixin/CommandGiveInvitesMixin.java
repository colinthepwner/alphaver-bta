package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.commands.CommandGive;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = CommandGive.class, remap = false)
public abstract class CommandGiveInvitesMixin {

	@ModifyExpressionValue(method = {"lambda$register$0", "lambda$register$1"}, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/net/command/helpers/EntitySelector;get(Lnet/minecraft/core/net/command/CommandSource;)Ljava/util/List;"))
	@SuppressWarnings({"rawtypes", "unchecked"})
	private static List alphaver$onlyInvited(List targets, CommandContext context) {
		CommandSource source = (CommandSource) context.getSource();
		AVInvites.checkGive(source.getSender(), targets, (ItemStack) context.getArgument("item", ItemStack.class));
		return targets;
	}
}
