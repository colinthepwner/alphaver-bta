package com.alphaver.client.sound;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Environment(EnvType.CLIENT)
public final class AVMusicIcons {
	private AVMusicIcons() {}

	private static final String INDEX = "/assets/alphaver/sidecar/sounds.json";
	private static final String[] POOLS = {"music.cypress", "music.hub"};

	private static Set<String> soundtrack;
	private static final Map<String, ItemStack> ICONS = new HashMap<>();

	@Nullable
	public static ItemStack iconFor(@Nullable String title) {
		if (title == null) {
			return null;
		}
		ItemStack cached = ICONS.get(title);
		if (cached != null) {
			return cached;
		}
		Item disc = discFor(title);
		if (disc == null && soundtrack().contains(title)) {
			disc = AVItems.RECORD_LEMURIA;
		}
		if (disc == null) {
			return null;
		}
		ItemStack icon = new ItemStack(disc);
		ICONS.put(title, icon);
		return icon;
	}

	@Nullable
	private static Item discFor(String title) {
		return switch (title) {
			case "Lemuria" -> AVItems.RECORD_LEMURIA;
			case "Hidden Den" -> AVItems.RECORD_HIDDEN_DEN;
			case "Downbeat Uplink" -> AVItems.RECORD_DOWNBEAT_UPLINK;
			case "Sandcastles" -> AVItems.RECORD_SANDCASTLES;
			case "K2" -> AVItems.RECORD_K2;
			case "Rock Beetle", "Rokkubitoru Tune" -> AVItems.RECORD_ROCK_BEETLE;
			case "Desambrier" -> AVItems.RECORD_DESAMBRIER;
			case "Juhry" -> AVItems.RECORD_JUHRY;
			case "Gyldan Sverd" -> AVItems.RECORD_GYLDAN_SVERD;
			default -> null;
		};
	}

	private static Set<String> soundtrack() {
		Set<String> titles = soundtrack;
		if (titles != null) {
			return titles;
		}
		titles = new HashSet<>();
		try (InputStream in = AVMusicIcons.class.getResourceAsStream(INDEX)) {
			if (in != null) {
				JsonObject index = JsonParser.parseReader(new InputStreamReader(in, StandardCharsets.UTF_8)).getAsJsonObject();
				for (String pool : POOLS) {
					JsonElement event = index.get(pool);
					if (event == null || !event.isJsonObject() || !event.getAsJsonObject().has("sounds")) {
						continue;
					}
					JsonArray sounds = event.getAsJsonObject().getAsJsonArray("sounds");
					for (JsonElement sound : sounds) {
						if (sound.isJsonObject() && sound.getAsJsonObject().has("title")) {
							titles.add(sound.getAsJsonObject().get("title").getAsString());
						}
					}
				}
			}
		} catch (Exception e) {

			AlphaVer.LOGGER.warn("Could not read AlphaVer's soundtrack titles for the music popup: {}", e.toString());
		}
		soundtrack = titles;
		return titles;
	}
}
