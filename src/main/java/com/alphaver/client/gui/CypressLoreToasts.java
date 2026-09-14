package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.asset.AVAssetSidecar;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayDeque;
import java.util.Deque;

@Environment(EnvType.CLIENT)
public final class CypressLoreToasts {
	private CypressLoreToasts() {}

	private static final long DURATION_MS = 20000L;

	private static final class Toast {
		final String text;
		final int id;
		final long durationMs;
		long shownAt = -1L;

		Toast(String text, int id, long durationMs) {
			this.text = text;
			this.id = id;
			this.durationMs = durationMs;
		}
	}

	private static final Deque<Toast> QUEUE = new ArrayDeque<>();

	public static synchronized void push(String loreKey, String title, int toastId) {
		pushText(text(loreKey, title), toastId, DURATION_MS);
	}

	public static synchronized void pushText(String text, int toastId, long durationMs) {
		Toast head = QUEUE.peekFirst();
		if (head != null && head.id == toastId) {
			return;
		}
		QUEUE.addLast(new Toast(text, toastId, durationMs));
	}

	public static synchronized boolean isShowing() {
		return !QUEUE.isEmpty();
	}

	public static synchronized String current() {
		Toast head = QUEUE.peekFirst();
		if (head == null) {
			return null;
		}
		long now = System.currentTimeMillis();
		if (head.shownAt == -1L) {
			head.shownAt = now;
		} else if (now > head.shownAt + head.durationMs) {
			QUEUE.pollFirst();
			head = QUEUE.peekFirst();
			if (head == null) {
				return null;
			}
		}
		return head.text;
	}

	private static String text(String loreKey, String title) {
		try {
			File gameDir = Minecraft.getMinecraft().getMinecraftDir();
			File file = new File(gameDir, "texturepacks/" + AVAssetSidecar.PACK_NAME + "/assets/alphaver/lore/" + loreKey + ".txt");
			if (file.isFile()) {
				return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
			}
		} catch (IOException | RuntimeException e) {
			AlphaVer.LOGGER.warn("Could not read the lore text '{}': {}", loreKey, e.toString());
		}
		return title;
	}
}
