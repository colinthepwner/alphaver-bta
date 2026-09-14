package com.alphaver.asset;

import com.alphaver.AVConfig;
import com.alphaver.AlphaVer;
import com.alphaver.painting.AVPaintings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.render.texturepack.TexturePack;
import net.minecraft.client.render.texturepack.TexturePackList;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Environment(EnvType.CLIENT)
public final class AVAssetSidecar {
	private AVAssetSidecar() {}

	public static final String PACK_NAME = "AlphaVerAssets";
	public static final String LOOK_PACK_NAME = "AlphaVerCypressLook";

	private static final String FILE_MANIFEST = "/assets/alphaver/asset-sidecar.properties";

	private static final String TILE_MANIFEST = "/assets/alphaver/block-sidecar.properties";

	private static final String ITEM_MANIFEST = "/assets/alphaver/item-sidecar.properties";

	private static final String LOOK_MANIFEST = "/assets/alphaver/look-sidecar.properties";

	private static final String STAMP = "sidecar-source.txt";

	private static final int SIDECAR_REVISION = 16;

	private static final String[] SIGNATURE = {
		"ext1605/shaders/default/final.fsh",
		"unl/8db7811c-71de-11ee-b962-0242ac120002.xAL",
		"terrain.png",
	};

	private static final String VISUALS_1604 = "unl/8db7811c-71de-11ee-b962-0242ac120002.xAL";

	public static int writtenCount = -1;
	public static int missingCount = -1;
	public static String sourceLabel = null;
	public static boolean usedCache = false;

	public static File run(File gameDir) {
		if (gameDir == null) {
			return null;
		}

		Manifests manifests = Manifests.load();
		if (manifests.isEmpty()) {
			AlphaVer.LOGGER.warn("Asset sidecar: every manifest is empty or missing, skipping");
			return null;
		}

		File packDir = new File(gameDir, "texturepacks/" + PACK_NAME);
		File lookDir = new File(gameDir, "texturepacks/" + LOOK_PACK_NAME);

		Stamp previous = Stamp.read(packDir);
		if (previous != null && previous.stillValid(gameDir) && gameDirOutputsPresent(manifests, gameDir)) {
			usedCache = true;
			sourceLabel = previous.label;
			AlphaVer.LOGGER.info("Asset sidecar: '{}' is already built from {}, skipping extraction",
				PACK_NAME, previous.label);
			return packDir;
		}

		Source source = configuredSource(gameDir);
		if (source == null) {
			source = findSource(gameDir, packDir, lookDir);
		}
		if (source == null) {
			return reportNothingFound(gameDir, packDir);
		}
		sourceLabel = "'" + relativePath(gameDir, source.file) + "'";

		com.alphaver.world.minigame.map.CypressJar.offer(source.file);

		Map<String, byte[]> entries;
		try {
			entries = source.read(manifests.wantedPaths());
		} catch (IOException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not read {}: {}", sourceLabel, e.toString());
			return reportNothingFound(gameDir, packDir);
		}

		if (AVConfig.VISUALS_1604 && entries.containsKey(VISUALS_1604)) {
			overlayVisuals(entries, entries.get(VISUALS_1604));
		}

		List<String> missing = new ArrayList<>();
		int written = 0;
		try {
			written += writeFiles(manifests.files, entries, gameDir, packDir, missing);
			written += writeTiles(manifests.tiles, entries, packDir, missing);
			written += writeCarvedDoorIcons(entries, packDir);
			written += writeLore(source, packDir);
			written += writePaintings(entries, packDir, missing);
			writeSoundIndex(gameDir, packDir);
			writePackMeta(packDir, "Cypress's own blocks, items, mobs and sounds for AlphaVer.");

			if (!manifests.look.isEmpty()) {
				written += writeLook(manifests.look, entries, lookDir, missing);
				writePackMeta(lookDir, "Cypress's look for BTA's own textures. AlphaVer switches this on inside its "
					+ "dimensions only -- enabling it by hand repaints the whole game.");
			}
		} catch (IOException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not write the packs: {}", e.toString());
			return null;
		}

		writtenCount = written;
		missingCount = missing.size();
		AlphaVer.LOGGER.info("Asset sidecar: {} files written from {} into '{}' and '{}'",
			written, sourceLabel, PACK_NAME, LOOK_PACK_NAME);
		if (!missing.isEmpty()) {
			AlphaVer.LOGGER.warn("Asset sidecar: {} source file(s) were not in {}: {}", missing.size(), sourceLabel,
				summarise(missing));
		}

		new Stamp(sourceLabel, source.file, gameDir).write(packDir);
		return packDir;
	}

