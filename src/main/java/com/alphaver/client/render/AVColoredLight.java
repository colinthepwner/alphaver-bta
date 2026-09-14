package com.alphaver.client.render;

import com.alphaver.AVConfig;
import com.alphaver.block.AVBlocks;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Color;
import net.minecraft.core.world.World;
import net.minecraft.core.world.chunk.Chunk;
import net.minecraft.core.world.chunk.ChunkSection;
import net.minecraft.core.world.pos.ChunkPos;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public final class AVColoredLight {
	private AVColoredLight() {}

	private static final int RADIUS = 14;
	private static final int SAMPLE_INTERVAL = 5;

	private static final float FOLLOW = 0.12F;

	private static final float STRENGTH = 0.7F;

	private static final int ID_MASK = 16383;
	private static final int WORLD_TOP = 255;

	private static final float LUMA_R = 0.299F;
	private static final float LUMA_G = 0.587F;
	private static final float LUMA_B = 0.114F;

	private static final float[] BLOCK_WEIGHT = new float[16];

	static {
		for (int level = 0; level < 16; level++) {
			float rise = smoothstep(level / 8.0F);
			float fall = 1.0F - smoothstep((level - 11) / 4.0F);
			BLOCK_WEIGHT[level] = rise * fall;
		}
	}

	private static float[][] colours;

	private static int clock;
	private static float red = 1.0F;
	private static float green = 1.0F;
	private static float blue = 1.0F;
	private static float strength;
	private static float targetRed = 1.0F;
	private static float targetGreen = 1.0F;
	private static float targetBlue = 1.0F;
	private static float targetStrength;

	public static boolean appliesTo(@Nullable World world) {
		return AVConfig.COLORED_LIGHT && world != null && AVWorlds.isAlphaVer(world);
	}

	public static void tick(Minecraft mc) {
		World world = mc.currentWorld;
		Player player = mc.thePlayer;
		if (player == null || !appliesTo(world)) {

			strength = 0.0F;
			targetStrength = 0.0F;
			return;
		}
		if (clock++ % SAMPLE_INTERVAL == 0) {
			sample(world, (int) Math.floor(player.x), (int) Math.floor(player.y), (int) Math.floor(player.z));
		}
		red += (targetRed - red) * FOLLOW;
		green += (targetGreen - green) * FOLLOW;
		blue += (targetBlue - blue) * FOLLOW;
		strength += (targetStrength - strength) * FOLLOW;
	}

	private static void sample(World world, int px, int py, int pz) {
		float[][] table = colours();
		int[] emission = Blocks.lightEmission;
		float sumRed = 0.0F;
		float sumGreen = 0.0F;
		float sumBlue = 0.0F;
		float coloured = 0.0F;
		float neutral = 0.0F;

		int minY = Math.max(0, py - RADIUS);
		int maxY = Math.min(WORLD_TOP, py + RADIUS);
		for (int chunkX = (px - RADIUS) >> 4; chunkX <= (px + RADIUS) >> 4; chunkX++) {
			for (int chunkZ = (pz - RADIUS) >> 4; chunkZ <= (pz + RADIUS) >> 4; chunkZ++) {
				ChunkPos pos = new ChunkPos(chunkX, chunkZ);
				if (!world.isChunkLoaded(pos)) {
					continue;
				}

				Chunk chunk = world.getChunk(pos, false);
				if (chunk == null) {
					continue;
				}
				int minX = Math.max(px - RADIUS, chunkX << 4);
				int maxX = Math.min(px + RADIUS, (chunkX << 4) + 15);
				int minZ = Math.max(pz - RADIUS, chunkZ << 4);
				int maxZ = Math.min(pz + RADIUS, (chunkZ << 4) + 15);
				for (int y = minY; y <= maxY; ) {
					int sectionTop = Math.min(maxY, (y | 15));
					ChunkSection section = chunk.getSection(y >> 4);
					short[] blocks = section == null ? null : section.blocks;

					if (blocks != null) {
						for (int by = y; by <= sectionTop; by++) {
							int dy = Math.abs(by - py);
							for (int bz = minZ; bz <= maxZ; bz++) {
								int dyz = dy + Math.abs(bz - pz);
								if (dyz > RADIUS) {
									continue;
								}
								for (int bx = minX; bx <= maxX; bx++) {
									int id = blocks[((by & 15) << 8) | ((bz & 15) << 4) | (bx & 15)] & ID_MASK;
									if (id == 0 || id >= emission.length) {
										continue;
									}

									int reach = emission[id] - dyz - Math.abs(bx - px);
									if (reach <= 0) {
										continue;
									}
									float weight = reach * reach;
									float[] colour = id < table.length ? table[id] : null;
									if (colour == null) {
										neutral += weight;
									} else {
										sumRed += colour[0] * weight;
										sumGreen += colour[1] * weight;
										sumBlue += colour[2] * weight;
										coloured += weight;
									}
								}
							}
						}
					}
					y = sectionTop + 1;
				}
			}
		}

		if (coloured <= 0.0F) {
			targetStrength = 0.0F;
			return;
		}
		targetRed = sumRed / coloured;
		targetGreen = sumGreen / coloured;
		targetBlue = sumBlue / coloured;
		targetStrength = coloured / (coloured + neutral);
	}

	public static void tint(int[] data) {
		float overall = strength * STRENGTH;
		if (overall <= 0.002F || data == null || data.length < 256) {
			return;
		}
		float tintLuma = luma(red, green, blue);
		if (tintLuma <= 0.001F) {
			return;
		}

		float[] blockAlone = new float[16];
		float[] skyAlone = new float[16];
		for (int level = 0; level < 16; level++) {
			blockAlone[level] = luma(data[level]);
			skyAlone[level] = luma(data[level * 16]);
		}

		for (int i = 0; i < 256; i++) {
			int sky = i / 16;
			int block = i % 16;
			float weight = BLOCK_WEIGHT[block];
			if (weight <= 0.0F || blockAlone[block] <= 0.0001F) {
				continue;
			}

			float share = smoothstep((blockAlone[block] - skyAlone[sky]) / blockAlone[block]);
			float amount = overall * weight * share;
			if (amount <= 0.002F) {
				continue;
			}

			int argb = data[i];
			float r = ((argb >> 16) & 0xFF) / 255.0F;
			float g = ((argb >> 8) & 0xFF) / 255.0F;
			float b = (argb & 0xFF) / 255.0F;
			float before = luma(r, g, b);
			if (before <= 0.0001F) {
				continue;
			}

			float lightRed = before * red / tintLuma;
			float lightGreen = before * green / tintLuma;
			float lightBlue = before * blue / tintLuma;

			float peak = Math.max(lightRed, Math.max(lightGreen, lightBlue));
			if (peak > 1.0F) {
				float pull = (1.0F - before) / (peak - before);
				lightRed = before + (lightRed - before) * pull;
				lightGreen = before + (lightGreen - before) * pull;
				lightBlue = before + (lightBlue - before) * pull;
			}

			r += (lightRed - r) * amount;
			g += (lightGreen - g) * amount;
			b += (lightBlue - b) * amount;
			data[i] = Color.floatToIntARGB(1.0F, clamp(r), clamp(g), clamp(b));
		}
	}

	private static float[][] colours() {
		float[][] table = colours;
		if (table != null) {
			return table;
		}
		table = new float[Blocks.blocksList.length][];
		put(table, AVBlocks.CELESTIAL_FLAME, 0x09E3E3);
		put(table, AVBlocks.WATER_LILY, 0x8CE7E3);
		put(table, AVBlocks.LILY_FLAME, 0x09E3E3);
		put(table, AVBlocks.LILY_GOLD, 0xFCFC00);
		put(table, AVBlocks.LILY_OBSIDIAN, 0x3134E9);
		put(table, AVBlocks.LOW_LILY, 0xD9FFBC);
		put(table, AVBlocks.LOW_VINE, 0xD9FFBC);
		put(table, AVBlocks.LOW_WART, 0xD9FFBC);
		put(table, AVBlocks.MYCON_CAP_GLOWING, 0x33C8E0);
		put(table, AVBlocks.ORE_GREENSTONE_GLOWING, 0x00F718);
		put(table, AVBlocks.TORCH_GREENSTONE_ACTIVE, 0x00F718);
		put(table, AVBlocks.FLAME_GLASS_SKY, 0x5BEDF5);
		put(table, AVBlocks.FLAME_GLASS_GOLD, 0xFFE14D);
		put(table, AVBlocks.FLAME_GLASS_OBSIDIAN, 0xB586FF);
		put(table, AVBlocks.FLAME_GLASS_LOW, 0x7CD389);
		put(table, AVBlocks.PLATE_DENIAL, 0xFFC2CA);
		put(table, AVBlocks.PLATE_SWITCH, 0xC1BFEA);
		put(table, AVBlocks.PLATE_LOOP, 0xBEE8D5);
		put(table, AVBlocks.PLATE_PART, 0xDBE9B2);
		put(table, AVBlocks.PLATE_TRINITY, 0xFFD39B);
		put(table, AVBlocks.PLATE_ASSOCIATION, 0xFF9E9F);
		put(table, AVBlocks.PLATE_DIALECT, 0xE5C4FC);
		put(table, AVBlocks.PLATE_SYLLABLES, 0xFF96CE);
		put(table, AVBlocks.PLATE_MIRRORS, 0xE2E2A1);
		put(table, AVBlocks.BLOCK_LACE_AGATE, 0x7073B5);
		put(table, AVBlocks.BLOCK_CLINOHUMITE, 0xF5810A);
		put(table, AVBlocks.BLOCK_MALACHITE, 0x4BB781);
		put(table, AVBlocks.BLOCK_PYRITE, 0xFED762);
		put(table, AVBlocks.ESSENCE_FOUNTAIN, 0xE5A0C9);
		colours = table;
		return table;
	}

	private static void put(float[][] table, @Nullable Block<?> block, int rgb) {
		if (block == null || block.id() < 0 || block.id() >= table.length) {
			return;
		}
		float r = ((rgb >> 16) & 0xFF) / 255.0F;
		float g = ((rgb >> 8) & 0xFF) / 255.0F;
		float b = (rgb & 0xFF) / 255.0F;
		float max = Math.max(r, Math.max(g, b));
		if (max <= 0.0F) {
			return;
		}
		table[block.id()] = new float[]{r / max, g / max, b / max};
	}

	private static float luma(int argb) {
		return luma(((argb >> 16) & 0xFF) / 255.0F, ((argb >> 8) & 0xFF) / 255.0F, (argb & 0xFF) / 255.0F);
	}

	private static float luma(float r, float g, float b) {
		return r * LUMA_R + g * LUMA_G + b * LUMA_B;
	}

	private static float smoothstep(float t) {
		float x = clamp(t);
		return x * x * (3.0F - 2.0F * x);
	}

	private static float clamp(float value) {
		return value < 0.0F ? 0.0F : (value > 1.0F ? 1.0F : value);
	}
}
