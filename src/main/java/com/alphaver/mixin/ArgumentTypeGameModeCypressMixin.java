package com.alphaver.mixin;

import com.alphaver.entity.AVGamemodes;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.net.command.CommandSource;
import net.minecraft.core.net.command.arguments.ArgumentTypeGameMode;
import net.minecraft.core.net.command.util.CommandHelper;
import net.minecraft.core.player.gamemode.Gamemode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.concurrent.CompletableFuture;

@Mixin(value = ArgumentTypeGameMode.class, remap = false)
public abstract class ArgumentTypeGameModeCypressMixin {

	@Inject(method = "listSuggestions", at = @At("HEAD"), cancellable = true)
	private void alphaver$hideCypressModes(CommandContext<?> context, SuggestionsBuilder builder,
		CallbackInfoReturnable<CompletableFuture<Suggestions>> cir) {
		Player player = context.getSource() instanceof CommandSource source ? source.getSender() : null;
		if (AVGamemodes.CYPRESS_SURVIVAL == null || AVGamemodes.shownTo(player)) {
			return;
		}
		String remaining = builder.getRemainingLowerCase();
		for (Gamemode gamemode : Registries.GAMEMODES) {
			if (AVGamemodes.startsInCypress(gamemode)) {
				continue;
			}
			String id = gamemode.getId();
			String offered = id.startsWith("minecraft:") ? id.replace("minecraft:gamemode/", "") : id.replace("gamemode/", "");
			CommandHelper.getStringToSuggest(offered, remaining).ifPresent(builder::suggest);
		}
		cir.setReturnValue(builder.buildFuture());
	}
}
