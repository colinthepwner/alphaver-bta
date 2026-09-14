package com.alphaver.mixin.server;

import com.alphaver.net.MessageHandshake;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketLogin;
import net.minecraft.server.net.handler.PacketHandlerLogin;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turniplabs.halplibe.helper.network.NetworkHandler;

@Mixin(value = PacketHandlerLogin.class, remap = false)
public abstract class PacketHandlerLoginHandshakeMixin {

	@Shadow
	public NetworkManager netManager;

	@Inject(method = "handleLogin(Lnet/minecraft/core/net/packet/PacketLogin;)V", at = @At("HEAD"))
	private void alphaver$sendHandshake(PacketLogin packet, CallbackInfo ci) {
		if (this.netManager != null) {
			this.netManager.addToSendQueue(NetworkHandler.generateCompatibilityNetworkMessagePacket(MessageHandshake.describeThisSide()));
		}
	}
}
