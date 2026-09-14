package com.alphaver.world.hub;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import org.jetbrains.annotations.NotNull;

public class ChunkGeneratorHub extends ChunkGenerator {

	private static final int MARGIN = 3;
	private static final int SPAN = 16 + 2 * MARGIN;
	private static final int[] DX = {1, 0, -1, 0};
	private static final int[] DZ = {0, 1, 0, -1};

	private final HubLayout layout;

	public ChunkGeneratorHub(@NotNull World world) {
		super(world, new ChunkDecoratorHub(world));
		this.layout = HubLayout.forSeed(world.getRandomSeed());
	}

	@NotNull
	@Override
	protected ChunkGeneratorResult doBlockGeneration(@NotNull Chunk chunk) {
		ChunkGeneratorResult result = new ChunkGeneratorResult();
		int baseX = chunk.pos.x * 16;
		int baseZ = chunk.pos.z * 16;
		Carving carving = new Carving(baseX, baseZ);

		int debug = AVBlocks.DEBUG_BLOCK.id();
		int wall = AVBlocks.DIMENSION_WALL.id();
		int window = AVBlocks.GLASS_FORTIFIED.id();
		int screen = AVBlocks.GREENSCREEN.id();
		int pillar = AVBlocks.PILLAR.id();
		int floorY = HubLayout.FLOOR_Y;

		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int worldX = baseX + x;
				int worldZ = baseZ + z;
				int height = carving.air(x, z);
				result.setBlock(x, 0, z, debug);
				for (int y = 1; y <= HubLayout.TOP_Y; y++) {
					result.setBlock(x, y, z, wall);
				}
				if (height > 0) {
					result.setBlock(x, floorY, z, this.layout.isPillarFloor(worldX, worldZ) ? pillar : tile(worldX, worldZ));
					for (int y = floorY + 1; y <= floorY + height; y++) {
						result.setBlock(x, y, z, 0);
					}
				} else if (!this.layout.isReserved(worldX, worldZ)) {
					boolean screenColumn = this.layout.isScreenColumn(worldX, worldZ);
					for (int level = 1; level <= HubLayout.ROOM_HEIGHT; level++) {
						int y = floorY + level;
						if (screenColumn && level <= HubLayout.HALL_HEIGHT) {
							result.setBlock(x, y, z, screen);
						} else if (carving.windowSide(x, z, level) >= 0) {
							result.setBlock(x, y, z, window);
						} else if (carving.behindWindow(x, z, level)) {
							result.setBlock(x, y, z, screen);
						}
					}
				}
			}
		}

		for (HubLayout.Placement placement : this.layout.placementsInChunk(chunk.pos.x, chunk.pos.z)) {
			int x = placement.x() - baseX;
			int z = placement.z() - baseZ;
			if (x < 0 || x > 15 || z < 0 || z > 15 || placement.y() < 0 || placement.y() > 127) {
				continue;
			}
			result.setBlock(x, placement.y(), z, blockId(placement.piece(), placement.x(), placement.z()));
		}
		return result;
	}

	private static int tile(int x, int z) {
		return ((x + z) & 1) == 0 ? AVBlocks.DIMENSION_TILE_BLUE.id() : AVBlocks.DIMENSION_TILE_YELLOW.id();
	}

	private static int blockId(HubLayout.Piece piece, int x, int z) {
		return switch (piece) {
			case AIR -> 0;
			case WALL -> AVBlocks.DIMENSION_WALL.id();
			case TILE -> tile(x, z);
			case PILLAR -> AVBlocks.PILLAR.id();
			case SCREEN -> AVBlocks.GREENSCREEN.id();
			case WATER -> Blocks.FLUID_WATER_STILL.id();
			case FAKE_GRASS -> AVBlocks.FAKE_GRASS.id();
			case FAKE_DIRT -> AVBlocks.FAKE_DIRT.id();
			case FAKE_STONE -> AVBlocks.FAKE_STONE.id();
			case FAKE_SAND -> AVBlocks.FAKE_SAND.id();
			case LOG -> Blocks.LOG_OAK.id();
			case LEAVES -> Blocks.LEAVES_OAK_RETRO.id();
			case FLAMEWOOD_LOG -> AVBlocks.LOG_FLAMEWOOD.id();
			case CELESTIAL_FLAME -> AVBlocks.CELESTIAL_FLAME.id();
			case WATER_LILY -> AVBlocks.WATER_LILY.id();
			case LILY_FLAME -> AVBlocks.LILY_FLAME.id();
			case LILY_GOLD -> AVBlocks.LILY_GOLD.id();
			case LILY_OBSIDIAN -> AVBlocks.LILY_OBSIDIAN.id();
		};
	}

	private final class Carving {
		private final int baseX;
		private final int baseZ;
		private final int[] air = new int[SPAN * SPAN];

		Carving(int baseX, int baseZ) {
			this.baseX = baseX;
			this.baseZ = baseZ;
			for (int a = 0; a < SPAN; a++) {
				for (int b = 0; b < SPAN; b++) {
					this.air[a + b * SPAN] = ChunkGeneratorHub.this.layout.airHeight(baseX - MARGIN + a, baseZ - MARGIN + b);
				}
			}
		}

		int air(int x, int z) {
			return this.air[(x + MARGIN) + (z + MARGIN) * SPAN];
		}

		private boolean plainWall(int x, int z) {
			int worldX = this.baseX + x;
			int worldZ = this.baseZ + z;
			return this.air(x, z) == 0 && !ChunkGeneratorHub.this.layout.isReserved(worldX, worldZ)
				&& !ChunkGeneratorHub.this.layout.isScreenColumn(worldX, worldZ);
		}

		int windowSide(int x, int z, int level) {
			if (!this.plainWall(x, z)) {
				return -1;
			}
			int exposed = -1;
			for (int side = 0; side < 4; side++) {
				if (this.air(x + DX[side], z + DZ[side]) >= level) {
					if (exposed >= 0) {
						return -1;
					}
					exposed = side;
				}
			}
			if (exposed < 0) {
				return -1;
			}
			boolean room = this.air(x + DX[exposed], z + DZ[exposed]) == HubLayout.ROOM_HEIGHT;
			if (level != 2 && !(room && level == 3)) {
				return -1;
			}
			int along = DX[exposed] != 0 ? this.baseZ + z : this.baseX + x;
			if (Math.floorMod(along, 3) == 0) {
				return -1;
			}
			int backX = x - DX[exposed];
			int backZ = z - DZ[exposed];
			if (!this.plainWall(backX, backZ)) {
				return -1;
			}
			for (int side = 0; side < 4; side++) {
				int nx = backX + DX[side];
				int nz = backZ + DZ[side];
				if ((nx != x || nz != z) && this.air(nx, nz) >= level) {
					return -1;
				}
			}
			return exposed;
		}

		boolean behindWindow(int x, int z, int level) {
			if (this.air(x, z) != 0) {
				return false;
			}
			for (int side = 0; side < 4; side++) {
				if (this.windowSide(x + DX[side], z + DZ[side], level) == side) {
					return true;
				}
			}
			return false;
		}
	}
}
