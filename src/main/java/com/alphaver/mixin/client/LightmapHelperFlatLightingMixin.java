package com.alphaver.mixin.client;

import com.alphaver.client.render.AVFlatLighting;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.render.LightmapHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = LightmapHelper.class, remap = false)
public abstract class LightmapHelperFlatLightingMixin {

	@Redirect(method = "generateGrayLightmap", at = @At(value = "FIELD",
		target = "Lnet/minecraft/client/option/OptionBoolean;value:Ljava/lang/Object;", opcode = org.objectweb.asm.Opcodes.GETFIELD))
	private Object alphaver$flatInAlphaVer(OptionBoolean option) {
		return AVFlatLighting.read(option);
	}
}
