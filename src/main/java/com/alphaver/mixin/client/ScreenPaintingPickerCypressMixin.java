package com.alphaver.mixin.client;

import com.alphaver.client.render.AVPaintingTextures;
import com.alphaver.painting.AVPaintings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenPaintingPicker;
import net.minecraft.client.render.texture.stitcher.IconCoordinate;
import net.minecraft.client.render.texture.stitcher.TextureRegistry;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.ArtType;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ScreenPaintingPicker.class, remap = false)
public abstract class ScreenPaintingPickerCypressMixin {

	@Shadow
	private int tallestSize;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void alphaver$tallestAllowed(Player player, CallbackInfo ci) {
		World world = Minecraft.getMinecraft().currentWorld;
		int tallest = 0;
		for (ArtType art : ArtType.values) {
			if (AVPaintings.allowedIn(art, world) && art.sizeY > tallest) {
				tallest = art.sizeY;
			}
		}
		if (tallest > 0) {
			this.tallestSize = tallest;
		}
	}

	@Redirect(method = "init", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/entity/player/Player;getSelectedArt()Lnet/minecraft/core/enums/ArtType;"))
	private ArtType alphaver$allowedCentre(Player player) {
		ArtType selected = player.getSelectedArt();
		return AVPaintings.allowedIn(selected, Minecraft.getMinecraft().currentWorld) ? selected : alphaver$step(selected, true);
	}

	@Redirect(method = {"render", "updateArtEntries"}, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/enums/ArtType;getNext(Lnet/minecraft/core/enums/ArtType;)Lnet/minecraft/core/enums/ArtType;"))
	private ArtType alphaver$nextAllowed(ArtType art) {
		return alphaver$step(art, true);
	}

	@Redirect(method = {"render", "updateArtEntries"}, at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/enums/ArtType;getPrevious(Lnet/minecraft/core/enums/ArtType;)Lnet/minecraft/core/enums/ArtType;"))
	private ArtType alphaver$previousAllowed(ArtType art) {
		return alphaver$step(art, false);
	}

	@Redirect(method = "drawPainting", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/texture/stitcher/TextureRegistry;getTexture(Ljava/lang/String;)Lnet/minecraft/client/render/texture/stitcher/IconCoordinate;"))
	private IconCoordinate alphaver$paintingTexture(String texture) {
		return TextureRegistry.getTexture(AVPaintingTextures.resolve(texture));
	}

	private static ArtType alphaver$step(ArtType from, boolean forward) {
		World world = Minecraft.getMinecraft().currentWorld;
		ArtType art = from;
		for (int i = 0; i < ArtType.values.size(); i++) {
			art = forward ? ArtType.getNext(art) : ArtType.getPrevious(art);
			if (AVPaintings.allowedIn(art, world)) {
				return art;
			}
		}
		return from;
	}
}
