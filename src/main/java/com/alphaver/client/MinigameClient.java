package com.alphaver.client;

import com.alphaver.net.MessageMinigameAction;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.AVMinigames;
import com.alphaver.world.minigame.MinigameHudState;
import com.alphaver.world.minigame.MinigameKind;
import com.alphaver.world.type.WorldTypeMinigame;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.KeyBinding;
import org.lwjgl.input.Keyboard;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Environment(EnvType.CLIENT)
public final class MinigameClient {
	private MinigameClient() {}

	public static void install() {
		WorldTypeMinigame.stageTime = world -> {
			MinigameHudState state = MinigameHudState.local;
			MinigameKind kind = MinigameKind.ofWorld(world);
			return kind != null && state.kind == kind.ordinal() && state.stageTime >= 0L ? state.stageTime : -1L;
		};
	}

	public static void onKey(Minecraft mc, int keyCode, int mouseCode, boolean pressed) {
		if (!pressed || mc.currentScreen != null || mc.thePlayer == null || !AVWorlds.isMinigame(mc.currentWorld)) {
			return;
		}
		MinigameHudState state = MinigameHudState.local;
		if (!state.inStage()) {
			return;
		}
		if (state.inFreerun() && matches(AVKeys.MINIGAME_CHECKPOINT, keyCode, mouseCode)) {
			send(mc, AVMinigames.ACTION_CHECKPOINT);
		} else if (state.inFreerun() && matches(AVKeys.MINIGAME_RESTART, keyCode, mouseCode)) {
			send(mc, AVMinigames.ACTION_RESTART);
		} else if (matches(AVKeys.MINIGAME_LEAVE, keyCode, mouseCode)) {
			send(mc, AVMinigames.ACTION_LEAVE);
		}
	}

	public static boolean buyInsteadOfInventory(Minecraft mc) {
		if (mc.thePlayer == null || !AVWorlds.isZombies(mc.currentWorld) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			return false;
		}
		MinigameHudState state = MinigameHudState.local;
		if (!state.inZombies() || !state.inStage()) {
			return false;
		}
		send(mc, AVMinigames.ACTION_BUY);
		return true;
	}

	private static boolean matches(KeyBinding key, int keyCode, int mouseCode) {
		return key != null && key.getInputDevice() != null && key.isKeyOrMouse(keyCode, mouseCode);
	}

	private static void send(Minecraft mc, int action) {
		if (mc.isMultiplayerWorld()) {
			NetworkHandler.sendToServer(new MessageMinigameAction(action));
		} else {
			AVMinigames.action(mc.thePlayer, action);
		}
	}
}
