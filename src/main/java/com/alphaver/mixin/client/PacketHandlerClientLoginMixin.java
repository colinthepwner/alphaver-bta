package com.alphaver.mixin.client;

import com.alphaver.net.AVServerSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenConnectFailed;
import net.minecraft.client.net.handler.PacketHandlerClient;
import net.minecraft.core.net.NetworkManager;
import net.minecraft.core.net.packet.PacketLogin;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PacketHandlerClient.class, remap = false)
public abstract class PacketHandlerClientLoginMixin {

	@Shadow
	private boolean disconnected;

	@Shadow
	@Final
	private NetworkManager netManager;

	@Shadow
	@Final
	private Minecraft mc;

	@Inject(method = "handleLogin(Lnet/minecraft/core/net/packet/PacketLogin;)V", at = @At("HEAD"), cancellable = true)
	private void alphaver$refuseMismatchedNumbers(PacketLogin packet, CallbackInfo ci) {
		String reason = AVServerSettings.takeRefusal();
		if (reason == null) {
			return;
		}
		ci.cancel();
		this.netManager.networkShutdown("disconnect.genericReason", new Object[]{reason});
		this.disconnected = true;
		this.mc.changeWorld(null);
		this.mc.displayScreen(new ScreenConnectFailed("disconnect.disconnected", "disconnect.genericReason", new Object[]{reason}));
	}
}
