package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.gen.AlphaMath;
import com.alphaver.world.gen.CypressPalette;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SuppressWarnings("deprecation")
public final class HighwoodTree {

	public static final HighwoodTree SMALL = new HighwoodTree(7, 10, 30, 40, 78, 8, 7, 2, 2, 0.6283185307179586, 2, 3, 4, 2);

	public static final HighwoodTree BIG = new HighwoodTree(16, 26, 20, 50, 89, 9, 8, 3, 2, 0.2243994752564138, 4, 4, 8, 3);

	private final int heightBase;
	private final int heightRange;
	private final int branchDivisor;
	private final int foliageChanceMin;
	private final int foliageChanceFrom;
	private final int foliageStepsRange;
	private final int foliageStepsBase;
	private final int foliageLayersRange;
	private final int foliageLayersBase;
	private final double rayStep;
	private final int rayBase;
	private final int rootCountRange;
	private final int rootLengthRange;
	private final int rootLengthBase;

	private HighwoodTree(int heightBase, int heightRange, int branchDivisor, int foliageChanceMin, int foliageChanceFrom,
	                     int foliageStepsRange, int foliageStepsBase, int foliageLayersRange, int foliageLayersBase,
	                     double rayStep, int rayBase, int rootCountRange, int rootLengthRange, int rootLengthBase) {
		this.heightBase = heightBase;
		this.heightRange = heightRange;
		this.branchDivisor = branchDivisor;
		this.foliageChanceMin = foliageChanceMin;
		this.foliageChanceFrom = foliageChanceFrom;
		this.foliageStepsRange = foliageStepsRange;
		this.foliageStepsBase = foliageStepsBase;
		this.foliageLayersRange = foliageLayersRange;
		this.foliageLayersBase = foliageLayersBase;
		this.rayStep = rayStep;
		this.rayBase = rayBase;
		this.rootCountRange = rootCountRange;
		this.rootLengthRange = rootLengthRange;
		this.rootLengthBase = rootLengthBase;
	}

	public boolean grow(World world, Random random, int x, int y, int z) {
		int grass = CypressPalette.grass();
		int dirt = Blocks.DIRT.id();
		int below = world.getBlockId(x, y - 1, z);
		if (below != grass && below != dirt) {
			return true;
		}

		int log = AVBlocks.LOG_HIGHWOOD.id();
		int leaves = AVBlocks.LEAVES_HIGHWOOD.id();
		Random growth = new Random(random.nextLong() + x + y + z + world.getRandomSeed());

		List<double[]> tips = new ArrayList<>();
		int height = this.heightBase + growth.nextInt(this.heightRange);
		tips.add(new double[]{x, y + height, z});

		for (int pass = 0; !tips.isEmpty(); pass++) {
			for (double[] tip : new ArrayList<>(tips)) {
				int tx = (int) Math.round(tip[0]);
				int ty = (int) Math.round(tip[1]);
				int tz = (int) Math.round(tip[2]);
				if ((!world.getBlockMaterial(tx, ty, tz).isSolid() || world.getBlockId(tx, ty, tz) != leaves)
					&& ty >= 0
					&& (growth.nextInt(3) != 0 || tips.size() <= 3)) {
					world.setBlock(tx, ty, tz, log);
					int spawned = 0;
					while (growth.nextInt(tips.size() / this.branchDivisor + 2) <= 1 && tips.size() <= 10000) {
						if (++spawned >= 4) {
							break;
						}
						double ox = growth.nextInt(3) - 1;
						double oz = growth.nextInt(3) - 1;
						tips.add(new double[]{tip[0] + ox, tip[1] - 1.0, tip[2] + oz});
					}
					if (pass > 2 && growth.nextInt(Math.max(this.foliageChanceMin, this.foliageChanceFrom - pass)) == 3) {
						this.foliage(world, random, tx, ty, tz, log, leaves);
					}
					tips.remove(tip);
					tips.add(new double[]{tip[0], tip[1] - 1.0, tip[2]});
				} else {
					tips.remove(tip);
					int id = world.getBlockId(tx, ty, tz);
					if (id == dirt || id == grass) {
						this.roots(world, growth, tx, ty, tz);
					}
				}
			}
		}
		return true;
	}

	private void foliage(World world, Random random, int x, int y, int z, int log, int leaves) {
		float travelled = 0.0F;
		float angle = random.nextFloat() * 3.1415927F * 2.0F;
		int steps = random.nextInt(this.foliageStepsRange) + this.foliageStepsBase;
		float fy = y;
		for (int i = 0; i < steps; i++) {
			angle = (float) (angle + (random.nextFloat() - 0.5) * 0.1);
			travelled++;
			float progress = travelled / steps;
			x = (int) (x + AlphaMath.cos(angle) * (1.0F - progress));
			fy += progress;
			z = (int) (z + AlphaMath.sin(angle) * (1.0F - progress));
			y = Math.round(fy);
			world.setBlock(x, y, z, log);
		}

		y += random.nextInt(2) + 1;
		int layers = random.nextInt(this.foliageLayersRange) + this.foliageLayersBase;
		for (int layer = 0; layer < layers; layer++) {
			world.setBlock(x, y, z, leaves);
			for (float ray = 0.0F; ray < 6.283185307179586; ray = (float) (ray + this.rayStep)) {
				int length = random.nextInt(layer + this.rayBase) + layer + this.rayBase;
				float fx = x;
				float fz = z;
				for (int i = 0; i < length; i++) {
					fz += AlphaMath.sin(ray);
					fx += AlphaMath.cos(ray);
					int lx = Math.round(fx);
					int lz = Math.round(fz);
					if (!world.getBlockMaterial(lx, y - layer, lz).isSolid()) {
						world.setBlock(lx, y - layer, lz, leaves);
					}
				}
			}
		}
	}

	private void roots(World world, Random random, int x, int y, int z) {
		int roots = AVBlocks.ROOTS_HIGHWOOD.id();
		int bedrock = Blocks.BEDROCK.id();
		int count = random.nextInt(this.rootCountRange);
		for (int i = 0; i < count; i++) {
			int length = random.nextInt(this.rootLengthRange) + this.rootLengthBase;
			int rx = x;
			int ry = y;
			int rz = z;
			for (int step = 0; step < length; step++) {
				rx += random.nextInt(3) - 1;
				ry--;
				rz += random.nextInt(3) - 1;
				if (world.getBlockId(rx, ry, rz) != bedrock) {
					world.setBlock(rx, ry, rz, roots);
				}
			}
		}
	}
}
