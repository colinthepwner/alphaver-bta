package com.alphaver.item;

import java.util.function.BooleanSupplier;

public final class AVItemHooks {
	private AVItemHooks() {}

	public static final String NOTIFICATION_SOUND = "alphaver:ext.notif";

	@FunctionalInterface
	public interface LoreDisplay {

		void show(String loreKey, String title, int toastId);
	}

	private static final LoreDisplay NONE = (loreKey, title, toastId) -> {};

	private static volatile LoreDisplay loreDisplay = NONE;

	public static void setLoreDisplay(LoreDisplay display) {
		loreDisplay = display == null ? NONE : display;
	}

	static void showLore(String loreKey, String title, int toastId) {
		loreDisplay.show(loreKey, title, toastId);
	}

	@FunctionalInterface
	public interface MessageDisplay {

		void show(String text, int toastId, long durationMs);
	}

	private static final MessageDisplay NO_MESSAGES = (text, toastId, durationMs) -> {};

	private static volatile MessageDisplay messageDisplay = NO_MESSAGES;

	public static void setMessageDisplay(MessageDisplay display) {
		messageDisplay = display == null ? NO_MESSAGES : display;
	}

	static void showMessage(String text, int toastId, long durationMs) {
		messageDisplay.show(text, toastId, durationMs);
	}

	private static final BooleanSupplier NEVER = () -> false;

	private static volatile BooleanSupplier rightShift = NEVER;

	public static void setRightShift(BooleanSupplier held) {
		rightShift = held == null ? NEVER : held;
	}

	static boolean isRightShiftHeld() {
		return rightShift.getAsBoolean();
	}
}
