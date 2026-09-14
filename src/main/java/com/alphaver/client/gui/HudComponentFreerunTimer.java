package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.client.AVKeys;
import com.alphaver.world.AVWorlds;
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
public final class HudComponentFreerunTimer extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":freerun_timer";

	private static final int WIDTH = 100;

	private static final int HEIGHT = 20;
	private static final int HINT_Y = 12;
	private static final int WHITE = 0xFFFFFFFF;
	private static final long HINT_NANOS = 3_000_000_000L;

	private static long seenStage = Long.MIN_VALUE;
	private static int seenSerial;

	private static long hintUntilNanos;

	private HudComponentFreerunTimer(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentFreerunTimer(new LayoutAbsolute(0.5F, 0.25F, ComponentAnchor.TOP_CENTER)));
	}

	@Override
	public boolean isVisible() {
		MinigameHudState state = MinigameHudState.local;
		return mc.thePlayer != null && AVWorlds.isFreerun(mc.currentWorld) && state.inFreerun() && state.inStage();
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		MinigameHudState state = MinigameHudState.local;
		draw(hud, this.getLayout().getComponentX(this, screenWidth), this.getLayout().getComponentY(this, screenHeight),
			state.displayTimerTicks(), hintShowing(state));
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), 487, true);
	}

	private static boolean hintShowing(MinigameHudState state) {
		long stage = ((long) state.kind << 32) | (state.stageCode & 0xFFFFFFFFL);
		long now = System.nanoTime();
		if (stage != seenStage) {
			seenStage = stage;
			seenSerial = state.leaveHintSerial;
			hintUntilNanos = now;
		} else if (state.leaveHintSerial != seenSerial) {
			seenSerial = state.leaveHintSerial;
			hintUntilNanos = now + HINT_NANOS;
		}
		return now < hintUntilNanos;
	}

	private static void draw(Gui gui, int x, int y, long ticks, boolean hint) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		gui.drawStringCenteredShadow(mc.font, "Time: " + MinigameHudState.formatTime(ticks), x + WIDTH / 2, y, WHITE);
		if (hint) {
			gui.drawStringCenteredShadow(mc.font, "Press " + leaveKeyName() + " to return", x + WIDTH / 2, y + HINT_Y, WHITE);
		}
		GLRenderer.disableState(State.BLEND);
	}

	private static String leaveKeyName() {
		return AVKeys.MINIGAME_LEAVE == null ? "K" : AVKeys.MINIGAME_LEAVE.getKeyName();
	}
}
