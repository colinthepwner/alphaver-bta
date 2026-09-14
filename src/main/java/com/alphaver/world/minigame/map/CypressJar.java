package com.alphaver.world.minigame.map;

import com.alphaver.AVConfig;
import com.alphaver.AlphaVer;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipFile;

public final class CypressJar {
	private CypressJar() {}

	public static final String MAPS = "wstatic.zip";

	private static final long MIN_BYTES = 32L << 20;
	private static final int MAX_DEPTH = 4;
	private static final int MAX_FILES = 30000;
	private static final Set<String> SKIPPED = new HashSet<>(Arrays.asList(
		"saves", "world", "logs", "crash-reports", "screenshots", "stats", "assets", "libraries", "natives", "resourcepacks",
		"texturepacks", "shaderpacks", "backups", "config", ".git", ".gradle", ".fabric", ".mixin.out", "build", "run"));

	public record Source(File file, boolean inJar) {}

	private static volatile File offered;

	public static void offer(@Nullable File file) {
		if (file != null && check(file) != null) {
			offered = file;
		}
	}

	@Nullable
	public static Source find() {
		File hint = offered;
		if (hint != null) {
			Source source = check(hint);
			if (source != null) {
				return source;
			}
		}
		File gameDir;
		try {
			gameDir = FabricLoader.getInstance().getGameDir().toFile();
		} catch (RuntimeException e) {
			gameDir = new File(".");
		}
		String configured = AVConfig.CYPRESS_SOURCE == null ? "" : AVConfig.CYPRESS_SOURCE.trim();
		if (!configured.isEmpty()) {
			File file = new File(configured);
			if (!file.isAbsolute()) {
				file = new File(gameDir, configured);
			}
			Source source = check(file);
			if (source != null) {
				return source;
			}
			AlphaVer.LOGGER.warn("Minigame maps: CYPRESS_SOURCE '{}' has no {} in it; searching the game directory instead.",
				configured, MAPS);
		}
		return search(gameDir);
	}

	@Nullable
	private static Source search(File root) {
		List<File> level = new ArrayList<>();
		level.add(root);
		int budget = MAX_FILES;
		for (int depth = 0; depth <= MAX_DEPTH && !level.isEmpty(); depth++) {
			List<File> next = new ArrayList<>();
			for (File dir : level) {
				File[] children = dir.listFiles();
				if (children == null) {
					continue;
				}

				if (depth > 0 && new File(dir, "level.dat").isFile()) {
					continue;
				}
				for (File child : children) {
					if (budget-- <= 0) {
						return null;
					}
					if (child.isDirectory()) {
						if (!SKIPPED.contains(child.getName().toLowerCase(Locale.ROOT))) {
							if (new File(child, MAPS).isFile()) {
								Source source = check(child);
								if (source != null) {
									return source;
								}
							}
							next.add(child);
						}
					} else if (child.length() >= MIN_BYTES) {
						Source source = check(child);
						if (source != null) {
							return source;
						}
					}
				}
			}
			level = next;
		}
		return null;
	}

	@Nullable
	private static Source check(File file) {
		try {
			if (file.isDirectory()) {
				File maps = new File(file, MAPS);
				return maps.isFile() && isZip(maps) ? new Source(maps, false) : null;
			}
			if (!file.isFile() || file.length() < MIN_BYTES || !isZip(file)) {
				return null;
			}
			if (file.getName().equalsIgnoreCase(MAPS)) {
				return new Source(file, false);
			}
			try (ZipFile zip = new ZipFile(file)) {
				return zip.getEntry(MAPS) != null ? new Source(file, true) : null;
			}
		} catch (IOException | RuntimeException e) {
			return null;
		}
	}

	private static boolean isZip(File file) {
		try (InputStream in = new FileInputStream(file)) {
			byte[] header = new byte[4];
			int read = 0;
			while (read < 4) {
				int step = in.read(header, read, 4 - read);
				if (step < 0) {
					return false;
				}
				read += step;
			}
			return header[0] == 'P' && header[1] == 'K' && header[2] == 3 && header[3] == 4;
		} catch (IOException e) {
			return false;
		}
	}
}
