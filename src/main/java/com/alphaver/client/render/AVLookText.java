package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.lang.Language;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Environment(EnvType.CLIENT)
public final class AVLookText {
	private AVLookText() {}

	private static final String ENGLISH = "en_US";

	private static volatile Map<String, String> overrides = Collections.emptyMap();
	@Nullable
	private static volatile String loadedFor;

	@Nullable
	public static String override(@NotNull String key) {
		if (!AVLookController.isApplied()) {
			return null;
		}
		return overridesFor(currentLanguage()).get(key);
	}

	@NotNull
	private static String currentLanguage() {
		Language language = I18n.getInstance().getCurrentLanguage();
		String id = language == null ? null : language.getId();
		return id == null ? ENGLISH : id;
	}

	@NotNull
	private static Map<String, String> overridesFor(@NotNull String languageId) {
		if (languageId.equals(loadedFor)) {
			return overrides;
		}
		Map<String, String> loaded = new HashMap<>();
		load(ENGLISH, loaded);
		if (!ENGLISH.equals(languageId)) {
			load(languageId, loaded);
		}
		overrides = loaded;
		loadedFor = languageId;
		return loaded;
	}

	private static void load(String languageId, Map<String, String> into) {
		String path = "/assets/alphaver/look/" + languageId + ".lang";
		try (InputStream in = AVLookText.class.getResourceAsStream(path)) {
			if (in == null) {
				return;
			}
			Properties properties = new Properties();
			properties.load(new InputStreamReader(in, StandardCharsets.UTF_8));
			for (String name : properties.stringPropertyNames()) {
				into.put(name, properties.getProperty(name));
			}
		} catch (IOException | RuntimeException e) {
			AlphaVer.LOGGER.warn("Could not read {}; those names and descriptions stay as BTA has them.", path, e);
		}
	}
}
