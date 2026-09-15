package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.commands.CommandSetBlock;
import net.minecraft.core.net.command.helpers.BlockInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommandSetBlock.class, remap = false)
public abstract class CommandSetBlockInvitesMixin {

	@Inject(method = "lambda$register$1", at = @At("HEAD"))
	@SuppressWarnings("rawtypes")
	private static void alphaver$onlyInvited(CommandContext context, CallbackInfoReturnable<Integer> cir) {
		CommandSource source = (CommandSource) context.getSource();
		BlockInput block = (BlockInput) context.getArgument("block", BlockInput.class);
		AVInvites.checkPlace(source.getSender(), block == null ? null : block.getBlock());
	}
}
