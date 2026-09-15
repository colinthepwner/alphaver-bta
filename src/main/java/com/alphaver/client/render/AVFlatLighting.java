package com.alphaver.client.render;

import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class AVFlatLighting {
	private AVFlatLighting() {}

	public static Object read(OptionBoolean option) {
		if (option == GameSettings.AMBIENT_OCCLUSION && forced()) {
			return Boolean.FALSE;
		}
		return option.value;
	}

	private record Answer(@Nullable World world, boolean flat) {}

	private static volatile Answer last = new Answer(null, false);

	private static boolean forced() {
		Minecraft mc = Minecraft.getMinecraft();
		World world = mc == null ? null : mc.currentWorld;
		Answer answer = last;
		if (answer.world() != world) {
			answer = new Answer(world, AVWorlds.isAlphaVer(world));
			last = answer;
		}
		return answer.flat();
	}
}
