package com.alphaver.world.gen.feature;

import net.minecraft.core.world.World;

import java.util.Random;

public interface CypressTreeFeature {

	default void setScale(double x, double y, double z) {
	}

	boolean generate(World world, Random rand, int x, int y, int z);
}
