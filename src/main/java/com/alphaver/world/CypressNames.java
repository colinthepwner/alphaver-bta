package com.alphaver.world;

public final class CypressNames {
	private CypressNames() {}

	public static final int AREA_BLOCKS = 32;

	private static final String[] SYLLABLES = {
		"SIE", "LOH", "KII", "HUR", "MIS", "RUU", "VY", "KA", "TAV", "OLE", "PAH", "MUI", "MAT", "JA", "SAU", "NIN",
		"UD", "MU", "NGI", "BAR", "LUG", "MAH", "GIR", "AK", "USU", "ESE", "IRU", "UUN", "AMTU", "AGAS", "HI", "TOOI",
		"YORU", "NEN", "PON", "ONNA", "TSU", "YA", "AO", "ONI", "AN", "KO", "SHI", "YUME", "YARI", "NIM", "GYEONG",
		"KKOT", "ISA", "MI", "OI", "YS", "LES", "OUX", "IA", "CHA", "NIDA", "JEO", "SANA", "KOLI", "LOHI", "KAAR", "ME",
		"KISSA", "TEST",
	};

	public static String area(long seed, int areaX, int areaZ) {
		long x = areaX + 392214;
		long z = areaZ + 392214;
		long hash = z * 784428L + x;
		hash ^= seed;
		hash &= 2147483647L;
		int distance = (int) Math.sqrt(areaX * areaX + areaZ * areaZ);

		int prefixLength = 0;
		int step = 3;
		for (int d = distance; d / step > 0; step *= 4) {
			prefixLength++;
		}

		StringBuilder name = new StringBuilder();
		if (prefixLength > 0) {
			for (int i = 0; i != prefixLength; i++) {
				name.append(SYLLABLES[(int) ((distance + prefixLength + step + hash * (2 + i)) % 64L)]);
			}
			name.append('-');
		}
		name.append(SYLLABLES[(int) ((hash * 2L + distance) % 64L)]);
		name.append(SYLLABLES[(int) ((hash * 3L + distance) % 64L)]);
		name.append(SYLLABLES[(int) ((hash * 4L + distance) % 64L)]);
		return name.toString();
	}
}
