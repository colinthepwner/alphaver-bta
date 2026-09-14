package com.alphaver.client.gui;

import com.alphaver.block.machine.MenuEssenceCloner;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class ScreenEssenceCloner extends ScreenCypressMachine {

	private static final String TEXTURE = "/assets/alphaver/textures/gui/essence_cloner.png";

	public ScreenEssenceCloner(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos) {
		super(new MenuEssenceCloner(player.inventory, world, tilePos));
	}

	public static void open(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos) {
		Minecraft.getMinecraft().displayScreen(new ScreenEssenceCloner(player, world, tilePos));
	}

	@Override
	public void removed() {
		super.removed();
		this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		this.label(translate("gui.alphaver.essence_cloner.title"), 30, 6);
		this.label(translate("gui.crafting.label.inventory"), 8, this.ySize - 96 + 2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		this.drawPanel(TEXTURE);
	}
}
