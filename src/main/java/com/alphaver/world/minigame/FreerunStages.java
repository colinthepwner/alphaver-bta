package com.alphaver.world.minigame;

import com.alphaver.world.minigame.map.CypressMap;
import org.jetbrains.annotations.NotNull;

final class FreerunStages {
	private FreerunStages() {}

	record Loc(int x1, int y1, int z1, int x2, int y2, int z2, boolean point) {
		static Loc point(int x, int y, int z) {
			return new Loc(x, y, z, x, y, z, true);
		}

		static Loc box(int x1, int y1, int z1, int x2, int y2, int z2) {
			return new Loc(x1, y1, z1, x2, y2, z2, false);
		}

		boolean contains(int x, int y, int z) {
			return x >= Math.min(this.x1, this.x2) && x <= Math.max(this.x1, this.x2)
				&& y >= Math.min(this.y1, this.y2) && y <= Math.max(this.y1, this.y2)
				&& z >= Math.min(this.z1, this.z2) && z <= Math.max(this.z1, this.z2);
		}

		double[] returnPosition() {
			if (this.point) {
				return new double[]{this.x1, this.y1 + 1, this.z1};
			}
			return new double[]{(this.x1 + this.x2) / 2, Math.min(this.y1, this.y2) + 1, (this.z1 + this.z2) / 2};
		}
	}

	record Portal(int x1, int y1, int z1, int x2, int y2, int z2, int toX, int toY, int toZ) {
		boolean contains(double x, double eyeY, double z) {
			return this.x1 <= x && x <= this.x2 && this.y1 <= eyeY && eyeY <= this.y2 && this.z1 <= z && z <= this.z2;
		}
	}

	abstract static class Stage {
		final MinigameStage stage;
		final Loc[] checkpoints;
		final Portal[] portals;

		final int offsetX;
		final int offsetZ;

		Stage(MinigameStage stage, Loc[] checkpoints, Portal[] portals, int offsetX, int offsetZ) {
			this.stage = stage;
			this.checkpoints = checkpoints;
			this.portals = portals;
			this.offsetX = offsetX;
			this.offsetZ = offsetZ;
		}

		abstract int resetY(long x, long z);
	}

	private static final class OnMap extends Stage {
		private final int resetY;

		OnMap(MinigameStage stage, int resetY, Loc[] checkpoints, Portal... portals) {
			super(stage, checkpoints, portals, stage.map == null ? 0 : stage.map.offsetX(), stage.map == null ? 0 : stage.map.offsetZ());
			this.resetY = resetY;
		}

		@Override
		int resetY(long x, long z) {
			return this.resetY;
		}
	}

	@NotNull
	static Stage of(@NotNull MinigameStage stage) {
		return switch (stage) {
			case INTRODUCTION -> new OnMap(stage, 70, new Loc[]{
				Loc.point(254, 79, 805),
				Loc.box(253, 78, 802, 255, 80, 800),
				Loc.box(247, 78, 796, 248, 80, 794),
				Loc.box(237, 80, 792, 240, 78, 796),
				Loc.box(236, 78, 772, 240, 80, 769)});
			case CONSTRUCT -> new OnMap(stage, 78, new Loc[]{
				Loc.point(-84, 97, 49),
				Loc.box(-124, 97, 50, -125, 95, 49),
				Loc.box(-132, 94, 81, -135, 100, 82),
				Loc.box(-130, 91, 94, -127, 94, 97),
				Loc.box(-85, 86, 79, -87, 89, 83),
				Loc.box(-112, 102, 56, -117, 107, 54)});
			case SKYLINE -> new OnMap(stage, 88, new Loc[]{
				Loc.point(-715, 103, -945),
				Loc.box(-718, 102, -944, -721, 104, -946),
				Loc.box(-752, 98, -910, -756, 102, -913),
				Loc.box(-698, 90, -818, -701, 94, -812),
				Loc.box(-697, 90, -783, -701, 93, -776),
				Loc.box(-652, 94, -800, -655, 96, -798),
				Loc.box(-633, 102, -769, -629, 104, -763),
				Loc.box(-574, 91, -776, -578, 95, -780)},
				new Portal(-759, 88, -859, -752, 95, -853, -705, 88, -819));
			case SORROW -> new Sorrow();
			case SHIVER -> new OnMap(stage, 77, new Loc[]{
				Loc.point(23, 82, -58),
				Loc.box(12, 83, -41, 14, 85, -44),
				Loc.box(17, 96, 33, 21, 100, 37),
				Loc.box(22, 94, 121, 18, 100, 118),
				Loc.box(12, 91, 196, 15, 95, 200),
				Loc.box(18, 112, 169, 21, 115, 172)});
			case FINALE -> new OnMap(stage, 76, new Loc[]{
				Loc.point(104, 97, -136),
				Loc.box(102, 96, -142, 105, 98, -141),
				Loc.box(116, 88, -284, 123, 92, -277)});
			default -> FreerunCourse.stage();
		};
	}

	private static final class Sorrow extends Stage {
		Sorrow() {
			super(MinigameStage.SORROW, new Loc[]{
					Loc.point(-233, 66, 22),
					Loc.box(-234, 65, 17, -231, 67, 16),
					Loc.box(-219, 68, -21, -218, 72, -24),
					Loc.box(-209, 72, -37, -212, 75, -40),
					Loc.box(-249, 120, -54, -253, 124, -57),
					Loc.box(-213, 65, -67, -211, 66, -69),
					Loc.box(-160, 65, -66, -158, 68, -70)},
				new Portal[]{
					new Portal(-254, 114, -48, -250, 120, -46, -254, 63, -48),
					new Portal(-227, 112, -58, -225, 115, -56, -216, 63, -72),
					new Portal(-254, 63, -53, -250, 68, -50, -254, 114, -53)},
				CypressMap.SORROW.offsetX(), CypressMap.SORROW.offsetZ());
		}

		@Override
		int resetY(long x, long z) {
			if (x >= -211L && x <= -158L && z >= -72L && z <= -64L) {
				return 51;
			}
			if (x >= -219L && x <= -204L && z >= -26L && z <= -22L) {
				return 63;
			}
			return 65;
		}
	}
}
