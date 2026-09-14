package com.alphaver.entity;

public interface AVMobEventData {
	long alphaver$observerCooldown();

	void alphaver$setObserverCooldown(long ticks);

	void alphaver$addObserverCooldown(long delta);

	boolean alphaver$tracking();

	long alphaver$lastBlockX();

	long alphaver$lastBlockZ();

	long alphaver$lastAreaX();

	long alphaver$lastAreaZ();

	void alphaver$track(long blockX, long blockZ, long areaX, long areaZ);

	void alphaver$stopTracking();
}
