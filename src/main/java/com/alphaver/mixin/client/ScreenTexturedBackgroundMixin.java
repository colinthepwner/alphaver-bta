package com.alphaver.mixin.client;

import com.alphaver.client.render.AVLoadingBackgrounds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Screen;
import net.minecraft.client.gui.ScreenDownloadTerrain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = Screen.class, remap = false)
public abstract class ScreenTexturedBackgroundMixin {

	@ModifyConstant(method = "renderTexturedBackground", constant = @Constant(stringValue = "/assets/minecraft/textures/gui/background.png"))
	private String alphaver$dimensionBackground(String path) {
		if (!((Object) this instanceof ScreenDownloadTerrain)) {
			return path;
		}
		Minecraft mc = Minecraft.getMinecraft();
		String own = mc == null || mc.currentWorld == null ? null : AVLoadingBackgrounds.pathFor(mc.currentWorld.dimension);
		return own != null ? own : path;
	}
}
