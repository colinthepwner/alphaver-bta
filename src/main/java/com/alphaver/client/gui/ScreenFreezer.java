package com.alphaver.client.gui;

import com.alphaver.block.machine.MenuFreezer;
import com.alphaver.block.machine.TileEntityFreezer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.player.inventory.container.ContainerInventory;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ScreenFreezer extends ScreenCypressMachine {

	private static final String TEXTURE = "/assets/alphaver/textures/gui/freezer.png";
	private static final int COLD_FILL = 0xFF9CD3FF;
	private static final int PROGRESS_FILL = 0xFFFFFFFF;

	private final TileEntityFreezer freezer;

	public ScreenFreezer(ContainerInventory inventory, TileEntityFreezer freezer) {
		super(new MenuFreezer(inventory, freezer));
		this.freezer = freezer;
	}

	public static void open(@NotNull Player player, @NotNull TileEntityFreezer freezer) {
		Minecraft.getMinecraft().displayScreen(new ScreenFreezer(player.inventory, freezer));
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		this.label(translate(this.freezer.titleKey()), 60, 6);
		this.label(translate("gui.crafting.label.inventory"), 8, this.ySize - 96 + 2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		int x = this.left();
		int y = this.top();
		boolean art = this.drawPanel(TEXTURE);
		int cold = this.freezer.isBurning() ? this.freezer.getBurnTimeRemainingScaled(24) : 0;
		int progress = this.freezer.getCookProgressScaled(24);
		if (art) {
			if (cold > 3) {
				this.drawTexturedModalRect(x + 36, y + 57 - cold, 176, 24 - cold, 20, cold - 3);
			}
			this.drawTexturedModalRect(x + 79, y + 35, 176, 21, progress + 1, 16);
		} else {
			if (cold > 3) {
				this.drawRectWidthHeight(x + 36, y + 57 - cold, 20, cold - 3, COLD_FILL);
			}
			if (progress > 0) {
				this.drawRectWidthHeight(x + 79, y + 42, progress, 3, PROGRESS_FILL);
			}
		}
	}
}
