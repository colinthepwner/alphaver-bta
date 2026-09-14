package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.gen.AlphaMath;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureVein {
	private FeatureVein() {}

	public static void ore(World world, Random rand, int x, int y, int z, int id, int lowId, int size) {
		int stone = Blocks.STONE.id();
		int lowStone = AVBlocks.LOW_RIVER_STONE.id();

		float angle = rand.nextFloat() * 3.1415927F;
		double startX = x + 8 + AlphaMath.sin(angle) * size / 8.0F;
		double endX = x + 8 - AlphaMath.sin(angle) * size / 8.0F;
		double startZ = z + 8 + AlphaMath.cos(angle) * size / 8.0F;
		double endZ = z + 8 - AlphaMath.cos(angle) * size / 8.0F;
		double startY = y + rand.nextInt(3) + 2;
		double endY = y + rand.nextInt(3) + 2;

		for (int step = 0; step <= size; step++) {
			double cx = startX + (endX - startX) * step / size;
			double cy = startY + (endY - startY) * step / size;
			double cz = startZ + (endZ - startZ) * step / size;
			double scale = rand.nextDouble() * size / 16.0;
			double width = (AlphaMath.sin(step * 3.1415927F / size) + 1.0F) * scale + 1.0;
			double height = (AlphaMath.sin(step * 3.1415927F / size) + 1.0F) * scale + 1.0;

			for (int bx = (int) (cx - width / 2.0); bx <= (int) (cx + width / 2.0); bx++) {
				for (int by = (int) (cy - height / 2.0); by <= (int) (cy + height / 2.0); by++) {
					for (int bz = (int) (cz - width / 2.0); bz <= (int) (cz + width / 2.0); bz++) {
						double dx = (bx + 0.5 - cx) / (width / 2.0);
						double dy = (by + 0.5 - cy) / (height / 2.0);
						double dz = (bz + 0.5 - cz) / (width / 2.0);
						if (dx * dx + dy * dy + dz * dz >= 1.0) {
							continue;
						}
						int existing = world.getBlockId(bx, by, bz);
						if (existing != stone && existing != lowStone) {
							continue;
						}
						world.setBlock(bx, by, bz, by <= 10 ? lowId : id);
						if (by <= 10) {
							for (int ox = -1; ox <= 1; ox += 2) {
								for (int oy = -1; oy <= 1; oy += 2) {
									for (int oz = -1; oz <= 1; oz += 2) {
										if (world.getBlockId(bx + ox, by + oy, bz + oz) == stone) {
											world.setBlock(bx + ox, by + oy, bz + oz, lowStone);
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	public static void clay(World world, Random rand, int x, int y, int z, int size) {
		if (world.getBlockMaterial(x, y, z) != Materials.WATER) {
			return;
		}
		int sand = Blocks.SAND.id();
		int clay = Blocks.BLOCK_CLAY.id();

		float angle = rand.nextFloat() * 3.1415927F;
		double startX = x + 8 + AlphaMath.sin(angle) * size / 8.0F;
		double endX = x + 8 - AlphaMath.sin(angle) * size / 8.0F;
		double startZ = z + 8 + AlphaMath.cos(angle) * size / 8.0F;
		double endZ = z + 8 - AlphaMath.cos(angle) * size / 8.0F;
		double startY = y + rand.nextInt(3) + 2;
		double endY = y + rand.nextInt(3) + 2;

		for (int step = 0; step <= size; step++) {
			double cx = startX + (endX - startX) * step / size;
			double cy = startY + (endY - startY) * step / size;
			double cz = startZ + (endZ - startZ) * step / size;
			double scale = rand.nextDouble() * size / 16.0;
			double width = (AlphaMath.sin(step * 3.1415927F / size) + 1.0F) * scale + 1.0;
			double height = (AlphaMath.sin(step * 3.1415927F / size) + 1.0F) * scale + 1.0;

			for (int bx = (int) (cx - width / 2.0); bx <= (int) (cx + width / 2.0); bx++) {
				for (int by = (int) (cy - height / 2.0); by <= (int) (cy + height / 2.0); by++) {
					for (int bz = (int) (cz - width / 2.0); bz <= (int) (cz + width / 2.0); bz++) {
						double dx = (bx + 0.5 - cx) / (width / 2.0);
						double dy = (by + 0.5 - cy) / (height / 2.0);
						double dz = (bz + 0.5 - cz) / (width / 2.0);
						if (dx * dx + dy * dy + dz * dz < 1.0 && world.getBlockId(bx, by, bz) == sand) {
							world.setBlock(bx, by, bz, clay);
						}
					}
				}
			}
		}
	}
}
