package com.alphaver.mixin.server;

import net.minecraft.server.entity.player.PlayerServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = PlayerServer.class, remap = false)
public interface PlayerServerWindowAccessor {

	@Invoker("getNextWindowId")
	void alphaver$nextWindowId();

	@Accessor("currentWindowId")
	int alphaver$getCurrentWindowId();
}
