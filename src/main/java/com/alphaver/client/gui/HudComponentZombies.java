package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.MinigameHudState;
import com.alphaver.world.minigame.MinigamePerk;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.HudComponents;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;

@Environment(EnvType.CLIENT)
public final class HudComponentZombies extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":zombies";

	public static final String PERK_ICON = AlphaVer.MOD_ID + ":gui/hud/zm_perks/";

	private static final int PERK_SIZE = 32;
	private static final int WIDTH = PERK_SIZE * 4;
	private static final int HEIGHT = 71;
	private static final int POINTS_Y = 0;
	private static final int ZOMBIES_LEFT_Y = 10;
	private static final int PERKS_Y = 25;
	private static final int WAVE_Y = 63;
	private static final int WHITE = 0xFFFFFFFF;

	private static final int[] STAND_IN = {0xFFC0392B, 0xFF7F8C8D, 0xFF2980B9, 0xFF27AE60};

	private HudComponentZombies(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentZombies(new LayoutAbsolute(0.0F, 1.0F, ComponentAnchor.BOTTOM_LEFT, 10, -4)));
	}

	public static void registerIcons() {
		try {
			for (MinigamePerk perk : MinigamePerk.values()) {
				String id = PERK_ICON + perk.icon;
				if (TextureRegistry.hasSourceFile(id)) {
					TextureRegistry.getTexture(id);
				}
			}
		} catch (RuntimeException e) {

			AlphaVer.LOGGER.warn("Could not queue Zombies' perk icons for the GUI atlas: {}", e.toString());
		}
	}

	static boolean showing() {
		MinigameHudState state = MinigameHudState.local;
		return mc.thePlayer != null && AVWorlds.isZombies(mc.currentWorld) && state.inZombies() && state.inStage();
	}

	@Override
	public boolean isVisible() {
		return GameSettings.IMMERSIVE_MODE.drawHotbar() && showing();
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		MinigameHudState state = MinigameHudState.local;
		draw(hud, this.getLayout().getComponentX(this, screenWidth), this.getLayout().getComponentY(this, screenHeight), state.wave,
			state.points, state.zombiesLeft, state.perks);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), 3, 1480, 33,
			MinigamePerk.ARMOR.bit() | MinigamePerk.QUICK_REVIVE.bit());
	}

	private static void draw(Gui gui, int x, int y, int wave, int points, int zombiesLeft, int perks) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		gui.drawStringShadow(mc.font, "Points: " + points, x, y + POINTS_Y, WHITE);
		gui.drawStringShadow(mc.font, "Zombies left: " + zombiesLeft, x, y + ZOMBIES_LEFT_Y, WHITE);
		int iconX = x;
		for (MinigamePerk perk : MinigamePerk.values()) {
			if (perk.in(perks)) {
				drawPerkIcon(gui, iconX, y + PERKS_Y, PERK_SIZE, perk);
				iconX += PERK_SIZE;
			}
		}
		gui.drawStringShadow(mc.font, "Wave " + wave, x, y + WAVE_Y, WHITE);
		GLRenderer.disableState(State.BLEND);
	}

	static void drawPerkIcon(Gui gui, int x, int y, int size, MinigamePerk perk) {
		String id = PERK_ICON + perk.icon;

		if (TextureRegistry.hasTexture(id)) {
			GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			gui.drawGuiIcon(x, y, size, size, TextureRegistry.getTexture(id));
			return;
		}
		int inset = size * 3 / 16;
		gui.drawRect(x + inset, y + inset, x + size - inset, y + size - inset, STAND_IN[perk.ordinal()]);
		if (size >= 16) {
			gui.drawStringCenteredShadow(mc.font, perk.promptName.substring(0, 1), x + size / 2, y + size / 2 - 4, WHITE);
		}
	}
}
