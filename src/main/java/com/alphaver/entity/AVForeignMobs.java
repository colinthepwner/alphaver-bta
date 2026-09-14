package com.alphaver.entity;

import com.alphaver.AlphaVer;
import com.alphaver.world.AVDimensions;
import com.alphaver.world.AVGameRules;
import com.alphaver.world.AVWorlds;
import com.alphaver.world.biome.BiomeCypress;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AVForeignMobs {
	private AVForeignMobs() {}

	private static final String BTA_NAMESPACE = "minecraft";

	private static final ClassValue<Owner> OWNERS = new ClassValue<>() {
		@Override
		protected Owner computeValue(Class<?> type) {
			return Owner.of(type);
		}
	};

	private static final Set<Class<?>> REPORTED = ConcurrentHashMap.newKeySet();

	private static final Map<List<SpawnListEntry>, Rebuilt> REBUILT = Collections.synchronizedMap(new IdentityHashMap<>());

	private static final AtomicBoolean ANNOUNCED = new AtomicBoolean();

	public static boolean isForeign(@Nullable Class<?> type) {
		return type != null && OWNERS.get(type).foreign();
	}

	public static boolean allowsForeignMobs(@NotNull World world) {
		return AVWorlds.isCypress(world) && AVGameRules.foreignMobsInCypress(world);
	}

	public static boolean keepsOut(@NotNull World world, @Nullable Entity entity) {
		if (world.isClientSide || !AVDimensions.isAlphaVer(world) || !(entity instanceof Mob) || entity instanceof Player) {
			return false;
		}
		return isForeign(entity.getClass()) && !allowsForeignMobs(world);
	}

	@NotNull
	public static List<SpawnListEntry> spawnList(@NotNull World world, @NotNull Biome biome, @NotNull MobCategory category,
	                                             @NotNull List<SpawnListEntry> live) {
		if (world.isClientSide || !AVDimensions.isAlphaVer(world)) {
			return live;
		}
		boolean foreignAllowed = allowsForeignMobs(world);
		if (biome instanceof BiomeCypress cypress) {
			List<SpawnListEntry> roster = cypress.naturalSpawns(category);
			return foreignAllowed ? rebuilt(live, roster) : roster;
		}

		return foreignAllowed ? live : rebuilt(live, null);
	}

	public static void report(@NotNull World world, @NotNull Entity entity, boolean removed) {
		if (!REPORTED.add(entity.getClass())) {
			return;
		}
		String where = world.dimension == null ? "an AlphaVer dimension" : AVDimensions.nameOf(world.dimension);
		String rule = AVWorlds.isCypress(world)
			? "gamerule " + AVGameRules.FOREIGN_MOBS_IN_CYPRESS_KEY + " lets them into Cypress"
			: "only Cypress takes them, with gamerule " + AVGameRules.FOREIGN_MOBS_IN_CYPRESS_KEY + " on";
		AlphaVer.LOGGER.info("{} {} {} {}: other mods' mobs are kept out of AlphaVer's dimensions ({}). Logged once per kind of mob.",
			removed ? "Removed" : "Kept", OWNERS.get(entity.getClass()).id(), removed ? "from" : "out of", where, rule);
	}

	@NotNull
	private static List<SpawnListEntry> rebuilt(@NotNull List<SpawnListEntry> live, @Nullable List<SpawnListEntry> roster) {
		Rebuilt cached = REBUILT.get(live);
		if (cached != null && cached.builtFrom(live, roster)) {
			return cached.list();
		}
		SpawnListEntry[] source = live.toArray(new SpawnListEntry[0]);
		List<SpawnListEntry> list = new ArrayList<>(source.length + (roster == null ? 0 : roster.size()));
		if (roster != null) {
			list.addAll(roster);
		}
		Set<String> foreignIds = new TreeSet<>();
		for (SpawnListEntry entry : source) {
			if (entry == null) {
				continue;
			}
			boolean foreign = isForeign(entry.entityClass);

			if (foreign == (roster != null)) {
				list.add(entry);
				if (foreign) {
					foreignIds.add(OWNERS.get(entry.entityClass).id());
				}
			}
		}
		if (!foreignIds.isEmpty() && ANNOUNCED.compareAndSet(false, true)) {
			AlphaVer.LOGGER.info("Gamerule {} is on, so Cypress spawns other mods' mobs as well as its own, among them {}.",
				AVGameRules.FOREIGN_MOBS_IN_CYPRESS_KEY, String.join(", ", foreignIds));
		}
		List<SpawnListEntry> result = Collections.unmodifiableList(list);
		REBUILT.put(live, new Rebuilt(source, roster, result));
		return result;
	}

	private record Rebuilt(SpawnListEntry[] source, @Nullable List<SpawnListEntry> roster, List<SpawnListEntry> list) {
		boolean builtFrom(List<SpawnListEntry> live, @Nullable List<SpawnListEntry> roster) {
			if (this.roster != roster || this.source.length != live.size()) {
				return false;
			}
			for (int i = 0; i < this.source.length; i++) {
				if (this.source[i] != live.get(i)) {
					return false;
				}
			}
			return true;
		}
	}

	private record Owner(boolean foreign, String id) {
		@SuppressWarnings({"unchecked", "rawtypes"})
		static Owner of(Class<?> type) {
			EntityDispatcher dispatcher = EntityDispatcher.getInstance();
			for (Class<?> c = type; dispatcher != null && c != null && Entity.class.isAssignableFrom(c); c = c.getSuperclass()) {
				EntityDispatcher.EntityDispatcherEntry<?> entry = dispatcher.entryForClass((Class) c);
				if (entry != null) {
					String namespace = entry.namespaceID.namespace();
					boolean foreign = !BTA_NAMESPACE.equals(namespace) && !AlphaVer.MOD_ID.equals(namespace);
					return new Owner(foreign, entry.namespaceID.toString());
				}
			}

			return new Owner(false, type.getName());
		}
	}
}
