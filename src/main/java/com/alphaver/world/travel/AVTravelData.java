package com.alphaver.world.travel;

public interface AVTravelData {

	boolean alphaver$hasReturn();

	int alphaver$returnX();

	int alphaver$returnY();

	int alphaver$returnZ();

	void alphaver$setReturn(int x, int y, int z);

	void alphaver$clearReturn();

	boolean alphaver$invited();

	void alphaver$setInvited(boolean invited);

	int UNKNOWN_DIMENSION = Integer.MIN_VALUE;

	boolean alphaver$hasHome();

	int alphaver$homeX();

	int alphaver$homeY();

	int alphaver$homeZ();

	void alphaver$setHome(int x, int y, int z);

	boolean alphaver$homeTripPending();

	void alphaver$setHomeTripPending(boolean pending);

	int alphaver$spawnDimension();

	boolean alphaver$loadedFromSave();
}
