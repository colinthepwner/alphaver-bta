package com.alphaver.world.minigame.map;

import com.alphaver.AlphaVer;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import com.mojang.nbt.tags.ListTag;
import com.mojang.nbt.tags.Tag;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public final class CypressMapStore {
	private CypressMapStore() {}

	public enum State {

		IDLE,

		CONVERTING,
		READY,

		NO_SOURCE,

		FAILED
	}

	public static final int REVISION = 1;
	private static final int FORMAT = 1;
	private static final String MANIFEST = "manifest.properties";
	private static final long RETRY_MILLIS = 60_000L;
	private static final int RECENT_CHUNKS = 64;

	public record ConvertedChunk(short[] ids, byte[] data, List<Sign> signs) {
		public static int index(int x, int y, int z) {
			return x << 11 | z << 7 | y;
		}
	}

	public record Sign(int x, int y, int z, String[] lines) {}

	private static volatile State state = State.IDLE;
	private static long lastAttempt;
	private static ZipFile cache;
	private static final Map<String, ConvertedChunk> RECENT = new LinkedHashMap<>(16, 0.75F, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<String, ConvertedChunk> eldest) {
			return this.size() > RECENT_CHUNKS;
		}
	};

	@NotNull
	public static State state() {
		return state;
	}

	public static boolean ready() {
		return state == State.READY;
	}

	public static void prepare() {
		synchronized (CypressMapStore.class) {
			if (state == State.READY || state == State.CONVERTING) {
				return;
			}
			long now = System.currentTimeMillis();
			if ((state == State.NO_SOURCE || state == State.FAILED) && now - lastAttempt < RETRY_MILLIS) {
				return;
			}
			lastAttempt = now;
			File file = cacheFile();
			if (open(file)) {
				state = State.READY;
				AlphaVer.LOGGER.info("Minigame maps: using {}.", file.getPath());
				return;
			}
			state = State.CONVERTING;
		}
		Thread thread = new Thread(CypressMapStore::convertInBackground, "AlphaVer minigame map conversion");
		thread.setDaemon(true);
		thread.setPriority(Thread.MIN_PRIORITY + 1);
		thread.start();
	}

	@Nullable
	public static ConvertedChunk chunk(@NotNull CypressMap map, int chunkX, int chunkZ) {
		if (state != State.READY || !map.containsCypressChunk(chunkX, chunkZ)) {
			return null;
		}
		String key = map.name() + "/" + chunkX + "." + chunkZ;
		synchronized (CypressMapStore.class) {
			ConvertedChunk recent = RECENT.get(key);
			if (recent != null) {
				return recent;
			}
			if (cache == null) {
				return null;
			}
			try {
				ZipEntry entry = cache.getEntry(key);
				if (entry == null) {

					return null;
				}
				ConvertedChunk chunk;
				try (DataInputStream in = new DataInputStream(new BufferedInputStream(cache.getInputStream(entry)))) {
					chunk = read(in);
				}
				RECENT.put(key, chunk);
				return chunk;
			} catch (IOException | RuntimeException e) {
				AlphaVer.LOGGER.error("Minigame maps: could not read {} from the cache.", key, e);
				return null;
			}
		}
	}

	@NotNull
	private static File cacheFile() {
		File gameDir;
		try {
			gameDir = FabricLoader.getInstance().getGameDir().toFile();
		} catch (RuntimeException e) {
			gameDir = new File(".");
		}
		return new File(gameDir, "alphaver/minigame-maps-r" + REVISION + ".zip");
	}

	private static boolean open(File file) {
		if (!file.isFile()) {
			return false;
		}
		try {
			ZipFile zip = new ZipFile(file);
			ZipEntry manifestEntry = zip.getEntry(MANIFEST);
			Properties manifest = new Properties();
			if (manifestEntry != null) {
				try (InputStream in = zip.getInputStream(manifestEntry)) {
					manifest.load(in);
				}
			}
			if (!String.valueOf(REVISION).equals(manifest.getProperty("revision"))
				|| !String.valueOf(FORMAT).equals(manifest.getProperty("format"))) {
				zip.close();
				return false;
			}
			if (cache != null) {
				cache.close();
			}
			cache = zip;
			RECENT.clear();
			return true;
		} catch (IOException e) {
			AlphaVer.LOGGER.warn("Minigame maps: {} is unreadable and will be converted again: {}", file.getPath(), e.toString());
			return false;
		}
	}

	private static void convertInBackground() {
		long started = System.nanoTime();
		try {
			CypressJar.Source source = CypressJar.find();
			if (source == null) {
				state = State.NO_SOURCE;
				AlphaVer.LOGGER.info("Minigame maps: no copy of Cypress with its {} was found. Zombies and Freerun keep their generated "
					+ "stages. Put the Cypress jar in the game folder (or point CYPRESS_SOURCE at it) to play Cypress's own maps.",
					CypressJar.MAPS);
				return;
			}
			File target = cacheFile();
			File parent = target.getParentFile();
			if (parent != null && !parent.isDirectory() && !parent.mkdirs()) {
				throw new IOException("could not create " + parent.getPath());
			}
			File temporary = new File(target.getPath() + ".part");
			Map<CypressMap, Integer> counts = convert(source, temporary);
			Files.move(temporary.toPath(), target.toPath(), StandardCopyOption.REPLACE_EXISTING);
			synchronized (CypressMapStore.class) {
				if (!open(target)) {
					throw new IOException("the finished cache did not open");
				}
				state = State.READY;
			}
			AlphaVer.LOGGER.info("Minigame maps: converted {} from {} in {} ms.", counts, source.file().getName(),
				(System.nanoTime() - started) / 1_000_000L);
		} catch (Throwable t) {
			state = State.FAILED;
			AlphaVer.LOGGER.error("Minigame maps: conversion failed; Zombies and Freerun keep their generated stages.", t);
		}
	}

	private static Map<CypressMap, Integer> convert(CypressJar.Source source, File temporary) throws IOException {
		Map<CypressMap, Integer> counts = new EnumMap<>(CypressMap.class);
		try (ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(temporary), 1 << 16))) {
			if (source.inJar()) {
				try (ZipFile jar = new ZipFile(source.file())) {
					ZipEntry maps = jar.getEntry(CypressJar.MAPS);
					if (maps == null) {
						throw new IOException(CypressJar.MAPS + " vanished from " + source.file().getName());
					}
					try (InputStream in = jar.getInputStream(maps)) {
						convertMaps(in, out, counts);
					}
				}
			} else {
				try (InputStream in = new FileInputStream(source.file())) {
					convertMaps(in, out, counts);
				}
			}
			Properties manifest = new Properties();
			manifest.setProperty("revision", String.valueOf(REVISION));
			manifest.setProperty("format", String.valueOf(FORMAT));
			manifest.setProperty("chunks", counts.toString());
			out.putNextEntry(new ZipEntry(MANIFEST));
			ByteArrayOutputStream text = new ByteArrayOutputStream();
			manifest.store(text, "AlphaVer minigame maps, converted from a local copy of Cypress");
			out.write(text.toByteArray());
			out.closeEntry();
		}
		MapBlockPalette.reportUnknown("Cypress's minigame maps");
		return counts;
	}

	private static void convertMaps(InputStream raw, ZipOutputStream out, Map<CypressMap, Integer> counts) throws IOException {
		ZipInputStream in = new ZipInputStream(new BufferedInputStream(raw, 1 << 16));
		ZipEntry entry;
		while ((entry = in.getNextEntry()) != null) {
			if (entry.isDirectory()) {
				continue;
			}
			String name = entry.getName();
			CypressMap map = CypressMap.ofEntry(name);
			if (map == null) {
				continue;
			}

			String file = name.substring(name.lastIndexOf('/') + 1);
			String[] parts = file.split("\\.");
			if (parts.length != 4 || !parts[0].equals("c") || !parts[3].equals("dat")) {
				continue;
			}
			int chunkX;
			int chunkZ;
			try {
				chunkX = Integer.parseInt(parts[1], 36);
				chunkZ = Integer.parseInt(parts[2], 36);
			} catch (NumberFormatException e) {
				continue;
			}
			if (!map.containsCypressChunk(chunkX, chunkZ)) {
				continue;
			}
			byte[] bytes = in.readAllBytes();
			CompoundTag level = NbtIo.readCompressed(new ByteArrayInputStream(bytes)).getCompound("Level");
			ConvertedChunk chunk = convertChunk(level, chunkX, chunkZ);
			out.putNextEntry(new ZipEntry(map.name() + "/" + chunkX + "." + chunkZ));
			write(new DataOutputStream(out), chunk);
			out.closeEntry();
			counts.merge(map, 1, Integer::sum);
		}
	}

	private static ConvertedChunk convertChunk(CompoundTag level, int chunkX, int chunkZ) {
		byte[] blocks = level.getByteArray("Blocks");
		byte[] nibbles = level.getByteArray("Data");
		short[] ids = new short[32768];
		byte[] data = new byte[32768];
		int count = Math.min(32768, blocks == null ? 0 : blocks.length);
		for (int i = 0; i < count; i++) {
			int alphaId = blocks[i] & 255;
			if (alphaId == 0) {
				continue;
			}
			int alphaData = nibbles == null || (i >> 1) >= nibbles.length ? 0 : (nibbles[i >> 1] >> ((i & 1) == 0 ? 0 : 4)) & 15;
			int packed = MapBlockPalette.convert(alphaId, alphaData);
			ids[i] = (short) (packed >>> 8);
			data[i] = (byte) packed;
		}
		List<Sign> signs = new ArrayList<>();
		ListTag tileEntities = level.getList("TileEntities");
		if (tileEntities != null) {
			for (Tag<?> tag : tileEntities) {
				if (!(tag instanceof CompoundTag tileEntity) || !"Sign".equals(tileEntity.getString("id"))) {
					continue;
				}
				int x = tileEntity.getInteger("x") - chunkX * 16;
				int y = tileEntity.getInteger("y");
				int z = tileEntity.getInteger("z") - chunkZ * 16;
				if (x < 0 || x > 15 || z < 0 || z > 15 || y < 0 || y > 127) {
					continue;
				}
				String[] lines = new String[4];
				for (int line = 0; line < 4; line++) {
					String text = tileEntity.getString("Text" + (line + 1));
					lines[line] = text == null ? "" : text.length() > 15 ? text.substring(0, 15) : text;
				}
				signs.add(new Sign(x, y, z, lines));
			}
		}
		return new ConvertedChunk(ids, data, signs);
	}

	private static void write(DataOutputStream out, ConvertedChunk chunk) throws IOException {
		out.writeInt(FORMAT);
		byte[] ids = new byte[chunk.ids().length * 2];
		for (int i = 0; i < chunk.ids().length; i++) {
			ids[i * 2] = (byte) (chunk.ids()[i] >> 8);
			ids[i * 2 + 1] = (byte) chunk.ids()[i];
		}
		out.write(ids);
		out.write(chunk.data());
		out.writeInt(chunk.signs().size());
		for (Sign sign : chunk.signs()) {
			out.writeByte(sign.x());
			out.writeShort(sign.y());
			out.writeByte(sign.z());
			for (String line : sign.lines()) {
				byte[] text = line.getBytes(StandardCharsets.UTF_8);
				out.writeShort(text.length);
				out.write(text);
			}
		}
		out.flush();
	}

	private static ConvertedChunk read(DataInputStream in) throws IOException {
		int format = in.readInt();
		if (format != FORMAT) {
			throw new IOException("chunk format " + format);
		}
		byte[] raw = new byte[65536];
		in.readFully(raw);
		short[] ids = new short[32768];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = (short) ((raw[i * 2] & 255) << 8 | (raw[i * 2 + 1] & 255));
		}
		byte[] data = new byte[32768];
		in.readFully(data);
		int signCount = in.readInt();
		List<Sign> signs = new ArrayList<>(signCount);
		for (int n = 0; n < signCount; n++) {
			int x = in.readUnsignedByte();
			int y = in.readShort();
			int z = in.readUnsignedByte();
			String[] lines = new String[4];
			for (int line = 0; line < 4; line++) {
				byte[] text = new byte[in.readUnsignedShort()];
				in.readFully(text);
				lines[line] = new String(text, StandardCharsets.UTF_8);
			}
			signs.add(new Sign(x, y, z, lines));
		}
		return new ConvertedChunk(ids, data, signs);
	}
}
