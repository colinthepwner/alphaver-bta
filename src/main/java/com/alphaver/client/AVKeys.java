package com.alphaver.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.input.InputDevice;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;

@Environment(EnvType.CLIENT)
public final class AVKeys {
	private AVKeys() {}

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static KeyBinding DASH;
	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static KeyBinding MINIGAME_CHECKPOINT;
	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static KeyBinding MINIGAME_RESTART;
	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static KeyBinding MINIGAME_LEAVE;

	public static void register() {
		if (DASH != null) {
			return;
		}
		DASH = GameSettings.register(new KeyBinding("key.alphaver.dash")
			.setDefault(InputDevice.keyboard, Keyboard.KEY_LCONTROL));
		MINIGAME_CHECKPOINT = GameSettings.register(new KeyBinding("key.alphaver.minigame_checkpoint")
			.setDefault(InputDevice.keyboard, Keyboard.KEY_TAB));
		MINIGAME_RESTART = GameSettings.register(new KeyBinding("key.alphaver.minigame_restart")
			.setDefault(InputDevice.keyboard, Keyboard.KEY_PAUSE));
		MINIGAME_LEAVE = GameSettings.register(new KeyBinding("key.alphaver.minigame_leave")
			.setDefault(InputDevice.keyboard, Keyboard.KEY_K));
	}
}
