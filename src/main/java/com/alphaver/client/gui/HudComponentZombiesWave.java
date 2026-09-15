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
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;

@Environment(EnvType.CLIENT)
public final class HudComponentZombiesWave extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":zombies_wave";

	private static final float SCALE = 2.0F;
	private static final int WIDTH = 240;
	private static final int HEIGHT = 18;
	private static final int WHITE = 0xFFFFFFFF;

	private HudComponentZombiesWave(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentZombiesWave(new LayoutAbsolute(0.5F, 0.25F, ComponentAnchor.TOP_CENTER)));
	}

	@Override
	public boolean isVisible() {
		return HudComponentZombies.showing() && MinigameHudState.local.displayNextWaveTicks() > 0;
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		MinigameHudState state = MinigameHudState.local;
		draw(hud, this.getLayout().getComponentX(this, screenWidth), this.getLayout().getComponentY(this, screenHeight), state.wave,
			state.displayNextWaveTicks());
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), 3, 147);
	}

	private static void draw(Gui gui, int x, int y, int wave, int ticks) {

		String text = "Wave " + wave + " starts in " + (ticks + 19) / 20;
		GLRenderer.pushFrame();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		GLRenderer.modelM4f().scale(SCALE, SCALE, 1.0F);
		gui.drawStringCenteredShadow(mc.font, text, (int) ((x + WIDTH / 2) / SCALE), (int) (y / SCALE), WHITE);
		GLRenderer.disableState(State.BLEND);
		GLRenderer.popFrame();
	}
}
