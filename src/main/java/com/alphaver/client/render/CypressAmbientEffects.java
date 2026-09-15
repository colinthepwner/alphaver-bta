package com.alphaver.client.render;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.AVWorlds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.util.helper.MathHelper;
import net.minecraft.core.world.World;

import java.util.Random;

@Environment(EnvType.CLIENT)
public final class CypressAmbientEffects {
	private CypressAmbientEffects() {}

	private static final int RANGE = 16;
	private static final int PICKS = 88;
	private static final Random RAND = new Random();

	public static void tick(Minecraft mc) {
		World world = mc.currentWorld;
		PlayerLocal player = mc.thePlayer;
		if (world == null || player == null || mc.isGamePaused || !AVWorlds.isAlphaVer(world)) {
			return;
		}
		int originX = MathHelper.floor(player.x);
		int originY = MathHelper.floor(player.y);
		int originZ = MathHelper.floor(player.z);
		int height = world.getHeightBlocks();
		boolean snowArt = AVParticles.hasSnowArt();
		for (int pick = 0; pick < PICKS; pick++) {
			int x = originX + RAND.nextInt(RANGE * 2 + 1) - RANGE;
			int y = originY + RAND.nextInt(RANGE * 2 + 1) - RANGE;
			int z = originZ + RAND.nextInt(RANGE * 2 + 1) - RANGE;
			if (y < 0 || y >= height) {
				continue;
			}
			int id = world.getBlockId(x, y, z);
			if (id == 0) {
				continue;
			}
			if (isCypressLeaves(id)) {
				if (RAND.nextBoolean()) {
					double dx = (RAND.nextDouble() - 0.5) * 0.1;
					double dz = (RAND.nextDouble() - 0.5) * 0.1;
					world.spawnParticle(AVParticles.LEAF, x, y, z, dx, -0.1, dz, 0, false);
				}
			} else if (snowArt && id == Blocks.BLOCK_SNOW.id()) {
				snowPuffs(world, x, y, z);
			}
		}
	}

	public static void breathBubbles(PlayerLocal player) {
		World world = player.world;
		if (world == null || !AVWorlds.isAlphaVer(world)) {
			return;
		}
		int air = player.airSupply;
		if (air < 150 && air % 15 == 0) {
			int bubbles = 1 + RAND.nextInt(player.isSneaking() ? 2 : 5);
			for (int i = 0; i < bubbles; i++) {
				world.spawnParticle("bubble", player.x, player.y, player.z, 0.0, 0.5, 0.0, 0, false);
			}
		}
	}

	private static void snowPuffs(World world, int x, int y, int z) {
		if (RAND.nextBoolean()) {
			world.spawnParticle(AVParticles.SNOW, x, y, z, (RAND.nextDouble() - 0.5) * 0.1, 0.8, (RAND.nextDouble() - 0.5) * 0.1, 1, false);
		}
		if (RAND.nextBoolean()) {
			world.spawnParticle(AVParticles.SNOW, x, y, z, (RAND.nextDouble() - 0.5) * 0.2, 0.95, (RAND.nextDouble() - 0.5) * 0.2, 3, false);
		}
		if (RAND.nextBoolean()) {

			world.spawnParticle(AVParticles.SNOW, x, y, z, (RAND.nextDouble() + 0.5) * 0.2, 0.45, (RAND.nextDouble() - 0.5) * 0.1, 2, false);
		}
	}

	private static boolean isCypressLeaves(int id) {
		return id == Blocks.LEAVES_OAK_RETRO.id() || is(AVBlocks.LEAVES_FLAMEWOOD, id) || is(AVBlocks.LEAVES_TEA, id);
	}

	private static boolean is(Block<?> block, int id) {
		return block != null && block.id() == id;
	}
}
