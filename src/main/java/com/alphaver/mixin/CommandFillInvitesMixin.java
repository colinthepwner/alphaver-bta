package com.alphaver.mixin;

import com.alphaver.world.travel.AVInvites;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.commands.CommandFill;
import net.minecraft.core.net.command.helpers.BlockInput;
import net.minecraft.core.net.command.helpers.IntegerCoordinates;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = CommandFill.class, remap = false)
public abstract class CommandFillInvitesMixin {

	@Inject(method = "fillReplace(Lnet/minecraft/core/net/command/CommandSource;Lnet/minecraft/core/world/World;Lnet/minecraft/core/net/command/helpers/IntegerCoordinates;Lnet/minecraft/core/net/command/helpers/IntegerCoordinates;Lnet/minecraft/core/net/command/helpers/BlockInput;Lnet/minecraft/core/net/command/helpers/BlockInput;Z)I",
		at = @At("HEAD"))
	private static void alphaver$replace(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second,
	                                     BlockInput block, BlockInput filter, boolean destroy, CallbackInfoReturnable<Integer> cir) {
		AVInvites.checkPlace(source.getSender(), block == null ? null : block.getBlock());
	}

	@Inject(method = {"fillHollow", "fillOutline"}, at = @At("HEAD"))
	private static void alphaver$shell(CommandSource source, World world, IntegerCoordinates first, IntegerCoordinates second,
	                                   BlockInput block, CallbackInfoReturnable<Integer> cir) {
		AVInvites.checkPlace(source.getSender(), block == null ? null : block.getBlock());
	}
}
