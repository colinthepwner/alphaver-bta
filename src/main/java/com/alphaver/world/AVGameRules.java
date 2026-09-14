package com.alphaver.world;

import com.alphaver.AlphaVer;
import net.minecraft.core.data.gamerule.GameRuleBoolean;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

public final class AVGameRules {
	private AVGameRules() {}

	public static final String FOREIGN_MOBS_IN_CYPRESS_KEY = "allowForeignMobsInCypress";

	@SuppressWarnings({"java:S1104", "java:S1444", "java:S3008"})
	public static GameRuleBoolean FOREIGN_MOBS_IN_CYPRESS;

	public static void register() {
		if (FOREIGN_MOBS_IN_CYPRESS != null) {
			return;
		}
		FOREIGN_MOBS_IN_CYPRESS = GameRules.register(
			new GameRuleBoolean(FOREIGN_MOBS_IN_CYPRESS_KEY, "gamerule.allow_foreign_mobs_in_cypress", false));
		AlphaVer.LOGGER.info("Registered gamerule '{}'.", FOREIGN_MOBS_IN_CYPRESS_KEY);
	}

	public static boolean foreignMobsInCypress(@Nullable World world) {
		if (world == null || FOREIGN_MOBS_IN_CYPRESS == null) {
			return false;
		}
		Boolean on = world.getGameRuleValue(FOREIGN_MOBS_IN_CYPRESS);
		return on != null && on;
	}
}
