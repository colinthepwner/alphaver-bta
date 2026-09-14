package com.alphaver.world.gen;

import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.generate.chunk.ChunkGenerator;
import net.minecraft.core.world.generate.chunk.ChunkGeneratorResult;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ChunkGeneratorCypress extends ChunkGenerator {
	private final CypressTerrain terrain;
	private final CypressCaves caves = new CypressCaves();
	private final long seed;

	public ChunkGeneratorCypress(@NotNull World world) {
		this(world, new CypressTerrain(world.getRandomSeed(), CypressWorldRules.isSandWorld(world.getRandomSeed())));
	}

	private ChunkGeneratorCypress(@NotNull World world, @NotNull CypressTerrain terrain) {
		super(world, new ChunkDecoratorCypress(world, terrain));
		this.terrain = terrain;
		this.seed = world.getRandomSeed();
	}

	@Override
	@NotNull
	protected ChunkGeneratorResult doBlockGeneration(@NotNull Chunk chunk) {
		int chunkX = chunk.pos.x;
		int chunkZ = chunk.pos.z;

		Random rand = new Random(chunkX * 341873128712L + chunkZ * 132897987541L);

		int sourceX = chunkX;
		int sourceZ = chunkZ;
		if (CypressWorldRules.isFractured()) {

			sourceX += rand.nextInt(2000) - rand.nextInt(1000);
			sourceZ += rand.nextInt(2000) - rand.nextInt(1000);
		}

		short[] blocks = new short[16 * 16 * CypressTerrain.HEIGHT];
		this.terrain.generateTerrain(sourceX, sourceZ, blocks);
		this.terrain.replaceSurfaceBlocks(sourceX, sourceZ, blocks, rand);
		this.caves.generate(this.seed, sourceX, sourceZ, blocks);

		ChunkGeneratorResult result = new ChunkGeneratorResult();
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				int column = (x * 16 + z) * CypressTerrain.HEIGHT;
				for (int y = 0; y < CypressTerrain.HEIGHT; y++) {
					int id = blocks[column + y];
					if (id != 0) {
						result.setBlock(x, y, z, id);
					}
				}
			}
		}
		return result;
	}
}
