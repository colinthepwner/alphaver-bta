package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.world.minigame.MinigameHudState;
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

@Environment(EnvType.CLIENT)
public final class HudComponentMinigamePrompt extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":minigame_prompt";

	private static final int WIDTH = 202;
	private static final int HEIGHT = 18;
	private static final int BAR_WIDTH = 200;
	private static final int WHITE = 0xFFFFFFFF;

	private HudComponentMinigamePrompt(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentMinigamePrompt(new LayoutAbsolute(0.5F, 0.75F, ComponentAnchor.CENTER)));
	}

	@Override
	public boolean isVisible() {
		MinigameHudState state = MinigameHudState.local;
		return HudComponentZombies.showing() && (state.downedTicks > 0 || !state.prompt.isEmpty());
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		MinigameHudState state = MinigameHudState.local;
		int x = this.getLayout().getComponentX(this, screenWidth);
		int y = this.getLayout().getComponentY(this, screenHeight);
		if (state.downedTicks > 0) {
			drawRevive(hud, x, y, mc.thePlayer.username, state.downedTicks);
		} else {
			drawPrompt(hud, x, y, state.prompt.replace(MinigameHudState.KEY, GameSettings.KEY_INVENTORY.getKeyName()));
		}
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		drawPrompt(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), "[E] buy Armor [1500 points]");
	}

	private static void drawPrompt(Gui gui, int x, int y, String text) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		gui.drawStringCenteredShadow(mc.font, text, x + WIDTH / 2, y + 5, WHITE);
		GLRenderer.disableState(State.BLEND);
	}

	private static void drawRevive(Gui gui, int x, int y, String name, int downedTicks) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		int centre = x + WIDTH / 2;
		gui.drawStringCenteredShadow(mc.font, "Reviving " + name + "...", centre, y, WHITE);
		int left = centre - BAR_WIDTH / 2;
		int filled = BAR_WIDTH * Math.max(0, MinigameHudState.REVIVE_TICKS - downedTicks) / MinigameHudState.REVIVE_TICKS;
		gui.drawGradientRect(left - 1, y + 11, left + BAR_WIDTH + 1, y + 18, 0xFF202020, 0xFF000000);
		gui.drawGradientRect(left, y + 12, left + filled, y + 17, 0xFFFFFFFF, 0xFF444445);
		GLRenderer.disableState(State.BLEND);
	}
}
