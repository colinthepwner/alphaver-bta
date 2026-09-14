package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.HudComponents;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutAbsolute;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;

@Environment(EnvType.CLIENT)
public final class HudComponentLoreToast extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":lore";

	private static final int WIDTH = 200;
	private static final int HEIGHT = 60;
	private static final int LEFT = 20;
	private static final int TOP = 30;
	private static final int PADDING = 10;
	private static final int LINE_HEIGHT = 10;
	private static final int COLOR_TOP = 0xA0000000;
	private static final int COLOR_BOTTOM = 0x70000000;

	private static final String PREVIEW = "Hours Long Past I";

	private HudComponentLoreToast(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentLoreToast(new LayoutAbsolute(0.0F, 0.0F, ComponentAnchor.TOP_LEFT, LEFT, TOP)));
	}

	@Override
	public boolean isVisible() {
		return CypressLoreToasts.isShowing();
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		String text = CypressLoreToasts.current();
		if (text == null) {
			return;
		}
		draw(hud, getLayout().getComponentX(this, screenWidth), getLayout().getComponentY(this, screenHeight), text);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), PREVIEW);
	}

	private static void draw(Gui gui, int x, int y, String text) {
		String[] lines = text.split("\n");
		int widest = 0;
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			int start = 0;
			while (start < line.length() && line.charAt(start) == ' ') {
				start++;
			}
			lines[i] = line.substring(start);
			widest = Math.max(widest, mc.font.stringWidth(lines[i]));
		}

		GLRenderer.pushFrame();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		gui.drawGradientRect(x, y, x + widest + PADDING * 2, y + PADDING * 2 + lines.length * LINE_HEIGHT, COLOR_TOP, COLOR_BOTTOM);
		for (int i = 0; i < lines.length; i++) {
			gui.drawStringShadow(mc.font, lines[i], x + PADDING, y + PADDING + LINE_HEIGHT * i, 0xFFFFFFFF);
		}
		GLRenderer.popFrame();
	}
}
