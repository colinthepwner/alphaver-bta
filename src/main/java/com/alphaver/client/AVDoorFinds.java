package com.alphaver.client;

import com.alphaver.world.travel.AVInvites;
import com.mojang.nbt.NbtIo;
import com.mojang.nbt.tags.CompoundTag;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

@Environment(EnvType.CLIENT)
public final class AVDoorFinds {
	private AVDoorFinds() {}

	private static final long NO_KEPT_NANOS = 10_000_000_000L;

	private static boolean found;
	private static boolean scanned;
	private static long scannedAt;

	public static boolean anyWorld(boolean fresh) {
		if (found) {
			return true;
		}
		long now = System.nanoTime();
		if (!fresh && scanned && now - scannedAt < NO_KEPT_NANOS) {
			return false;
		}
		scanned = true;
		scannedAt = now;
		found = scanSaves();
		return found;
	}

	private static boolean scanSaves() {
		File[] worlds = new File(Minecraft.getMinecraft().getMinecraftDir(), "saves").listFiles(File::isDirectory);
		if (worlds == null) {
			return false;
		}
		for (File world : worlds) {
			CompoundTag level = read(new File(world, "level.dat"));
			if (level != null && level.containsKey("Data")) {
				CompoundTag data = level.getCompound("Data");
				if (data.containsKey("Player") && invited(data.getCompound("Player"))) {
					return true;
				}
			}
			File[] players = new File(world, "players").listFiles((dir, name) -> name.endsWith(".dat"));
			if (players == null) {
				continue;
			}
			for (File player : players) {
				CompoundTag tag = read(player);
				if (tag != null && invited(tag)) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean invited(CompoundTag player) {
		return player.containsKey(AVInvites.SAVE_KEY) && player.getBoolean(AVInvites.SAVE_KEY);
	}

	@Nullable
	private static CompoundTag read(File file) {
		if (!file.isFile()) {
			return null;
		}
		try (InputStream in = Files.newInputStream(file.toPath())) {
			return NbtIo.readCompressed(in);
		} catch (IOException | RuntimeException e) {
			return null;
		}
	}
}
