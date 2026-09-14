package com.alphaver.world.travel;

import com.alphaver.AVConfig;
import com.alphaver.block.AVBlocks;
import com.alphaver.block.BlockLogicAlphaVerDoor;
import com.alphaver.world.AVDimensions;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.material.Material;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.world.Dimension;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;

@SuppressWarnings("deprecation")
public final class AVHubDoors {
	private AVHubDoors() {}

	public static void scatter(World world, Chunk chunk) {
		if (AVConfig.HUB_DOOR_CHANCE <= 0 || AVDimensions.HUB == null || AVBlocks.HUB_DOOR == null
			|| world.dimension != Dimension.OVERWORLD) {
			return;
		}
		HubDoorSites.Site site = HubDoorSites.site(world.getRandomSeed(), chunk.pos.x, chunk.pos.z);
		if (site == null) {
			return;
		}
		int x = site.x();
		int z = site.z();
		boolean axisX = site.axisX();
		int y = world.getHeightValue(x, z);
		if (y < 2 || y > world.getHeightBlocks() - 3) {
			return;
		}
		Material ground = world.getBlockMaterial(x, y - 1, z);
		if (!ground.isSolid() || ground == Materials.LEAVES || world.getBlockId(x, y - 1, z) == AVBlocks.WATER_LILY.id()
			|| world.getBlockId(x, y, z) != 0 || world.getBlockId(x, y + 1, z) != 0) {
			return;
		}
		place(world, x, y, z, axisX, true);
	}

	public static void place(World world, int x, int y, int z, boolean axisX, boolean toHub) {
		place(world, x, y, z, axisX, false, toHub);
	}

	public static void place(World world, int x, int y, int z, boolean axisX, boolean far, boolean toHub) {
		int axis = (axisX ? BlockLogicAlphaVerDoor.AXIS_X : 0) | (far && toHub ? BlockLogicAlphaVerDoor.FAR : 0);
		if (toHub) {
			world.setBlockAndMetadata(x, y, z, AVBlocks.HUB_DOOR.id(), axis);
			world.setBlockAndMetadata(x, y + 1, z, AVBlocks.HUB_DOOR.id(), axis | BlockLogicAlphaVerDoor.UPPER);
		} else {
			world.setBlockAndMetadata(x, y, z, AVBlocks.CYPRESS_DOOR_LOWER.id(), axis);
			world.setBlockAndMetadata(x, y + 1, z, AVBlocks.CYPRESS_DOOR_UPPER.id(), axis | BlockLogicAlphaVerDoor.UPPER);
		}
	}

	public static void placeMinigameDoor(World world, int x, int y, int z, boolean axisX, boolean far, boolean zombies) {
		Block<?> door = zombies ? AVBlocks.ZOMBIES_DOOR : AVBlocks.FREERUN_DOOR;
		if (door == null) {
			return;
		}
		int bits = (axisX ? BlockLogicAlphaVerDoor.AXIS_X : 0) | (far ? BlockLogicAlphaVerDoor.FAR : 0);
		world.setBlockAndMetadata(x, y, z, door.id(), bits);
		world.setBlockAndMetadata(x, y + 1, z, door.id(), bits | BlockLogicAlphaVerDoor.UPPER);
	}
}
