package com.alphaver.world.gen.feature;

import com.alphaver.block.AVBlocks;
import com.alphaver.world.gen.AlphaMath;
import com.alphaver.world.gen.CypressPalette;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.world.World;

import java.util.Random;

@SuppressWarnings("deprecation")
public final class FeatureBigTree implements CypressTreeFeature {
	private static final byte[] OTHER_COORD_PAIRS = {2, 0, 0, 1, 2, 1};

	private final Boolean highwood;
	private final Random rand = new Random();
	private World world;
	private final int[] basePos = new int[3];
	private int heightLimit = 0;
	private int height;
	private final double heightAttenuation = 0.618;
	private final double branchSlope = 0.381;
	private double scaleWidth = 1.0;
	private double leafDensity = 1.0;
	private int heightLimitLimit = 12;
	private int leafDistanceLimit = 4;
	private int[][] leafNodes;
	private int mainWoodId = CypressPalette.log();
	private int mainLeavesId = CypressPalette.leaves();

	public FeatureBigTree(Boolean highwood) {
		this.highwood = highwood;
	}

	@Override
	public void setScale(double scaleX, double scaleY, double scaleZ) {
		this.heightLimitLimit = (int) (scaleX * 12.0);
		if (scaleX > 0.5) {
			this.leafDistanceLimit = 5;
		}
		this.scaleWidth = scaleY;
		this.leafDensity = scaleZ;
	}

	@Override
	public boolean generate(World world, Random rand, int x, int y, int z) {
		if (this.highwood == null || rand.nextInt(150) != 0 && !this.highwood) {
			if (rand.nextInt(100) <= 5) {
				this.mainWoodId = AVBlocks.LOG_FLAMEWOOD.id();
				this.mainLeavesId = AVBlocks.LEAVES_FLAMEWOOD.id();
			}
			this.world = world;
			this.rand.setSeed(rand.nextLong());
			this.basePos[0] = x;
			this.basePos[1] = y;
			this.basePos[2] = z;
			if (this.heightLimit == 0) {
				this.heightLimit = 5 + this.rand.nextInt(this.heightLimitLimit);
			}
			if (!this.validTreeLocation()) {
				return false;
			}
			this.generateLeafNodeList();
			this.generateLeaves();
			this.generateTrunk();
			this.generateLeafNodeBases();
			return true;
		}
		return HighwoodTree.BIG.grow(world, rand, x, y, z);
	}

	private void generateLeafNodeList() {
		this.height = (int) (this.heightLimit * this.heightAttenuation);
		if (this.height >= this.heightLimit) {
			this.height = this.heightLimit - 1;
		}
		int perLayer = (int) (1.382 + Math.pow(this.leafDensity * this.heightLimit / 13.0, 2.0));
		if (perLayer < 1) {
			perLayer = 1;
		}
		int[][] nodes = new int[perLayer * this.heightLimit][4];
		int layerY = this.basePos[1] + this.heightLimit - this.leafDistanceLimit;
		int count = 1;
		int trunkTop = this.basePos[1] + this.height;
		int relative = layerY - this.basePos[1];
		nodes[0][0] = this.basePos[0];
		nodes[0][1] = layerY;
		nodes[0][2] = this.basePos[2];
		nodes[0][3] = trunkTop;
		layerY--;

		while (relative >= 0) {
			int placed = 0;
			float size = this.layerSize(relative);
			if (size < 0.0F) {
				layerY--;
				relative--;
				continue;
			}
			while (placed < perLayer) {
				double radius = this.scaleWidth * size * (this.rand.nextFloat() + 0.328);
				double angle = this.rand.nextFloat() * 2.0 * 3.14159;
				int nx = (int) (radius * Math.sin(angle) + this.basePos[0] + 0.5);
				int nz = (int) (radius * Math.cos(angle) + this.basePos[2] + 0.5);
				int[] start = {nx, layerY, nz};
				int[] end = {nx, layerY + this.leafDistanceLimit, nz};
				if (this.checkBlockLine(start, end) == -1) {
					int[] base = {this.basePos[0], this.basePos[1], this.basePos[2]};
					double distance = Math.sqrt(Math.pow(Math.abs(this.basePos[0] - start[0]), 2.0)
						+ Math.pow(Math.abs(this.basePos[2] - start[2]), 2.0));
					double drop = distance * this.branchSlope;
					if (start[1] - drop > trunkTop) {
						base[1] = trunkTop;
					} else {
						base[1] = (int) (start[1] - drop);
					}
					if (this.checkBlockLine(base, start) == -1) {
						nodes[count][0] = nx;
						nodes[count][1] = layerY;
						nodes[count][2] = nz;
						nodes[count][3] = base[1];
						count++;
					}
				}
				placed++;
			}
			layerY--;
			relative--;
		}

		this.leafNodes = new int[count][4];
		System.arraycopy(nodes, 0, this.leafNodes, 0, count);
	}

	private void genTreeLayer(int x, int y, int z, float radius, byte axis, int blockId) {
		int reach = (int) (radius + 0.618);
		byte first = OTHER_COORD_PAIRS[axis];
		byte second = OTHER_COORD_PAIRS[axis + 3];
		int[] center = {x, y, z};
		int[] pos = new int[3];
		pos[axis] = center[axis];
		for (int i = -reach; i <= reach; i++) {
			pos[first] = center[first] + i;
			int j = -reach;
			while (j <= reach) {
				double distance = Math.sqrt(Math.pow(Math.abs(i) + 0.5, 2.0) + Math.pow(Math.abs(j) + 0.5, 2.0));
				if (distance > radius) {
					j++;
					continue;
				}
				pos[second] = center[second] + j;
				int id = this.world.getBlockId(pos[0], pos[1], pos[2]);
				if (id == 0 || id == this.mainLeavesId) {
					this.world.setBlock(pos[0], pos[1], pos[2], blockId);
				}
				j++;
			}
		}
	}

