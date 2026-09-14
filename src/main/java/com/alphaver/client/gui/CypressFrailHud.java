package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;

@Environment(EnvType.CLIENT)
public final class CypressFrailHud {
	private CypressFrailHud() {}

	public static final String ICON = AlphaVer.MOD_ID + ":gui/hud/frail";
	private static final String FALLBACK = "minecraft:gui/hud/heart/survival/full";

	private static final int HEART_SIZE = 9;

	private static final int HEART_STEP = 8;
	private static final int LAST_HEART = 9;

	public static void register() {
		try {
			if (TextureRegistry.hasSourceFile(ICON)) {
				TextureRegistry.getTexture(ICON);
			}
		} catch (RuntimeException e) {

			AlphaVer.LOGGER.warn("Could not queue Frail's glass heart for the GUI atlas: {}", e.toString());
		}
	}

	public static void draw(HudIngame hud, int x, int y) {
		boolean vertical = GameSettings.VERTICAL_HEALTH_BAR.value;
		boolean flipped = GameSettings.FLIP_HEALTH_BAR.value;
		int heartX = x;
		int heartY = y;

		if (vertical) {
			if (!flipped) {
				heartY = y + LAST_HEART * HEART_STEP;
			}
		} else if (flipped) {
			heartX = x + LAST_HEART * HEART_STEP;
		}

		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);

		hud.drawGuiIcon(heartX, heartY, HEART_SIZE, HEART_SIZE,
			TextureRegistry.getTexture(TextureRegistry.hasTexture(ICON) ? ICON : FALLBACK));
		GLRenderer.disableState(State.BLEND);
	}
}
