package com.alphaver.mixin.client;

import com.mojang.nbt.tags.CompoundTag;
import net.minecraft.client.Minecraft;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.save.LevelData;
import net.minecraft.core.world.save.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;
import java.util.UUID;

@Mixin(value = Minecraft.class, remap = false)
public abstract class MinecraftStartWorldPlayerMixin {

	@Unique
	private String alphaver$startingWorld;

	@Inject(method = "startWorld(Ljava/lang/String;)V", at = @At("HEAD"))
	private void alphaver$rememberWorld(String worldDirName, CallbackInfo ci) {
		this.alphaver$startingWorld = worldDirName;
	}

	@Redirect(method = "startWorld(Ljava/lang/String;)V", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/core/world/save/LevelStorage;getPlayerData(Ljava/lang/String;Ljava/util/UUID;)Lcom/mojang/nbt/tags/CompoundTag;"))
	private CompoundTag alphaver$playerWhoWillBeLoaded(LevelStorage storage, String username, UUID uuid) {
		CompoundTag tag = storage.getPlayerData(username, uuid);
		if (tag == null && this.alphaver$startingWorld != null) {
			try {
				File dir = new File(((Minecraft) (Object) this).getMinecraftDir(), "saves/" + this.alphaver$startingWorld);
				UUID last = LevelData.fromWorldDir(dir).getLastPlayerUUID();
				if (last != null && !last.equals(uuid)) {
					tag = storage.getPlayerData(null, last);
				}
			} catch (Exception ignored) {

			}
		}

		if (tag != null && tag.containsKey("Dimension") && Dimension.getDimensionList().get(tag.getInteger("Dimension")) == null) {
			tag.putInt("Dimension", Dimension.OVERWORLD.id);
		}
		return tag;
	}
}
