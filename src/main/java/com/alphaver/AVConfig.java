package com.alphaver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.TomlConfigHandler;
import turniplabs.halplibe.util.toml.Toml;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.alphaver.AlphaVer.MOD_ID;

@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
public final class AVConfig {
	private AVConfig() {}

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static TomlConfigHandler cfg;

	public static final String GENERAL_CATEGORY = "General";
	public static final String GENERATION_CATEGORY = "Generation";
	public static final String CLIENT_CATEGORY = "Client";
	public static final String EVENTS_CATEGORY = "Events";
	public static final String GAMEPLAY_CATEGORY = "Gameplay";

	public static final int DIMENSION_AUTO = -1;

	public static int HUB_DOOR_CHANCE = 300;

	public static String FRACTURED_WORLD = "clock";

	public static String SAND_WORLD = "seeded";

	public static boolean BIOMES = true;

	public static String NEBULA = "milestones";

	public static boolean OBSERVERS = true;

	public static boolean COLOSSUS = true;

	public static boolean RECRUITERS = true;

	public static int OVERWORLD_RECRUITER_CHANCE = 1000;

	public static boolean RAIN_FLOODING = true;

	public static boolean FRAIL = false;

	public static boolean LILYPAD_HUNGER = true;

	public static String VISUALS = "standard";

	public static boolean VISUALS_1604 = false;

	public static boolean RETEXTURE = true;

	public static boolean DASHING = true;

	public static boolean MUSIC = true;

	public static boolean COLORED_LIGHT = true;

	public static boolean TEXTURE_FILTER = false;

	public static String CYPRESS_SOURCE = "";