	private float layerSize(int layer) {
		if (layer < this.heightLimit * 0.3) {
			return -1.618F;
		}
		float half = this.heightLimit / 2.0F;
		float offset = this.heightLimit / 2.0F - layer;
		float size;
		if (offset == 0.0F) {
			size = half;
		} else if (Math.abs(offset) >= half) {
			size = 0.0F;
		} else {
			size = (float) Math.sqrt(Math.pow(Math.abs(half), 2.0) - Math.pow(Math.abs(offset), 2.0));
		}
		return size * 0.5F;
	}

	private float leafSize(int layer) {
		if (layer < 0 || layer >= this.leafDistanceLimit) {
			return -1.0F;
		}
		return layer != 0 && layer != this.leafDistanceLimit - 1 ? 3.0F : 2.0F;
	}

	private void generateLeafNode(int x, int y, int z) {
		for (int iy = y; iy < y + this.leafDistanceLimit; iy++) {
			this.genTreeLayer(x, iy, z, this.leafSize(iy - y), (byte) 1, this.mainLeavesId);
		}
	}

	private void placeBlockLine(int[] from, int[] to, int blockId) {
		int[] delta = new int[3];
		byte major = 0;
		for (byte axis = 0; axis < 3; axis++) {
			delta[axis] = to[axis] - from[axis];
			if (Math.abs(delta[axis]) > Math.abs(delta[major])) {
				major = axis;
			}
		}
		if (delta[major] == 0) {
			return;
		}
		byte first = OTHER_COORD_PAIRS[major];
		byte second = OTHER_COORD_PAIRS[major + 3];
		byte sign = (byte) (delta[major] > 0 ? 1 : -1);
		double slopeFirst = (double) delta[first] / delta[major];
		double slopeSecond = (double) delta[second] / delta[major];
		int[] pos = new int[3];
		for (int i = 0, end = delta[major] + sign; i != end; i += sign) {
			pos[major] = AlphaMath.floorDouble(from[major] + i + 0.5);
			pos[first] = AlphaMath.floorDouble(from[first] + i * slopeFirst + 0.5);
			pos[second] = AlphaMath.floorDouble(from[second] + i * slopeSecond + 0.5);
			this.world.setBlock(pos[0], pos[1], pos[2], blockId);
		}
	}

	private void generateLeaves() {
		for (int[] node : this.leafNodes) {
			this.generateLeafNode(node[0], node[1], node[2]);
		}
	}

	private boolean leafNodeNeedsBase(int relativeY) {
		return relativeY >= this.heightLimit * 0.2;
	}

	private void generateTrunk() {
		int[] bottom = {this.basePos[0], this.basePos[1], this.basePos[2]};
		int[] top = {this.basePos[0], this.basePos[1] + this.height, this.basePos[2]};
		this.placeBlockLine(bottom, top, this.mainWoodId);
	}

	private void generateLeafNodeBases() {
		int[] base = {this.basePos[0], this.basePos[1], this.basePos[2]};
		for (int[] node : this.leafNodes) {
			int[] tip = {node[0], node[1], node[2]};
			base[1] = node[3];
			if (this.leafNodeNeedsBase(base[1] - this.basePos[1])) {
				this.placeBlockLine(base, tip, this.mainWoodId);
			}
		}
	}

	private int checkBlockLine(int[] from, int[] to) {
		int[] delta = new int[3];
		byte major = 0;
		for (byte axis = 0; axis < 3; axis++) {
			delta[axis] = to[axis] - from[axis];
			if (Math.abs(delta[axis]) > Math.abs(delta[major])) {
				major = axis;
			}
		}
		if (delta[major] == 0) {
			return -1;
		}
		byte first = OTHER_COORD_PAIRS[major];
		byte second = OTHER_COORD_PAIRS[major + 3];
		byte sign = (byte) (delta[major] > 0 ? 1 : -1);
		double slopeFirst = (double) delta[first] / delta[major];
		double slopeSecond = (double) delta[second] / delta[major];
		int[] pos = new int[3];
		int i = 0;
		int end = delta[major] + sign;
		for (; i != end; i += sign) {
			pos[major] = from[major] + i;
			pos[first] = (int) (from[first] + i * slopeFirst);
			pos[second] = (int) (from[second] + i * slopeSecond);
			int id = this.world.getBlockId(pos[0], pos[1], pos[2]);
			if (id != 0 && id != this.mainLeavesId) {
				break;
			}
		}
		return i == end ? -1 : Math.abs(i);
	}

	private boolean validTreeLocation() {
		int[] bottom = {this.basePos[0], this.basePos[1], this.basePos[2]};
		int[] top = {this.basePos[0], this.basePos[1] + this.heightLimit - 1, this.basePos[2]};
		int below = this.world.getBlockId(this.basePos[0], this.basePos[1] - 1, this.basePos[2]);
		if (below != CypressPalette.grass() && below != Blocks.DIRT.id()) {
			return false;
		}
		int reach = this.checkBlockLine(bottom, top);
		if (reach == -1) {
			return true;
		}
		if (reach < 6) {
			return false;
		}
		this.heightLimit = reach;
		return true;
	}
}
