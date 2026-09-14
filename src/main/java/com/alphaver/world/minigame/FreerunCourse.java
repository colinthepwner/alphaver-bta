package com.alphaver.world.minigame;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.travel.AVTravel;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

final class FreerunCourse {
	private FreerunCourse() {}

	static final int START_X = 0;
	static final int START_Y = 80;
	static final int START_Z = 48;

	private static final int START = 0;
	private static final int JUMP = 1;
	private static final int CHECKPOINT = 2;
	private static final int FINISH = 3;

	private static final int LEGS = 4;
	private static final int JUMPS_PER_LEG = 8;
	private static final int MAX_Y = 116;

	private static final int FALL_BELOW = 4;

	record Pad(int x, int y, int z, int radius, int type) {}

	static final List<Pad> PADS = layout();
	private static final int MIN_Y = PADS.stream().mapToInt(Pad::y).min().orElse(START_Y);

	private static List<Pad> layout() {
		Random rand = new Random(0x46524545L);
		List<Pad> pads = new ArrayList<>();

		int[][] directions = {{0, 1}, {1, 0}, {0, 1}, {-1, 0}};
		int x = START_X;
		int y = START_Y;
		int z = START_Z;
		int radius = 2;
		pads.add(new Pad(x, y, z, radius, START));
		for (int leg = 0; leg < LEGS; leg++) {
			int ux = directions[leg][0];
			int uz = directions[leg][1];
			for (int jump = 1; jump <= JUMPS_PER_LEG; jump++) {
				boolean lastOfLeg = jump == JUMPS_PER_LEG;
				int type = lastOfLeg ? (leg == LEGS - 1 ? FINISH : CHECKPOINT) : JUMP;
				int next = switch (type) {
					case FINISH -> 2;
					case CHECKPOINT -> 1;
					default -> rand.nextInt(4) == 0 ? 0 : 1;
				};
				int roll = rand.nextInt(10);
				int dy = roll < 4 && y < MAX_Y ? 1 : roll < 6 && y > START_Y - 4 ? -1 : 0;
				int gap = dy > 0 ? 1 : dy < 0 ? 1 + rand.nextInt(3) : 1 + rand.nextInt(2);

				int step = radius + gap + next + 1;
				int lateral = radius + next == 0 ? 0 : rand.nextInt(3) - 1;
				x += ux * step + (ux == 0 ? lateral : 0);
				z += uz * step + (uz == 0 ? lateral : 0);
				y += dy;
				pads.add(new Pad(x, y, z, next, type));
				radius = next;
			}
		}
		return Collections.unmodifiableList(pads);
	}

	static void build(World world) {

		AVMinigames.focusChunks(world, START_X, START_Z);
		TilePos pos = new TilePos();
		Pad start = PADS.get(0);
		int doorZ = start.z - start.radius;
		for (Pad pad : PADS) {
			AVTravel.loadChunksAround(world, pad.x, pad.z);
			Block<?> top = switch (pad.type) {
				case START -> AVBlocks.DIMENSION_FLOOR;
				case CHECKPOINT -> AVBlocks.DIMENSION_TILE_YELLOW;
				case FINISH -> AVBlocks.GREENSCREEN;
				default -> AVBlocks.DIMENSION_TILE_BLUE;
			};
			for (int dx = -pad.radius; dx <= pad.radius; dx++) {
				for (int dz = -pad.radius; dz <= pad.radius; dz++) {
					AVMinigames.setBlock(world, pos, pad.x + dx, pad.y, pad.z + dz, top, 0);
					boolean door = pad.type == START && dx == 0 && pad.z + dz == doorZ;
					for (int h = door ? 3 : 1; h <= 3; h++) {
						AVMinigames.setBlock(world, pos, pad.x + dx, pad.y + h, pad.z + dz, null, 0);
					}
				}
			}
			AVMinigames.setBlock(world, pos, pad.x, pad.y - 1, pad.z, Blocks.GLOWSTONE, 0);
		}

		AVMinigames.setDoor(world, pos, start.x, start.y + 1, doorZ, AVBlocks.FREERUN_DOOR,
			MinigameStage.withDoorCode(0, MinigameStage.CODE_LOBBY));
	}

	static FreerunStages.Stage stage() {
		List<FreerunStages.Loc> checkpoints = new ArrayList<>();
		Pad start = PADS.get(0);

		checkpoints.add(FreerunStages.Loc.point(start.x, start.y + 3, start.z));
		checkpoints.add(box(PADS.get(1)));
		for (Pad pad : PADS) {
			if (pad.type == CHECKPOINT || pad.type == FINISH) {
				checkpoints.add(box(pad));
			}
		}
		int resetY = MIN_Y - FALL_BELOW;
		return new FreerunStages.Stage(MinigameStage.COURSE, checkpoints.toArray(new FreerunStages.Loc[0]), new FreerunStages.Portal[0], 0, 0) {
			@Override
			int resetY(long x, long z) {
				return resetY;
			}
		};
	}

	private static FreerunStages.Loc box(Pad pad) {
		return FreerunStages.Loc.box(pad.x - pad.radius, pad.y + 2, pad.z - pad.radius, pad.x + pad.radius, pad.y + 3, pad.z + pad.radius);
	}
}
