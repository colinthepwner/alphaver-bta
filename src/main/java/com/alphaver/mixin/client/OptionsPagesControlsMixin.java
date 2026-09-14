package com.alphaver.mixin.client;

import com.alphaver.client.AVKeys;
import net.minecraft.client.gui.options.components.KeyBindingComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = OptionsPages.class, remap = false)
public abstract class OptionsPagesControlsMixin {

	@Unique
	private static boolean alphaver$added;

	@Inject(method = "init", at = @At("TAIL"))
	private static void alphaver$addDashKey(CallbackInfo ci) {
		if (alphaver$added || OptionsPages.CONTROLS == null || AVKeys.DASH == null) {
			return;
		}
		alphaver$added = true;
		OptionsCategory category = new OptionsCategory("gui.options.page.controls.category.alphaver")
			.withComponent(new KeyBindingComponent(AVKeys.DASH));

		for (KeyBinding key : new KeyBinding[]{AVKeys.MINIGAME_CHECKPOINT, AVKeys.MINIGAME_RESTART, AVKeys.MINIGAME_LEAVE}) {
			if (key != null) {
				category.withComponent(new KeyBindingComponent(key));
			}
		}
		OptionsPages.CONTROLS.withComponent(category);
	}
}
