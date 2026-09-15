package com.alphaver.mixin.server;

import com.alphaver.AlphaVer;
import com.alphaver.entity.AVGamemodes;
import com.alphaver.world.AVGameRules;
import com.alphaver.world.travel.AVCypressHome;
import com.alphaver.world.travel.AVTravelData;
import net.minecraft.core.world.World;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.entity.player.PlayerServer;
import net.minecraft.server.net.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerList.class, remap = false)
public abstract class PlayerListCypressStartMixin {

	@Inject(method = "playerLoggedIn", at = @At("HEAD"))
	private void alphaver$startInCypress(PlayerServer player, CallbackInfo ci) {
		MinecraftServer server = player.mcServer;
		if (server == null) {
			return;
		}
		World world = server.getDimensionWorld(player.dimension);
		if (world == null) {
			return;
		}
		if (AVGameRules.START_IN_CYPRESS != null && AVGamemodes.startsInCypress(server.defaultGamemode)
			&& !AVGameRules.startInCypress(world)) {
			world.getLevelData().getGameRules().setValue(AVGameRules.START_IN_CYPRESS, true);
			AlphaVer.LOGGER.info("default-gamemode is {}: this world now starts in Cypress.", server.defaultGamemode.getId());
		}
		if (!((AVTravelData) player).alphaver$loadedFromSave() && AVCypressHome.active(world)) {
			AVCypressHome.sendHome(player);
		}
	}
}
