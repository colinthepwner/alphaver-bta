package com.alphaver.mixin.client;

import com.alphaver.item.AVItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.render.renderer.GLRenderer;
import net.minecraft.client.render.renderer.State;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HudIngame.class, remap = false)
public abstract class HudIngameSunglassesMixin {

	@Inject(method = "renderGameOverlay(FZII)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/HudIngame;updateAcidOverlayForFrame(IIF)V"))
	private void alphaver$sunglassesShade(float partialTicks, boolean flag, int mouseX, int mouseY, CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null || AVItems.SUNGLASSES == null) {
			return;
		}
		ItemStack head = mc.thePlayer.inventory.armorItemInSlot(HumanArmorShape.HEAD);
		if (head == null || head.itemID != AVItems.SUNGLASSES.id) {
			return;
		}
		int width = mc.resolution.getScaledWidthScreenCoords();
		int height = mc.resolution.getScaledHeightScreenCoords();
		GLRenderer.disableState(State.DEPTH_TEST);
		GLRenderer.setDepthMask(false);
		((Gui) (Object) this).drawGradientRect(0, 0, width, height, 0xA0808080, 0xA0202020);
		GLRenderer.setDepthMask(true);
		GLRenderer.enableState(State.DEPTH_TEST);
	}
}
