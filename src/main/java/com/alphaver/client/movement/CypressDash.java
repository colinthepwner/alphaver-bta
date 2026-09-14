package com.alphaver.client.movement;

import com.alphaver.AVConfig;
import com.alphaver.client.AVKeys;
import com.alphaver.net.AVSounds;
import com.alphaver.net.MessageDashSound;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.MinigameHudState;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.KeyBinding;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Environment(EnvType.CLIENT)
public final class CypressDash {
	private CypressDash() {}

	public static final int DASH_TICKS = 30;

	public static final int CHAIN_BELOW = 15;
	public static final int LONG_JUMP_TICKS = 10;

	private static final int LOCAL_ONLY = -1;

	private static int timer;

	public static int timer() {
		return timer;
	}

	public static boolean enabledFor(Minecraft mc) {
		return AVConfig.DASHING && mc != null && mc.thePlayer != null && AVWorlds.isAlphaVer(mc.currentWorld)
			&& !mc.thePlayer.getGamemode().hasPlayerFlight()
			&& (!AVWorlds.isZombies(mc.currentWorld) || MinigameHudState.local.mayDash());
	}

	public static void onKey(Minecraft mc, int keyCode, int mouseCode, boolean pressed) {
		if (!pressed || mc.currentScreen != null || !enabledFor(mc)) {
			return;
		}

		if (isBound(AVKeys.DASH) && AVKeys.DASH.isKeyOrMouse(keyCode, mouseCode)) {
			dash(mc, mc.thePlayer);
		} else if (GameSettings.KEY_JUMP.isKeyOrMouse(keyCode, mouseCode)) {
			longJump(mc, mc.thePlayer);
		}
	}

	public static void tick(PlayerLocal player) {
		Minecraft mc = Minecraft.getMinecraft();
		if (player != mc.thePlayer) {
			return;
		}
		if (!enabledFor(mc)) {
			timer = 0;
			return;
		}
		if (timer > 0) {
			timer--;
			if (timer == 0) {
				play(mc, player, AVSounds.RECHARGE, AVSounds.RECHARGE_VOLUME, LOCAL_ONLY);
			}
		}
	}

	private static void dash(Minecraft mc, PlayerLocal player) {
		if (timer == 0) {
			play(mc, player, AVSounds.DASH, AVSounds.DASH_VOLUME, MessageDashSound.DASH);
			timer = DASH_TICKS;
			player.xd *= 10.0;
			player.yd *= 3.0;
			player.zd *= 10.0;
		} else if (timer < CHAIN_BELOW) {
			play(mc, player, AVSounds.BOOST, AVSounds.BOOST_VOLUME, MessageDashSound.BOOST);
			double heading = inputHeading(player);
			timer = DASH_TICKS;
			player.xd += Math.cos(heading);
			player.yd += 0.2;
			player.zd += Math.sin(heading);
		}
	}

	private static void longJump(Minecraft mc, PlayerLocal player) {
		if (player.isSneaking() && timer == 0) {
			play(mc, player, AVSounds.BOOST, AVSounds.BOOST_VOLUME, MessageDashSound.BOOST);
			double heading = inputHeading(player);
			timer = LONG_JUMP_TICKS;
			player.xd += Math.cos(heading);
			player.yd += 0.5;
			player.zd += Math.sin(heading);
		}
	}

	private static double inputHeading(PlayerLocal player) {
		double inputDegrees = Math.toDegrees(Math.atan2(-player.input.moveStrafe, player.input.moveForward));
		return Math.toRadians(player.yRot + 90.0F + inputDegrees);
	}

	private static boolean isBound(KeyBinding key) {
		return key != null && key.getInputDevice() != null;
	}

	private static void play(Minecraft mc, PlayerLocal player, String event, float volume, int networkSound) {
		if (player.world != null) {
			player.world.playSoundAtEntity(null, player, event, volume, 1.0F);
		}
		if (networkSound != LOCAL_ONLY && mc.isMultiplayerWorld()) {
			NetworkHandler.sendToServer(new MessageDashSound(networkSound));
		}
	}
}
