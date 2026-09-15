package com.alphaver.mixin.client;

import com.alphaver.client.gui.CypressFrailHud;
import com.alphaver.entity.CypressFrail;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.hud.HudIngame;
import net.minecraft.client.gui.hud.component.HudComponent;
import net.minecraft.client.gui.hud.component.HudComponentHealthBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = HudComponentHealthBar.class, remap = false)
public abstract class HudComponentHealthBarFrailMixin {

	@Inject(method = "render(Lnet/minecraft/client/gui/hud/HudIngame;IIF)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$frailHeart(HudIngame hud, int xSizeScreen, int ySizeScreen, float partialTick, CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || !CypressFrail.active(mc.currentWorld, mc.thePlayer)) {
			return;
		}
		ci.cancel();

		if (mc.thePlayer.getHealth() <= 0) {
			return;
		}
		HudComponent self = (HudComponent) (Object) this;
		CypressFrailHud.draw(hud, self.getLayout().getComponentX(self, xSizeScreen), self.getLayout().getComponentY(self, ySizeScreen));
	}
}
