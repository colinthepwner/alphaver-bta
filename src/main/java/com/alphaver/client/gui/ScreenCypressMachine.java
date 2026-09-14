package com.alphaver.client.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.texture.Texture;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.player.inventory.menu.MenuAbstract;
import net.minecraft.core.player.inventory.slot.Slot;

@Environment(EnvType.CLIENT)
public abstract class ScreenCypressMachine extends ScreenContainerAbstract {

	private static final int PANEL_FILL = 0xFFC6C6C6;
	private static final int PANEL_HIGHLIGHT = 0xFFFFFFFF;
	private static final int PANEL_SHADOW = 0xFF555555;
	private static final int SLOT_FRAME = 0xFF373737;
	private static final int SLOT_WELL = 0xFF8B8B8B;
	private static final int SLOT_SIZE = 16;
	private static final int LABEL_BRIDGED = 0xFFFFFF;
	private static final int LABEL_FALLBACK = 0x404040;

	protected boolean bridged;

	protected ScreenCypressMachine(MenuAbstract menu) {
		super(menu);
	}

	protected int left() {
		return (this.width - this.xSize) / 2;
	}

	protected int top() {
		return (this.height - this.ySize) / 2;
	}

	protected boolean drawPanel(String texture) {
		GLRenderer.setColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		Texture background = this.mc.textureManager.loadTextureNoDefault(texture);
		if (background != null) {
			background.bind();
			this.drawTexturedModalRect(this.left(), this.top(), 0, 0, this.xSize, this.ySize);
			this.bridged = true;
		} else {
			this.drawFallbackPanel(this.left(), this.top());
			this.bridged = false;
		}
		return this.bridged;
	}

	protected void label(String text, int x, int y) {
		this.drawStringNoShadow(this.fontRenderer, text, x, y, this.bridged ? LABEL_BRIDGED : LABEL_FALLBACK);
	}

	protected static String translate(String key) {
		return I18n.getInstance().translateKey(key);
	}

	private void drawFallbackPanel(int frameX, int frameY) {
		this.drawRectWidthHeight(frameX, frameY, this.xSize, this.ySize, PANEL_FILL);
		this.drawRectWidthHeight(frameX, frameY, this.xSize, 1, PANEL_HIGHLIGHT);
		this.drawRectWidthHeight(frameX, frameY, 1, this.ySize, PANEL_HIGHLIGHT);
		this.drawRectWidthHeight(frameX, frameY + this.ySize - 1, this.xSize, 1, PANEL_SHADOW);
		this.drawRectWidthHeight(frameX + this.xSize - 1, frameY, 1, this.ySize, PANEL_SHADOW);
		for (Slot slot : this.inventorySlots.slots) {
			if (slot.x < 0 || slot.x + SLOT_SIZE > this.xSize || slot.y < 0 || slot.y + SLOT_SIZE > this.ySize) {
				continue;
			}
			this.drawRectWidthHeight(frameX + slot.x - 1, frameY + slot.y - 1, SLOT_SIZE + 2, SLOT_SIZE + 2, SLOT_FRAME);
			this.drawRectWidthHeight(frameX + slot.x, frameY + slot.y, SLOT_SIZE, SLOT_SIZE, SLOT_WELL);
		}
	}
}
