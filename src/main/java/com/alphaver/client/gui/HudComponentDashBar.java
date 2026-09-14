package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.client.movement.CypressDash;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.ComponentAnchor;
import net.minecraft.client.gui.hud.component.HudComponentMovable;
import net.minecraft.client.gui.hud.component.HudComponents;
import net.minecraft.client.gui.hud.component.layout.Layout;
import net.minecraft.client.gui.hud.component.layout.LayoutSnap;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;

@Environment(EnvType.CLIENT)
public final class HudComponentDashBar extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":dash_bar";

	private static final int WIDTH = 102;
	private static final int HEIGHT = 7;
	private static final int GAP_ABOVE_HOTBAR = 62;

	private static final int FRAME_TOP = 0xFF202020;
	private static final int FRAME_BOTTOM = 0xFF000000;
	private static final int CHARGING_TOP = 0xFFFFF200;
	private static final int CHARGING_BOTTOM = 0xFF2BFF00;
	private static final int CHAIN_TOP = 0xFF00A2FF;
	private static final int CHAIN_BOTTOM = 0xFF0044AB;

	private static final int PREVIEW_TIMER = 10;

	private HudComponentDashBar(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentDashBar(new LayoutSnap(HudComponents.HOTBAR, ComponentAnchor.TOP_CENTER,
			ComponentAnchor.BOTTOM_CENTER, 0, -GAP_ABOVE_HOTBAR)));
	}

	@Override
	public boolean isVisible() {
		return CypressDash.timer() != 0 && CypressDash.enabledFor(mc);
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		drawBar(hud, getLayout().getComponentX(this, screenWidth), getLayout().getComponentY(this, screenHeight),
			CypressDash.timer());
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		drawBar(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), PREVIEW_TIMER);
	}

	private static void drawBar(Gui gui, int x, int y, int timer) {
		gui.drawGradientRect(x, y, x + WIDTH, y + HEIGHT, FRAME_TOP, FRAME_BOTTOM);
		boolean chain = timer < CypressDash.CHAIN_BELOW;
		int filled = (int) (100.0F * (1.0F - timer / (float) CypressDash.DASH_TICKS));
		if (filled > 0) {
			gui.drawGradientRect(x + 1, y + 1, x + 1 + filled, y + HEIGHT - 1,
				chain ? CHAIN_TOP : CHARGING_TOP, chain ? CHAIN_BOTTOM : CHARGING_BOTTOM);
		}
	}
}
