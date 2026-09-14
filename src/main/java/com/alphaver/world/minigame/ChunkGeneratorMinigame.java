package com.alphaver.world.minigame;

import com.alphaver.world.minigame.map.CypressMap;
import com.alphaver.world.minigame.map.CypressMapStore;
import net.minecraft.core.block.entity.TileEntitySign;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.data.ChunkUnsignedByteArray;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import net.minecraft.core.world.pos.ChunkTilePos;
import org.jetbrains.annotations.NotNull;

public class ChunkGeneratorMinigame extends ChunkGenerator {

	private final MinigameKind kind;

	public ChunkGeneratorMinigame(@NotNull World world, @NotNull MinigameKind kind) {
		super(world, new ChunkDecoratorMinigame(world, kind));
		this.kind = kind;
	}

	@NotNull
	@Override
	protected ChunkGeneratorResult doBlockGeneration(@NotNull Chunk chunk) {
		ChunkGeneratorResult result = new ChunkGeneratorResult();
		CypressMap map = CypressMap.atDimensionChunk(this.kind, chunk.pos.x, chunk.pos.z);
		if (map == null) {
			return result;
		}
		CypressMapStore.ConvertedChunk converted = CypressMapStore.chunk(map, map.cypressChunkX(chunk.pos.x), map.cypressChunkZ(chunk.pos.z));
		if (converted == null) {
			return result;
		}

		short[] ids = converted.ids();
		byte[] data = converted.data();
		ChunkUnsignedByteArray[] sectionData = new ChunkUnsignedByteArray[8];
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 128; y++) {
					int index = CypressMapStore.ConvertedChunk.index(x, y, z);
					int id = ids[index] & 0xFFFF;
					if (id == 0) {
						continue;
					}
					result.setBlock(x, y, z, id);
					int value = data[index] & 255;
					if (value != 0) {
						int section = y >> 4;
						if (sectionData[section] == null) {
							sectionData[section] = new ChunkUnsignedByteArray(16, 16, 16);
						}
						sectionData[section].set(x, y & 15, z, value);
					}
				}
			}
		}
		for (int section = 0; section < sectionData.length; section++) {
			if (sectionData[section] != null) {
				chunk.getSection(section).data = sectionData[section];
			}
		}

		for (CypressMapStore.Sign sign : converted.signs()) {
			TileEntitySign tileEntity = new TileEntitySign();
			tileEntity.worldObj = this.world;
			tileEntity.tilePos.set(chunk.pos.x * 16 + sign.x(), sign.y(), chunk.pos.z * 16 + sign.z());
			for (int line = 0; line < 4; line++) {
				tileEntity.signText[line] = sign.lines()[line];
			}
			tileEntity.validate();
			chunk.tileEntityMap.put(new ChunkTilePos(sign.x(), sign.y(), sign.z()), tileEntity);
		}
		return result;
	}
}
