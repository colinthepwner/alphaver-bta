package com.alphaver.client.gui;

import com.alphaver.block.machine.MenuEssenceTransformer;
import com.alphaver.net.MessageMachineAction;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ButtonElement;
import net.minecraft.client.gui.SliderElement;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Environment(EnvType.CLIENT)
public class ScreenEssenceTransformer extends ScreenCypressMachine {

	private static final String TEXTURE = "/assets/alphaver/textures/gui/essence_transformer.png";

	private final MenuEssenceTransformer menu;
	private SliderElement confirm;

	public ScreenEssenceTransformer(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos) {
		this(new MenuEssenceTransformer(player.inventory, world, tilePos));
	}

	private ScreenEssenceTransformer(MenuEssenceTransformer menu) {
		super(menu);
		this.menu = menu;
	}

	public static void open(@NotNull Player player, @NotNull World world, @NotNull TilePosc tilePos) {
		Minecraft.getMinecraft().displayScreen(new ScreenEssenceTransformer(player, world, tilePos));
	}

	@Override
	public void init() {
		super.init();
		this.confirm = this.add(new SliderElement(0, this.left() + 95, this.top() + 58, 75, 20,
			translate("gui.alphaver.essence_transformer.confirm"), 0.0F));
	}

	@Override
	protected void buttonReleased(@NotNull ButtonElement button) {
		if (button == this.confirm) {
			if (this.confirm.sliderValue >= 1.0) {
				if (this.mc.isMultiplayerWorld()) {
					NetworkHandler.sendToServer(new MessageMachineAction(this.menu.containerId, MessageMachineAction.CONFIRM_TRANSFORM));
				} else {
					this.menu.confirm(this.mc.thePlayer);
				}
			}
			this.confirm.sliderValue = 0.0;
		}
	}

	@Override
	public void removed() {
		super.removed();
		this.inventorySlots.onCraftGuiClosed(this.mc.thePlayer);
	}

	@Override
	protected void drawGuiContainerForegroundLayer() {
		this.label(translate("gui.alphaver.essence_transformer.title"), 30, 6);
		this.label(translate("gui.alphaver.essence_transformer.value"), 90, 16);
		this.label(this.menu.totalValue() + "e", 90, 26);
		this.label(translate("gui.crafting.label.inventory"), 8, this.ySize - 96 + 2);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick) {
		this.drawPanel(TEXTURE);
	}
}
