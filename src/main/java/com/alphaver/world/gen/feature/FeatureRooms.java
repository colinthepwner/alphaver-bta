package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.item.AVItems;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntityChest;
import net.minecraft.core.block.entity.TileEntityMobSpawner;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureRooms {
	private FeatureRooms() {}

	private static final int ROOM_HEIGHT = 3;

	public static boolean dungeon(World world, Random rand, int x, int y, int z) {
		int rx = rand.nextInt(2) + 2;
		int rz = rand.nextInt(2) + 2;
		int openings = 0;

		for (int ix = x - rx - 1; ix <= x + rx + 1; ix++) {
			for (int iy = y - 1; iy <= y + ROOM_HEIGHT + 1; iy++) {
				for (int iz = z - rz - 1; iz <= z + rz + 1; iz++) {
					Material material = world.getBlockMaterial(ix, iy, iz);
					if ((iy == y - 1 || iy == y + ROOM_HEIGHT + 1) && !material.isSolid()) {
						return false;
					}
					if ((ix == x - rx - 1 || ix == x + rx + 1 || iz == z - rz - 1 || iz == z + rz + 1)
						&& iy == y && world.getBlockId(ix, iy, iz) == 0 && world.getBlockId(ix, iy + 1, iz) == 0) {
						openings++;
					}
				}
			}
		}
		if (openings < 1 || openings > 5) {
			return false;
		}

		int mossy = Blocks.COBBLE_STONE_MOSSY.id();
		int cobble = Blocks.COBBLE_STONE.id();
		for (int ix = x - rx - 1; ix <= x + rx + 1; ix++) {
			for (int iy = y + ROOM_HEIGHT; iy >= y - 1; iy--) {
				for (int iz = z - rz - 1; iz <= z + rz + 1; iz++) {
					if (ix != x - rx - 1 && iy != y - 1 && iz != z - rz - 1 && ix != x + rx + 1
						&& iy != y + ROOM_HEIGHT + 1 && iz != z + rz + 1) {
						world.setBlockWithNotify(ix, iy, iz, 0);
					} else if (iy >= 0 && !world.getBlockMaterial(ix, iy - 1, iz).isSolid()) {
						world.setBlockWithNotify(ix, iy, iz, 0);
					} else if (world.getBlockMaterial(ix, iy, iz).isSolid()) {
						if (iy == y - 1 && rand.nextInt(4) != 0) {
							world.setBlockWithNotify(ix, iy, iz, mossy);
						} else {
							world.setBlockWithNotify(ix, iy, iz, cobble);
						}
					}
				}
			}
		}

		for (int chest = 0; chest < 2; chest++) {
			for (int attempt = 0; attempt < 3; attempt++) {
				int cx = x + rand.nextInt(rx * 2 + 1) - rx;
				int cz = z + rand.nextInt(rz * 2 + 1) - rz;
				if (world.getBlockId(cx, y, cz) != 0) {
					continue;
				}
				if (solidWalls(world, cx, y, cz) == 1) {
					fillChest(world, rand, cx, y, cz, false);
					break;
				}
			}
		}

		world.setBlockWithNotify(x, y, z, Blocks.MOBSPAWNER.id());
		String mob = dungeonMob(rand);
		if (world.getTileEntity(x, y, z) instanceof TileEntityMobSpawner spawner) {
			spawner.setMobId(mob);
		}
		return true;
	}

	public static boolean floodedVault(World world, Random rand, int x, int y, int z) {
		int still = Blocks.FLUID_WATER_STILL.id();
		int flowing = Blocks.FLUID_WATER_FLOWING.id();
		int rx = rand.nextInt(2) + 2;
		int rz = rand.nextInt(2) + 2;
		int contacts = 0;

		for (int ix = x - rx - 1; ix <= x + rx + 1; ix++) {
			for (int iy = y - 1; iy <= y + ROOM_HEIGHT + 1; iy++) {
				for (int iz = z - rz - 1; iz <= z + rz + 1; iz++) {
					boolean edge = ix == x - rx - 1 || ix == x + rx + 1 || iz == z - rz - 1 || iz == z + rz + 1;
					int id = world.getBlockId(ix, iy, iz);
					int above = world.getBlockId(ix, iy + 1, iz);

					if (edge && iy == y && id == still || id == flowing && above == still || above == flowing) {
						contacts++;
					}
				}
			}
		}
		if (!(contacts >= 1 && contacts <= 5 && rand.nextBoolean())) {
			return false;
		}

		int stone = Blocks.STONE.id();
		int smooth = AVBlocks.SMOOTH_STONE.id();
		int wart = AVBlocks.LOW_WART.id();
		for (int ix = x - rx - 1; ix <= x + rx + 1; ix++) {
			for (int iy = y + ROOM_HEIGHT; iy >= y - 1; iy--) {
				for (int iz = z - rz - 1; iz <= z + rz + 1; iz++) {
					if (ix != x - rx - 1 && iy != y - 1 && iz != z - rz - 1 && ix != x + rx + 1
						&& iy != y + ROOM_HEIGHT + 1 && iz != z + rz + 1) {
						world.setBlockWithNotify(ix, iy, iz, still);
					} else if (iy >= 0 && world.getBlockMaterial(ix, iy - 1, iz).isSolid()) {
						world.setBlockWithNotify(ix, iy, iz, stone);
					} else if (!world.getBlockMaterial(ix, iy, iz).isSolid() && (iy == y - 1 || rand.nextInt(12) == 1)) {
						if (iy == y - 1 && rand.nextInt(8) != 0) {
							world.setBlockWithNotify(ix, iy, iz, smooth);
						} else {
							world.setBlockWithNotify(ix, iy, iz, iy == y - 1 ? wart : smooth);
						}
					}
				}
			}
		}

		for (int chest = 0; chest < 2; chest++) {
			for (int attempt = 0; attempt < 2; attempt++) {
				int cx = x + rand.nextInt(rx * 2 + 1) - rx;
				int cz = z + rand.nextInt(rz * 2 + 1) - rz;
				if (solidWalls(world, cx, y, cz) != 1) {
					fillChest(world, rand, cx, y, cz, true);
					break;
				}
			}
		}
		return true;
	}

	private static int solidWalls(World world, int x, int y, int z) {
		int walls = 0;
		if (world.getBlockMaterial(x - 1, y, z).isSolid()) {
			walls++;
		}
		if (world.getBlockMaterial(x + 1, y, z).isSolid()) {
			walls++;
		}
		if (world.getBlockMaterial(x, y, z - 1).isSolid()) {
			walls++;
		}
		if (world.getBlockMaterial(x, y, z + 1).isSolid()) {
			walls++;
		}
		return walls;
	}

	private static void fillChest(World world, Random rand, int x, int y, int z, boolean vault) {
		world.setBlockWithNotify(x, y, z, Blocks.CHEST_PLANKS_OAK.id());
		TileEntityChest chest = world.getTileEntity(x, y, z) instanceof TileEntityChest found ? found : null;
		for (int roll = 0; roll < 8; roll++) {
			ItemStack stack = vault ? vaultLoot(rand) : dungeonLoot(rand);
			if (stack != null) {

				int slot = rand.nextInt(chest != null ? chest.getContainerSize() : 27);
				if (chest != null) {
					chest.setItem(slot, stack);
				}
			}
		}
	}

	@Nullable
	private static ItemStack dungeonLoot(Random rand) {
		int roll = rand.nextInt(11);
		if (roll == 0) {
			return new ItemStack(Items.SADDLE);
		} else if (roll == 1) {
			return new ItemStack(Items.INGOT_IRON, rand.nextInt(4) + 1);
		} else if (roll == 2) {
			return new ItemStack(Items.FOOD_BREAD);
		} else if (roll == 3) {
			return new ItemStack(Items.WHEAT, rand.nextInt(4) + 1);
		} else if (roll == 4) {
			return new ItemStack(Items.GUNPOWDER, rand.nextInt(4) + 1);
		} else if (roll == 5) {
			return new ItemStack(Items.STRING, rand.nextInt(4) + 1);
		} else if (roll == 6) {
			return new ItemStack(Items.BUCKET_IRON);
		} else if (roll == 7 && rand.nextInt(100) == 0) {
			return new ItemStack(Items.FOOD_APPLE_GOLD);
		} else if (roll == 8 && rand.nextInt(2) == 0) {
			return new ItemStack(AVItems.GREENSTONE, rand.nextInt(4) + 1);
		} else if (roll == 9 && rand.nextInt(10) == 0) {

			return new ItemStack(rand.nextInt(2) == 0 ? AVItems.RECORD_LEMURIA : AVItems.RECORD_HIDDEN_DEN);
		}
		return null;
	}

	@Nullable
	private static ItemStack vaultLoot(Random rand) {
		int roll = rand.nextInt(13);
		if (roll == 0) {
			return new ItemStack(Items.SADDLE);
		} else if (roll == 1) {
			return new ItemStack(Items.INGOT_GOLD, rand.nextInt(4) + 2);
		} else if (roll == 2) {
			return CypressLoot.pear();
		} else if (roll == 3) {
			return CypressLoot.teaLeaf(rand.nextInt(4) + 1);
		} else if (roll == 4) {
			return new ItemStack(Items.GUNPOWDER, rand.nextInt(4) + 1);
		} else if (roll == 5) {
			return new ItemStack(Items.PAPER, rand.nextInt(4) + 1);
		} else if (roll == 6) {
			return new ItemStack(Items.BUCKET_IRON);
		} else if (roll == 7) {
			return CypressLoot.rainConch();
		} else if (roll == 8) {
			return CypressLoot.hoursLongPast(1);
		} else if (roll == 9) {
			return CypressLoot.hoursLongPast(2);
		} else if (roll == 10 && rand.nextInt(100) == 0) {
			return CypressLoot.obsidianPear();
		} else if (roll == 11 && rand.nextInt(2) == 0) {
			return new ItemStack(AVItems.GREENSTONE, rand.nextInt(8) + 2);
		} else if (roll == 12 && rand.nextInt(10) == 0) {
			return CypressLoot.sandcastles();
		}
		return null;
	}

	private static String dungeonMob(Random rand) {
		return switch (rand.nextInt(4)) {
			case 0 -> "Skeleton";
			case 3 -> "Spider";
			default -> "Zombie";
		};
	}
}
