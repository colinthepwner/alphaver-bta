package com.alphaver.painting;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public final class AVPaintings {
	private AVPaintings() {}

	public static final int SHEET_WIDTH = 512;

	private static final int MAX_KEY_LENGTH = 26;

	public record Painting(@NotNull String name, @NotNull String title, int u, int v, int width, int height) {
		@NotNull
		public String key() {
			return AlphaVer.MOD_ID + ":" + this.name;
		}

		@NotNull
		public String texture() {
			return AlphaVer.MOD_ID + ":art/" + this.name;
		}

		@NotNull
		public String file() {
			return "assets/" + AlphaVer.MOD_ID + "/textures/art/" + this.name + ".png";
		}
	}

	public static final List<Painting> TABLE = List.of(
		art("night_garden", "NightGarden", 0, 16, 64, 16),
		art("gradient", "Gradient", 16, 80, 48, 16),
		art("fog", "Fog", 96, 128, 32, 32),
		art("quartet", "Quartet", 128, 128, 32, 32),
		art("bull", "Bull", 160, 128, 32, 32),
		art("digital_man", "DigitalMan", 160, 80, 32, 48),
		art("shrine", "Shrine", 208, 112, 48, 48),
		art("galaxy", "Ear Wax", 208, 160, 48, 32),
		art("deepsea_coral", "DeepseaCoral", 192, 192, 64, 32),
		art("landscape", "Landscape", 192, 224, 64, 32),
		art("fragments", "Fragments", 256, 128, 32, 64),
		art("night_light", "NightLight", 64, 16, 16, 16),
		art("salt", "Empty (Small)", 112, 16, 16, 16),
		art("fate", "Fate", 48, 48, 32, 32),
		art("big_salt", "Empty", 80, 48, 32, 32),
		art("maze", "Merriweather", 112, 48, 16, 16),
		art("darkness", "Darkness", 112, 64, 16, 16),
		art("cat_one", "Cat", 32, 160, 32, 32),
		art("parrot", "Parrot", 64, 160, 32, 32),
		art("hill", "Hill", 96, 160, 32, 32),
		art("vermin", "Vermin", 64, 256, 64, 64),
		art("bloodfest", "Towers", 0, 320, 48, 48),
		art("mirror", "Mirror", 48, 336, 32, 32),
		art("atmosphere", "Atmosphere", 80, 320, 48, 48),
		art("apple", "Apple", 0, 432, 16, 32),
		art("laptop", "Laptop", 0, 464, 16, 32),
		art("plant", "Plant", 16, 368, 16, 32),
		art("landscaping", "Landscaping", 16, 400, 16, 32),
		art("pillars", "Pillars", 16, 432, 16, 32),
		art("step_forward", "Step Forward", 16, 464, 16, 32),
		art("ascension", "Ascension", 32, 368, 16, 64),
		art("infiltration", "Infiltration", 48, 368, 80, 64),
		art("lost_paradise", "Lost Paradise", 32, 432, 96, 64),
		art("distant", "Distant", 128, 0, 64, 64),
		art("symbol", "Symbol", 160, 64, 16, 16),
		art("blue_guts", "Divine Drowned", 176, 64, 16, 16),
		art("handdrawn", "Handdrawn Life", 128, 160, 48, 48),
		art("blue", "Blue", 176, 160, 16, 48),
		art("distortion", "Masque", 128, 288, 32, 48),
		art("cat_broom", "Katze", 160, 288, 32, 32),
		art("slain", "Slain", 128, 336, 32, 32),
		art("galaxia", "Cosmos", 160, 320, 48, 48),
		art("hidden", "Hidden", 128, 368, 64, 64),
		art("tiled_surface", "Tiled Surface", 128, 432, 64, 64),
		art("sunrise", "Sunrise", 192, 0, 80, 64),
		art("cosmos", "Cosmos", 192, 64, 16, 64),
		art("tree_plain", "Satellite", 192, 128, 16, 64),
		art("shattered", "Shattered", 192, 256, 64, 64),
		art("worrysome", "Worrysome", 192, 368, 48, 16),
		art("integrity", "Stickbug", 192, 384, 48, 48),
		art("globe", "Global", 272, 0, 64, 64),
		art("gorey", "Jam", 256, 96, 32, 32),
		art("sea_animal", "Sea Animal", 256, 192, 32, 32),
		art("feline", "Feline", 256, 224, 32, 32),
		art("monopoly_board", "Colours", 256, 256, 32, 32),
		art("moon", "Moon", 256, 288, 32, 32),
		art("fighter", "Past", 288, 112, 48, 64),
		art("sideview", "Sideview", 288, 176, 48, 48),
		art("hexagon", "Okinawa", 288, 224, 64, 64),
		art("night_sky", "Night Sky", 336, 112, 64, 16),
		art("the_top", "Superstructure", 352, 256, 64, 32),
		art("door_thing_idk", "Lineframe", 336, 128, 32, 32),
		art("haeven", "Heaven", 368, 128, 32, 32),
		art("jupiter", "Jupiter", 336, 160, 64, 64),
		art("weed", "Cyanobacteria Spiral", 400, 160, 64, 64),
		art("seaside", "Seaside View", 384, 64, 48, 48),
		art("erupt", "Erupt", 432, 0, 16, 16),
		art("graded_sky", "Graded Sky", 432, 32, 16, 32),
		art("black_hole", "Celestial Center", 416, 224, 32, 32),
		art("magenta", "Magenta", 496, 64, 16, 48),
		art("low_river_skull", "Golden Skull", 464, 112, 48, 48),
		art("missing_tex", "Texture", 464, 160, 48, 64),
		art("flipped_shape", "Diamond", 448, 448, 32, 32),
		art("company", "Company", 288, 64, 48, 48),
		art("game", "Game", 336, 64, 48, 48),
		art("kitten", "Kitten", 256, 320, 16, 32),
		art("big_fish", "Oarfish", 0, 496, 192, 16),
		art("dreamland", "Dreamed World", 192, 480, 48, 32),
		art("unseen_land", "Unseen Land", 288, 352, 128, 128),
		art("fields", "Snow Growth", 288, 288, 64, 64),
		art("emil", "Emil", 496, 320, 16, 16),
		art("zombies", "Untoten", 240, 464, 48, 48),
		art("cube2", "Cube 2", 416, 480, 32, 32),
		art("cubes", "Cubes", 416, 448, 32, 32),
		art("spirit", "Space Spirit", 384, 480, 32, 32),
		art("pengo", "Penguin", 352, 480, 32, 32),
		art("rogue", "Rogue", 320, 480, 32, 32),
		art("encounter", "Encounter", 288, 480, 32, 32),
		art("flipped_shape2", "Cyanine", 480, 448, 32, 32),
		art("flipped_shape3", "Emerald", 480, 480, 32, 32),
		art("flipped_shape4", "Amber", 448, 480, 32, 32),
		art("city_licht", "Licht", 240, 368, 48, 32),
		art("waterhall", "Depth", 240, 400, 48, 32),
		art("mall", "Mall", 416, 352, 96, 96)
	);

	private static final Set<ArtType> REGISTERED = Collections.newSetFromMap(new IdentityHashMap<>());

	private static Painting art(String name, String title, int u, int v, int width, int height) {
		return new Painting(name, title, u, v, width, height);
	}

	public static synchronized void register() {
		for (Painting painting : TABLE) {
			String key = painting.key();
			if (key.length() > MAX_KEY_LENGTH) {
				AlphaVer.LOGGER.error("Painting key '{}' is longer than the {} characters BTA's painting packet accepts; skipped.",
					key, MAX_KEY_LENGTH);
				continue;
			}
			if (ArtType.map.containsKey(key)) {
				continue;
			}
			REGISTERED.add(new ArtType(key, painting.title(), "", painting.texture(), painting.width(), painting.height()));
		}
		AlphaVer.LOGGER.info("Registered {} of Cypress's paintings.", REGISTERED.size());
	}

	public static boolean isAlphaVer(@Nullable ArtType art) {
		return art != null && REGISTERED.contains(art);
	}

	public static boolean allowedIn(@Nullable ArtType art, @Nullable World world) {
		return !isAlphaVer(art) || AVWorlds.isAlphaVer(world);
	}
}
