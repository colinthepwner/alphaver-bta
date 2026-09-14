package com.alphaver.world.minigame;

import com.alphaver.world.minigame.map.CypressMap;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.LightLayer;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkSection;
import net.minecraft.core.world.generate.chunk.ChunkDecorator;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;

public class ChunkDecoratorMinigame implements ChunkDecorator {

	private static final int DEEP_LAVA_BELOW = 48;

	private final World world;
	private final MinigameKind kind;

	public ChunkDecoratorMinigame(@NotNull World world, @NotNull MinigameKind kind) {
		this.world = world;
		this.kind = kind;
	}

	@Override
	public void decorate(@NotNull Chunk chunk) {
		if (CypressMap.atDimensionChunk(this.kind, chunk.pos.x, chunk.pos.z) == null) {
			return;
		}
		int lavaFlowing = Blocks.FLUID_LAVA_FLOWING.id();
		int lavaStill = Blocks.FLUID_LAVA_STILL.id();
		for (int sectionY = 0; sectionY < 8; sectionY++) {
			ChunkSection section = chunk.getSection(sectionY);
			if (section == null || section.blocks == null) {
				continue;
			}
			for (int x = 0; x < 16; x++) {
				for (int y = 0; y < 16; y++) {
					for (int z = 0; z < 16; z++) {
						int id = section.blocks[ChunkSection.makeBlockIndex(x, y, z)] & 16383;
						if (id <= 0 || id >= Blocks.lightEmission.length || Blocks.lightEmission[id] <= 0) {
							continue;
						}
						int worldY = sectionY * 16 + y;
						if ((id == lavaFlowing || id == lavaStill) && worldY < DEEP_LAVA_BELOW) {
							continue;
						}
						this.world.scheduleLightingUpdate(LightLayer.Block, new TilePos(chunk.pos.x * 16 + x, worldY, chunk.pos.z * 16 + z));
					}
				}
			}
		}
	}
}