	@SuppressWarnings({"java:S899", "ResultOfMethodCallIgnored"})
	static void init() {
		LOGGER.info("Initializing config..");

		Toml props = new Toml("AlphaVer");
		assembleProperties(props);

		cfg = new TomlConfigHandler(MOD_ID, props);

		if (cfg.getConfigFile().exists()) {
			cfg.loadConfig();
		} else {
			try {
				cfg.getConfigFile().createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			cfg.writeConfig();
		}

		loadProperties();
	}

	private static void loadProperties() {
		HUB_DOOR_CHANCE =cfgGetValueOrDefault(GENERATION_CATEGORY + ".HUB_DOOR_CHANCE", HUB_DOOR_CHANCE);
		FRACTURED_WORLD = cfgGetValueOrDefault(GENERATION_CATEGORY + ".FRACTURED_WORLD", FRACTURED_WORLD)
			.trim().toLowerCase(Locale.ROOT);
		SAND_WORLD = cfgGetValueOrDefault(GENERATION_CATEGORY + ".SAND_WORLD", SAND_WORLD)
			.trim().toLowerCase(Locale.ROOT);
		BIOMES = cfgGetValueOrDefault(GENERATION_CATEGORY + ".BIOMES", BIOMES);
		NEBULA = cfgGetValueOrDefault(EVENTS_CATEGORY + ".NEBULA", NEBULA).trim().toLowerCase(Locale.ROOT);
		OBSERVERS = cfgGetValueOrDefault(EVENTS_CATEGORY + ".OBSERVERS", OBSERVERS);
		COLOSSUS = cfgGetValueOrDefault(EVENTS_CATEGORY + ".COLOSSUS", COLOSSUS);
		RECRUITERS = cfgGetValueOrDefault(EVENTS_CATEGORY + ".RECRUITERS", RECRUITERS);
		OVERWORLD_RECRUITER_CHANCE = cfgGetValueOrDefault(EVENTS_CATEGORY + ".OVERWORLD_RECRUITER_CHANCE", OVERWORLD_RECRUITER_CHANCE);
		RAIN_FLOODING = cfgGetValueOrDefault(GAMEPLAY_CATEGORY + ".RAIN_FLOODING", RAIN_FLOODING);
		FRAIL = cfgGetValueOrDefault(GAMEPLAY_CATEGORY + ".FRAIL", FRAIL);
		LILYPAD_HUNGER = cfgGetValueOrDefault(GAMEPLAY_CATEGORY + ".LILYPAD_HUNGER", LILYPAD_HUNGER);

		VISUALS = cfgGetValueOrDefault(CLIENT_CATEGORY + ".VISUALS", VISUALS).trim().toLowerCase(Locale.ROOT);

		VISUALS_1604 = VISUALS.equals("1604") || VISUALS.equals("16.04") || VISUALS.equals("legacy");
		RETEXTURE = cfgGetValueOrDefault(CLIENT_CATEGORY + ".RETEXTURE", RETEXTURE);
		DASHING = cfgGetValueOrDefault(CLIENT_CATEGORY + ".DASHING", DASHING);
		MUSIC = cfgGetValueOrDefault(CLIENT_CATEGORY + ".MUSIC", MUSIC);
		COLORED_LIGHT = cfgGetValueOrDefault(CLIENT_CATEGORY + ".COLORED_LIGHT", COLORED_LIGHT);
		TEXTURE_FILTER = cfgGetValueOrDefault(CLIENT_CATEGORY + ".TEXTURE_FILTER", TEXTURE_FILTER);
		CYPRESS_SOURCE = cfgGetValueOrDefault(CLIENT_CATEGORY + ".CYPRESS_SOURCE", CYPRESS_SOURCE).trim();
	}

	public static int recordedDimensionId(String key) {
		File file = cfg == null ? null : cfg.getConfigFile();
		if (file == null || !file.isFile()) {
			return DIMENSION_AUTO;
		}
		Pattern line = Pattern.compile("^\\s*" + Pattern.quote(key) + "\\s*=\\s*(-?\\d+)\\s*(#.*)?$");
		try {
			for (String text : Files.readAllLines(file.toPath(), StandardCharsets.UTF_8)) {
				Matcher matcher = line.matcher(text);
				if (matcher.matches()) {
					return Integer.parseInt(matcher.group(1));
				}
			}
		} catch (IOException | RuntimeException e) {
			LOGGER.warn("Could not read {} from the config; treating it as unrecorded.", key, e);
		}
		return DIMENSION_AUTO;
	}

	public static void recordDimensionId(String key, int id) {
		File file = cfg == null ? null : cfg.getConfigFile();
		if (file == null || !file.isFile()) {
			LOGGER.warn("No config file to record the resolved {} in; it will be re-allocated on the next launch.", key);
			return;
		}

		try {
			List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
			Pattern line = Pattern.compile("^(\\s*)" + Pattern.quote(key) + "\\s*=.*$");
			int anchor = -1;
			boolean replaced = false;

			for (int i = 0; i < lines.size(); i++) {
				Matcher matcher = line.matcher(lines.get(i));
				if (matcher.matches()) {
					lines.set(i, matcher.group(1) + key + " = " + id);
					replaced = true;
					break;
				}
				if (anchor < 0 && lines.get(i).trim().startsWith("DIMENSION_ID")) {
					anchor = i;
				}
			}

			if (!replaced) {

				if (anchor < 0) {
					LOGGER.warn("Config file has no {} line to update; it will be re-allocated on the next launch.", key);
					return;
				}
				String anchorLine = lines.get(anchor);
				String indent = anchorLine.substring(0, anchorLine.indexOf("DIMENSION_ID"));
				lines.add(anchor + 1, indent + key + " = " + id);
			}

			Files.write(file.toPath(), lines, StandardCharsets.UTF_8);
			LOGGER.info("Recorded {} = {} in the config.", key, id);
		} catch (IOException | RuntimeException e) {
			LOGGER.warn("Could not write the resolved {} back to the config.", key, e);
		}
	}

	private static void assembleProperties(Toml properties) {
		properties.addCategory(GENERAL_CATEGORY)
			.addEntry("cfgVersion", 1)
			.addEntry("DIMENSION_ID", "Cypress's dimension id. -1 takes the lowest free id and records it here.", DIMENSION_AUTO)
			.addEntry("HUB_DIMENSION_ID", "The Hub's dimension id. -1 takes the lowest free id.", DIMENSION_AUTO)
			.addEntry("ZOMBIES_DIMENSION_ID", "The Zombies minigame's dimension id. -1 takes the lowest free id.", DIMENSION_AUTO)
			.addEntry("FREERUN_DIMENSION_ID", "The Freerun minigame's dimension id. -1 takes the lowest free id.", DIMENSION_AUTO);

		properties.addCategory(GENERATION_CATEGORY)
			.addEntry("FRACTURED_WORLD",
				"clock | always | never -- Cypress fractures chunks generated between 23:00 and 04:59.",
				FRACTURED_WORLD)
			.addEntry("SAND_WORLD",
				"seeded | always | never -- one Cypress world in four is sand covered.",
				SAND_WORLD)
			.addEntry("BIOMES",
				"false is Cypress's no-biome mode: no Highwood, no Mycon.",
				BIOMES)
			.addEntry("HUB_DOOR_CHANCE",
				"One overworld chunk in this many gets a Hub Door, with a twin at the same x/z in the Hub. 0 disables them.",
				HUB_DOOR_CHANCE);

		properties.addCategory(EVENTS_CATEGORY)
			.addEntry("NEBULA",
				"milestones | always | never -- the Crab Nebula replaces the sun one milestone in ten.",
				NEBULA)
			.addEntry("OBSERVERS", "Observers stalk players through dark Cypress nights.", OBSERVERS)
			.addEntry("COLOSSUS", "Now and then a Colossus drops out of the sky near a player.", COLOSSUS)
			.addEntry("RECRUITERS", "Black figures watch from a distance and vanish when approached.", RECRUITERS)
			.addEntry("OVERWORLD_RECRUITER_CHANCE",
				"One 32x32 area crossed in this many brings a Recruiter to the regular overworld. 0 disables.",
				OVERWORLD_RECRUITER_CHANCE);

		properties.addCategory(GAMEPLAY_CATEGORY)
			.addEntry("RAIN_FLOODING", "Rain floods unlit low ground in Cypress, as it did there.", RAIN_FLOODING)
			.addEntry("FRAIL", "Cypress's Frail difficulty inside AlphaVer's dimensions: unarmoured, any hurt kills.", FRAIL)
			.addEntry("LILYPAD_HUNGER",
				"Lilypad's hunger meter inside AlphaVer's dimensions: a heart each minute it runs dry; Liquified Flame refills it.",
				LILYPAD_HUNGER);

		properties.addCategory(CLIENT_CATEGORY)
			.addEntry("VISUALS",
				"standard | 1604 -- Cypress's own art, or its legacy visuals from 16.04. Rebuilt on next launch.",
				VISUALS)
			.addEntry("RETEXTURE",
				"Repaint BTA's own blocks, items, mobs and GUI with Cypress's art inside AlphaVer's dimensions (locked on there while true).",
				RETEXTURE)
			.addEntry("DASHING", "Cypress's dash and long jump inside AlphaVer's dimensions.", DASHING)
			.addEntry("MUSIC", "Cypress's soundtrack inside AlphaVer's dimensions.", MUSIC)
			.addEntry("COLORED_LIGHT", "Light from AlphaVer's glowing blocks takes their colour, in any lighting style.", COLORED_LIGHT)
			.addEntry("TEXTURE_FILTER", "Cypress's smoothed (bilinear) texture filter while its look is on. No visuals pack Cypress shipped used it.",
				TEXTURE_FILTER)
			.addEntry("CYPRESS_SOURCE",
				"Path to your copy of Cypress if it is not under the game directory. Empty searches the game directory.",
				CYPRESS_SOURCE);
	}

	@SuppressWarnings("unchecked")
	static <T> T cfgGetValueOrDefault(String key, T def) {
		T res = null;

		try {
			if (def instanceof String) {
				res = (T) cfg.getString(key);
			} else if (def instanceof Integer) {
				res = (T) Integer.valueOf(cfg.getInt(key));
			} else if (def instanceof Long) {
				res = (T) Long.valueOf(cfg.getLong(key));
			} else if (def instanceof Boolean) {
				res = (T) Boolean.valueOf(cfg.getBoolean(key));
			} else if (def instanceof Double || def instanceof Float) {
				double raw = cfg.getDouble(key);
				if (def instanceof Float) {
					res = (T) Float.valueOf((float) raw);
				} else {
					res = (T) Double.valueOf(raw);
				}
			} else {
				throw new RuntimeException("Invalid value type!");
			}
		} catch (NullPointerException | ClassCastException ignored) {  }

		if (res == null) {
			LOGGER.warn("Failed to load \"{}\"! Assuming default...", key);
			return def;
		}

		return res;
	}
}
