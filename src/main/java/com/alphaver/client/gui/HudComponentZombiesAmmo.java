package com.alphaver.client.gui;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
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
import net.minecraft.core.item.ItemStack;

@Environment(EnvType.CLIENT)
public final class HudComponentZombiesAmmo extends HudComponentMovable {

	public static final String KEY = AlphaVer.MOD_ID + ":zombies_ammo";

	private static final int WIDTH = 48;
	private static final int HEIGHT = 8;
	private static final int WHITE = 0xFFFFFFFF;

	private HudComponentZombiesAmmo(Layout layout) {
		super(KEY, WIDTH, HEIGHT, layout);
	}

	public static void register() {
		if (HudComponents.INSTANCE.getComponent(KEY) != null) {
			return;
		}
		HudComponents.register(new HudComponentZombiesAmmo(new LayoutAbsolute(11.0F / 12.0F, 1.0F, ComponentAnchor.BOTTOM_LEFT, 0, -4)));
	}

	@Override
	public boolean isVisible() {
		return GameSettings.IMMERSIVE_MODE.drawHotbar() && HudComponentZombies.showing() && holdingGun();
	}

	@Override
	public void render(HudIngame hud, int screenWidth, int screenHeight, float partialTick) {
		hud.drawStringShadow(mc.font, "> " + ammo(), this.getLayout().getComponentX(this, screenWidth),
			this.getLayout().getComponentY(this, screenHeight), WHITE);
	}

	@Override
	public void renderPreview(Gui gui, Layout layout, int screenWidth, int screenHeight) {
		gui.drawStringShadow(mc.font, "> 200", layout.getComponentX(this, screenWidth), layout.getComponentY(this, screenHeight), WHITE);
	}

	private static boolean holdingGun() {
		ItemStack held = mc.thePlayer.inventory.getCurrentItem();
		if (held == null) {
			return false;
		}
		return AVItems.ESSENCE_RIFLE != null && held.itemID == AVItems.ESSENCE_RIFLE.id
			|| AVItems.GRAY_GUN != null && held.itemID == AVItems.GRAY_GUN.id;
	}

	private static int ammo() {
		if (AVItems.ESSENCE == null) {
			return 0;
		}
		int count = 0;
		for (ItemStack stack : mc.thePlayer.inventory.mainInventory) {
			if (stack != null && stack.itemID == AVItems.ESSENCE.id) {
				count += stack.stackSize;
			}
		}
		return count;
	}
}
