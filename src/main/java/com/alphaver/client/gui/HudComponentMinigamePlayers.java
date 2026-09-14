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

import java.util.List;

@Environment(EnvType.CLIENT)
public final class HudComponentMinigamePlayers extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":minigame_players";

	private static final int WIDTH = 110;
	private static final int ZOMBIES_ROW = 21;
	private static final int FREERUN_ROW = 10;

	private static final int HEIGHT = ZOMBIES_ROW * 3;
	private static final int PERK_ICON = 9;
	private static final int WHITE = 0xFFFFFFFF;
	private static final int YELLOW = 0xFFFFFF55;
	private static final int RED = 0xFFFF5555;
	private static final int GREY = 0xFFAAAAAA;

	private HudComponentMinigamePlayers(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentMinigamePlayers(new LayoutAbsolute(1.0F, 0.3F, ComponentAnchor.TOP_RIGHT, -4, 0)));
	}

	@Override
	public boolean isVisible() {
		MinigameHudState state = MinigameHudState.local;
		if (mc.thePlayer == null || !GameSettings.IMMERSIVE_MODE.drawHotbar() || !state.inStage() || state.mates.isEmpty()) {
			return false;
		}
		return state.inZombies() && AVWorlds.isZombies(mc.currentWorld) || state.inFreerun() && AVWorlds.isFreerun(mc.currentWorld);
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		MinigameHudState state = MinigameHudState.local;
		draw(hud, this.getLayout().getComponentX(this, screenWidth), this.getLayout().getComponentY(this, screenHeight), state.inZombies(),
			state.mates, state.checkpoints);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), true, List.of(
			new MinigameHudState.Mate("Steve", 1480, MinigamePerk.ARMOR.bit() | MinigamePerk.DASH.bit(), false, 0, -1),
			new MinigameHudState.Mate("Alex", 620, MinigamePerk.QUICK_REVIVE.bit(), true, 0, -1)), 0);
	}

	private static void draw(Gui gui, int x, int y, boolean zombies, List<MinigameHudState.Mate> mates, int checkpoints) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		int rowY = y;
		for (MinigameHudState.Mate mate : mates) {
			if (zombies) {
				gui.drawStringShadow(mc.font, mate.downed() ? mate.name() + " (down)" : mate.name(), x, rowY, mate.downed() ? RED : WHITE);
				String points = String.valueOf(mate.points());
				gui.drawStringShadow(mc.font, points, x + WIDTH - mc.font.stringWidth(points), rowY, YELLOW);
				int iconX = x;
				for (MinigamePerk perk : MinigamePerk.values()) {
					if (mate.has(perk)) {
						HudComponentZombies.drawPerkIcon(gui, iconX, rowY + 10, PERK_ICON, perk);
						iconX += PERK_ICON + 1;
					}
				}
				rowY += ZOMBIES_ROW;
			} else {
				gui.drawStringShadow(mc.font, mate.name(), x, rowY, WHITE);

				boolean showTime = mate.checkpoint() == 0 && mate.finishTicks() >= 0;
				String progress = showTime ? MinigameHudState.formatTime(mate.finishTicks()) : mate.checkpoint() + "/" + checkpoints;
				gui.drawStringShadow(mc.font, progress, x + WIDTH - mc.font.stringWidth(progress), rowY, showTime ? YELLOW : GREY);
				rowY += FREERUN_ROW;
			}
		}
		GLRenderer.disableState(State.BLEND);
	}
}
