package com.alphaver.client.sound;

import com.alphaver.AVConfig;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.sound.SoundEvent;
import net.minecraft.client.sound.SoundRepository;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class CypressMusic {
	private CypressMusic() {}

	public static final String CYPRESS = "alphaver:music.cypress";
	public static final String HUB = "alphaver:music.hub";

	@Nullable
	public static SoundEvent eventFor(@Nullable World world) {
		if (!AVConfig.MUSIC || !AVWorlds.isAlphaVer(world) || SoundRepository.SOUNDS == null) {
			return null;
		}
		if (AVWorlds.isHub(world)) {

			SoundEvent hub = SoundRepository.SOUNDS.getSoundEvent(HUB);
			if (hub != null) {
				return hub;
			}
		}
		return SoundRepository.SOUNDS.getSoundEvent(CYPRESS);
	}
}
