package com.alphaver.item;

public interface AVMirrorSpawnData {

	boolean alphaver$hasMirrorSpawn();

	int alphaver$mirrorDimension();

	int alphaver$mirrorX();

	int alphaver$mirrorY();

	int alphaver$mirrorZ();

	void alphaver$setMirrorSpawn(int dimension, int x, int y, int z);

	void alphaver$clearMirrorSpawn();
}
