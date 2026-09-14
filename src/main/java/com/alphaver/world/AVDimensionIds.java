package com.alphaver.world;

import com.alphaver.AVConfig;
import com.alphaver.AlphaVer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.world.Dimension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

final class AVDimensionIds {
	private AVDimensionIds() {}

	private static final int LAST_CANDIDATE = 127;

	@NotNull
	static int[] allocate(@NotNull List<AVDimensions.NamedDimension> dimensions) {
		int count = dimensions.size();
		int[] chosen = new int[count];
		Arrays.fill(chosen, -1);
		if (!Dimension.getDimensionList().containsValue(Dimension.OVERWORLD)) {
			AlphaVer.LOGGER.error("AlphaVer's dimensions asked for ids before BTA registered its own; none of them will register.");
			return chosen;
		}

		int[] recorded = new int[count];
		Set<Integer> promised = new HashSet<>();
		for (int i = 0; i < count; i++) {
			recorded[i] = AVConfig.recordedDimensionId(dimensions.get(i).configKey);
			if (recorded[i] >= 0 && recorded[i] <= LAST_CANDIDATE && isFree(recorded[i], promised)) {
				chosen[i] = recorded[i];
				promised.add(recorded[i]);
			}
		}

		for (int i = 0; i < count; i++) {
			if (chosen[i] >= 0) {
				continue;
			}
			AVDimensions.NamedDimension dimension = dimensions.get(i);
			int id = lowestFree(promised);
			if (id < 0) {
				AlphaVer.LOGGER.error("Every dimension id up to {} is taken, so {} cannot be registered. Registered ids: {}.",
					LAST_CANDIDATE, dimension.englishName, describeIdSpace());
				continue;
			}
			chosen[i] = id;
			promised.add(id);
			explainAllocation(dimension, recorded[i], id);
		}

		closeHoles(dimensions, chosen, promised);

		for (int i = 0; i < count; i++) {
			if (chosen[i] >= 0 && chosen[i] != recorded[i]) {
				AVConfig.recordDimensionId(dimensions.get(i).configKey, chosen[i]);
			}
		}
		return chosen;
	}

	private static void closeHoles(List<AVDimensions.NamedDimension> dimensions, int[] chosen, Set<Integer> promised) {
		while (true) {
			int top = highestTaken(promised);
			int index = indexOf(chosen, top);
			int lowest = lowestFree(promised);
			if (index < 0 || lowest < 0 || lowest > top) {

				return;
			}
			AVDimensions.NamedDimension dimension = dimensions.get(index);
			List<String> saved = worldsWith(dimension, top);
			if (!saved.isEmpty()) {
				AlphaVer.LOGGER.warn("Dimension id {} is free, but {} stays at {}: world(s) {} have it saved there. The gap stays until "
					+ "another dimension mod takes id {}.", lowest, dimension.englishName, top, String.join(", ", saved), lowest);
				return;
			}
			AlphaVer.LOGGER.info("{} moves from dimension id {} down to {}: {} is free, and no world here has {} saved at {}. BTA's "
				+ "world type groups need the ids contiguous.", dimension.englishName, top, lowest, lowest, dimension.englishName, top);
			promised.remove(top);
			promised.add(lowest);
			chosen[index] = lowest;
		}
	}

	private static void explainAllocation(AVDimensions.NamedDimension dimension, int recorded, int id) {
		String name = dimension.englishName;
		String key = dimension.configKey;
		if (recorded == AVConfig.DIMENSION_AUTO) {
			AlphaVer.LOGGER.info("{} takes dimension id {}, the lowest free one, recorded as {} = {} in config/alphaver.cfg.",
				name, id, key, id);
			return;
		}
		if (recorded < 0 || recorded > LAST_CANDIDATE) {
			AlphaVer.LOGGER.warn("config/alphaver.cfg has {} = {}, which is not a usable dimension id; {} takes {} instead.",
				key, recorded, name, id);
			return;
		}
		Dimension holder = Dimension.getDimensionList().get(recorded);
		if (holder == null) {

			AlphaVer.LOGGER.warn("config/alphaver.cfg records dimension id {} for {} and for another AlphaVer dimension, which keeps "
				+ "it; {} takes {}.", recorded, name, name, id);
			return;
		}
		List<String> saved = worldsWith(dimension, recorded);
		if (saved.isEmpty()) {
			AlphaVer.LOGGER.warn("Dimension id {}, recorded for {}, is held by '{}' now, so {} takes id {} instead. No world here has {} "
					+ "saved at {}, so nothing is lost; {} = {} is recorded.",
				recorded, name, holder.languageKey, name, id, name, recorded, key, id);
			return;
		}

		AlphaVer.LOGGER.error("Dimension id {}, recorded for {}, is held by '{}' now, so {} moves to id {}. World(s) here already have "
				+ "{} saved at {}: {}. That terrain is still on disk under dimensions/{}/, but at that number those worlds now open "
				+ "'{}', and {} starts over at {}. To get it back, remove the mod that added '{}' and set {} = {} in "
				+ "config/alphaver.cfg. A player who logged out inside {} will arrive in '{}'.",
			recorded, name, holder.languageKey, name, id, name, recorded, String.join(", ", saved), recorded, holder.languageKey, name,
			id, holder.languageKey, key, recorded, name, holder.languageKey);
	}

	private static boolean isFree(int id, Set<Integer> promised) {
		return !Dimension.getDimensionList().containsKey(id) && !promised.contains(id);
	}

