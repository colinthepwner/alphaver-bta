package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.entity.LilypadHunger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.HudComponents;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;

@Environment(EnvType.CLIENT)
public final class HudComponentLilypadHunger extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":lilypad_hunger";

	public static final String FRAME = AlphaVer.MOD_ID + ":gui/hud/lilypad_hunger/frame";

	public static final String FILL = AlphaVer.MOD_ID + ":gui/hud/lilypad_hunger/fill_";

	private static final int WIDTH = 32;
	private static final int HEIGHT = 16;
	private static final int GAP_ABOVE_HOTBAR = 12;

	private static final int PREVIEW_STAGE = 1;

	private static final int TRACK_INSET_Y = 3;
	private static final int TRACK_EDGE = 0xFF000000;
	private static final int TRACK_TOP = 0xFF4A4A4A;
	private static final int TRACK_BOTTOM = 0xFF2C2C2C;
	private static final int FILL_TOP = 0xFFFFF04A;
	private static final int FILL_BOTTOM = 0xFFE0A800;

	private HudComponentLilypadHunger(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentLilypadHunger(new LayoutSnap(HudComponents.HOTBAR, ComponentAnchor.TOP_CENTER,
			ComponentAnchor.BOTTOM_CENTER, 0, -GAP_ABOVE_HOTBAR)));
	}

	public static void registerIcons() {
		try {
			queue(FRAME);
			for (int fill = 0; fill < LilypadHunger.STAGES; fill++) {
				queue(FILL + fill);
			}
		} catch (RuntimeException e) {

			AlphaVer.LOGGER.warn("Could not queue Lilypad's hunger meter for the GUI atlas: {}", e.toString());
		}
	}

	private static void queue(String id) {
		if (TextureRegistry.hasSourceFile(id)) {
			TextureRegistry.getTexture(id);
		}
	}

	@Override
	public boolean isVisible() {
		return mc.thePlayer != null && GameSettings.IMMERSIVE_MODE.drawHotbar()
			&& LilypadHunger.displayStage(mc.thePlayer) != LilypadHunger.HIDDEN;
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		int stage = LilypadHunger.displayStage(mc.thePlayer);
		if (stage == LilypadHunger.HIDDEN) {
			return;
		}
		draw(hud, this.getLayout().getComponentX(this, screenWidth), this.getLayout().getComponentY(this, screenHeight), stage);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), PREVIEW_STAGE);
	}

	private static void draw(Gui gui, int x, int y, int stage) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		String fill = FILL + (LilypadHunger.STAGES - 1 - stage);

		if (TextureRegistry.hasTexture(FRAME) && TextureRegistry.hasTexture(fill)) {
			gui.drawGuiIcon(x, y, WIDTH, HEIGHT, TextureRegistry.getTexture(FRAME));
			gui.drawGuiIcon(x, y, WIDTH, HEIGHT, TextureRegistry.getTexture(fill));
		} else {
			int top = y + TRACK_INSET_Y;
			int bottom = y + HEIGHT - TRACK_INSET_Y;
			gui.drawGradientRect(x, top, x + WIDTH, bottom, TRACK_EDGE, TRACK_EDGE);
			gui.drawGradientRect(x + 1, top + 1, x + WIDTH - 1, bottom - 1, TRACK_TOP, TRACK_BOTTOM);
			int filled = (WIDTH - 2) * (LilypadHunger.STAGES - stage) / LilypadHunger.STAGES;
			gui.drawGradientRect(x + 1, top + 1, x + 1 + filled, bottom - 1, FILL_TOP, FILL_BOTTOM);
		}
		GLRenderer.disableState(State.BLEND);
	}
}
