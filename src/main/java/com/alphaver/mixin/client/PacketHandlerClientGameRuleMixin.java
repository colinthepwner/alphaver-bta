package com.alphaver.mixin.client;

import com.alphaver.client.render.CypressHomeLook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.packet.PacketGameRule;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerClient.class, remap = false)
public abstract class PacketHandlerClientGameRuleMixin {

	@Inject(method = "handleGameRule", at = @At("TAIL"))
	private void alphaver$cypressDoorLook(PacketGameRule packet, CallbackInfo ci) {
		CypressHomeLook.rulesReceived(Minecraft.getMinecraft());
	}
}
