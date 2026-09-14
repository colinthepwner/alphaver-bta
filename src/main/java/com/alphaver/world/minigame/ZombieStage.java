package com.alphaver.world.minigame;

import com.alphaver.block.AVBlocks;
import com.alphaver.net.AVSounds;
import com.alphaver.world.minigame.map.CypressMap;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePos;
import org.jetbrains.annotations.NotNull;
import org.joml.primitives.AABBd;

abstract class ZombieStage {

	final MinigameStage stage;

	ZombieStage(MinigameStage stage) {
		this.stage = stage;
	}

	abstract double[] playerSpawn(World world);

	float spawnYaw() {
		return 180.0F;
	}

	abstract int[][] zombieSpawns();

	abstract double teleportBelow();

	abstract AABBd bounds();

	boolean contains(double x, double y, double z) {
		AABBd box = this.bounds();
		return x >= box.minX && x <= box.maxX && y >= box.minY && y <= box.maxY && z >= box.minZ && z <= box.maxZ;
	}

	void setup(World world) {
	}

	int doorPrice(int x, int z) {
		return 250;
	}

	void interactGiver(ZombieGame game, Player player, int x, int y, int z) {
	}

	@NotNull
	static ZombieStage of(@NotNull MinigameStage stage) {
		return switch (stage) {
			case RUINEN_DER_UNTOTEN -> new RuinenDerUntoten();
			case METSAN_TALO -> new MetsanTalo();
			default -> new Arena();
		};
	}

	abstract static class OnMap extends ZombieStage {
		final CypressMap map;

		final int spawnX;
		final int spawnEyeY;
		final int spawnZ;
		final int[][] spawns;

		final int[][] doors;
		final int belowY;

		OnMap(MinigameStage stage, int spawnX, int spawnEyeY, int spawnZ, int[][] spawns, int[][] doors, int belowY) {
			super(stage);
			this.map = stage.map;
			this.spawnX = spawnX;
			this.spawnEyeY = spawnEyeY;
			this.spawnZ = spawnZ;
			this.spawns = spawns;
			this.doors = doors;
			this.belowY = belowY;
		}

		@Override
		double[] playerSpawn(World world) {
			int x = this.map.x(this.spawnX);
			int z = this.map.z(this.spawnZ);

			int feet = AVMinigames.standingY(world, x, (int) Math.floor(this.spawnEyeY - 1.62), z);
			return new double[]{x + 0.5, feet, z + 0.5};
		}

		@Override
		int[][] zombieSpawns() {
			int[][] out = new int[this.spawns.length][];
			for (int i = 0; i < this.spawns.length; i++) {
				out[i] = new int[]{this.map.x(this.spawns[i][0]), this.spawns[i][1], this.map.z(this.spawns[i][2])};
			}
			return out;
		}

		@Override
		double teleportBelow() {
			return this.belowY;
		}

		@Override
		AABBd bounds() {
			double minX = this.map.x(this.map.cropMinChunkX * 16);
			double minZ = this.map.z(this.map.cropMinChunkZ * 16);
			double maxX = this.map.x((this.map.cropMaxChunkX + 1) * 16);
			double maxZ = this.map.z((this.map.cropMaxChunkZ + 1) * 16);
			return new AABBd(minX, -64.0, minZ, maxX, 192.0, maxZ);
		}

		@Override
		void setup(World world) {
			AVMinigames.focusChunks(world, this.map.x(this.spawnX), this.map.z(this.spawnZ));
			TilePos pos = new TilePos();
			for (int[] box : this.doors) {

				AVMinigames.prepareArea(world, this.map.x(box[0]), this.map.z(box[2]));
				for (int x = Math.min(box[0], box[3]); x <= Math.max(box[0], box[3]); x++) {
					for (int y = Math.min(box[1], box[4]); y <= Math.max(box[1], box[4]); y++) {
						for (int z = Math.min(box[2], box[5]); z <= Math.max(box[2], box[5]); z++) {
							this.fillIfAir(world, pos, x, y, z, AVBlocks.WIREFRAME_DOOR);
						}
					}
				}
			}
		}

		@Override
		int doorPrice(int x, int z) {
			double dx = this.map.cypressX(x) - this.spawnX;
			double dz = this.map.cypressZ(z) - this.spawnZ;
			return Math.max((int) (Math.sqrt(dx * dx + dz * dz) / 10.0) * 250, 250);
		}

		void set(World world, TilePos pos, int x, int y, int z, Block<?> block) {
			AVMinigames.prepareArea(world, this.map.x(x), this.map.z(z));
			AVMinigames.setBlock(world, pos, this.map.x(x), y, this.map.z(z), block, 0);
		}

		void fillIfAir(World world, TilePos pos, int x, int y, int z, Block<?> block) {
			if (world.getBlockId(this.map.x(x), y, this.map.z(z)) == 0) {
				this.set(world, pos, x, y, z, block);
			}
		}

		boolean at(int x, int y, int z, int cypressX, int cypressY, int cypressZ) {
			return x == this.map.x(cypressX) && y == cypressY && z == this.map.z(cypressZ);
		}
	}

	static final class RuinenDerUntoten extends OnMap {
		private static final int[] EGG_START = {-314, 71, 159};
		private static final int[] ELDER_BRICK = {-314, 71, 157};
		private static final int[][] EGG_SPOTS = {{-299, 86, 155}, {-233, 73, 122}, {-234, 87, 57}, {-174, 76, 62}, {-177, 83, 99}, {-186, 68, 140}};

