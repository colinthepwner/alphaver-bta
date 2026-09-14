package com.alphaver.world;

import com.alphaver.AlphaVer;
import com.alphaver.world.type.WorldTypeCypress;
import com.alphaver.world.type.WorldTypeHub;
import com.alphaver.world.type.WorldTypeMinigame;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicPortal;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.type.WorldType;
import net.minecraft.core.world.type.WorldTypeGroups;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import turniplabs.halplibe.helper.EnvironmentHelper;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AVDimensions {
	private AVDimensions() {}

	@SuppressWarnings({"java:S1104", "java:S1444"})
	public static Dimension CYPRESS;
	@SuppressWarnings({"java:S1104", "java:S1444"})
	public static Dimension HUB;

	@SuppressWarnings({"java:S1104", "java:S1444"})
	public static Dimension ZOMBIES;

	@SuppressWarnings({"java:S1104", "java:S1444"})
	public static Dimension FREERUN;

	private static final List<NamedDimension> ALL = new ArrayList<>();

	private static boolean registrationAttempted = false;

	public static void create() {

		CYPRESS = define("alphaver.cypress", "Cypress", WorldTypeCypress.CYPRESS, "DIMENSION_ID", "grass_pathway_bottom");
		HUB = define("alphaver.hub", "Hub World", WorldTypeHub.HUB, "HUB_DIMENSION_ID", "dimension_wall");
		ZOMBIES = define("alphaver.zombies", "Zombies", WorldTypeMinigame.ZOMBIES, "ZOMBIES_DIMENSION_ID", "elder_brick");
		FREERUN = define("alphaver.freerun", "Freerun", WorldTypeMinigame.FREERUN, "FREERUN_DIMENSION_ID", "mojang_block_blue");
	}

	@NotNull
	private static Dimension define(String languageKey, String englishName, WorldType defaultWorldType, String configKey,
	                                String loadingBackground) {
		NamedDimension dimension = new NamedDimension(languageKey, englishName, defaultWorldType, configKey, loadingBackground);
		ALL.add(dimension);
		return dimension;
	}

	@Nullable
	public static String loadingBackgroundOf(@Nullable Dimension dimension) {
		return dimension instanceof NamedDimension named ? named.loadingBackground : null;
	}

	@NotNull
	public static List<Dimension> all() {
		return Collections.unmodifiableList(ALL);
	}

	public static boolean isAlphaVer(@Nullable Dimension dimension) {
		return dimension instanceof NamedDimension;
	}

	public static boolean isAlphaVer(@Nullable World world) {
		return world != null && world.dimension instanceof NamedDimension;
	}

	@NotNull
	public static String nameOf(@NotNull Dimension dimension) {
		return dimension instanceof NamedDimension named ? named.englishName : dimension.languageKey;
	}

	public static String commandName(Dimension dimension) {
		return dimension instanceof NamedDimension ? dimension.getTranslatedName() : dimension.languageKey;
	}

	static final class NamedDimension extends Dimension {
		final String englishName;
		final String configKey;
		final String loadingBackground;

		NamedDimension(String languageKey, String englishName, WorldType defaultWorldType, String configKey, String loadingBackground) {
			super(languageKey, Dimension.OVERWORLD, 1.0F, null, defaultWorldType);
			this.englishName = englishName;
			this.configKey = configKey;
			this.loadingBackground = loadingBackground;
		}

		@Override
		public String getTranslatedName() {
			return EnvironmentHelper.isMultiplayerServer() ? this.englishName : super.getTranslatedName();
		}
	}

	public static void attachPortalBlocks(Block<? extends BlockLogicPortal> hubDoor, Block<? extends BlockLogicPortal> cypressDoor,
	                                      Block<? extends BlockLogicPortal> zombiesDoor, Block<? extends BlockLogicPortal> freerunDoor) {
		attach(HUB, hubDoor, "the Hub");
		attach(CYPRESS, cypressDoor, "Cypress");
		attach(ZOMBIES, zombiesDoor, "Zombies");
		attach(FREERUN, freerunDoor, "Freerun");
	}

	private static void attach(Dimension dimension, Block<? extends BlockLogicPortal> portal, String name) {
		try {
			Field field = Dimension.class.getField("portalBlock");
			field.setAccessible(true);
			field.set(dimension, portal);
		} catch (ReflectiveOperationException | RuntimeException e) {
			AlphaVer.LOGGER.error("Could not attach {}'s door block; travel to and from it will fail inside PortalHandler.",
				name, e);
		}
	}

	public static synchronized void register() {
		if (registrationAttempted) {
			return;
		}
		registrationAttempted = true;
		try {
			int[] ids = AVDimensionIds.allocate(ALL);
			boolean any = false;
			for (int i = 0; i < ALL.size(); i++) {
				any |= registerOne(ALL.get(i), ids[i]);
			}
			if (any) {
				registerWorldTypeGroups();
			}
			AlphaVer.LOGGER.info("Dimension ids: {}.", AVDimensionIds.describeIdSpace());
		} catch (Throwable t) {
			AlphaVer.LOGGER.error("Registering AlphaVer's dimensions failed; they will not be reachable.", t);
		}
	}

	private static boolean registerOne(NamedDimension dimension, int id) {
		if (id < 0) {

			return false;
		}
		try {
			Dimension.registerDimension(id, dimension);
		} catch (IllegalArgumentException raced) {

			AlphaVer.LOGGER.error("Dimension id {} was taken between choosing it and claiming it; {} will not be reachable.",
				id, dimension.englishName, raced);
			return false;
		}
		AlphaVer.LOGGER.info("Registered {} as dimension {}.", dimension.englishName, id);
		return true;
	}

	public static boolean isRegistered(Dimension dimension) {
		return dimension != null && Dimension.getDimensionList().containsValue(dimension);
	}

	private static void registerWorldTypeGroups() {
		List<Integer> plugged = AVDimensionIds.plugHoles();
		int groups;
		try {
			groups = WorldTypeGroups.GROUPS.size();
		} catch (Throwable sparse) {

			AlphaVer.LOGGER.error("BTA's world type groups could not be built over dimension ids {}; creating a world may crash.",
				AVDimensionIds.describeIdSpace(), sparse);
			return;
		} finally {
			AVDimensionIds.unplugHoles(plugged);
		}
		int healed = 0;
		for (WorldTypeGroups.Group group : WorldTypeGroups.GROUPS) {
			for (NamedDimension dimension : ALL) {
				if (isRegistered(dimension)) {
					group.with(dimension, dimension.defaultWorldType);
				}
			}
			for (Dimension dimension : Dimension.getDimensionList().values()) {
				try {
					group.get(dimension);
				} catch (NullPointerException missing) {
					WorldType fallback = dimension.defaultWorldType;
					group.with(dimension, fallback);
					healed++;
				}
			}
		}
		AlphaVer.LOGGER.info("Added AlphaVer's dimensions to {} world-type groups{}.", groups,
			healed == 0 ? "" : " and filled " + healed + " missing group entries for other dimensions");
	}
}
