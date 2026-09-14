package com.alphaver.mixin.client;

import com.alphaver.entity.CypressFrail;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.CypressEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenGameOver;
import net.minecraft.core.lang.I18n;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ScreenGameOver.class, remap = false)
public abstract class ScreenGameOverCypressMixin {

	@Redirect(method = "render(IIF)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/lang/I18n;translateKey(Ljava/lang/String;)Ljava/lang/String;"))
	private String alphaver$shattered(I18n i18n, String key) {
		if (CypressFrail.active(Minecraft.getMinecraft().currentWorld)) {
			return i18n.translateKey("gui.alphaver.game_over.shattered");
		}
		return i18n.translateKey(key);
	}

	@Redirect(method = "render(IIF)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/lang/I18n;translateKeyAndFormat(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"))
	private String alphaver$milestone(I18n i18n, String key, Object[] args) {
		World world = Minecraft.getMinecraft().currentWorld;
		if (AVWorlds.isAlphaVer(world)) {
			return i18n.translateKeyAndFormat("gui.alphaver.game_over.milestone", CypressEvents.milestone(world.getWorldTime()));
		}
		return i18n.translateKeyAndFormat(key, args);
	}
}