		RuinenDerUntoten() {
			super(MinigameStage.RUINEN_DER_UNTOTEN, -173, 66, 123,
				new int[][]{{-188, 66, 141}, {-188, 66, 151}, {-187, 67, 92}, {-162, 66, 109}, {-172, 66, 178}, {-248, 78, 158}},
				new int[][]{
					{-181, 65, 144, -170, 68, 144}, {-181, 65, 130, -181, 68, 117}, {-213, 64, 123, -213, 68, 120},
					{-191, 65, 165, -191, 68, 160}, {-187, 65, 168, -170, 68, 168}, {-172, 65, 115, -181, 68, 115},
					{-181, 66, 87, -174, 69, 87}, {-252, 75, 150, -248, 82, 148}, {-268, 75, 158, -270, 82, 156},
					{-217, 76, 156, -218, 79, 159}, {-235, 79, 107, -230, 85, 104}},
				64);
		}

		@Override
		void setup(World world) {
			super.setup(world);
			TilePos pos = new TilePos();
			this.set(world, pos, EGG_START[0], EGG_START[1], EGG_START[2], AVBlocks.WEAPON_GIVER);
			this.set(world, pos, ELDER_BRICK[0], ELDER_BRICK[1], ELDER_BRICK[2], AVBlocks.ELDER_BRICK);

			AVMinigames.prepareArea(world, this.map.x(-340), this.map.z(165));
			for (int y = 60; y <= 67; y++) {
				for (int z = 159; z <= 172; z++) {
					this.fillIfAir(world, pos, -340, y, z, AVBlocks.GHOST_BLOCK);
				}
			}
			for (int[] spot : EGG_SPOTS) {
				this.set(world, pos, spot[0], spot[1], spot[2], null);
			}
		}

		@Override
		void interactGiver(ZombieGame game, Player player, int x, int y, int z) {
			World world = game.world;
			TilePos pos = new TilePos();
			if (this.at(x, y, z, EGG_START[0], EGG_START[1], EGG_START[2])) {
				game.eggStarted = true;
				AVMinigames.setBlock(world, pos, x, y, z, null, 0);
				game.notifyAll("ext.notif", "0/6");
				for (int[] spot : EGG_SPOTS) {
					this.set(world, pos, spot[0], spot[1], spot[2], AVBlocks.WEAPON_GIVER);
				}
			} else if (game.eggStarted) {
				game.eggFound++;
				AVMinigames.setBlock(world, pos, x, y, z, null, 0);
				game.notifyAll("ext.notif", game.eggFound + "/6");
				world.playSoundAtEntity(null, player, "random.glass", 1.0F, 1.0F);
				if (game.eggFound == EGG_SPOTS.length) {
					this.set(world, pos, ELDER_BRICK[0], ELDER_BRICK[1], ELDER_BRICK[2], null);
				}
			}
		}
	}

	static final class MetsanTalo extends OnMap {
		private static final int[] GIVER = {-101, 78, -61};
		private static final int[][] PILLARS = {{-99, 75, -66}, {-99, 76, -66}};

		MetsanTalo() {
			super(MinigameStage.METSAN_TALO, -102, 73, -56,
				new int[][]{{-105, 71, -66}, {-103, 71, -70}, {-101, 71, -64}},
				new int[][]{{-107, 70, -74, -107, 73, -74}, {-101, 75, -66, -106, 76, -66}},
				71);
		}

		@Override
		void setup(World world) {
			super.setup(world);
			TilePos pos = new TilePos();
			for (int[] pillar : PILLARS) {
				this.set(world, pos, pillar[0], pillar[1], pillar[2], AVBlocks.CRUDE_PILLAR);
			}
			this.set(world, pos, GIVER[0], GIVER[1], GIVER[2], AVBlocks.WEAPON_GIVER);
		}

		@Override
		void interactGiver(ZombieGame game, Player player, int x, int y, int z) {
			if (!this.at(x, y, z, GIVER[0], GIVER[1], GIVER[2])) {
				return;
			}
			TilePos pos = new TilePos();
			for (int[] pillar : PILLARS) {
				this.set(game.world, pos, pillar[0], pillar[1], pillar[2], null);
			}
			this.set(game.world, pos, GIVER[0], GIVER[1], GIVER[2], null);
			game.world.playSoundAtEntity(null, player, "random.glass", 1.0F, 1.0F);
		}
	}

	static final class Arena extends ZombieStage {
		Arena() {
			super(MinigameStage.ARENA);
		}

		@Override
		double[] playerSpawn(World world) {
			return new double[]{ZombieArena.CX + 0.5, ZombieArena.FLOOR + 1, ZombieArena.CZ + 0.5};
		}

		@Override
		float spawnYaw() {
			return 0.0F;
		}

		@Override
		int[][] zombieSpawns() {
			int[][] out = new int[ZombieArena.SPAWNS.length][];
			for (int i = 0; i < out.length; i++) {
				out[i] = new int[]{ZombieArena.CX + ZombieArena.SPAWNS[i][0], ZombieArena.FLOOR + 1, ZombieArena.CZ + ZombieArena.SPAWNS[i][1]};
			}
			return out;
		}

		@Override
		double teleportBelow() {
			return ZombieArena.FLOOR - 2;
		}

		@Override
		AABBd bounds() {
			int reach = ZombieArena.HALF + 8;
			return new AABBd(ZombieArena.CX - reach, ZombieArena.FLOOR - 16, ZombieArena.CZ - reach,
				ZombieArena.CX + reach + 1, ZombieArena.ROOF + 8, ZombieArena.CZ + reach + 1);
		}

		@Override
		void setup(World world) {
			ZombieArena.build(world);
		}
	}

	static void soundFor(Player player, String sound) {
		AVSounds.playFor(player, "alphaver:" + sound, 1.0F, 1.0F);
	}
}
