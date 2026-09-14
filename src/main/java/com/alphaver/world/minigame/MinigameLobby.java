package com.alphaver.world.minigame;

import com.alphaver.block.AVBlocks;
import com.alphaver.block.BlockLogicAlphaVerDoor;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class MinigameLobby {
	private MinigameLobby() {}

	public static final int FLOOR_Y = 64;
	public static final int SPAWN_X = 0;
	public static final int SPAWN_Z = 0;

	public static final float SPAWN_YAW = 180.0F;

	private static final int HALF_X = 8;
	private static final int HALF_Z = 6;

	private static final int HEIGHT = 5;
	private static final int SIGN_ROW = FLOOR_Y + 3;

	private static final int SIGN_ON_NORTH_WALL = 3;
	private static final int SIGN_ON_SOUTH_WALL = 2;
	private static final String[] EXIT_SIGN = {"", "Back to", "the Hub", ""};

	static int doorX(int index, int count) {
		return -(count - 1) + index * 2;
	}

	public static void build(@NotNull World world, @NotNull MinigameKind kind) {
		AVMinigames.focusChunks(world, SPAWN_X, SPAWN_Z);
		AVTravel.loadChunksAround(world, SPAWN_X, SPAWN_Z);
		TilePos pos = new TilePos();
		int wallX = HALF_X + 1;
		int wallZ = HALF_Z + 1;
		for (int x = -wallX; x <= wallX; x++) {
			for (int z = -wallZ; z <= wallZ; z++) {
				boolean wall = Math.abs(x) == wallX || Math.abs(z) == wallZ;
				Block<?> floor = wall ? AVBlocks.DIMENSION_WALL
					: ((x + z) & 1) == 0 ? AVBlocks.DIMENSION_TILE_BLUE : AVBlocks.DIMENSION_TILE_YELLOW;
				AVMinigames.setBlock(world, pos, x, FLOOR_Y, z, floor, 0);
				for (int y = FLOOR_Y + 1; y <= FLOOR_Y + HEIGHT; y++) {
					if (!wall) {

						if (y == SIGN_ROW && Math.abs(z) == HALF_Z && world.getBlockId(x, y, z) == Blocks.SIGN_WALL_PLANKS_OAK.id()) {
							continue;
						}
						AVMinigames.setBlock(world, pos, x, y, z, null, 0);
					} else if (!isDoorColumn(kind, x, z) || y > FLOOR_Y + 2) {
						AVMinigames.setBlock(world, pos, x, y, z, AVBlocks.DIMENSION_WALL, 0);
					}
				}
				boolean lamp = !wall && Math.floorMod(x, 4) == 0 && Math.floorMod(z, 3) == 0;
				AVMinigames.setBlock(world, pos, x, FLOOR_Y + HEIGHT + 1, z, lamp ? Blocks.GLOWSTONE : AVBlocks.DIMENSION_WALL, 0);
			}
		}

		Block<?> door = kind == MinigameKind.ZOMBIES ? AVBlocks.ZOMBIES_DOOR : AVBlocks.FREERUN_DOOR;
		List<MinigameStage> stages = MinigameStage.of(kind);
		for (int i = 0; i < stages.size(); i++) {
			MinigameStage stage = stages.get(i);
			int x = doorX(i, stages.size());

			AVMinigames.setDoor(world, pos, x, FLOOR_Y + 1, -wallZ, door,
				MinigameStage.withDoorCode(BlockLogicAlphaVerDoor.FAR, stage.code));
			sign(world, pos, x, SIGN_ROW, -HALF_Z, SIGN_ON_NORTH_WALL, stage.signLines());
		}

		AVMinigames.setDoor(world, pos, 0, FLOOR_Y + 1, wallZ, door, MinigameStage.withDoorCode(0, MinigameStage.CODE_EXIT));
		sign(world, pos, 0, SIGN_ROW, HALF_Z, SIGN_ON_SOUTH_WALL, EXIT_SIGN);
	}

	private static boolean isDoorColumn(MinigameKind kind, int x, int z) {
		if (z == HALF_Z + 1) {
			return x == 0;
		}
		if (z != -(HALF_Z + 1)) {
			return false;
		}
		int count = MinigameStage.of(kind).size();
		for (int i = 0; i < count; i++) {
			if (doorX(i, count) == x) {
				return true;
			}
		}
		return false;
	}

	private static void sign(World world, TilePos pos, int x, int y, int z, int data, String[] lines) {
		AVMinigames.setBlock(world, pos, x, y, z, Blocks.SIGN_WALL_PLANKS_OAK, data);
		pos.set(x, y, z);
		if (!(world.getTileEntity(pos) instanceof TileEntitySign sign)) {
			return;
		}
		boolean changed = false;
		for (int line = 0; line < 4; line++) {
			String text = line < lines.length ? lines[line] : "";
			if (!text.equals(sign.signText[line])) {
				sign.signText[line] = text;
				changed = true;
			}
		}
		if (changed) {
			sign.setChanged();
			world.markBlockNeedsUpdate(pos);
		}
	}

	public static boolean inside(double x, double y, double z) {
		return Math.abs(x - 0.5) <= HALF_X + 1.5 && Math.abs(z - 0.5) <= HALF_Z + 1.5 && y >= FLOOR_Y - 1 && y <= FLOOR_Y + HEIGHT + 2;
	}
}
