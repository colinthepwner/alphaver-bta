package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVWorlds;
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
public final class HudComponentAreaName extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":area_name";

	private static final int WIDTH = 160;
	private static final int HEIGHT = 18;
	private static final int TOP = 40;
	private static final int RIGHT_MARGIN = 10;

	private static final String PREVIEW = "KII-SAUNINMU";

	private HudComponentAreaName(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentAreaName(new LayoutAbsolute(1.0F, 0.0F, ComponentAnchor.TOP_RIGHT, 0, TOP)));
	}

	@Override
	public boolean isVisible() {
		return CypressAreaNames.current() != null && AVWorlds.isCypress(mc.currentWorld);
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		String name = CypressAreaNames.current();
		if (name == null) {
			return;
		}
		draw(hud, getLayout().getComponentX(this, screenWidth), getLayout().getComponentY(this, screenHeight), name,
			CypressAreaNames.alpha());
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), PREVIEW, 255);
	}

	private static void draw(Gui gui, int x, int y, String name, int alpha) {
		float scale = 1.0F + (float) Math.pow(0.5, name.length() / 10);
		int textWidth = mc.font.stringWidth(name);
		GLRenderer.pushFrame();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.modelM4f().scale(scale, scale, 1.0F);
		int drawX = (int) ((x + WIDTH) / scale - textWidth - RIGHT_MARGIN);
		int drawY = (int) (y / scale);
		gui.drawStringShadow(mc.font, name, drawX, drawY, 0xFFFFFF | (alpha << 24));
		GLRenderer.popFrame();
	}
}
