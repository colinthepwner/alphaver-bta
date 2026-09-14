package com.alphaver.net;

import com.alphaver.AlphaVer;
import com.alphaver.entity.EntityEssenceShot;
import com.alphaver.entity.EntityGrayGunShot;
import net.minecraft.core.net.entity.NetEntityHandler;
import turniplabs.halplibe.helper.network.NetworkHandler;

public final class AVNetwork {
	private AVNetwork() {}

	public static final int TYPE_SPEAR = 26001;
	public static final int TYPE_ESSENCE_SHOT = 26002;
	public static final int TYPE_GRAY_GUN_SHOT = 26003;

	public static void registerMessages() {
		NetworkHandler.registerNetworkMessage(MessageHandshake::new);
		NetworkHandler.registerNetworkMessage(MessageOpenMachine::new);
		NetworkHandler.registerNetworkMessage(MessageMachineAction::new);
		NetworkHandler.registerNetworkMessage(MessageDashSound::new);
		NetworkHandler.registerNetworkMessage(MessageLilypadHunger::new);
		NetworkHandler.registerNetworkMessage(MessageMinigameState::new);
		NetworkHandler.registerNetworkMessage(MessageMinigameAction::new);
	}

	public static void registerEntityEntries() {
		NetEntityHandler.registerNetworkEntry(new NetEntrySpear(), TYPE_SPEAR);
		NetEntityHandler.registerNetworkEntry(new NetEntryEssenceShot<>(EntityEssenceShot.class, EntityEssenceShot::new), TYPE_ESSENCE_SHOT);
		NetEntityHandler.registerNetworkEntry(new NetEntryEssenceShot<>(EntityGrayGunShot.class, EntityGrayGunShot::new), TYPE_GRAY_GUN_SHOT);
		AlphaVer.LOGGER.info("Registered network spawn entries for AlphaVer's projectiles.");
	}
}
