package com.alphaver.mixin;

import com.alphaver.entity.AVForeignMobs;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.entity.SpawnListEntry;
import net.minecraft.core.enums.MobCategory;
import net.minecraft.core.world.SpawnerMobs;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

@Mixin(value = SpawnerMobs.class, remap = false)
public abstract class SpawnerMobsForeignMobsMixin {

	@WrapOperation(
		method = "performSpawning",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/core/world/biome/Biome;getSpawnableList(Lnet/minecraft/core/enums/MobCategory;)Ljava/util/List;"))
	private static List<SpawnListEntry> alphaver$withoutForeignMobs(Biome biome, MobCategory category,
	                                                              Operation<List<SpawnListEntry>> original,
	                                                              @Local(argsOnly = true) World world) {
		return AVForeignMobs.spawnList(world, biome, category, original.call(biome, category));
	}
}
