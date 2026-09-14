package com.alphaver.world.gen;

public enum CypressBiomeKind {

	SURFACE_0(0.2, 0.2, Layer.SURFACE, 0),

	SURFACE_1(0.3, 0.3, Layer.SURFACE, 1),

	SURFACE_2(0.4, 0.3, Layer.SURFACE, 2),

	UNDERGROUND_0(0.2, 0.2, Layer.UNDERGROUND, 0),

	LOW_RIVER(0.1, 0.3, Layer.LOW_RIVER, 0),

	MYCON(0.15, 0.4, Layer.LOW_RIVER, -1);

	public enum Layer {
		SURFACE,
		UNDERGROUND,
		LOW_RIVER
	}

	public final double temperature;
	public final double humidity;
	public final Layer declaredLayer;

	public final int variant;

	CypressBiomeKind(double temperature, double humidity, Layer declaredLayer, int variant) {
		this.temperature = temperature;
		this.humidity = humidity;
		this.declaredLayer = declaredLayer;
		this.variant = variant;
	}

	private static final CypressBiomeKind[] VALUES = values();

	public static CypressBiomeKind nearest(double temperature, double humidity) {
		double best = Double.MAX_VALUE;
		CypressBiomeKind chosen = null;
		for (CypressBiomeKind kind : VALUES) {
			double distance = Math.abs(temperature - kind.temperature) + Math.abs(humidity - kind.humidity);
			if (best > distance) {
				best = distance;
				chosen = kind;
			}
		}
		return chosen;
	}

	public boolean decoratesNormally() {
		return this != MYCON;
	}

	public boolean isHighwoodVariant() {
		return this.variant == 2 && this.declaredLayer == Layer.SURFACE;
	}

	public static CypressBiomeKind byOrdinal(int ordinal) {
		return VALUES[ordinal];
	}
}
