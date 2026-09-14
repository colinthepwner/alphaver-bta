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
import net.minecraft.core.entity.EntityPainting;
import net.minecraft.core.util.phys.HitResult;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class HudComponentPaintingTitle extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":painting_title";

	private static final int WIDTH = 120;
	private static final int HEIGHT = 13;
	private static final int PADDING = 2;
	private static final int TEXT_DOWN = 2;
	private static final int BAR_TOP = 0xA0000000;
	private static final int BAR_BOTTOM = 0x80000000;

	private static final String PREVIEW = "Wanderer";

	private HudComponentPaintingTitle(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentPaintingTitle(new LayoutAbsolute(0.5F, 0.25F, ComponentAnchor.TOP_CENTER, 0, 0)));
	}

	@Override
	public boolean isVisible() {
		return AVWorlds.isAlphaVer(mc.currentWorld) && title() != null;
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		String title = title();
		if (title == null) {
			return;
		}
		draw(hud, getLayout().getComponentX(this, screenWidth) + WIDTH / 2, getLayout().getComponentY(this, screenHeight), title);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth) + WIDTH / 2, layout.getComponentY(this, screenHeight), PREVIEW);
	}

	@Nullable
	private static String title() {
		if (mc.objectMouseOver instanceof HitResult.Entity hit && hit.entity instanceof EntityPainting painting
			&& painting.art != null) {
			return painting.art.title;
		}
		return null;
	}

	private static void draw(Gui gui, int centreX, int top, String title) {
		int half = mc.font.stringWidth(title) / 2;
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		gui.drawGradientRect(centreX - half - PADDING, top, centreX + half + PADDING, top + HEIGHT, BAR_TOP, BAR_BOTTOM);
		gui.drawStringShadow(mc.font, title, centreX - half, top + TEXT_DOWN, 0xFFFFFFFF);
	}
}
