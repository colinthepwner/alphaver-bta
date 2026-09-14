package com.alphaver.world.hub;

import com.alphaver.world.travel.AVHubDoors;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

public class ChunkDecoratorHub implements ChunkDecorator {
	private final World world;
	private final HubLayout layout;

	public ChunkDecoratorHub(@NotNull World world) {
		this.world = world;
		this.layout = HubLayout.forSeed(world.getRandomSeed());
	}

	@Override
	public void decorate(@NotNull Chunk chunk) {
		for (HubLayout.Decoration decoration : this.layout.decorationsInChunk(chunk.pos.x, chunk.pos.z)) {
			TilePos pos = new TilePos(decoration.x(), decoration.y(), decoration.z());
			switch (decoration.fixture()) {
				case HUB_DOOR, CYPRESS_DOOR -> AVHubDoors.place(this.world, decoration.x(), decoration.y(), decoration.z(),
					(decoration.data() & HubLayout.DOOR_AXIS_X) != 0, (decoration.data() & HubLayout.DOOR_FAR) != 0,
					decoration.fixture() == HubLayout.Fixture.HUB_DOOR);
				case ZOMBIES_DOOR, FREERUN_DOOR -> AVHubDoors.placeMinigameDoor(this.world, decoration.x(), decoration.y(), decoration.z(),
					(decoration.data() & HubLayout.DOOR_AXIS_X) != 0, (decoration.data() & HubLayout.DOOR_FAR) != 0,
					decoration.fixture() == HubLayout.Fixture.ZOMBIES_DOOR);
				case TORCH -> this.world.setBlockTypeDataRaw(pos, Blocks.TORCH_COAL, decoration.data());
				case LADDER -> this.world.setBlockTypeDataRaw(pos, Blocks.LADDER_OAK, decoration.data());
				case WATER_SOURCE -> this.world.setBlockTypeDataNotify(pos, Blocks.FLUID_WATER_FLOWING, 0);
			}
		}
	}
}