	public static void enablePacks(TexturePackList packs, File packDir) {
		if (packs == null) {
			return;
		}
		try {
			packs.updateAvailableTexturePacks();

			for (TexturePack pack : new ArrayList<>(packs.selectedPacks)) {
				if (LOOK_PACK_NAME.equals(pack.fileName)) {

					packs.unsetTexturePack(pack);
					AlphaVer.LOGGER.info("Asset sidecar: deselected '{}', which only belongs on inside AlphaVer's "
						+ "dimensions", LOOK_PACK_NAME);
				}
			}

			if (packDir == null || !packDir.isDirectory()) {
				return;
			}
			for (TexturePack pack : packs.availableTexturePacks()) {
				if (!PACK_NAME.equals(pack.fileName)) {
					continue;
				}
				if (!packs.selectedPacks.contains(pack)) {

					packs.setTexturePack(pack);
					AlphaVer.LOGGER.info("Asset sidecar: texture pack '{}' enabled automatically", PACK_NAME);
				}
				return;
			}
			AlphaVer.LOGGER.info("Asset sidecar: '{}' was written but BTA did not list it; enable it in Options",
				PACK_NAME);
		} catch (Throwable t) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not enable '{}' automatically ({}); enable it in Options",
				PACK_NAME, t.toString());
		}
	}

	private static File reportNothingFound(File gameDir, File packDir) {
		writtenCount = 0;
		if (new File(packDir, "assets").isDirectory()) {
			usedCache = true;
			Stamp previous = Stamp.read(packDir);
			sourceLabel = previous != null ? previous.label : "a pack built elsewhere";
			AlphaVer.LOGGER.info("Asset sidecar: no copy of Cypress found under '{}', but '{}' is already built "
				+ "from {} -- using it as it stands.", gameDir.getPath(), PACK_NAME, sourceLabel);
			return packDir;
		}

		AlphaVer.LOGGER.info("Asset sidecar: no copy of Cypress (ext1605_20_client.jar) found anywhere under '{}'. "
			+ "AlphaVer's blocks and mobs will use stand-in textures. Drop your own copy in -- jar, zip or unpacked "
			+ "folder, any name, any depth -- to restore Cypress's look. Nothing is downloaded.", gameDir.getPath());
		return null;
	}

	public static void logSummary() {
		if (writtenCount <= 0 && !usedCache) {
			AlphaVer.LOGGER.warn("Asset sidecar: none of Cypress's art is loaded. See the README.");
		}
	}

	private static final int MAX_SCAN_DEPTH = 6;
	private static final int MAX_SCAN_FILES = 40000;
	private static final int MAX_CONTAINERS_OPENED = 400;

	private static final long MIN_CONTAINER_BYTES = 1L << 20;

	private static final Set<String> SKIPPED_DIRS = new HashSet<>(Arrays.asList(
		"saves", "logs", "crash-reports", "screenshots", "stats", "assets", "libraries",
		"natives", "server-resource-packs", "backups", "shaderpacks", ".git", ".fabric", ".mixin.out"));

	private static final int RANK_EXPLICIT = 0;
	private static final int RANK_NAMED = 1;
	private static final int RANK_MODS = 2;
	private static final int RANK_ANYWHERE = 3;

	@FunctionalInterface
	private interface ClassSink {
		void accept(String path, byte[] bytes) throws IOException;
	}

	private abstract static class Source implements Comparable<Source> {
		final File file;
		final int rank;
		final int depth;

		Source(File file, int rank, int depth) {
			this.file = file;
			this.rank = rank;
			this.depth = depth;
		}

		@Override
		public int compareTo(Source other) {
			if (this.rank != other.rank) {
				return Integer.compare(this.rank, other.rank);
			}
			if (this.depth != other.depth) {
				return Integer.compare(this.depth, other.depth);
			}
			return this.file.getPath().compareToIgnoreCase(other.file.getPath());
		}

		abstract boolean isCypress() throws IOException;

		abstract Map<String, byte[]> read(Set<String> wanted) throws IOException;

		abstract void forEachClass(ClassSink sink) throws IOException;
	}

	private static final class ZipSource extends Source {
		ZipSource(File file, int rank, int depth) {
			super(file, rank, depth);
		}

		@Override
		boolean isCypress() throws IOException {
			try (ZipFile zip = new ZipFile(this.file)) {
				for (String entry : SIGNATURE) {
					if (zip.getEntry(entry) == null) {
						return false;
					}
				}
				return true;
			}
		}

		@Override
		Map<String, byte[]> read(Set<String> wanted) throws IOException {
			Map<String, byte[]> found = new HashMap<>();
			try (ZipFile zip = new ZipFile(this.file)) {
				for (String path : wanted) {
					ZipEntry entry = zip.getEntry(path);
					if (entry == null || entry.isDirectory()) {
						continue;
					}
					try (InputStream in = zip.getInputStream(entry)) {
						found.put(path, readFully(in));
					}
				}
			}
			return found;
		}

		@Override
		void forEachClass(ClassSink sink) throws IOException {
			try (ZipFile zip = new ZipFile(this.file)) {
				for (ZipEntry entry : Collections.list(zip.entries())) {
					if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
						try (InputStream in = zip.getInputStream(entry)) {
							sink.accept(entry.getName(), readFully(in));
						}
					}
				}
			}
		}
	}

	private static final class NestedSource extends Source {
		private String innerName;

		NestedSource(File file, int rank, int depth) {
			super(file, rank, depth);
		}

		@Override
		boolean isCypress() throws IOException {
			try (ZipFile zip = new ZipFile(this.file)) {
				for (ZipEntry entry : Collections.list(zip.entries())) {
					String name = entry.getName().toLowerCase(Locale.ROOT);
					if (!entry.isDirectory() && name.endsWith(".jar") && entry.getSize() > MIN_CONTAINER_BYTES) {
						try (InputStream in = zip.getInputStream(entry)) {
							if (streamHasSignature(in)) {
								this.innerName = entry.getName();
								return true;
							}
						}
					}
				}
			}
			return false;
		}

		@Override
		Map<String, byte[]> read(Set<String> wanted) throws IOException {
			Map<String, byte[]> found = new HashMap<>();
			try (ZipFile zip = new ZipFile(this.file)) {
				ZipEntry inner = zip.getEntry(this.innerName);
				if (inner == null) {
					return found;
				}
				try (ZipInputStream in = new ZipInputStream(new BufferedInputStream(zip.getInputStream(inner)))) {
					ZipEntry entry;
					while ((entry = in.getNextEntry()) != null) {
						if (!entry.isDirectory() && wanted.contains(entry.getName())) {
							found.put(entry.getName(), readFully(in));
						}
					}
				}
			}
			return found;
		}

		@Override
		void forEachClass(ClassSink sink) throws IOException {
			try (ZipFile zip = new ZipFile(this.file)) {
				ZipEntry inner = zip.getEntry(this.innerName);
				if (inner == null) {
					return;
				}
				try (ZipInputStream in = new ZipInputStream(new BufferedInputStream(zip.getInputStream(inner)))) {
					ZipEntry entry;
					while ((entry = in.getNextEntry()) != null) {
						if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
							sink.accept(entry.getName(), readFully(in));
						}
					}
				}
			}
		}

		private static boolean streamHasSignature(InputStream raw) throws IOException {
			Set<String> needed = new HashSet<>(Arrays.asList(SIGNATURE));
			ZipInputStream in = new ZipInputStream(new BufferedInputStream(raw));
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				needed.remove(entry.getName());
				if (needed.isEmpty()) {
					return true;
				}
			}
			return false;
		}
	}

	private static final class FolderSource extends Source {
		FolderSource(File file, int rank, int depth) {
			super(file, rank, depth);
		}

		@Override
		boolean isCypress() {
			for (String entry : SIGNATURE) {
				if (!new File(this.file, entry).isFile()) {
					return false;
				}
			}
			return true;
		}

		@Override
		Map<String, byte[]> read(Set<String> wanted) throws IOException {
			Map<String, byte[]> found = new HashMap<>();
			for (String path : wanted) {
				File candidate = new File(this.file, path);
				if (candidate.isFile()) {
					found.put(path, Files.readAllBytes(candidate.toPath()));
				}
			}
			return found;
		}

		@Override
		void forEachClass(ClassSink sink) throws IOException {
			visitClasses(this.file, 0, sink);
		}

		private static void visitClasses(File dir, int depth, ClassSink sink) throws IOException {
			File[] children = dir.listFiles();
			if (children == null || depth > MAX_SCAN_DEPTH) {
				return;
			}
			for (File child : children) {
				if (child.isDirectory()) {
					visitClasses(child, depth + 1, sink);
				} else if (child.getName().endsWith(".class")) {
					sink.accept(child.getName(), Files.readAllBytes(child.toPath()));
				}
			}
		}
	}

	private static Source configuredSource(File gameDir) {
		String configured = AVConfig.CYPRESS_SOURCE == null ? "" : AVConfig.CYPRESS_SOURCE.trim();
		if (configured.isEmpty()) {
			return null;
		}
		File file = new File(configured);
		if (!file.isAbsolute()) {
			file = new File(gameDir, configured);
		}
		try {
			if (file.isDirectory()) {
				FolderSource folder = new FolderSource(file, RANK_EXPLICIT, 0);
				if (folder.isCypress()) {
					return folder;
				}
			} else if (file.isFile() && isZip(file)) {
				ZipSource zip = new ZipSource(file, RANK_EXPLICIT, 0);
				if (zip.isCypress()) {
					return zip;
				}
				NestedSource nested = new NestedSource(file, RANK_EXPLICIT, 0);
				if (nested.isCypress()) {
					return nested;
				}
			}
		} catch (IOException | RuntimeException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not read CYPRESS_SOURCE '{}': {}", configured, e.toString());
			return null;
		}
		AlphaVer.LOGGER.warn("Asset sidecar: CYPRESS_SOURCE '{}' is not a copy of Cypress; searching the game "
			+ "directory instead", configured);
		return null;
	}

	private static Source findSource(File gameDir, File packDir, File lookDir) {
		String rootPath = safePath(gameDir);
		Set<String> ownPacks = new HashSet<>(Arrays.asList(safePath(packDir), safePath(lookDir)));
		List<Source> candidates = new ArrayList<>();

		List<File> current = new ArrayList<>();
		current.add(gameDir);
		int budget = MAX_SCAN_FILES;

		boolean truncated = false;
		for (int depth = 0; depth <= MAX_SCAN_DEPTH && !current.isEmpty() && !truncated; depth++) {
			List<File> next = new ArrayList<>();

			level:
			for (File dir : current) {
				File[] children = dir.listFiles();
				if (children == null) {
					continue;
				}
				for (File child : children) {
					if (budget-- <= 0) {
						AlphaVer.LOGGER.warn("Asset sidecar: stopped searching after {} files at depth {}. If your copy "
							+ "of Cypress is buried deep in a large game directory, move it nearer the top or point "
							+ "CYPRESS_SOURCE in the config at it.", MAX_SCAN_FILES, depth);
						truncated = true;
						break level;
					}
					if (child.isDirectory()) {
						String name = child.getName().toLowerCase(Locale.ROOT);
						if (SKIPPED_DIRS.contains(name) || ownPacks.contains(safePath(child))) {
							continue;
						}
						if (new File(child, "ext1605").isDirectory() && new File(child, "terrain.png").isFile()) {
							candidates.add(new FolderSource(child, rank(rootPath, child), depth));
						}
						next.add(child);
					} else if (child.isFile() && !isOwnJar(child.getName()) && isZip(child)) {
						candidates.add(new ZipSource(child, rank(rootPath, child), depth));
					}
				}
			}
			current = next;
		}

		Collections.sort(candidates);
		int opened = 0;
		for (Source candidate : candidates) {
			if (opened++ >= MAX_CONTAINERS_OPENED) {
				break;
			}
			try {
				if (candidate.isCypress()) {
					return candidate;
				}
				if (candidate instanceof ZipSource) {
					NestedSource nested = new NestedSource(candidate.file, candidate.rank, candidate.depth);
					if (nested.isCypress()) {
						return nested;
					}
				}
			} catch (IOException | RuntimeException e) {

				AlphaVer.LOGGER.debug("Asset sidecar: skipped '{}': {}", candidate.file.getPath(), e.toString());
			}
		}
		return null;
	}

	private static int rank(String rootPath, File file) {
		String relative = relativeTo(rootPath, safePath(file)).toLowerCase(Locale.ROOT);
		if (relative.contains("ext1605")) {
			return RANK_EXPLICIT;
		}
		if (relative.contains("cypress") || relative.contains("alphaver") || relative.contains("1.0.16.05")) {
			return RANK_NAMED;
		}
		if (relative.startsWith("mods/")) {
			return RANK_MODS;
		}
		return RANK_ANYWHERE;
	}

	private static boolean isOwnJar(String name) {
		return name.toLowerCase(Locale.ROOT).matches("^alphaver-\\d+\\.\\d.*");
	}

	private static boolean isZip(File file) {
		if (file.length() < MIN_CONTAINER_BYTES) {
			return false;
		}
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
			return header[0] == 'P' && header[1] == 'K'
				&& ((header[2] == 3 && header[3] == 4) || (header[2] == 5 && header[3] == 6) || (header[2] == 7 && header[3] == 8));
		} catch (IOException e) {
			return false;
		}
	}

	private static void overlayVisuals(Map<String, byte[]> entries, byte[] visualsZip) {
		int replaced = 0;
		try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(visualsZip))) {
			ZipEntry entry;
			while ((entry = in.getNextEntry()) != null) {
				if (!entry.isDirectory() && entries.containsKey(entry.getName())) {
					entries.put(entry.getName(), readFully(in));
					replaced++;
				}
			}
		} catch (IOException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not read the 16.04 visuals, using the standard ones: {}",
				e.toString());
			return;
		}
		AlphaVer.LOGGER.info("Asset sidecar: 16.04 legacy visuals supplied {} file(s)", replaced);
	}

	private static int writeFiles(Map<String, List<String>> manifest, Map<String, byte[]> entries, File gameDir,
	                              File packDir, List<String> missing) throws IOException {
		int written = 0;
		for (Map.Entry<String, List<String>> mapping : manifest.entrySet()) {
			byte[] bytes = entries.get(mapping.getKey());
			if (bytes == null) {
				missing.add(mapping.getKey());
				continue;
			}
			for (String path : mapping.getValue()) {
				writeCopy(destination(gameDir, packDir, path), path, bytes);
				written++;
			}
		}
		return written;
	}

	private static final String PAINTING_SHEET = "art/kz.png";

	private static int writePaintings(Map<String, byte[]> entries, File packDir, List<String> missing) throws IOException {
		byte[] bytes = entries.get(PAINTING_SHEET);
		BufferedImage sheet = bytes == null ? null : decode(bytes);
		if (sheet == null) {
			missing.add(PAINTING_SHEET);
			return 0;
		}
		double scale = sheet.getWidth() / (double) AVPaintings.SHEET_WIDTH;
		int written = 0;
		for (AVPaintings.Painting painting : AVPaintings.TABLE) {
			int x = (int) Math.round(painting.u() * scale);
			int y = (int) Math.round(painting.v() * scale);
			int w = Math.max(1, (int) Math.round(painting.width() * scale));
			int h = Math.max(1, (int) Math.round(painting.height() * scale));
			if (x + w > sheet.getWidth() || y + h > sheet.getHeight()) {
				AlphaVer.LOGGER.warn("Asset sidecar: painting '{}' lies outside {}; skipped.", painting.name(), PAINTING_SHEET);
				continue;
			}
			writePng(new File(packDir, painting.file()), CypressLookArt.region(sheet, x, y, w, h));
			written++;
		}
		return written;
	}

	private static void writeCopy(File target, String path, byte[] bytes) throws IOException {
		if (SKY_GLOW.contains(path)) {
			BufferedImage sky = decode(bytes);
			if (sky != null) {
				writePng(target, NEBULA_GLOW.contains(path)
					? CypressLookArt.taperGlow(sky, CypressLookArt.NEBULA_INNER)
					: CypressLookArt.taperGlow(sky));
				return;
			}
		}
		write(target, bytes);
	}

	private static final String GAME_DIR_PREFIX = "resources/";

	private static final String SOUND_ROOT = "resources/alphaver/sounds";

	private static final String SOUND_INDEX_TEMPLATE = "/assets/alphaver/sidecar/sounds.json";

	private static final String SOUND_MARKER = ".sidecar";

	private static File destination(File gameDir, File packDir, String path) {
		return path.startsWith(GAME_DIR_PREFIX) ? new File(gameDir, path) : new File(packDir, path);
	}

	private static void writeSoundIndex(File gameDir, File packDir) throws IOException {
		File soundRoot = new File(gameDir, SOUND_ROOT);
		File index = new File(packDir, "assets/alphaver/sounds/sounds.json");
		if (!soundRoot.isDirectory()) {
			if (index.isFile() && !index.delete()) {
				AlphaVer.LOGGER.warn("Asset sidecar: could not remove a sound index with no sounds behind it: {}", index);
			}
			return;
		}
		try (InputStream in = AVAssetSidecar.class.getResourceAsStream(SOUND_INDEX_TEMPLATE)) {
			if (in == null) {
				AlphaVer.LOGGER.warn("Asset sidecar: {} is missing from the mod jar; Cypress's sounds will be silent",
					SOUND_INDEX_TEMPLATE);
				return;
			}
			write(index, readFully(in));
		}
		write(new File(soundRoot, SOUND_MARKER), Stamp.header().getBytes(StandardCharsets.UTF_8));
	}

	private static boolean gameDirOutputsPresent(Manifests manifests, File gameDir) {
		for (List<String> paths : manifests.files.values()) {
			for (String path : paths) {
				if (path.startsWith(GAME_DIR_PREFIX)) {
					return new File(new File(gameDir, SOUND_ROOT), SOUND_MARKER).isFile();
				}
			}
		}
		return true;
	}

	private static final String[][] LORE = {
		{"Hours Long Past I \n", "hours_long_past_1"},
		{"Hours Long Past II \n", "hours_long_past_2"},
		{"Hours Long Past III \n", "hours_long_past_3"},
		{"Hours Long Past IV \n", "hours_long_past_4"},
		{"The One True Book \n", "the_one_true_book"},
	};

	private static final byte[] LORE_MARKER_HLP = "Hours Long Past ".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
	private static final byte[] LORE_MARKER_BOOK = "The One True Book ".getBytes(java.nio.charset.StandardCharsets.US_ASCII);

	private static int writeLore(Source source, File packDir) throws IOException {
		Map<String, String> found = new LinkedHashMap<>();
		try {
			source.forEachClass((path, bytes) -> {
				if (found.size() == LORE.length || (indexOf(bytes, LORE_MARKER_HLP) < 0 && indexOf(bytes, LORE_MARKER_BOOK) < 0)) {
					return;
				}
				for (String constant : utf8Constants(bytes)) {
					for (String[] lore : LORE) {
						if (constant.startsWith(lore[0])) {
							found.putIfAbsent(lore[1], constant);
						}
					}
				}
			});
		} catch (IOException | RuntimeException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not scan {} for the lore text: {}", sourceLabel, e.toString());
		}

		int written = 0;
		for (Map.Entry<String, String> lore : found.entrySet()) {
			File target = new File(packDir, "assets/alphaver/lore/" + lore.getKey() + ".txt");
			File parent = target.getParentFile();
			if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
				throw new IOException("could not create " + parent);
			}
			Files.write(target.toPath(), lore.getValue().getBytes(java.nio.charset.StandardCharsets.UTF_8));
			written++;
		}
		if (found.size() < LORE.length) {
			AlphaVer.LOGGER.warn("Asset sidecar: found the text of {} of {} lore items in {}; the rest will show their titles",
				found.size(), LORE.length, sourceLabel);
		}
		return written;
	}

	private static List<String> utf8Constants(byte[] bytes) {
		List<String> strings = new ArrayList<>();
		try (java.io.DataInputStream in = new java.io.DataInputStream(new ByteArrayInputStream(bytes))) {
			if (in.readInt() != 0xCAFEBABE) {
				return strings;
			}
			in.readUnsignedShort();
			in.readUnsignedShort();
			int count = in.readUnsignedShort();
			for (int slot = 1; slot < count; slot++) {
				int tag = in.readUnsignedByte();
				switch (tag) {
					case 1 -> strings.add(in.readUTF());
					case 3, 4 -> in.skipBytes(4);
					case 5, 6 -> {
						in.skipBytes(8);
						slot++;
					}
					case 7, 8, 16, 19, 20 -> in.skipBytes(2);
					case 9, 10, 11, 12, 17, 18 -> in.skipBytes(4);
					case 15 -> in.skipBytes(3);
					default -> {
						return strings;
					}
				}
			}
		} catch (IOException e) {

		}
		return strings;
	}

	private static int indexOf(byte[] haystack, byte[] needle) {
		outer:
		for (int i = 0; i + needle.length <= haystack.length; i++) {
			for (int j = 0; j < needle.length; j++) {
				if (haystack[i + j] != needle[j]) {
					continue outer;
				}
			}
			return i;
		}
		return -1;
	}

	private static Recipe tileRecipe(String key) {
		return Recipe.parse(key.startsWith("region:") ? key : "tile:" + key);
	}

	private static int writeTiles(Map<String, List<String>> manifest, Map<String, byte[]> entries, File packDir,
	                              List<String> missing) throws IOException {
		int written = 0;
		SheetCache sheets = new SheetCache(entries);
		for (Map.Entry<String, List<String>> mapping : manifest.entrySet()) {
			Recipe recipe = tileRecipe(mapping.getKey());
			if (recipe == null) {
				continue;
			}
			BufferedImage image = recipe.render(sheets, missing);
			if (image == null) {
				continue;
			}
			for (String path : mapping.getValue()) {
				writePng(new File(packDir, path), image);
				written++;
			}
		}
		return written;
	}

	private static final String CARVED_DOOR_ICONS = "assets/alphaver/textures/block/door_icon/carved/";

	private static int writeCarvedDoorIcons(Map<String, byte[]> entries, File packDir) throws IOException {

		List<String> ignored = new ArrayList<>();
		SheetCache sheets = new SheetCache(entries);
		BufferedImage cypressDoor = cut(sheets, "tile:terrain.png#129@scale=16x16", ignored);
		BufferedImage cypressIron = cut(sheets, "tile:terrain.png#98@scale=16x16", ignored);
		BufferedImage btaIron = btaImage(TEXTURES + "block/door/iron/bottom.png");
		BufferedImage sapling = cut(sheets, "tile:terrain.png#15@scale=16x16", ignored);
		BufferedImage zombie = cut(sheets, "region:mob/zombie.png@8,8,8,8@scale=16x16", ignored);
		BufferedImage chevrons = btaImage("assets/alphaver/textures/block/door_icon/freerun.png");

		int count = 0;
		if (cypressDoor != null && sapling != null) {
			writePng(new File(packDir, CARVED_DOOR_ICONS + "cypress_sapling.png"), DoorIconCarving.carve(cypressDoor, sapling, false));
			count++;
		}
		count += writeCarvedIronDoorIcon(packDir, "zombies", cypressIron, zombie);
		count += writeCarvedIronDoorIcon(packDir, "freerun", cypressIron, chevrons);
		count += writeCarvedIronDoorIcon(packDir, "zombies_bta", btaIron, zombie);
		count += writeCarvedIronDoorIcon(packDir, "freerun_bta", btaIron, chevrons);
		return count;
	}

	private static int writeCarvedIronDoorIcon(File packDir, String name, BufferedImage door, BufferedImage picture) throws IOException {
		if (door == null || picture == null) {
			return 0;
		}
		writePng(new File(packDir, CARVED_DOOR_ICONS + name + "_west.png"), DoorIconCarving.carve(door, picture, false));
		writePng(new File(packDir, CARVED_DOOR_ICONS + name + "_east.png"), DoorIconCarving.carve(door, picture, true));
		return 2;
	}

	private static BufferedImage cut(SheetCache sheets, String recipe, List<String> missing) {
		Recipe parsed = Recipe.parse(recipe);
		return parsed == null ? null : parsed.render(sheets, missing);
	}

	private static int writeLook(Map<String, List<String>> manifest, Map<String, byte[]> entries, File lookDir,
	                             List<String> missing) throws IOException {
		int written = 0;
		Set<String> writtenPaths = new HashSet<>();
		SheetCache sheets = new SheetCache(entries);
		for (Map.Entry<String, List<String>> mapping : manifest.entrySet()) {
			Recipe recipe = Recipe.parse(mapping.getKey());
			if (recipe == null) {
				AlphaVer.LOGGER.warn("Asset sidecar: unreadable look recipe '{}'", mapping.getKey());
				continue;
			}
			if (recipe.kind == Recipe.Kind.COPY) {
				byte[] bytes = entries.get(recipe.sheet);
				if (bytes == null) {
					missing.add(recipe.sheet);
					continue;
				}
				for (String path : mapping.getValue()) {
					writeCopy(new File(lookDir, path), path, bytes);
					writtenPaths.add(path);
					written++;
				}
				continue;
			}
			BufferedImage image = recipe.render(sheets, missing);
			if (image == null) {
				continue;
			}
			for (String path : mapping.getValue()) {
				writePng(new File(lookDir, path), image);
				writtenPaths.add(path);
				written++;
			}
		}
		return written + writeLookExtras(sheets, lookDir, writtenPaths, missing);
	}

	private static final String TEXTURES = "assets/minecraft/textures/";

	private static final String[] NINE_SLICE_WIDGETS = {
		"gui/sprites/widgets/button/button.png",
		"gui/sprites/widgets/button/button_highlighted.png",
		"gui/sprites/widgets/button/button_disabled.png",
		"gui/sprites/widgets/slider/slider_background.png",
	};

	private static final String NINE_SLICE_MCMETA =
		"{\"gui\":{\"scaling\":{\"type\":\"nine_slice\",\"width\":200,\"height\":20,\"border\":3}}}\n";

	private static final String FONT_PAGE = "gui/font/default/font_00.png";
	private static final String GLYPH_SIZES = "gui/font/default/glyph_sizes.bin";
	private static final String CYPRESS_FONT = "default.png";

	private static int writeLookExtras(SheetCache sheets, File lookDir, Set<String> written, List<String> missing)
		throws IOException {
		int count = 0;
		for (String widget : NINE_SLICE_WIDGETS) {
			if (written.contains(TEXTURES + widget)) {
				write(new File(lookDir, TEXTURES + widget + ".mcmeta"), NINE_SLICE_MCMETA.getBytes(StandardCharsets.UTF_8));
				count++;
			}
		}
		if (written.contains(TEXTURES + FONT_PAGE)) {
			byte[] sizes = glyphSizes(sheets.get(CYPRESS_FONT, missing));
			if (sizes != null) {
				write(new File(lookDir, TEXTURES + GLYPH_SIZES), sizes);
				count++;
			}
		}

		for (String metal : METAL_BLOCKS) {
			if (written.contains(TEXTURES + "block/" + metal + "_top.png") && written.contains(TEXTURES + "block/" + metal + "_bottom.png")) {
				writeText(new File(lookDir, BLOCK_MODELS + metal + ".json"), "{\"parent\":\"minecraft:block/cube_bottom_top\","
					+ "\"textures\":{\"top\":\"minecraft:block/" + metal + "_top\",\"bottom\":\"minecraft:block/" + metal
					+ "_bottom\",\"side\":\"minecraft:block/" + metal + "\"}}\n");
				count++;
			}
		}

		if (written.contains(TEXTURES + "block/bookshelf_top.png")) {
			writeText(new File(lookDir, BLOCK_MODELS + "bookshelf.json"), "{\"parent\":\"minecraft:block/cube_column\","
				+ "\"textures\":{\"end\":\"minecraft:block/bookshelf_top\",\"side\":\"minecraft:block/bookshelf\"}}\n");
			count++;
		}

		for (String state : PRESSURE_PLATE_STATES) {
			writeText(new File(lookDir, BLOCK_MODELS + "pressure_plate/planks/oak/" + state + ".json"),
				"{\"parent\":\"minecraft:block/pressure_plate/" + state + "\",\"textures\":{\"texture\":\"minecraft:block/log/oak_side\"}}\n");
			count++;
		}

		BufferedImage stars = new BufferedImage(STARS_WIDTH, STARS_HEIGHT, BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < STARS_WIDTH; x++) {
			for (int y = 0; y < STARS_HEIGHT; y++) {
				stars.setRGB(x, y, 0xFFFFFFFF);
			}
		}
		writePng(new File(lookDir, TEXTURES + "colormap/stars/default.png"), stars);
		count++;

		count += writeCypressGui(sheets, lookDir, written, missing);
		return count;
	}

	private static final Set<String> SKY_GLOW = Set.of(
		"assets/alphaver/textures/environment/sun.png",
		"assets/alphaver/textures/environment/sun_nebula.png",
		"assets/alphaver/textures/environment/moon.png",
		"assets/alphaver/textures/environment/moon_nebula.png",
		TEXTURES + "terrain/sun.png",
		TEXTURES + "terrain/moon.png");

	private static final Set<String> NEBULA_GLOW = Set.of(
		"assets/alphaver/textures/environment/sun_nebula.png",
		"assets/alphaver/textures/environment/moon_nebula.png");

	private static final String SPRITES = "gui/sprites/";

	private static final String[] ICON_BUTTONS = {
		"misc/button_support", "misc/button_support_highlighted",
		"misc/button_language", "misc/button_language_highlighted",
		"misc/button_folder", "misc/button_folder_highlighted", "misc/button_folder_disabled",
		"misc/button_random", "misc/button_random_highlighted",
		"misc/button_reset", "misc/button_reset_highlighted",
		"misc/button_search", "misc/button_search_highlighted",
		"misc/button_flag_flip", "misc/button_flag_flip_highlighted", "misc/button_flag_flip_disabled",
		"widgets/button/button_locked", "widgets/button/button_locked_highlighted", "widgets/button/button_locked_disabled",
		"screen/creative/clear", "screen/creative/clear_highlighted",
		"screen/options/clear", "screen/options/clear_highlighted",
	};

	private static final String[] RECOLOURED_SCREENS = {"dispenser", "trommel", "blastfurnace", "activator", "flag_editor"};

	private static int writeCypressGui(SheetCache sheets, File lookDir, Set<String> written, List<String> missing) throws IOException {
		int count = 0;
		BufferedImage gui = sheets.get("gui/gui.png", missing);
		if (gui != null && gui.getWidth() == 256) {
			BufferedImage normal = CypressLookArt.region(gui, 0, CypressLookArt.ROW_NORMAL, 200, 20);
			BufferedImage highlighted = CypressLookArt.region(gui, 0, CypressLookArt.ROW_HIGHLIGHTED, 200, 20);
			BufferedImage disabled = CypressLookArt.region(gui, 0, CypressLookArt.ROW_DISABLED, 200, 20);

			count += writeWidget(lookDir, "widgets/slider/slider", CypressLookArt.squeeze(normal, 8));
			count += writeWidget(lookDir, "widgets/slider/slider_highlighted", CypressLookArt.squeeze(highlighted, 8));
			count += writeWidget(lookDir, "widgets/slider/slider_disabled", CypressLookArt.squeeze(disabled, 8));
			count += writeWidget(lookDir, "widgets/switch/switch_background", disabled);
			count += writeWidget(lookDir, "widgets/switch/switch", normal);
			count += writeWidget(lookDir, "widgets/switch/switch_highlighted", highlighted);
			count += writeWidget(lookDir, "widgets/switch/switch_disabled", disabled);

			BufferedImage btaNormal = btaImage(TEXTURES + SPRITES + "widgets/button/button.png");
			BufferedImage btaHighlighted = btaImage(TEXTURES + SPRITES + "widgets/button/button_highlighted.png");
			BufferedImage btaDisabled = btaImage(TEXTURES + SPRITES + "widgets/button/button_disabled.png");
			if (btaNormal != null && btaHighlighted != null && btaDisabled != null) {
				for (String sprite : ICON_BUTTONS) {
					BufferedImage icon = btaImage(TEXTURES + SPRITES + sprite + ".png");
					if (icon == null) {
						continue;
					}
					boolean isHighlighted = sprite.endsWith("_highlighted");
					boolean isDisabled = sprite.endsWith("_disabled");
					BufferedImage themed = CypressLookArt.iconButton(icon,
						isHighlighted ? btaHighlighted : isDisabled ? btaDisabled : btaNormal,
						isHighlighted ? highlighted : isDisabled ? disabled : normal);
					if (themed != null) {
						writePng(new File(lookDir, TEXTURES + SPRITES + sprite + ".png"), themed);
						count++;
					}
				}
			}

			for (int hotbar = 0; hotbar < 4; hotbar++) {
				writePng(new File(lookDir, TEXTURES + SPRITES + "hud/hotbar_selector" + hotbar + ".png"), CypressLookArt.hotbarTab(gui, hotbar));
				count++;
			}
			writePng(new File(lookDir, TEXTURES + SPRITES + "hud/hotbar_selector.png"), CypressLookArt.hotbarTabSheet(gui));
			count++;
		}

		BufferedImage inventory = sheets.get("gui/inventory.png", missing);
		if (inventory != null && inventory.getWidth() == 256 && inventory.getHeight() == 256) {
			BufferedImage btaInventory = btaImage(TEXTURES + "gui/container/inventory.png");
			if (btaInventory != null && written.contains(TEXTURES + "gui/container/inventory.png")) {
				writePng(new File(lookDir, TEXTURES + "gui/container/inventory.png"), CypressLookArt.withBtaSprites(inventory, btaInventory, 176));
				count++;
			}
			BufferedImage btaCreative = btaImage(TEXTURES + "gui/container/creative.png");
			if (btaCreative != null && btaCreative.getWidth() >= 298 && btaCreative.getHeight() >= 166) {
				writePng(new File(lookDir, TEXTURES + "gui/container/creative.png"), CypressLookArt.creative(inventory, btaCreative));
				count++;
			}
			int panel = inventory.getRGB(100, 20);
			int frame = inventory.getRGB(0, 40);
			int well = inventory.getRGB(20, 40);
			for (String screen : RECOLOURED_SCREENS) {
				BufferedImage bta = btaImage(TEXTURES + "gui/container/" + screen + ".png");
				if (bta != null) {
					writePng(new File(lookDir, TEXTURES + "gui/container/" + screen + ".png"), CypressLookArt.recolour(bta, panel, frame, well));
					count++;
				}
			}
		}
		return count;
	}

	private static int writeWidget(File lookDir, String sprite, BufferedImage image) throws IOException {
		writePng(new File(lookDir, TEXTURES + SPRITES + sprite + ".png"), image);
		writeText(new File(lookDir, TEXTURES + SPRITES + sprite + ".png.mcmeta"),
			"{\"gui\":{\"scaling\":{\"type\":\"nine_slice\",\"width\":" + image.getWidth() + ",\"height\":" + image.getHeight()
				+ ",\"border\":3}}}\n");
		return 1;
	}

	private static BufferedImage btaImage(String path) {
		try (InputStream in = AVAssetSidecar.class.getResourceAsStream("/" + path)) {
			return in == null ? null : ImageIO.read(in);
		} catch (IOException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not read BTA's {}: {}", path, e.toString());
			return null;
		}
	}

	private static BufferedImage decode(byte[] bytes) {
		try {
			return ImageIO.read(new ByteArrayInputStream(bytes));
		} catch (IOException e) {
			return null;
		}
	}

	private static final String BLOCK_MODELS = "assets/minecraft/models/block/";
	private static final String[] METAL_BLOCKS = {"block_gold", "block_iron", "block_diamond"};
	private static final String[] PRESSURE_PLATE_STATES = {"idle", "active", "inventory"};

	private static final int STARS_WIDTH = 512;
	private static final int STARS_HEIGHT = 16;

	private static void writeText(File target, String text) throws IOException {
		write(target, text.getBytes(StandardCharsets.UTF_8));
	}

	private static byte[] glyphSizes(BufferedImage font) throws IOException {
		if (font == null) {
			return null;
		}
		byte[] sizes;
		try (InputStream in = AVAssetSidecar.class.getResourceAsStream("/" + TEXTURES + GLYPH_SIZES)) {
			if (in == null) {
				AlphaVer.LOGGER.warn("Asset sidecar: BTA's {} was not found; Cypress's font keeps BTA's widths", GLYPH_SIZES);
				return null;
			}
			sizes = readFully(in);
		}
		int cell = font.getWidth() / 16;
		if (cell <= 0) {
			return null;
		}
		for (int index = 0x21; index <= 0xAF && index < sizes.length; index++) {
			int cellX = (index & 15) * cell;
			int cellY = (index >> 4) * cell;
			int rightmost = -1;
			for (int column = cell - 1; column >= 0 && rightmost < 0; column--) {
				for (int row = 0; row < cell; row++) {
					if ((font.getRGB(cellX + column, cellY + row) & 0xFF) > 0) {
						rightmost = column;
						break;
					}
				}
			}
			if (rightmost < 0) {
				continue;
			}
			int last = Math.min(15, (rightmost + 1) * 16 / cell - 1);
			sizes[index] = (byte) last;
		}
		return sizes;
	}

	private static final class SheetCache {
		private final Map<String, byte[]> entries;
		private final Map<String, BufferedImage> decoded = new HashMap<>();
		private final Set<String> failed = new HashSet<>();

		SheetCache(Map<String, byte[]> entries) {
			this.entries = entries;
		}

		BufferedImage get(String path, List<String> missing) {
			BufferedImage image = this.decoded.get(path);
			if (image != null || this.failed.contains(path)) {
				return image;
			}
			byte[] bytes = this.entries.get(path);
			if (bytes == null) {
				this.failed.add(path);
				missing.add(path);
				return null;
			}
			try {
				image = ImageIO.read(new ByteArrayInputStream(bytes));
			} catch (IOException e) {
				image = null;
			}
			if (image == null) {
				this.failed.add(path);
				AlphaVer.LOGGER.warn("Asset sidecar: {} could not be decoded as an image", path);
				return null;
			}
			this.decoded.put(path, image);
			return image;
		}
	}

	static final class Recipe {
		enum Kind { COPY, TILE, REGION }

		final Kind kind;
		final String sheet;
		final int index;
		final int x;
		final int y;
		final int w;
		final int h;
		final int scaleW;
		final int scaleH;

		final int keepX;
		final int keepY;
		final int keepW;
		final int keepH;

		final double shade;

		final String emissive;

		private Recipe(Kind kind, String sheet, int index, int x, int y, int w, int h, int scaleW, int scaleH, int[] keep,
		               double shade, String emissive) {
			this.kind = kind;
			this.sheet = sheet;
			this.index = index;
			this.x = x;
			this.y = y;
			this.w = w;
			this.h = h;
			this.scaleW = scaleW;
			this.scaleH = scaleH;
			this.keepX = keep == null ? 0 : keep[0];
			this.keepY = keep == null ? 0 : keep[1];
			this.keepW = keep == null ? -1 : keep[2];
			this.keepH = keep == null ? -1 : keep[3];
			this.shade = shade;
			this.emissive = emissive;
		}

		static Recipe parse(String key) {
			String spec = key.trim();
			int scaleW = -1;
			int scaleH = -1;
			int[] keep = null;
			double shade = 1.0;
			String emissive = null;

			try {
				while (true) {
					int at = spec.lastIndexOf('@');
					if (at < 0) {
						break;
					}
					String modifier = spec.substring(at + 1).trim();
					if (modifier.startsWith("scale=")) {
						String[] dims = modifier.substring("scale=".length()).split("x");
						scaleW = Integer.parseInt(dims[0].trim());
						scaleH = Integer.parseInt(dims[1].trim());
					} else if (modifier.startsWith("keep=")) {
						String[] parts = modifier.substring("keep=".length()).split(",");
						keep = new int[]{Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
							Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim())};
					} else if (modifier.startsWith("shade=")) {
						shade = Double.parseDouble(modifier.substring("shade=".length()).trim());
					} else if (modifier.startsWith("emissive=")) {
						emissive = modifier.substring("emissive=".length()).trim();
					} else {
						break;
					}
					spec = spec.substring(0, at);
				}

				if (spec.startsWith("copy:")) {
					return new Recipe(Kind.COPY, spec.substring(5), -1, 0, 0, 0, 0, -1, -1, null, 1.0, null);
				}
				if (spec.startsWith("tile:")) {
					int hash = spec.lastIndexOf('#');
					return new Recipe(Kind.TILE, spec.substring(5, hash), Integer.parseInt(spec.substring(hash + 1).trim()),
						0, 0, 0, 0, scaleW, scaleH, keep, shade, emissive);
				}
				if (spec.startsWith("region:")) {
					int at = spec.lastIndexOf('@');
					String[] parts = spec.substring(at + 1).split(",");
					return new Recipe(Kind.REGION, spec.substring(7, at), -1,
						Integer.parseInt(parts[0].trim()), Integer.parseInt(parts[1].trim()),
						Integer.parseInt(parts[2].trim()), Integer.parseInt(parts[3].trim()), scaleW, scaleH, keep, shade, emissive);
				}
			} catch (RuntimeException e) {
				return null;
			}
			return null;
		}

		private static final Map<String, Integer> NATIVE_WIDTHS = Map.of("default.png", 128, "particles.png", 128, "art/kz.png", 512,

			"mob/zombie.png", 64);
		private static final int DEFAULT_NATIVE_WIDTH = 256;

		BufferedImage render(SheetCache sheets, List<String> missing) {
			BufferedImage sheetImage = sheets.get(this.sheet, missing);
			if (sheetImage == null) {
				return null;
			}

			int sx;
			int sy;
			int sw;
			int sh;
			if (this.kind == Kind.TILE) {
				int tile = sheetImage.getWidth() / 16;
				sx = (this.index & 15) * tile;
				sy = (this.index >> 4) * tile;
				sw = tile;
				sh = tile;
			} else {

				double factor = sheetImage.getWidth() / (double) NATIVE_WIDTHS.getOrDefault(this.sheet, DEFAULT_NATIVE_WIDTH);
				sx = (int) Math.round(this.x * factor);
				sy = (int) Math.round(this.y * factor);
				sw = (int) Math.round(this.w * factor);
				sh = (int) Math.round(this.h * factor);
			}

			if (sw <= 0 || sh <= 0 || sx + sw > sheetImage.getWidth() || sy + sh > sheetImage.getHeight()) {
				AlphaVer.LOGGER.warn("Asset sidecar: {} lies outside {} ({}x{})", this.describe(), this.sheet,
					sheetImage.getWidth(), sheetImage.getHeight());
				return null;
			}

			int outW = this.scaleW > 0 ? this.scaleW : sw;
			int outH = this.scaleH > 0 ? this.scaleH : sh;

			BufferedImage out = new BufferedImage(outW, outH, BufferedImage.TYPE_INT_ARGB);
			for (int oy = 0; oy < outH; oy++) {
				int srcY = sy + oy * sh / outH;
				for (int ox = 0; ox < outW; ox++) {
					int srcX = sx + ox * sw / outW;
					out.setRGB(ox, oy, sheetImage.getRGB(srcX, srcY));
				}
			}
			this.keepAndShade(out, this.kind == Kind.TILE ? 16 : this.w, this.kind == Kind.TILE ? 16 : this.h);
			if (this.emissive != null && !toEmissive(out, this.emissive)) {
				AlphaVer.LOGGER.warn("Asset sidecar: unknown emissive rule '{}' on {} of {}", this.emissive, this.describe(), this.sheet);
				return null;
			}
			return out;
		}

		private void keepAndShade(BufferedImage out, int nativeW, int nativeH) {
			if (this.keepW < 0 && this.shade == 1.0) {
				return;
			}
			double fx = out.getWidth() / (double) nativeW;
			double fy = out.getHeight() / (double) nativeH;
			int left = this.keepW < 0 ? 0 : (int) Math.round(this.keepX * fx);
			int top = this.keepW < 0 ? 0 : (int) Math.round(this.keepY * fy);
			int right = this.keepW < 0 ? out.getWidth() : (int) Math.round((this.keepX + this.keepW) * fx);
			int bottom = this.keepW < 0 ? out.getHeight() : (int) Math.round((this.keepY + this.keepH) * fy);
			for (int py = 0; py < out.getHeight(); py++) {
				for (int px = 0; px < out.getWidth(); px++) {
					if (px < left || px >= right || py < top || py >= bottom) {
						out.setRGB(px, py, 0);
						continue;
					}
					if (this.shade != 1.0) {
						int argb = out.getRGB(px, py);
						int r = Math.min(255, (int) Math.round(((argb >> 16) & 0xFF) * this.shade));
						int g = Math.min(255, (int) Math.round(((argb >> 8) & 0xFF) * this.shade));
						int b = Math.min(255, (int) Math.round((argb & 0xFF) * this.shade));
						out.setRGB(px, py, (argb & 0xFF000000) | (r << 16) | (g << 8) | b);
					}
				}
			}
		}

		private static final int EMISSIVE_ON = 0xFFFF0000;
		private static final int EMISSIVE_OFF = 0xFF000000;

		static boolean toEmissive(BufferedImage out, String rule) {
			String kind = rule;
			String argument = "";
			int colon = rule.indexOf(':');
			if (colon >= 0) {
				kind = rule.substring(0, colon).trim();
				argument = rule.substring(colon + 1).trim();
			}
			float threshold = 0.0F;
			float hueLow = 0.0F;
			float hueHigh = 0.0F;
			try {
				switch (kind) {
					case "all":
					case "petals":
					case "pale":
						break;
					case "bright":
					case "saturated":
						threshold = Float.parseFloat(argument);
						break;
					case "hue":
						String[] range = argument.split("-");
						hueLow = Float.parseFloat(range[0].trim());
						hueHigh = Float.parseFloat(range[1].trim());
						break;
					default:
						return false;
				}
			} catch (RuntimeException e) {
				return false;
			}

			float[] hsb = new float[3];
			for (int py = 0; py < out.getHeight(); py++) {
				for (int px = 0; px < out.getWidth(); px++) {
					int argb = out.getRGB(px, py);
					boolean glows = false;
					if ((argb >>> 24) >= 128) {
						java.awt.Color.RGBtoHSB((argb >> 16) & 0xFF, (argb >> 8) & 0xFF, argb & 0xFF, hsb);
						float hue = hsb[0] * 360.0F;
						float saturation = hsb[1];
						float value = hsb[2];
						switch (kind) {
							case "all":
								glows = true;
								break;
							case "petals":
								glows = !(hue >= 70.0F && hue <= 165.0F && saturation >= 0.35F);
								break;
							case "pale":
								glows = saturation < 0.35F && value >= 0.85F;
								break;
							case "bright":
								glows = value >= threshold;
								break;
							case "saturated":
								glows = saturation >= threshold;
								break;
							default:
								glows = hue >= hueLow && hue <= hueHigh && saturation >= 0.5F;
								break;
						}
					}
					out.setRGB(px, py, glows ? EMISSIVE_ON : EMISSIVE_OFF);
				}
			}
			return true;
		}

		String describe() {
			switch (this.kind) {
				case TILE:
					return "tile " + this.index;
				case REGION:
					return "region " + this.x + "," + this.y + "," + this.w + "," + this.h;
				default:
					return this.sheet;
			}
		}
	}

	private static final class Manifests {
		final Map<String, List<String>> files;
		final Map<String, List<String>> tiles;
		final Map<String, List<String>> look;

		private Manifests(Map<String, List<String>> files, Map<String, List<String>> tiles,
		                  Map<String, List<String>> look) {
			this.files = files;
			this.tiles = tiles;
			this.look = look;
		}

		static Manifests load() {

			Map<String, List<String>> tiles = new LinkedHashMap<>();
			for (String manifest : new String[]{TILE_MANIFEST, ITEM_MANIFEST}) {
				for (Map.Entry<String, List<String>> mapping : readManifest(manifest).entrySet()) {
					tiles.computeIfAbsent(mapping.getKey(), key -> new ArrayList<>()).addAll(mapping.getValue());
				}
			}
			return new Manifests(readManifest(FILE_MANIFEST), tiles, readManifest(LOOK_MANIFEST));
		}

		boolean isEmpty() {
			return this.files.isEmpty() && this.tiles.isEmpty() && this.look.isEmpty();
		}

		Set<String> wantedPaths() {
			Set<String> wanted = new TreeSet<>(this.files.keySet());
			for (String key : this.tiles.keySet()) {
				Recipe recipe = tileRecipe(key);
				if (recipe != null) {
					wanted.add(recipe.sheet);
				}
			}
			for (String key : this.look.keySet()) {
				Recipe recipe = Recipe.parse(key);
				if (recipe != null) {
					wanted.add(recipe.sheet);
				}
			}
			if (AVConfig.VISUALS_1604) {
				wanted.add(VISUALS_1604);
			}
			return wanted;
		}
	}

	private static Map<String, List<String>> readManifest(String resource) {
		Map<String, List<String>> map = new LinkedHashMap<>();
		try (InputStream in = AVAssetSidecar.class.getResourceAsStream(resource)) {
			if (in == null) {
				return map;
			}
			BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			String line;
			int number = 0;
			while ((line = reader.readLine()) != null) {
				number++;
				String trimmed = line.trim();
				if (trimmed.isEmpty() || trimmed.startsWith("#") || trimmed.startsWith("!")) {
					continue;
				}
				int split = trimmed.indexOf(" = ");
				if (split < 0) {
					AlphaVer.LOGGER.warn("Asset sidecar: {} line {} has no ' = ' separator; skipped", resource, number);
					continue;
				}
				List<String> paths = map.computeIfAbsent(trimmed.substring(0, split).trim(), key -> new ArrayList<>());
				for (String path : trimmed.substring(split + 3).split(",")) {
					String value = path.trim();
					if (!value.isEmpty()) {
						paths.add(value);
					}
				}
			}
		} catch (IOException e) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not read manifest {}: {}", resource, e.toString());
		}
		map.values().removeIf(List::isEmpty);
		return map;
	}

	private static final class Stamp {
		final String label;
		final String line;

		Stamp(String label, File source, File gameDir) {
			this.label = label;
			this.line = sourceLine(gameDir, source);
		}

		private Stamp(String label, String line) {
			this.label = label;
			this.line = line;
		}

		static String header() {
			return SIDECAR_REVISION + (AVConfig.VISUALS_1604 ? " visuals=1604" : " visuals=standard");
		}

		static String sourceLine(File gameDir, File file) {
			return relativePath(gameDir, file) + "|" + file.length() + "|" + file.lastModified();
		}

		static Stamp read(File packDir) {
			File file = new File(packDir, STAMP);
			if (!file.isFile()) {
				return null;
			}
			try {
				List<String> lines = Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
				if (lines.size() < 3 || !lines.get(0).trim().equals(header())) {
					return null;
				}
				return new Stamp(lines.get(1).trim(), lines.get(2).trim());
			} catch (IOException | RuntimeException e) {
				return null;
			}
		}

		boolean stillValid(File gameDir) {
			int split = this.line.indexOf('|');
			if (split < 0) {
				return false;
			}

			String path = this.line.substring(0, split);
			File file = new File(path);
			if (!file.isAbsolute()) {
				file = new File(gameDir, path);
			}
			return file.exists() && sourceLine(gameDir, file).equals(this.line);
		}

		void write(File packDir) {
			String text = header() + "\n" + this.label + "\n" + this.line + "\n";
			try {
				AVAssetSidecar.write(new File(packDir, STAMP), text.getBytes(StandardCharsets.UTF_8));
			} catch (IOException e) {
				AlphaVer.LOGGER.warn("Asset sidecar: could not record what the packs were built from: {}", e.toString());
			}
		}
	}

	private static void writePackMeta(File packDir, String description) throws IOException {
		write(new File(packDir, "pack.txt"),
			(description + "\n\nGenerated by AlphaVer from a copy of Cypress found on this computer. These files were\n"
				+ "extracted from that copy and were NOT downloaded or redistributed. Do not share this folder.\n"
				+ "Delete it to remove it, or to force the search to run again.\n").getBytes(StandardCharsets.UTF_8));
	}

	private static void write(File target, byte[] bytes) throws IOException {
		File parent = target.getParentFile();
		if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
			throw new IOException("could not create " + parent);
		}
		try (OutputStream out = new FileOutputStream(target)) {
			out.write(bytes);
		}
	}

	private static void writePng(File target, BufferedImage image) throws IOException {
		File parent = target.getParentFile();
		if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
			throw new IOException("could not create " + parent);
		}
		ImageIO.write(image, "png", target);
	}

	static byte[] readFully(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[16384];
		int read;
		while ((read = in.read(buffer)) > 0) {
			out.write(buffer, 0, read);
		}
		return out.toByteArray();
	}

	private static final int MAX_PATHS_LOGGED = 8;

	private static String summarise(List<String> missing) {
		int shown = Math.min(missing.size(), MAX_PATHS_LOGGED);
		String head = String.join(", ", missing.subList(0, shown));
		return missing.size() > shown ? head + ", and " + (missing.size() - shown) + " more" : head;
	}

	private static String relativePath(File root, File file) {
		return relativeTo(safePath(root), safePath(file));
	}

	private static String relativeTo(String rootPath, String filePath) {
		String relative = filePath.startsWith(rootPath + File.separator)
			? filePath.substring(rootPath.length() + 1)
			: filePath;
		return relative.replace(File.separatorChar, '/');
	}

	private static String safePath(File file) {
		try {
			return file.getCanonicalPath();
		} catch (IOException e) {
			return file.getAbsolutePath();
		}
	}

	public static File gameDir() {
		Minecraft client;
		try {
			client = Minecraft.getMinecraft();
		} catch (Throwable t) {
			return null;
		}
		if (client == null) {
			AlphaVer.LOGGER.warn("Asset sidecar: no Minecraft instance yet, so no art can be read. This is a "
				+ "startup-order problem, not a missing copy of Cypress.");
			return null;
		}
		try {
			return client.getMinecraftDir();
		} catch (Throwable t) {
			AlphaVer.LOGGER.warn("Asset sidecar: could not resolve the game directory ({})", t.toString());
			return null;
		}
	}
}
