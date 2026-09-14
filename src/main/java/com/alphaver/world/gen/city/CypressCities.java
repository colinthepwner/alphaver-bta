package com.alphaver.world.gen.city;

import com.alphaver.block.AVBlocks;
import com.alphaver.block.BlockLogicCypressPlate;
import com.alphaver.world.gen.CypressPalette;
import com.alphaver.world.gen.feature.CypressLoot;
import com.alphaver.world.gen.noise.AlphaPerlinNoise;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class CypressCities {

	public enum Type {
		EMPTY,
		PATHWAY,
		TOWER
	}

	private enum Side {
		DOOR,
		BLACK_GLASS,
		WHITE_GLASS,
		BLUE_GLASS,
		LIME_GLASS,
		PLATE_DECOR,
		DOORLESS
	}

	private static final float WEIGHT_EMPTY = 0.01F;
	private static final float WEIGHT_PATHWAY = 1.1F;
	private static final float WEIGHT_TOWER = 1.0F;

	private static final float TOTAL_WEIGHT = WEIGHT_EMPTY + WEIGHT_PATHWAY + WEIGHT_TOWER;

	private static final boolean[] BASE = new boolean[256];

	static {
		int[][] rows = {{4, 7, 8}, {5, 5, 10}, {6, 5, 10}, {7, 4, 11}, {8, 4, 11}, {9, 5, 10}, {10, 5, 10}, {11, 7, 8}};
		for (int[] row : rows) {
			for (int x = row[1]; x <= row[2]; x++) {
				BASE[x + row[0] * 16] = true;
			}
		}
	}

	private final AlphaPerlinNoise weightNoise;
	private final AlphaPerlinNoise[] masks = new AlphaPerlinNoise[4];
	private final AlphaPerlinNoise floorNoise;
	private final AlphaPerlinNoise sideNoise;

	public CypressCities(Random constructorRandom) {
		this.weightNoise = new AlphaPerlinNoise(constructorRandom);
		for (int i = 0; i < this.masks.length; i++) {
			this.masks[i] = new AlphaPerlinNoise(constructorRandom);
		}

		new AlphaPerlinNoise(constructorRandom);

		this.floorNoise = new AlphaPerlinNoise(constructorRandom);
		this.sideNoise = new AlphaPerlinNoise(constructorRandom);
		new AlphaPerlinNoise(constructorRandom);
	}

	public void generate(World world, int chunkX, int chunkZ, boolean sandWorld) {
		switch (this.classify(chunkX, chunkZ)) {
			case TOWER -> this.tower(world, chunkX, chunkZ);
			case PATHWAY -> {
				if (!sandWorld) {
					this.pathway(world, chunkX, chunkZ);
				}
			}
			default -> {
			}
		}
	}

	public Type classify(int chunkX, int chunkZ) {
		if (Math.pow(Math.min(100, Math.abs(chunkX)), 2.0) + Math.pow(Math.min(100, Math.abs(chunkZ)), 2.0) < 256.0) {
			return Type.EMPTY;
		}
		for (AlphaPerlinNoise mask : this.masks) {
			if (mask.generateNoise(chunkX / 16.0, chunkZ / 16.0) < 0.5 * 2.0 - 1.0) {
				return Type.EMPTY;
			}
		}
		double weight = (this.weightNoise.generateNoise(chunkX, chunkZ) / 2.0 + 0.5) * TOTAL_WEIGHT;
		if (weight <= (double) WEIGHT_EMPTY) {
			return Type.EMPTY;
		}
		weight -= WEIGHT_EMPTY;
		if (weight <= (double) WEIGHT_PATHWAY) {
			return Type.PATHWAY;
		}
		weight -= WEIGHT_PATHWAY;
		if (weight <= (double) WEIGHT_TOWER) {
			return Type.TOWER;
		}
		return Type.EMPTY;
	}

	private void tower(World world, int chunkX, int chunkZ) {
		int top = (int) ((this.floorNoise.generateNoise(chunkX, chunkZ) / 2.0 + 1.0) * 8.0);
		for (int k = top; k >= 0; k--) {
			int y = 30 + k * 8;
			if (!airAbove(world, chunkX, y, chunkZ)) {
				if (top != k) {
					foundation(world, chunkX, y, chunkZ);
				}
				break;
			}
			this.floor(world, chunkX, y, chunkZ);
		}
	}

	private static boolean airAbove(World world, int chunkX, int y, int chunkZ) {
		int budget = 256;
		for (int dy = 0; dy < 8; dy++) {
			for (int dx = 0; dx < 15; dx++) {
				for (int dz = 0; dz < 15; dz++) {
					if (solid(world, chunkX * 16 + dx, y + dy, chunkZ * 16 + dz) && --budget == 0) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private static void foundation(World world, int chunkX, int y, int chunkZ) {
		int smooth = AVBlocks.SMOOTH_STONE.id();
		for (int dx = 2; dx <= 13; dx++) {
			for (int dz = 2; dz <= 13; dz++) {
				for (int dy = 5; dy < 8; dy++) {
					if (!solid(world, chunkX * 16 + dx, y + dy, chunkZ * 16 + dz)) {
						world.setBlock(chunkX * 16 + dx, y + dy, chunkZ * 16 + dz, smooth);
					}
				}
			}
		}
		int pillar = AVBlocks.CRUDE_PILLAR.id();
		for (int dx = 3; dx <= 12; dx++) {
			for (int dz = 3; dz <= 12; dz++) {
				int x = chunkX * 16 + dx;
				int z = chunkZ * 16 + dz;
				for (int py = y + 4; py >= 0; py--) {
					if (solid(world, x, py, z)) {
						break;
					}
					world.setBlock(x, py, z, pillar);
				}
			}
		}
	}

	private static boolean solid(World world, int x, int y, int z) {
		int id = world.getBlockId(x, y, z);
		if (id == 0 || !Blocks.solid[id]) {
			return false;
		}
		Block<?> block = Blocks.blocksList[id];
		return block != null && block.isCubeShaped();
	}

	private void floor(World world, int chunkX, int y, int chunkZ) {
		int bx = chunkX * 16;
		int bz = chunkZ * 16;
		int slatePillar = AVBlocks.PILLAR_SLATE.id();
		int slateBricks = AVBlocks.BRICK_SLATE.id();
		int smooth = AVBlocks.SMOOTH_STONE.id();
		int flamewood = AVBlocks.PILLAR_FLAMEWOOD.id();

		for (int h = 7; h > 0; h--) {
			world.setBlock(bx + 2, y + h, bz + 2, slatePillar);
			world.setBlock(bx + 2, y + h, bz + 13, slatePillar);
			world.setBlock(bx + 13, y + h, bz + 2, slatePillar);
			world.setBlock(bx + 13, y + h, bz + 13, slatePillar);
			if (h == 7) {
				continue;
			}
			for (int x = 3; x <= 12; x++) {
				int inset = x >= 6 && x <= 9 ? 1 : 0;
				if (inset != 0 || h < 5) {
					world.setBlock(bx + x, y + h, bz + 3 + inset, slateBricks);
					world.setBlock(bx + x, y + h, bz + 12 - inset, slateBricks);
				}
			}
			for (int z = 4; z <= 11; z++) {
				int inset = z >= 6 && z <= 9 ? 1 : 0;
				if (inset != 0 || h < 5) {
					world.setBlock(bx + 3 + inset, y + h, bz + z, slateBricks);
					world.setBlock(bx + 12 - inset, y + h, bz + z, slateBricks);
				}
			}
		}

		for (int x = 2; x <= 13; x++) {
			for (int z = 2; z <= 13; z++) {
				world.setBlock(bx + x, y, bz + z, smooth);
			}
		}

		for (int v = 1; v < 15; v++) {
			for (int w = 7; w <= 8; w++) {
				world.setBlock(bx + v, y + 7, bz + w, flamewood);
				world.setBlock(bx + w, y + 7, bz + v, flamewood);
			}
		}
		for (int x = 5; x <= 10; x++) {
			for (int z = 5; z <= 10; z++) {
				world.setBlock(bx + x, y + 7, bz + z, flamewood);
			}
		}

		for (int v = 0; v < 4; v++) {
			world.setBlock(bx + 4 - v, y + 6, bz + 6, flamewood);
			world.setBlock(bx + 4 - v, y + 6, bz + 9, flamewood);
			world.setBlock(bx + 11 + v, y + 6, bz + 6, flamewood);
			world.setBlock(bx + 11 + v, y + 6, bz + 9, flamewood);
			world.setBlock(bx + 6, y + 6, bz + 4 - v, flamewood);
			world.setBlock(bx + 9, y + 6, bz + 4 - v, flamewood);
			world.setBlock(bx + 6, y + 6, bz + 11 + v, flamewood);
			world.setBlock(bx + 9, y + 6, bz + 11 + v, flamewood);
		}
		int[][] corners6 = {{4, 4}, {4, 5}, {5, 4}, {11, 11}, {10, 11}, {11, 10}, {11, 4}, {10, 4}, {11, 5}, {4, 11}, {5, 11}, {4, 10}};
		for (int[] c : corners6) {
			world.setBlock(bx + c[0], y + 6, bz + c[1], flamewood);
		}

		for (int v = 0; v < 3; v++) {
			world.setBlock(bx + 3 - v, y + 5, bz + 5, flamewood);
			world.setBlock(bx + 3 - v, y + 5, bz + 10, flamewood);
			world.setBlock(bx + 12 + v, y + 5, bz + 5, flamewood);
			world.setBlock(bx + 12 + v, y + 5, bz + 10, flamewood);
			world.setBlock(bx + 5, y + 5, bz + 3 - v, flamewood);
			world.setBlock(bx + 10, y + 5, bz + 3 - v, flamewood);
			world.setBlock(bx + 5, y + 5, bz + 12 + v, flamewood);
			world.setBlock(bx + 10, y + 5, bz + 12 + v, flamewood);
		}
		for (int v = 0; v < 2; v++) {
			world.setBlock(bx + 4, y + 5, bz + 3 - v, flamewood);
			world.setBlock(bx + 3, y + 5, bz + 3 - v, flamewood);
			world.setBlock(bx + 3 - v, y + 5, bz + 4, flamewood);
			world.setBlock(bx + 3 - v, y + 5, bz + 3, flamewood);
			world.setBlock(bx + 11, y + 5, bz + 12 + v, flamewood);
			world.setBlock(bx + 12, y + 5, bz + 12 + v, flamewood);
			world.setBlock(bx + 12 + v, y + 5, bz + 11, flamewood);
			world.setBlock(bx + 12 + v, y + 5, bz + 12, flamewood);
			world.setBlock(bx + 4, y + 5, bz + 12 + v, flamewood);
			world.setBlock(bx + 3, y + 5, bz + 12 + v, flamewood);
			world.setBlock(bx + 3 - v, y + 5, bz + 11, flamewood);
			world.setBlock(bx + 3 - v, y + 5, bz + 12, flamewood);
			world.setBlock(bx + 11, y + 5, bz + 3 - v, flamewood);
			world.setBlock(bx + 12, y + 5, bz + 3 - v, flamewood);
			world.setBlock(bx + 12 + v, y + 5, bz + 4, flamewood);
			world.setBlock(bx + 12 + v, y + 5, bz + 3, flamewood);
		}

		int level = y / 8;
		Side[] sides = new Side[4];
		for (int s = 0; s < 4; s++) {
			sides[s] = this.sideType(chunkX, level, chunkZ, s);
		}
		for (int s = 0; s < 4; s++) {
			this.decorateSide(world, chunkX, chunkZ, bx, y, bz, level, s, sides[s]);
		}

		chest(world, bx + 4, y + 1, bz + 4);
		chest(world, bx + 11, y + 1, bz + 4);
		chest(world, bx + 4, y + 1, bz + 11);
		chest(world, bx + 11, y + 1, bz + 11);
	}

	private Side sideType(int chunkX, int level, int chunkZ, int side) {
		int a = chunkX * 5436345 + level * -12416265;
		int b = chunkZ * 4256285 + side * -82344232;
		double d = this.sideNoise.generateNoise(a / 634.0, b / 634.0);
		if (d < 0.0) {
			d = -d;
		}
		if (d > 1.0) {
			d %= 1.0;
		}
		if (d < 0.3) {
			return Side.DOOR;
		} else if (d < 0.4) {
			return Side.BLACK_GLASS;
		} else if (d < 0.5) {
			return Side.WHITE_GLASS;
		} else if (d < 0.6) {
			return Side.BLUE_GLASS;
		} else if (d < 0.7) {
			return Side.LIME_GLASS;
		} else if (d < 0.8) {
			return Side.PLATE_DECOR;
		} else if (d < 0.9) {
			return Side.DOORLESS;
		}
		return Side.DOOR;
	}

	private void decorateSide(World world, int chunkX, int chunkZ, int bx, int y, int bz, int level, int s, Side type) {
		switch (type) {
			case WHITE_GLASS -> window(world, bx, y, bz, s, Blocks.GLASS.id());
			case BLUE_GLASS -> window(world, bx, y, bz, s, AVBlocks.GLASS_BLUE.id());
			case LIME_GLASS -> window(world, bx, y, bz, s, AVBlocks.GLASS_GREEN.id());
			case BLACK_GLASS -> window(world, bx, y, bz, s, AVBlocks.GLASS_BLACK.id());
			case PLATE_DECOR -> plates(world, bx, y, bz, s);
			case DOOR -> {
				if (this.pairsWithNeighbour(chunkX, chunkZ, level, s)) {
					int wire = AVBlocks.WIREFRAME.id();
					place(world, bx, y, bz, 0, 0, 0, wire, s);
					place(world, bx, y, bz, 0, 0, 1, wire, s);
					place(world, bx, y, bz, 0, 1, 0, wire, s);
					place(world, bx, y, bz, 0, 1, 1, wire, s);
					bridge(world, bx, y, bz, s);
				}
			}
			case DOORLESS -> {
				if (this.pairsWithNeighbour(chunkX, chunkZ, level, s)) {
					bridge(world, bx, y, bz, s);
				}
			}
			default -> {
			}
		}
	}

	private boolean pairsWithNeighbour(int chunkX, int chunkZ, int level, int s) {
		int dx = s == 0 ? 1 : s == 2 ? -1 : 0;
		int dz = s == 1 ? 1 : s == 3 ? -1 : 0;
		return this.sideType(chunkX + dx, level, chunkZ + dz, (s + 2) % 4) == Side.DOOR
			&& this.classify(chunkX + dx, chunkZ + dz) == Type.TOWER;
	}

	private static void window(World world, int bx, int y, int bz, int s, int glass) {
		place(world, bx, y, bz, 0, 1, 0, glass, s);
		place(world, bx, y, bz, 0, 2, 0, glass, s);
		place(world, bx, y, bz, 0, 3, 0, glass, s);
		place(world, bx, y, bz, 0, 1, 1, glass, s);
		place(world, bx, y, bz, 0, 2, 1, glass, s);
		place(world, bx, y, bz, 0, 3, 1, glass, s);
		balcony(world, bx, y, bz, s);
	}

	private static void plates(World world, int bx, int y, int bz, int s) {
		int smooth = AVBlocks.SMOOTH_STONE.id();
		place(world, bx, y, bz, 0, 1, 0, smooth, s);
		place(world, bx, y, bz, 0, 2, 0, smooth, s);
		place(world, bx, y, bz, 0, 3, 0, smooth, s);
		place(world, bx, y, bz, 0, 1, 1, smooth, s);
		place(world, bx, y, bz, 0, 2, 1, smooth, s);
		place(world, bx, y, bz, 0, 3, 1, smooth, s);
		int meta = plateFacing(s);
		placeWithMeta(world, bx, y, bz, 1, 1, 0, AVBlocks.PLATE_SOLAR.id(), meta, s);
		placeWithMeta(world, bx, y, bz, 1, 2, 0, AVBlocks.PLATE_DENIAL.id(), meta, s);
		placeWithMeta(world, bx, y, bz, 1, 3, 0, AVBlocks.PLATE_SWITCH.id(), meta, s);
		placeWithMeta(world, bx, y, bz, 1, 1, 1, AVBlocks.PLATE_LOOP.id(), meta, s);
		placeWithMeta(world, bx, y, bz, 1, 2, 1, AVBlocks.PLATE_PART.id(), meta, s);
		placeWithMeta(world, bx, y, bz, 1, 3, 1, AVBlocks.PLATE_ASSOCIATION.id(), meta, s);
		balcony(world, bx, y, bz, s);
	}

	private static void balcony(World world, int bx, int y, int bz, int s) {
		int grass = CypressPalette.grass();
		int slatePillar = AVBlocks.PILLAR_SLATE.id();
		int flamewood = AVBlocks.PILLAR_FLAMEWOOD.id();
		for (int c = -1; c <= 2; c++) {
			place(world, bx, y, bz, 1, 0, c, grass, s);
		}
		for (int c = -2; c <= 3; c++) {
			place(world, bx, y, bz, 2, 0, c, slatePillar, s);
		}
		for (int b = 1; b <= 3; b++) {
			place(world, bx, y, bz, 2, b, 3, flamewood, s);
		}
		for (int b = 1; b <= 3; b++) {
			place(world, bx, y, bz, 2, b, -2, flamewood, s);
		}
	}

	private static void bridge(World world, int bx, int y, int bz, int s) {
		int smooth = AVBlocks.SMOOTH_STONE.id();
		place(world, bx, y, bz, 3, -1, 0, smooth, s);
		place(world, bx, y, bz, 3, -1, 1, smooth, s);
		place(world, bx, y, bz, 4, -1, 0, smooth, s);
		place(world, bx, y, bz, 4, -1, 1, smooth, s);
	}

	private static void place(World world, int bx, int y, int bz, int a, int b, int c, int id, int s) {
		int[] pos = sidePosition(bx, y, bz, a, b, c, s);
		world.setBlock(pos[0], pos[1], pos[2], id);
	}

	private static void placeWithMeta(World world, int bx, int y, int bz, int a, int b, int c, int id, int meta, int s) {
		int[] pos = sidePosition(bx, y, bz, a, b, c, s);
		world.setBlockAndMetadata(pos[0], pos[1], pos[2], id, meta);
	}

	private static int[] sidePosition(int bx, int y, int bz, int a, int b, int c, int s) {
		int py = y + 1 + b;
		return switch (s) {
			case 0 -> new int[]{bx + 11 + a, py, bz + 7 + c};
			case 1 -> new int[]{bx + 8 - c, py, bz + 11 + a};
			case 2 -> new int[]{bx + 7 + c, py, bz + 4 - a};
			default -> new int[]{bx + 4 - a, py, bz + 8 - c};
		};
	}

	private static int plateFacing(int s) {
		return switch (s) {
			case 0 -> BlockLogicCypressPlate.wallMeta(-1, 0);
			case 1 -> BlockLogicCypressPlate.wallMeta(0, -1);
			case 2 -> BlockLogicCypressPlate.wallMeta(0, 1);
			default -> BlockLogicCypressPlate.wallMeta(1, 0);
		};
	}

	private static void chest(World world, int x, int y, int z) {
		Random random = new Random(x * 456856235L - y * 184761862L + z * 4618624L + 18162412L);
		if (random.nextInt(127) != 0) {
			return;
		}

		world.setBlockWithNotify(x, y, z, Blocks.CHEST_PLANKS_OAK.id());
		if (!(world.getTileEntity(x, y, z) instanceof TileEntityChest chest)) {
			return;
		}
		Block<?>[] firstPlates = {AVBlocks.PLATE_SOLAR, AVBlocks.PLATE_DENIAL, AVBlocks.PLATE_SWITCH, AVBlocks.PLATE_LOOP,
			AVBlocks.PLATE_PART};
		Block<?>[] secondPlates = {AVBlocks.PLATE_TRINITY, AVBlocks.PLATE_ASSOCIATION, AVBlocks.PLATE_DIALECT,
			AVBlocks.PLATE_SYLLABLES, AVBlocks.PLATE_MIRRORS};
		for (int slot = 0; slot < chest.getContainerSize(); slot++) {
			int roll = random.nextInt(30);
			ItemStack stack = null;
			if (roll < 1) {
				stack = new ItemStack(Items.INGOT_GOLD);
			} else if (roll < 6) {
				stack = new ItemStack(firstPlates[roll - 1], random.nextInt(15) + 1);
			} else if (roll < 11) {
				stack = new ItemStack(secondPlates[roll - 6], random.nextInt(15) + 1);
			} else if (roll < 12) {
				stack = new ItemStack(Items.FOOD_APPLE);
			} else if (roll < 13) {
				stack = new ItemStack(Items.FOOD_BREAD);
			} else if (roll < 14) {
				stack = CypressLoot.pear();
			} else if (roll < 15) {
				stack = CypressLoot.hoursLongPast(3);
			} else if (roll < 16) {
				stack = CypressLoot.hoursLongPast(4);
			}
			if (stack != null) {
				chest.setItem(slot, stack);
			}
		}
	}

	private void pathway(World world, int chunkX, int chunkZ) {
		boolean[] mask = BASE.clone();
		boolean east = this.isPathway(chunkX + 1, chunkZ);
		boolean west = this.isPathway(chunkX - 1, chunkZ);
		boolean south = this.isPathway(chunkX, chunkZ + 1);
		boolean north = this.isPathway(chunkX, chunkZ - 1);

		if (east) {
			fill(mask, 9, 15, 4, 11);
		}
		if (west) {
			fill(mask, 0, 6, 4, 11);
		}
		if (south) {
			fill(mask, 4, 11, 9, 15);
		}
		if (north) {
			fill(mask, 4, 11, 0, 6);
		}
		if (east && this.isPathway(chunkX + 1, chunkZ + 1) && south) {
			fill(mask, 12, 15, 12, 15);
		} else if (east && south) {
			mark(mask, 12, 12, 13, 12, 14, 12, 12, 13, 12, 14);
		}
		if (west && this.isPathway(chunkX - 1, chunkZ - 1) && north) {
			fill(mask, 0, 3, 0, 3);
		} else if (west && north) {
			mark(mask, 3, 3, 2, 3, 1, 3, 3, 2, 3, 1);
		}
		if (west && this.isPathway(chunkX - 1, chunkZ + 1) && south) {
			fill(mask, 0, 3, 12, 15);
		} else if (west && south) {
			mark(mask, 3, 12, 2, 12, 1, 12, 3, 13, 3, 14);
		}
		if (east && this.isPathway(chunkX + 1, chunkZ - 1) && north) {
			fill(mask, 12, 15, 0, 3);
		} else if (east && north) {
			mark(mask, 12, 3, 13, 3, 14, 3, 12, 2, 12, 1);
		}

		int grass = CypressPalette.grass();
		int sand = Blocks.SAND.id();
		int path = AVBlocks.GRASS_PATHWAY.id();
		int plant = AVBlocks.TALLGRASS.id();
		int bx = chunkX * 16;
		int bz = chunkZ * 16;
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				if (!mask[x + z * 16]) {
					continue;
				}
				for (int y = 128; y > 40; y--) {
					int id = world.getBlockId(bx + x, y, bz + z);
					if (id == grass || id == sand) {
						world.setBlock(bx + x, y, bz + z, path);
						if (world.getBlockId(bx + x, y + 1, bz + z) == plant) {
							world.setBlock(bx + x, y + 1, bz + z, 0);
						}
						break;
					}
				}
			}
		}
	}

	private boolean isPathway(int chunkX, int chunkZ) {
		return this.classify(chunkX, chunkZ) == Type.PATHWAY;
	}

	private static void fill(boolean[] mask, int minX, int maxX, int minZ, int maxZ) {
		for (int x = minX; x <= maxX; x++) {
			for (int z = minZ; z <= maxZ; z++) {
				mask[x + z * 16] = true;
			}
		}
	}

	private static void mark(boolean[] mask, int... coordinates) {
		for (int i = 0; i + 1 < coordinates.length; i += 2) {
			mask[coordinates[i] + coordinates[i + 1] * 16] = true;
		}
	}
}
