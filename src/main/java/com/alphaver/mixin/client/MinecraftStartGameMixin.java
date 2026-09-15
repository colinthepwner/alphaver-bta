package com.alphaver.mixin.client;

import com.alphaver.AlphaVer;
import com.alphaver.client.AVKeys;
import com.alphaver.client.gui.HudComponentAreaName;
import com.alphaver.client.gui.HudComponentColossusBar;
import com.alphaver.client.gui.HudComponentDashBar;
import com.alphaver.client.gui.HudComponentFreerunTimer;
import com.alphaver.client.gui.HudComponentLilypadHunger;
import com.alphaver.client.gui.HudComponentMinigamePlayers;
import com.alphaver.client.gui.HudComponentMinigamePrompt;
import com.alphaver.client.gui.HudComponentZombies;
import com.alphaver.client.gui.HudComponentZombiesAmmo;
import com.alphaver.client.gui.HudComponentZombiesWave;
import com.alphaver.client.gui.HudComponentLoreToast;
import com.alphaver.client.gui.HudComponentPaintingTitle;
import com.alphaver.entity.AVGamemodes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenCreateWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftStartGameMixin {

	@Inject(
		method = "startGame",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/option/GameSettings;init(Ljava/io/File;)V",
			shift = At.Shift.BEFORE
		)
	)
	private void alphaver$registerControlsAndHud(CallbackInfo ci) {
		AVKeys.register();
		HudComponentDashBar.register();
		HudComponentAreaName.register();
		HudComponentLoreToast.register();
		HudComponentColossusBar.register();
		HudComponentPaintingTitle.register();
		HudComponentLilypadHunger.register();
		HudComponentZombies.register();
		HudComponentZombiesAmmo.register();
		HudComponentMinigamePrompt.register();
		HudComponentFreerunTimer.register();
		HudComponentMinigamePlayers.register();
		HudComponentZombiesWave.register();

		if (AVGamemodes.CYPRESS_SURVIVAL != null && !ScreenCreateWorld.GAMEMODES.contains(AVGamemodes.CYPRESS_SURVIVAL)) {
			ScreenCreateWorld.GAMEMODES.add(AVGamemodes.CYPRESS_SURVIVAL);
			ScreenCreateWorld.GAMEMODES.add(AVGamemodes.CYPRESS_FRAIL);
		}
		AlphaVer.LOGGER.info("Registered AlphaVer's key bindings, HUD components and Create World gamemodes.");
	}
}
