package com.alphaver.net;

import com.alphaver.AlphaVer;
import com.alphaver.server.AVServerSounds;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.sound.SoundTypes;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class AVSounds {
	private AVSounds() {}

	public static final String DASH = "alphaver:ext.dash";
	public static final String BOOST = "alphaver:ext.marior";
	public static final String RECHARGE = "alphaver:ext.recharg";
	public static final float DASH_VOLUME = 0.6F;
	public static final float BOOST_VOLUME = 1.0F;
	public static final float RECHARGE_VOLUME = 0.6F;

	private static final String INDEX = "/assets/alphaver/sidecar/sounds.json";

	public static void registerNames() {
		try (InputStream in = AVSounds.class.getResourceAsStream(INDEX)) {
			if (in == null) {
				AlphaVer.LOGGER.warn("No sound index at {}; AlphaVer's sounds will not play for players on a server.", INDEX);
				return;
			}
			JsonElement index = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			int added = 0;
			for (String event : index.getAsJsonObject().keySet()) {
				if (SoundTypes.register(AlphaVer.MOD_ID + ":" + event)) {
					added++;
				}
			}
			AlphaVer.LOGGER.info("Registered {} AlphaVer sound events for network play.", added);
		} catch (IOException | RuntimeException e) {
			AlphaVer.LOGGER.warn("Could not read {}; AlphaVer's sounds will not play for players on a server.", INDEX, e);
		}
	}

	public static void playFor(@NotNull Player player, @NotNull String sound, float volume, float pitch) {
		World world = player.world;
		if (world == null || world.isClientSide) {
			return;
		}
		if (EnvironmentHelper.isMultiplayerServer()) {
			AVServerSounds.playTo(player, sound, volume, pitch);
			return;
		}
		world.playSoundAtEntity(null, player, sound, volume, pitch);
	}
}
