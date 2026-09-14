package com.alphaver.mixin.client;

import net.minecraft.client.util.dispatch.Dispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(value = Dispatcher.class, remap = false)
public interface DispatcherAccessor {

	@Accessor("dispatches")
	Map<Object, Object> alphaver$getDispatches();
}
