package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.entity.MobColossus;
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
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;

@Environment(EnvType.CLIENT)
public final class HudComponentColossusBar extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":colossus_bar";

	private static final int WIDTH = 302;
	private static final int HEIGHT = 16;
	private static final int TOP = 59;
	private static final int FILL_WIDTH = 300;
	private static final double RANGE_SQ = 256.0 * 256.0;

	private static final String PREVIEW_NAME = "Giant of KII-SAUNINMU";

	private static MobColossus found;
	private static long foundAtTick = Long.MIN_VALUE;
	private static World foundIn;

	private HudComponentColossusBar(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentColossusBar(new LayoutAbsolute(0.5F, 0.0F, ComponentAnchor.TOP_CENTER, 0, TOP)));
	}

	@Override
	public boolean isVisible() {
		return boss() != null;
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		MobColossus boss = boss();
		if (boss == null) {
			return;
		}
		draw(hud, getLayout().getComponentX(this, screenWidth), getLayout().getComponentY(this, screenHeight),
			boss.getBossName(), boss.getHealth(), boss.getMaxHealth());
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		draw(gui, layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), PREVIEW_NAME, 3, 4);
	}

	private static MobColossus boss() {
		World world = mc.currentWorld;
		Player player = mc.thePlayer;
		if (world == null || player == null) {
			found = null;
			foundIn = null;
			return null;
		}
		long tick = world.getWorldTime();
		if (world == foundIn && tick == foundAtTick) {
			return found;
		}
		foundIn = world;
		foundAtTick = tick;
		found = null;
		double best = RANGE_SQ;
		for (Entity entity : world.getLoadedEntityList()) {
			if (entity instanceof MobColossus colossus && colossus.isAlive() && !colossus.removed) {
				double distance = colossus.distanceToSqr(player);
				if (distance < best) {
					best = distance;
					found = colossus;
				}
			}
		}
		return found;
	}

	private static void draw(Gui gui, int x, int y, String name, int health, int maxHealth) {
		int filled = maxHealth > 0 ? (int) ((long) FILL_WIDTH * Math.max(health, 0) / maxHealth) : 0;
		GLRenderer.pushFrame();
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GLRenderer.enableState(State.BLEND);
		gui.drawGradientRect(x, y, x + WIDTH, y + 7, 0xFF202020, 0xFF000000);
		if (filled > 0) {
			gui.drawGradientRect(x + 1, y + 1, x + 1 + filled, y + 6, 0xFFD12717, 0xFFF3FF05);
		}
		gui.drawStringShadow(mc.font, name, x, y + 6, 0xFFFFFFFF);
		GLRenderer.popFrame();
	}
}
