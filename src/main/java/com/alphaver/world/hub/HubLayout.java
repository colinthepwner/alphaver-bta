package com.alphaver.world.hub;

import com.alphaver.world.travel.HubDoorSites;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public final class HubLayout {

	public static final int CELL = 6;

	public static final int REGION = 16;

	public static final int FLOOR_Y = 32;

	public static final int HALL_HEIGHT = 3;

	public static final int ROOM_HEIGHT = 5;

	public static final int TOP_Y = FLOOR_Y + 7;

	public static final int SPAWN_X = 2;
	public static final int SPAWN_Z = 2;

	public static final int DOOR_IN_CELL = 2;

	public static final int XP = 0;
	public static final int ZP = 1;
	public static final int XM = 2;
	public static final int ZM = 3;
	private static final int[] DX = {1, 0, -1, 0};
	private static final int[] DZ = {0, 1, 0, -1};

	private static final int EDGE_OPENINGS = 3;
	private static final int BRAID_CHANCE = 8;
	private static final int CYPRESS_DOOR_CHANCE = 10;

	private static final int GAME_DOOR_CHANCE = 3;
	private static final int DIAGONAL_CHANCE = 30;

	private static final int ROOM_CHANCE = 4;
	private static final int BUBBLE_CHANCE = 2;
	private static final int REGION_CACHE = 64;

	private static final int[] TORCH_ON_SIDE = {2, 4, 1, 3};

	private static final int TORCH_ON_FLOOR = 5;

	private static final int[] LADDER_ON_SIDE = {4, 2, 5, 3};

	public static final int DOOR_AXIS_X = 1;
	public static final int DOOR_FAR = 4;

	private static final byte END_NONE = 0;
	private static final byte END_DOOR = 1;
	private static final byte END_SCREEN = 2;
	private static final byte END_LADDER = 3;

	public enum Piece {
		AIR, WALL, TILE, PILLAR, SCREEN, WATER, FAKE_GRASS, FAKE_DIRT, FAKE_STONE, FAKE_SAND, LOG, LEAVES, FLAMEWOOD_LOG,
		CELESTIAL_FLAME, WATER_LILY, LILY_FLAME, LILY_GOLD, LILY_OBSIDIAN
	}

	public enum Fixture {
		HUB_DOOR, CYPRESS_DOOR, TORCH, LADDER, WATER_SOURCE, ZOMBIES_DOOR, FREERUN_DOOR
	}

	public record Placement(int x, int y, int z, Piece piece) {}

	public record Decoration(Fixture fixture, int x, int y, int z, int data) {}

	private static final Map<Long, HubLayout> LAYOUTS = new HashMap<>();

	public static synchronized HubLayout forSeed(long seed) {
		return LAYOUTS.computeIfAbsent(seed, HubLayout::new);
	}

	private final long seed;
	private final Map<Long, Region> regions = new LinkedHashMap<>(16, 0.75F, true) {
		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, Region> eldest) {
			return this.size() > REGION_CACHE;
		}
	};

	private HubLayout(long seed) {
		this.seed = seed;
	}

	public int airHeight(int x, int z) {
		int i = Math.floorDiv(x, CELL);
		int k = Math.floorDiv(z, CELL);
		return this.regionOfCell(i, k).airHeight(x, z, i, k);
	}

	public boolean isReserved(int x, int z) {
		return this.regionOfCell(Math.floorDiv(x, CELL), Math.floorDiv(z, CELL)).reserved.contains(key(x, z));
	}

	public boolean isScreenColumn(int x, int z) {
		int i = Math.floorDiv(x, CELL);
		int k = Math.floorDiv(z, CELL);
		Region region = this.regionOfCell(i, k);
		return region.screens.contains(key(x, z)) || region.isCrossPost(x - i * CELL, z - k * CELL, i, k);
	}

	public boolean isPillarFloor(int x, int z) {
		int i = Math.floorDiv(x, CELL);
		int k = Math.floorDiv(z, CELL);
		return x - i * CELL == 1 && z - k * CELL == 1 && this.regionOfCell(i, k).isCrossing(i, k);
	}

	public List<Placement> placementsInChunk(int chunkX, int chunkZ) {
		List<Placement> found = new ArrayList<>();
		for (Region region : this.regionsOverChunk(chunkX, chunkZ)) {
			List<Placement> list = region.placementsByChunk.get(key(chunkX, chunkZ));
			if (list != null) {
				found.addAll(list);
			}
		}
		return found;
	}

	public List<Decoration> decorationsInChunk(int chunkX, int chunkZ) {
		List<Decoration> found = new ArrayList<>();
		for (Region region : this.regionsOverChunk(chunkX, chunkZ)) {
			List<Decoration> list = region.decorationsByChunk.get(key(chunkX, chunkZ));
			if (list != null) {
				found.addAll(list);
			}
		}
		return found;
	}

	public int[] landingNear(int x, int z) {
		int i = Math.floorDiv(x, CELL);
		int k = Math.floorDiv(z, CELL);
		int[] best = null;
		int bestDistance = Integer.MAX_VALUE;
		for (int lx = 0; lx < CELL; lx++) {
			for (int lz = 0; lz < CELL; lz++) {
				int wx = i * CELL + lx;
				int wz = k * CELL + lz;
				if (this.airHeight(wx, wz) == 0 || this.isReserved(wx, wz)) {
					continue;
				}
				int distance = Math.abs(wx - x) + Math.abs(wz - z);
				if (distance < bestDistance) {
					bestDistance = distance;
					best = new int[]{wx, wz};
				}
			}
		}
		return best != null ? best : new int[]{i * CELL + 1, k * CELL + 1};
	}

	private Region regionOfCell(int i, int k) {
		return this.region(Math.floorDiv(i, REGION), Math.floorDiv(k, REGION));
	}

	private List<Region> regionsOverChunk(int chunkX, int chunkZ) {
		List<Region> found = new ArrayList<>(4);
		for (int rx = regionOfBlock(chunkX * 16); rx <= regionOfBlock(chunkX * 16 + 15); rx++) {
			for (int rz = regionOfBlock(chunkZ * 16); rz <= regionOfBlock(chunkZ * 16 + 15); rz++) {
				found.add(this.region(rx, rz));
			}
		}
		return found;
	}

	private record RegionMemo(int rx, int rz, Region region) {}

	private final ThreadLocal<RegionMemo> lastRegion = new ThreadLocal<>();

	private Region region(int rx, int rz) {
		RegionMemo memo = this.lastRegion.get();
		if (memo != null && memo.rx() == rx && memo.rz() == rz) {
			return memo.region();
		}
		Region region = this.sharedRegion(rx, rz);
		this.lastRegion.set(new RegionMemo(rx, rz, region));
		return region;
	}

	private synchronized Region sharedRegion(int rx, int rz) {
		long regionKey = key(rx, rz);
		Region region = this.regions.get(regionKey);
		if (region == null) {
			region = new Region(this.seed, rx, rz);
			this.regions.put(regionKey, region);
		}
		return region;
	}

	private static int regionOfBlock(int blockCoordinate) {
		return Math.floorDiv(Math.floorDiv(blockCoordinate, CELL), REGION);
	}

	private static long key(int a, int b) {
		return ((long) a << 32) ^ (b & 0xFFFFFFFFL);
	}

	private static int bit(int side) {
		return 1 << side;
	}

	private static int opposite(int side) {
		return (side + 2) & 3;
	}

	private static long mix(long seed, int a, int b, long salt) {
		long h = seed ^ salt;
		h ^= a * 0x9E3779B97F4A7C15L;
		h = Long.rotateLeft(h, 31) ^ (b * 0xC2B2AE3D27D4EB4FL);
		return Long.rotateLeft(h, 17) * 0x94D049BB133111EBL;
	}

	private static boolean mazeCarves(int open, int lx, int lz) {
		boolean rows = lz >= 1 && lz <= 3;
		boolean columns = lx >= 1 && lx <= 3;
		if (rows && columns) {
			return true;
		}
		if (rows && (lx >= 4 ? (open & bit(XP)) != 0 : (open & bit(XM)) != 0)) {
			return true;
		}
		return columns && (lz >= 4 ? (open & bit(ZP)) != 0 : (open & bit(ZM)) != 0);
	}

	private record Room(int x0, int z0, int variant) {
		boolean contains(int x, int z) {
			return x >= this.x0 && x <= this.x0 + 9 && z >= this.z0 && z <= this.z0 + 9;
		}
	}

	private record Diagonal(int ox, int oz, boolean down) {
		boolean carves(int x, int z) {
			int dx = x - this.ox;
			int dz = z - this.oz;
			if (dx < 0 || dx > 6) {
				return false;
			}
			return this.down ? dz >= 0 && dz <= 6 && Math.abs(dx - dz) <= 1 : dz <= 0 && dz >= -6 && Math.abs(dx + dz) <= 1;
		}
	}

	private static final class Region {
		private final long seed;
		private final int baseI;
		private final int baseK;
		private final boolean spawnRegion;
		private final int[] open = new int[REGION * REGION];
		private final boolean[] hubDoorCell = new boolean[REGION * REGION];
		private final Room[] roomOf = new Room[REGION * REGION];
		private final byte[] end = new byte[REGION * REGION];
		private final byte[] endSide = new byte[REGION * REGION];

		private final Fixture[] gameDoor = new Fixture[REGION * REGION];
		@SuppressWarnings("unchecked")
		private final List<Diagonal>[] diagonalsOf = new List[REGION * REGION];
		private final Map<Long, List<Placement>> placementsByChunk = new HashMap<>();
		private final Map<Long, List<Decoration>> decorationsByChunk = new HashMap<>();
		private final Set<Long> reserved = new HashSet<>();
		private final Set<Long> screens = new HashSet<>();

		Region(long seed, int rx, int rz) {
			this.seed = seed;
			this.baseI = rx * REGION;
			this.baseK = rz * REGION;
			this.spawnRegion = rx == 0 && rz == 0;
			Random rand = new Random(mix(seed, rx, rz, 0x4855422148554221L));

			this.carve(rand);
			this.braid(rand);
			this.openEdges(seed, rx, rz);
			if (this.spawnRegion) {

				this.openBoth(0, 0, XP);
				this.openBoth(0, 0, ZP);
				this.open[0] |= bit(XM);
				this.addDoor(Fixture.HUB_DOOR, DOOR_IN_CELL, 0, false, true);
			}
			this.hubDoors();
			this.room(rand);
			this.chooseEnds(rand);
			this.chooseExterior(rand);
			this.chooseGameDoors(seed, rx, rz);
			this.buildEnds(rand);
			this.diagonals(rand);
			this.torches();
		}

		int airHeight(int x, int z, int i, int k) {
			int idx = index(i - this.baseI, k - this.baseK);
			Room room = this.roomOf[idx];
			if (room != null && room.contains(x, z)) {
				return ROOM_HEIGHT;
			}
			if (mazeCarves(this.open[idx], x - i * CELL, z - k * CELL)) {
				return HALL_HEIGHT;
			}
			List<Diagonal> diagonals = this.diagonalsOf[idx];
			if (diagonals != null) {
				for (Diagonal diagonal : diagonals) {
					if (diagonal.carves(x, z)) {
						return HALL_HEIGHT;
					}
				}
			}
			return 0;
		}

		boolean isCrossing(int i, int k) {
			int idx = index(i - this.baseI, k - this.baseK);
			return this.roomOf[idx] == null && Integer.bitCount(this.open[idx]) >= 3;
		}

		boolean isCrossPost(int lx, int lz, int i, int k) {
			if (!this.isCrossing(i, k)) {
				return false;
			}
			int open = this.open[index(i - this.baseI, k - this.baseK)];
			int sideX = lx == 0 ? XM : lx == 4 ? XP : -1;
			int sideZ = lz == 0 ? ZM : lz == 4 ? ZP : -1;
			return sideX >= 0 && sideZ >= 0 && (open & bit(sideX)) != 0 && (open & bit(sideZ)) != 0;
		}

		private void carve(Random rand) {
			boolean[] visited = new boolean[REGION * REGION];
			Deque<Integer> stack = new ArrayDeque<>();
			int start = rand.nextInt(REGION * REGION);
			visited[start] = true;
			stack.push(start);
			int[] order = {XP, ZP, XM, ZM};
			while (!stack.isEmpty()) {
				int current = stack.peek();
				int li = current % REGION;
				int lk = current / REGION;
				shuffle(order, rand);
				boolean moved = false;
				for (int side : order) {
					int ni = li + DX[side];
					int nk = lk + DZ[side];
					if (ni < 0 || nk < 0 || ni >= REGION || nk >= REGION || visited[index(ni, nk)]) {
						continue;
					}
					this.openBoth(li, lk, side);
					visited[index(ni, nk)] = true;
					stack.push(index(ni, nk));
					moved = true;
					break;
				}
				if (!moved) {
					stack.pop();
				}
			}
		}

		private void braid(Random rand) {
			for (int idx = 0; idx < REGION * REGION; idx++) {
				int side = rand.nextInt(4);
				if (rand.nextInt(BRAID_CHANCE) != 0) {
					continue;
				}
				int ni = idx % REGION + DX[side];
				int nk = idx / REGION + DZ[side];
				if (ni >= 0 && nk >= 0 && ni < REGION && nk < REGION) {
					this.openBoth(idx % REGION, idx / REGION, side);
				}
			}
		}

		private void openEdges(long seed, int rx, int rz) {
			for (int along = 0; along < REGION; along++) {
				if (edgeOpen(seed, true, rx, rz, along)) {
					this.open[index(REGION - 1, along)] |= bit(XP);
				}
				if (edgeOpen(seed, true, rx - 1, rz, along)) {
					this.open[index(0, along)] |= bit(XM);
				}
				if (edgeOpen(seed, false, rx, rz, along)) {
					this.open[index(along, REGION - 1)] |= bit(ZP);
				}
				if (edgeOpen(seed, false, rx, rz - 1, along)) {
					this.open[index(along, 0)] |= bit(ZM);
				}
			}
		}

		private void hubDoors() {
			int minX = this.baseI * CELL;
			int maxX = (this.baseI + REGION) * CELL - 1;
			int minZ = this.baseK * CELL;
			int maxZ = (this.baseK + REGION) * CELL - 1;
			for (int chunkX = Math.floorDiv(minX, 16); chunkX <= Math.floorDiv(maxX, 16); chunkX++) {
				for (int chunkZ = Math.floorDiv(minZ, 16); chunkZ <= Math.floorDiv(maxZ, 16); chunkZ++) {
					HubDoorSites.Site site = HubDoorSites.site(this.seed, chunkX, chunkZ);
					if (site == null || site.x() < minX || site.x() > maxX || site.z() < minZ || site.z() > maxZ) {
						continue;
					}
					this.hubDoorCell[index(Math.floorDiv(site.x(), CELL) - this.baseI, Math.floorDiv(site.z(), CELL) - this.baseK)] = true;
					this.addDoor(Fixture.HUB_DOOR, site.x(), site.z(), site.axisX(), false);
				}
			}
		}

		private void room(Random rand) {
			if (rand.nextInt(ROOM_CHANCE) == 0) {
				return;
			}
			for (int attempt = 0; attempt < 24; attempt++) {
				int li = rand.nextInt(REGION - 1);
				int lk = rand.nextInt(REGION - 1);
				int variant = rand.nextInt(3);
				if (!this.roomFits(li, lk)) {
					continue;
				}
				Room room = new Room((this.baseI + li) * CELL + 1, (this.baseK + lk) * CELL + 1, variant);
				for (int di = 0; di < 2; di++) {
					for (int dk = 0; dk < 2; dk++) {
						this.roomOf[index(li + di, lk + dk)] = room;
					}
				}

				this.openBoth(li, lk, XP);
				this.openBoth(li, lk, ZP);
				this.openBoth(li + 1, lk, ZP);
				this.openBoth(li, lk + 1, XP);
				this.furnish(room);
				return;
			}
		}

		private boolean roomFits(int li, int lk) {
			for (int di = 0; di < 2; di++) {
				for (int dk = 0; dk < 2; dk++) {
					int idx = index(li + di, lk + dk);
					if (this.hubDoorCell[idx] || this.spawnRegion && idx == 0) {
						return false;
					}
				}
			}
			return true;
		}

		private void furnish(Room room) {
			int x0 = room.x0();
			int z0 = room.z0();
			int f = FLOOR_Y;
			switch (room.variant()) {
				case 0 -> {

					Piece[] lilies = {Piece.LILY_FLAME, Piece.LILY_GOLD, Piece.LILY_OBSIDIAN};
					for (int ox = 3; ox <= 6; ox++) {
						for (int oz = 3; oz <= 6; oz++) {
							this.place(x0 + ox, f + 1, z0 + oz, Piece.WALL);
							this.reserve(x0 + ox, z0 + oz);
							if (ox >= 4 && ox <= 5 && oz >= 4 && oz <= 5) {
								for (int y = f + 2; y <= f + ROOM_HEIGHT; y++) {
									this.place(x0 + ox, y, z0 + oz, Piece.FLAMEWOOD_LOG);
								}
							} else {
								this.place(x0 + ox, f + 2, z0 + oz, lilies[Math.floorMod(ox + oz, 3)]);
							}
						}
					}
					int[][] flames = {{1, 1}, {1, 8}, {8, 1}, {8, 8}, {4, 2}, {2, 5}, {7, 4}, {5, 7}};
					for (int[] flame : flames) {
						this.place(x0 + flame[0], f + ROOM_HEIGHT, z0 + flame[1], Piece.CELESTIAL_FLAME);
					}
				}
				case 1 -> {

					for (int ox = 3; ox <= 6; ox++) {
						for (int oz = 3; oz <= 6; oz++) {
							this.place(x0 + ox, f - 1, z0 + oz, Piece.TILE);
							this.place(x0 + ox, f, z0 + oz, Piece.WATER);
							this.reserve(x0 + ox, z0 + oz);
						}
					}
					int[][] lilies = {{3, 4}, {5, 3}, {6, 5}, {4, 6}};
					for (int[] lily : lilies) {
						this.place(x0 + lily[0], f + 1, z0 + lily[1], Piece.WATER_LILY);
					}
					this.screenPillar(x0 + 1, z0 + 1, 1);
					this.screenPillar(x0 + 8, z0 + 1, 1);
					this.screenPillar(x0 + 1, z0 + 8, 1);
					this.screenPillar(x0 + 8, z0 + 8, 1);
				}
				default -> {

					this.screenPillar(x0 + 2, z0 + 2, 2);
					this.screenPillar(x0 + 6, z0 + 2, 2);
					this.screenPillar(x0 + 2, z0 + 6, 2);
					this.screenPillar(x0 + 6, z0 + 6, 2);
					for (int ox = 4; ox <= 5; ox++) {
						for (int oz = 4; oz <= 5; oz++) {
							this.place(x0 + ox, f, z0 + oz, Piece.PILLAR);
						}
					}
					this.place(x0 + 4, f + ROOM_HEIGHT, z0 + 4, Piece.CELESTIAL_FLAME);
					this.place(x0 + 5, f + ROOM_HEIGHT, z0 + 5, Piece.CELESTIAL_FLAME);
				}
			}

			for (int along : new int[]{2, 7}) {
				this.roomTorch(x0, z0 + along, XM);
				this.roomTorch(x0 + 9, z0 + along, XP);
				this.roomTorch(x0 + along, z0, ZM);
				this.roomTorch(x0 + along, z0 + 9, ZP);
			}
		}

		private void screenPillar(int x, int z, int size) {
			for (int dx = 0; dx < size; dx++) {
				for (int dz = 0; dz < size; dz++) {
					for (int y = FLOOR_Y + 1; y <= FLOOR_Y + ROOM_HEIGHT; y++) {
						this.place(x + dx, y, z + dz, Piece.SCREEN);
					}
					this.reserve(x + dx, z + dz);
				}
			}
		}

		private void roomTorch(int x, int z, int side) {
			int wallX = x + DX[side];
			int wallZ = z + DZ[side];
			int i = Math.floorDiv(wallX, CELL);
			int k = Math.floorDiv(wallZ, CELL);
			if (mazeCarves(this.open[index(i - this.baseI, k - this.baseK)], wallX - i * CELL, wallZ - k * CELL)) {
				return;
			}
			this.addDecoration(new Decoration(Fixture.TORCH, x, FLOOR_Y + 4, z, TORCH_ON_SIDE[side]));
		}

		private void chooseEnds(Random rand) {
			int fallback = -1;
			boolean anyDoor = false;
			for (int idx = 0; idx < REGION * REGION; idx++) {
				if (Integer.bitCount(this.open[idx]) != 1 || this.roomOf[idx] != null || this.hubDoorCell[idx]
					|| this.spawnRegion && idx == 0) {
					continue;
				}
				this.endSide[idx] = (byte) opposite(Integer.numberOfTrailingZeros(this.open[idx]));
				fallback = idx;
				if (rand.nextInt(CYPRESS_DOOR_CHANCE) == 0) {
					this.end[idx] = END_DOOR;
					anyDoor = true;
				} else {
					this.end[idx] = END_SCREEN;
				}
			}
			if (!anyDoor && fallback >= 0) {
				this.end[fallback] = END_DOOR;
			}
		}

		private void chooseExterior(Random rand) {
			if (rand.nextInt(BUBBLE_CHANCE) != 0) {
				return;
			}
			List<Integer> candidates = new ArrayList<>();
			for (int idx = 0; idx < REGION * REGION; idx++) {
				int li = idx % REGION;
				int lk = idx / REGION;
				if (this.end[idx] == END_SCREEN && li >= 1 && lk >= 1 && li <= REGION - 2 && lk <= REGION - 2 && this.noRoomAround(li, lk)) {
					candidates.add(idx);
				}
			}
			if (!candidates.isEmpty()) {
				this.end[candidates.get(rand.nextInt(candidates.size()))] = END_LADDER;
			}
		}

		private void chooseGameDoors(long seed, int rx, int rz) {
			Random rand = new Random(mix(seed, rx, rz, 0x4D494E4947414D45L));
			int cypressDoors = 0;
			for (int idx = 0; idx < REGION * REGION; idx++) {
				if (this.end[idx] == END_DOOR) {
					cypressDoors++;
				}
			}
			for (int idx = 0; idx < REGION * REGION; idx++) {
				if (this.end[idx] != END_DOOR) {
					continue;
				}
				boolean chosen = rand.nextInt(GAME_DOOR_CHANCE) == 0;
				boolean zombies = rand.nextBoolean();
				if (chosen && cypressDoors > 1) {
					this.gameDoor[idx] = zombies ? Fixture.ZOMBIES_DOOR : Fixture.FREERUN_DOOR;
					cypressDoors--;
				}
			}
		}

		private boolean noRoomAround(int li, int lk) {
			for (int di = -1; di <= 1; di++) {
				for (int dk = -1; dk <= 1; dk++) {
					if (this.roomOf[index(li + di, lk + dk)] != null) {
						return false;
					}
				}
			}
			return true;
		}

		private void buildEnds(Random rand) {
			for (int idx = 0; idx < REGION * REGION; idx++) {
				int li = idx % REGION;
				int lk = idx / REGION;
				int side = this.endSide[idx];
				switch (this.end[idx]) {
					case END_DOOR -> this.doorEnd(li, lk, side);
					case END_SCREEN -> {
						for (int along = 1; along <= 3; along++) {
							int[] wall = this.endColumn(li, lk, side, along, 0);
							this.screens.add(key(wall[0], wall[1]));
						}
					}
					case END_LADDER -> this.ladderEnd(li, lk, side, rand);
					default -> {
					}
				}
			}
		}

		private int[] endColumn(int li, int lk, int side, int along, int depth) {
			int bx = (this.baseI + li) * CELL;
			int bz = (this.baseK + lk) * CELL;
			return switch (side) {
				case XP -> new int[]{bx + 4 - depth, bz + along};
				case XM -> new int[]{bx + depth, bz + along};
				case ZP -> new int[]{bx + along, bz + 4 - depth};
				default -> new int[]{bx + along, bz + depth};
			};
		}

		private void doorEnd(int li, int lk, int side) {
			int[] door = this.endColumn(li, lk, side, 2, 0);
			boolean axisX = side == XP || side == XM;
			Fixture kind = this.gameDoor[index(li, lk)];

			this.addDoor(kind != null ? kind : Fixture.CYPRESS_DOOR, door[0], door[1], axisX, side == XM || side == ZM);
			for (int along = 1; along <= 3; along += 2) {
				for (int depth = 1; depth <= 2; depth++) {
					int[] channel = this.endColumn(li, lk, side, along, depth);
					this.place(channel[0], FLOOR_Y - 1, channel[1], Piece.TILE);
					this.place(channel[0], FLOOR_Y, channel[1], Piece.AIR);
					this.reserve(channel[0], channel[1]);
					if (depth == 1) {
						this.addDecoration(new Decoration(Fixture.WATER_SOURCE, channel[0], FLOOR_Y, channel[1], 0));
					}
				}
			}
		}

		private void ladderEnd(int li, int lk, int side, Random rand) {
			int[] ladder = this.endColumn(li, lk, side, 2, 1);
			int ground = FLOOR_Y + 11;
			this.exterior(li, lk, ladder, rand);

			for (int y = FLOOR_Y + HALL_HEIGHT + 1; y <= ground; y++) {
				this.place(ladder[0], y, ladder[1], Piece.AIR);
			}
			for (int y = FLOOR_Y + 1; y <= ground; y++) {
				this.addDecoration(new Decoration(Fixture.LADDER, ladder[0], y, ladder[1], LADDER_ON_SIDE[side]));
			}
			this.reserve(ladder[0], ladder[1]);

			int[] wall = this.endColumn(li, lk, side, 2, 0);
			this.reserve(wall[0], wall[1]);
		}

		private void exterior(int li, int lk, int[] ladder, Random rand) {
			int cx = (this.baseI + li) * CELL;
			int cz = (this.baseK + lk) * CELL;
			int x0 = cx - 5;
			int x1 = cx + 9;
			int z0 = cz - 5;
			int z1 = cz + 9;
			int bottom = FLOOR_Y + 8;
			int top = FLOOR_Y + 20;
			int ground = FLOOR_Y + 11;

			for (int x = x0; x <= x1; x++) {
				for (int z = z0; z <= z1; z++) {
					this.place(x, bottom, z, Piece.SCREEN);
					this.place(x, top, z, Piece.SCREEN);
					boolean shell = x == x0 || x == x1 || z == z0 || z == z1;
					if (shell) {
						for (int y = bottom + 1; y < top; y++) {
							this.place(x, y, z, Piece.SCREEN);
						}
					} else {
						this.place(x, ground - 2, z, Piece.FAKE_STONE);
						this.place(x, ground - 1, z, Piece.FAKE_DIRT);
						this.place(x, ground, z, Piece.FAKE_GRASS);
					}
				}
			}

			Set<Long> taken = new HashSet<>();
			for (int dx = -1; dx <= 1; dx++) {
				for (int dz = -1; dz <= 1; dz++) {
					taken.add(key(ladder[0] + dx, ladder[1] + dz));
				}
			}
			int innerX0 = x0 + 1;
			int innerZ0 = z0 + 1;
			int innerSize = x1 - x0 - 1;

			for (int attempt = 0; attempt < 16; attempt++) {
				int width = 3;
				int depthZ = 2 + rand.nextInt(2);
				int px = innerX0 + 1 + rand.nextInt(innerSize - width - 1);
				int pz = innerZ0 + 1 + rand.nextInt(innerSize - depthZ - 1);
				if (!free(taken, px - 1, pz - 1, width + 2, depthZ + 2)) {
					continue;
				}
				for (int x = px - 1; x <= px + width; x++) {
					for (int z = pz - 1; z <= pz + depthZ; z++) {
						boolean water = x >= px && x < px + width && z >= pz && z < pz + depthZ;
						this.place(x, ground, z, water ? Piece.WATER : Piece.FAKE_SAND);
						if (water) {
							this.place(x, ground - 1, z, Piece.FAKE_SAND);
						}
						taken.add(key(x, z));
					}
				}
				break;
			}

			for (int tree = 0; tree < 2; tree++) {
				for (int attempt = 0; attempt < 16; attempt++) {
					int tx = innerX0 + 2 + rand.nextInt(innerSize - 4);
					int tz = innerZ0 + 2 + rand.nextInt(innerSize - 4);
					if (!free(taken, tx - 2, tz - 2, 5, 5)) {
						continue;
					}
					for (int y = ground + 1; y <= ground + 4; y++) {
						this.place(tx, y, tz, Piece.LOG);
					}
					for (int dy = 3; dy <= 6; dy++) {
						int radius = dy <= 4 ? 2 : 1;
						for (int dx = -radius; dx <= radius; dx++) {
							for (int dz = -radius; dz <= radius; dz++) {
								if (dx == 0 && dz == 0 && dy <= 4) {
									continue;
								}
								if (Math.abs(dx) == radius && Math.abs(dz) == radius && (radius == 2 || dy == 6)) {
									continue;
								}
								this.place(tx + dx, ground + dy, tz + dz, Piece.LEAVES);
							}
						}
					}
					for (int dx = -2; dx <= 2; dx++) {
						for (int dz = -2; dz <= 2; dz++) {
							taken.add(key(tx + dx, tz + dz));
						}
					}
					break;
				}
			}

			for (int n = 0; n < 8; n++) {
				int[] spot = freeSpot(taken, innerX0, innerZ0, innerSize, rand);
				if (spot == null) {
					break;
				}
				if (n < 3) {
					this.place(spot[0], ground + 1, spot[1], Piece.FAKE_STONE);
					if (rand.nextBoolean()) {
						this.place(spot[0], ground + 2, spot[1], Piece.FAKE_STONE);
					}
				} else if (n < 7) {
					this.place(spot[0], ground + 1, spot[1], Piece.WATER_LILY);
				} else {
					this.place(spot[0], ground + 1, spot[1], Piece.WALL);
					this.place(spot[0], ground + 2, spot[1], Piece.WALL);
					this.addDecoration(new Decoration(Fixture.TORCH, spot[0], ground + 3, spot[1], TORCH_ON_FLOOR));
				}
			}
			for (int n = 0; n < 2; n++) {
				int fx = innerX0 + 1 + rand.nextInt(innerSize - 2);
				int fz = innerZ0 + 1 + rand.nextInt(innerSize - 2);
				this.place(fx, ground + 7, fz, Piece.CELESTIAL_FLAME);
			}
		}

		private static boolean free(Set<Long> taken, int x, int z, int width, int depth) {
			for (int dx = 0; dx < width; dx++) {
				for (int dz = 0; dz < depth; dz++) {
					if (taken.contains(key(x + dx, z + dz))) {
						return false;
					}
				}
			}
			return true;
		}

		private static int[] freeSpot(Set<Long> taken, int x0, int z0, int size, Random rand) {
			for (int attempt = 0; attempt < 24; attempt++) {
				int x = x0 + rand.nextInt(size);
				int z = z0 + rand.nextInt(size);
				if (!taken.contains(key(x, z))) {
					taken.add(key(x, z));
					return new int[]{x, z};
				}
			}
			return null;
		}

		private void diagonals(Random rand) {
			boolean[] cornerUsed = new boolean[REGION * REGION];
			for (int lk = 0; lk < REGION; lk++) {
				for (int li = 0; li < REGION - 1; li++) {
					boolean down = rand.nextBoolean();
					if (rand.nextInt(DIAGONAL_CHANCE) != 0) {
						continue;
					}
					int lk2 = lk + (down ? 1 : -1);
					if (lk2 < 0 || lk2 >= REGION) {
						continue;
					}
					int[] cells = {index(li, lk), index(li + 1, lk), index(li, lk2), index(li + 1, lk2)};
					boolean plain = true;
					for (int cell : cells) {
						plain &= this.isPlain(cell);
					}
					int corner = index(li, Math.min(lk, lk2));
					if (!plain || cornerUsed[corner]) {
						continue;
					}
					cornerUsed[corner] = true;
					Diagonal diagonal = new Diagonal((this.baseI + li) * CELL + 2, (this.baseK + lk) * CELL + 2, down);
					for (int cell : cells) {
						if (this.diagonalsOf[cell] == null) {
							this.diagonalsOf[cell] = new ArrayList<>(2);
						}
						this.diagonalsOf[cell].add(diagonal);
					}
				}
			}
		}

		private boolean isPlain(int idx) {
			return this.roomOf[idx] == null && this.end[idx] == END_NONE && !this.hubDoorCell[idx] && !(this.spawnRegion && idx == 0);
		}

		private void torches() {
			for (int idx = 0; idx < REGION * REGION; idx++) {
				if (this.roomOf[idx] != null) {
					continue;
				}
				int li = idx % REGION;
				int lk = idx / REGION;
				int start = Math.floorMod((this.baseI + li) * 31 + (this.baseK + lk) * 17, 4);
				for (int n = 0; n < 4; n++) {
					int side = (start + n) & 3;
					if ((this.open[idx] & bit(side)) != 0 || this.end[idx] == END_LADDER && side == this.endSide[idx]) {
						continue;
					}
					int[] spot = this.endColumn(li, lk, side, 2, 1);
					this.addDecoration(new Decoration(Fixture.TORCH, spot[0], FLOOR_Y + HALL_HEIGHT, spot[1], TORCH_ON_SIDE[side]));
					break;
				}
			}
		}

		private void addDoor(Fixture fixture, int x, int z, boolean axisX, boolean far) {
			this.addDecoration(new Decoration(fixture, x, FLOOR_Y + 1, z, (axisX ? DOOR_AXIS_X : 0) | (far ? DOOR_FAR : 0)));
			this.reserve(x, z);
		}

		private void addDecoration(Decoration decoration) {
			this.decorationsByChunk.computeIfAbsent(key(Math.floorDiv(decoration.x(), 16), Math.floorDiv(decoration.z(), 16)),
				k -> new ArrayList<>()).add(decoration);
		}

		private void place(int x, int y, int z, Piece piece) {
			this.placementsByChunk.computeIfAbsent(key(Math.floorDiv(x, 16), Math.floorDiv(z, 16)), k -> new ArrayList<>())
				.add(new Placement(x, y, z, piece));
		}

		private void reserve(int x, int z) {
			this.reserved.add(key(x, z));
		}

		private void openBoth(int li, int lk, int side) {
			this.open[index(li, lk)] |= bit(side);
			this.open[index(li + DX[side], lk + DZ[side])] |= bit(opposite(side));
		}

		private static int index(int li, int lk) {
			return lk * REGION + li;
		}

		private static void shuffle(int[] order, Random rand) {
			for (int n = order.length - 1; n > 0; n--) {
				int swap = rand.nextInt(n + 1);
				int held = order[n];
				order[n] = order[swap];
				order[swap] = held;
			}
		}
	}

	private static boolean edgeOpen(long seed, boolean east, int lowRx, int lowRz, int along) {
		if (east && lowRx == -1 && lowRz == 0 && along == 0) {
			return true;
		}
		if (!east && lowRx == 0 && lowRz == -1 && along == 0) {
			return false;
		}
		Random rand = new Random(mix(seed, lowRx, lowRz, east ? 0x45415354L : 0x534F5554L));
		int chosen = 0;
		boolean[] open = new boolean[REGION];
		while (chosen < EDGE_OPENINGS) {
			int at = rand.nextInt(REGION);
			if (!open[at]) {
				open[at] = true;
				chosen++;
			}
		}
		return open[along];
	}
}
