package com.alphaver.world.biome;

import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.entity.animal.MobChicken;
import net.minecraft.core.entity.animal.MobCow;
import net.minecraft.core.entity.animal.MobPig;
import net.minecraft.core.entity.animal.MobSheep;
import net.minecraft.core.entity.monster.MobCreeper;
import net.minecraft.core.entity.monster.MobSkeleton;
import net.minecraft.core.entity.monster.MobSlime;
import net.minecraft.core.entity.monster.MobSpider;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.generate.feature.WorldFeature;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTree;
import net.minecraft.core.world.generate.feature.tree.WorldFeatureTreeFancy;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BiomeCypress extends Biome {

	private final Map<MobCategory, List<SpawnListEntry>> natural = new EnumMap<>(MobCategory.class);

	private final Map<MobCategory, List<SpawnListEntry>> naturalView = new EnumMap<>(MobCategory.class);

	public BiomeCypress(String key) {
		super(key);
		for (MobCategory category : MobCategory.values()) {
			List<SpawnListEntry> entries = new ArrayList<>();
			this.natural.put(category, entries);
			this.naturalView.put(category, Collections.unmodifiableList(entries));
		}
		this.spawnableMonsterList.clear();
		this.spawnableCreatureList.clear();
		this.spawnableWaterCreatureList.clear();
		this.spawnableAmbientCreatureList.clear();
		this.addNatural(MobCategory.MONSTER, MobSpider.class, 10);
		this.addNatural(MobCategory.MONSTER, MobZombie.class, 10);
		this.addNatural(MobCategory.MONSTER, MobSkeleton.class, 10);
		this.addNatural(MobCategory.MONSTER, MobCreeper.class, 10);
		this.addNatural(MobCategory.MONSTER, MobSlime.class, 10);
		this.addNatural(MobCategory.CREATURE, MobSheep.class, 102);
		this.addNatural(MobCategory.CREATURE, MobPig.class, 102);
		this.addNatural(MobCategory.CREATURE, MobChicken.class, 102);
		this.addNatural(MobCategory.CREATURE, MobCow.class, 102);
	}

	private void addNatural(MobCategory category, Class<? extends Mob> type, int weight) {
		this.getSpawnableList(category).add(new SpawnListEntry(type, weight));
		this.natural.get(category).add(new SpawnListEntry(type, weight));
	}

	@NotNull
	public List<SpawnListEntry> naturalSpawns(@NotNull MobCategory category) {
		return this.naturalView.get(category);
	}

	@Override
	@NotNull
	public WorldFeature getTreeFeature(@NotNull Random random) {
		WorldFeature feature = new WorldFeatureTree(Blocks.LEAVES_OAK_RETRO.id(), Blocks.LOG_OAK.id(), 4);
		if (random.nextInt(10) == 0) {
			feature = new WorldFeatureTreeFancy(Blocks.LEAVES_OAK_RETRO.id(), Blocks.LOG_OAK.id());
		}
		return feature;
	}
}
