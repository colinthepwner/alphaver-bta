package com.alphaver;

import com.alphaver.block.AVBlocks;
import com.alphaver.block.machine.AVTileEntities;
import com.alphaver.entity.AVEntities;
import com.alphaver.entity.AVGamemodes;
import com.alphaver.item.AVItems;
import com.alphaver.net.AVNetwork;
import com.alphaver.net.AVSounds;
import com.alphaver.painting.AVPaintings;
import com.alphaver.world.AVDimensions;
import com.alphaver.world.AVGameRules;
import com.alphaver.world.biome.CypressBiomes;
import com.alphaver.world.type.WorldTypeCypress;
import com.alphaver.world.type.WorldTypeHub;
import com.alphaver.world.type.WorldTypeMinigame;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.HalpLibe;
import turniplabs.halplibe.event.defs.CommonEvents;
import turniplabs.halplibe.util.dependency.Key;

public class AlphaVer implements ModInitializer {
	public static final String MOD_ID = HalpLibe.registerMod("alphaver", true);
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		AVConfig.init();

		AVGameRules.register();

		AVGamemodes.register();

		AVNetwork.registerMessages();
		CommonEvents.BEFORE_GAME_START.listen(Key.of(MOD_ID), this::beforeGameStart);

		CommonEvents.RECIPES_READY.listen(Key.of(MOD_ID), this::recipesReadyGuarded);
		CommonEvents.AFTER_GAME_START.listen(Key.of(MOD_ID), this::afterGameStartGuarded);

		CommonEvents.RECIPES_NAMESPACE_INIT.listen(Key.of(MOD_ID), AVRecipes::initNamespace);
		LOGGER.info("AlphaVer initialized.");
	}

	private void beforeGameStart() {

		AVBlocks.register();

		AVTileEntities.register();

		AVItems.register();

		AVEntities.register();

		AVNetwork.registerEntityEntries();
		AVSounds.registerNames();

		AVPaintings.register();

		CypressBiomes.init();

		WorldTypeCypress.register();
		WorldTypeHub.register();
		WorldTypeMinigame.register();

		AVDimensions.create();

		AVBlocks.registerDoors();
		AVDimensions.attachPortalBlocks(AVBlocks.HUB_DOOR, AVBlocks.CYPRESS_DOOR_LOWER, AVBlocks.ZOMBIES_DOOR, AVBlocks.FREERUN_DOOR);

		AVBlocks.hideFromCreativeMenu();
		AVItems.hideFromCreativeMenu();
	}

	private void afterGameStartGuarded() {
		try {
			afterGameStart();
		} catch (Throwable t) {
			LOGGER.error("afterGameStart failed; parts of this mod may be missing. Deliberately swallowed: "
				+ "throwing here would cancel afterGameStart for every mod loaded after this one.", t);
		}
	}

	private void recipesReadyGuarded() {
		try {
			AVRecipes.register();
		} catch (Throwable t) {
			LOGGER.error("Registering AlphaVer's recipes failed; some or all of them will be missing. Deliberately swallowed: "
				+ "throwing here would cancel the recipes of every mod loaded after this one.", t);
		}
	}

	private void afterGameStart() {

		AVDimensions.register();

		AVBlocks.registerMiningLevels();

	}
}
