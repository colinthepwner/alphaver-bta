package com.alphaver.mixin;

import com.alphaver.world.AVDimensions;
import net.minecraft.core.net.command.commands.CommandTeleport;
import net.minecraft.core.world.Dimension;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = CommandTeleport.class, remap = false)
public abstract class CommandTeleportDimensionNameMixin {

	@Redirect(method = {"lambda$register$0", "lambda$register$1"},
		at = @At(value = "FIELD", target = "Lnet/minecraft/core/world/Dimension;languageKey:Ljava/lang/String;", opcode = Opcodes.GETFIELD))
	private static String alphaver$dimensionName(Dimension dimension) {
		return AVDimensions.commandName(dimension);
	}
}