	private static int lowestFree(Set<Integer> promised) {
		for (int id = 0; id <= LAST_CANDIDATE; id++) {
			if (isFree(id, promised)) {
				return id;
			}
		}
		return -1;
	}

	private static int highestTaken(Set<Integer> promised) {
		int highest = -1;
		for (int id : Dimension.getDimensionList().keySet().toIntArray()) {
			highest = Math.max(highest, id);
		}
		for (int id : promised) {
			highest = Math.max(highest, id);
		}
		return highest;
	}

	private static int indexOf(int[] ids, int id) {
		for (int i = 0; i < ids.length; i++) {
			if (ids[i] == id) {
				return i;
			}
		}
		return -1;
	}

	@NotNull
	private static List<String> worldsWith(AVDimensions.NamedDimension dimension, int id) {
		List<String> worlds = new ArrayList<>();
		String key = Registries.WORLD_TYPES.getKey(dimension.defaultWorldType);
		if (key == null) {
			return worlds;
		}
		byte[] needle = nbtString(key);
		Path gameDir;
		try {
			gameDir = FabricLoader.getInstance().getGameDir();
		} catch (RuntimeException e) {
			gameDir = Path.of("");
		}
		scan(gameDir.resolve("saves"), id, needle, worlds);
		scan(gameDir, id, needle, worlds);
		return worlds;
	}

	private static void scan(Path root, int id, byte[] needle, List<String> worlds) {
		if (!Files.isDirectory(root)) {
			return;
		}
		try (DirectoryStream<Path> children = Files.newDirectoryStream(root, Files::isDirectory)) {
			for (Path world : children) {
				Path file = world.resolve("dimensions").resolve(String.valueOf(id)).resolve("dimension.dat");
				if (!Files.isRegularFile(file)) {
					continue;
				}
				Boolean match = holds(file, needle);
				if (match == null) {
					worlds.add(world.getFileName() + " (unreadable)");
				} else if (match) {
					worlds.add(String.valueOf(world.getFileName()));
				}
			}
		} catch (IOException | RuntimeException e) {
			AlphaVer.LOGGER.warn("Could not look through {} for saved AlphaVer dimensions.", root, e);
		}
	}

	@Nullable
	private static Boolean holds(Path file, byte[] needle) {
		byte[] data;
		try {
			byte[] raw = Files.readAllBytes(file);
			try (GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(raw))) {
				data = gzip.readAllBytes();
			} catch (IOException notGzip) {
				data = raw;
			}
		} catch (IOException | RuntimeException e) {
			return null;
		}
		for (int start = 0; start <= data.length - needle.length; start++) {
			int matched = 0;
			while (matched < needle.length && data[start + matched] == needle[matched]) {
				matched++;
			}
			if (matched == needle.length) {
				return true;
			}
		}
		return false;
	}

	private static byte[] nbtString(String value) {
		byte[] text = value.getBytes(StandardCharsets.UTF_8);
		byte[] out = new byte[text.length + 2];
		out[0] = (byte) (text.length >>> 8);
		out[1] = (byte) text.length;
		System.arraycopy(text, 0, out, 2, text.length);
		return out;
	}

	@NotNull
	static List<Integer> plugHoles() {
		List<Integer> holes = new ArrayList<>();
		int highest = highestTaken(Set.of());
		for (int id = 0; id < highest; id++) {
			if (!Dimension.getDimensionList().containsKey(id)) {
				holes.add(id);
			}
		}
		if (holes.isEmpty()) {
			return holes;
		}
		AlphaVer.LOGGER.warn("Dimension ids are not contiguous ({}; {} missing). BTA's WorldTypeGroups.Group constructor needs every id "
			+ "from 0 up, so the missing ones point at the overworld while BTA's world type groups are built, and are removed straight "
			+ "after. A mod that builds a world type group later without the same care will crash.", describeIdSpace(), holes);
		try {
			Int2ObjectMap<Dimension> live = mutableDimensionList();
			for (int id : holes) {
				live.put(id, Dimension.OVERWORLD);
			}
			return holes;
		} catch (ReflectiveOperationException | RuntimeException e) {
			AlphaVer.LOGGER.error("Could not reach Dimension.dimensionList to fill the missing ids; building BTA's world type groups may "
				+ "fail.", e);
			return new ArrayList<>();
		}
	}

	static void unplugHoles(@NotNull List<Integer> plugged) {
		if (plugged.isEmpty()) {
			return;
		}
		try {
			Int2ObjectMap<Dimension> live = mutableDimensionList();
			for (int id : plugged) {
				live.remove(id);
			}
		} catch (ReflectiveOperationException | RuntimeException e) {
			AlphaVer.LOGGER.error("Could not take the temporary entries {} out of the dimension list again, so those ids now lead to the "
				+ "overworld. Restart the game before creating or joining a world.", plugged, e);
		}
	}

	@SuppressWarnings("unchecked")
	private static Int2ObjectMap<Dimension> mutableDimensionList() throws ReflectiveOperationException {
		Field field = Dimension.class.getDeclaredField("dimensionList");
		field.setAccessible(true);
		return (Int2ObjectMap<Dimension>) field.get(null);
	}

	@NotNull
	static String describeIdSpace() {
		int[] ids = Dimension.getDimensionList().keySet().toIntArray();
		Arrays.sort(ids);
		List<String> parts = new ArrayList<>(ids.length);
		for (int id : ids) {
			Dimension dimension = Dimension.getDimensionList().get(id);
			parts.add(id + " ('" + (dimension == null ? "?" : dimension.languageKey) + "')");
		}
		return String.join(", ", parts);
	}
}
