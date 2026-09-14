package com.alphaver.mixin.client;

import com.alphaver.client.render.AVWaterLook;
import net.minecraft.client.render.dynamictexture.DynamicTextureWaterFlow;
import net.minecraft.core.data.tag.Tag;
import net.minecraft.core.world.type.WorldType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = DynamicTextureWaterFlow.class, remap = false)
public abstract class DynamicTextureWaterFlowMixin {

	@Redirect(
		method = "update",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/core/world/type/WorldType;hasTag(Lnet/minecraft/core/data/tag/Tag;)Z"))
	private boolean alphaver$alphaWater(WorldType type, Tag<WorldType> tag) {
		return AVWaterLook.hasTag(type, tag);
	}
}
