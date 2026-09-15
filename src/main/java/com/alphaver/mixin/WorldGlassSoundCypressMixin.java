package com.alphaver.mixin;

import com.alphaver.block.AVBlockSounds;
import com.alphaver.world.AVWorlds;
import net.minecraft.core.sound.BlockSound;
import net.minecraft.core.sound.BlockSounds;
import net.minecraft.core.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = World.class, remap = false)
public abstract class WorldGlassSoundCypressMixin {

	@Redirect(method = "playBlockSoundEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/sound/BlockSound;getStepSoundName()Ljava/lang/String;"))
	private String alphaver$cypressGlass(BlockSound sound) {
		if (sound == BlockSounds.GLASS && AVWorlds.isAlphaVer((World) (Object) this)) {
			return AVBlockSounds.GLASS.getStepSoundName();
		}
		return sound.getStepSoundName();
	}
}
