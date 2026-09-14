package com.alphaver.block.machine;

import com.alphaver.block.AVBlocks;
import net.minecraft.core.block.entity.TileEntityFurnace;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.sound.SoundCategory;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class TileEntityFreezer extends TileEntityFurnace {

	private boolean weezered;

	public TileEntityFreezer() {
		this.maxCookTime = AVFreezerRecipes.FREEZE_TICKS;
	}

	@NotNull
	@Override
	public String getNameTranslationKey() {
		return "container.alphaver.freezer.name";
	}

	@NotNull
	public String titleKey() {
		if (!this.weezered && this.worldObj != null && new Random(this.worldObj.getRandomSeed()).nextInt(2) == 0) {
			this.weezered = true;
			this.worldObj.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, this.tilePos.x, this.tilePos.y, this.tilePos.z,
				"alphaver:random.riff", 1.0F, 1.0F);
			return "gui.alphaver.freezer.title_weezer";
		}
		return "gui.alphaver.freezer.title";
	}

	@Override
	public void tick() {
		boolean wasCold = this.currentBurnTime > 0;
		boolean changed = false;
		if (this.currentBurnTime > 0) {
			this.currentBurnTime--;
		}

		if (this.worldObj == null || !this.worldObj.isClientSide) {
			if (this.currentBurnTime == 0 && this.canFreeze()) {
				this.maxBurnTime = this.currentBurnTime = AVFreezerRecipes.coolantTicks(this.furnaceItemStacks[1]);
				if (this.currentBurnTime > 0) {
					changed = true;
					ItemStack coolant = this.furnaceItemStacks[1];
					coolant.stackSize--;
					if (coolant.stackSize <= 0) {
						this.furnaceItemStacks[1] = null;
					}
				}
			}

			if (this.currentBurnTime > 0 && this.canFreeze()) {
				this.currentCookTime++;
				if (this.currentCookTime >= this.maxCookTime) {
					this.currentCookTime = 0;
					this.freezeItem();
					changed = true;
				}
			} else {
				this.currentCookTime = 0;
			}

			if (wasCold != this.currentBurnTime > 0) {
				changed = true;
				this.updateFurnace(false);
			}
		}

		if (changed) {
			this.setChanged();
		}
	}

	@Override
	public void smeltItem() {
		this.freezeItem();
	}

	private boolean canFreeze() {
		ItemStack result = AVFreezerRecipes.result(this.furnaceItemStacks[0]);
		if (result == null) {
			return false;
		}
		ItemStack output = this.furnaceItemStacks[2];
		if (output == null) {
			return true;
		}
		if (!output.isItemEqual(result)) {
			return false;
		}
		return output.stackSize + result.stackSize <= Math.min(this.getMaxStackSize(), output.getMaxStackSize());
	}

	private void freezeItem() {
		if (!this.canFreeze()) {
			return;
		}
		ItemStack result = AVFreezerRecipes.result(this.furnaceItemStacks[0]);
		if (result == null) {
			return;
		}
		boolean wasEmpty = this.furnaceItemStacks[2] == null;
		if (wasEmpty) {
			this.furnaceItemStacks[2] = result;
		} else {
			this.furnaceItemStacks[2].stackSize += result.stackSize;
		}
		AVFreezerRecipes.consumeInput(this.furnaceItemStacks, 0);
		if (this.worldObj != null && wasEmpty) {
			this.worldObj.markBlockNeedsUpdate(this.tilePos.x, this.tilePos.y, this.tilePos.z);
		}
	}

	@Override
	protected void updateFurnace(boolean forceLit) {
		boolean working = forceLit || this.currentBurnTime > 0;
		if (this.worldObj != null) {
			BlockLogicFreezer.setWorking(this.worldObj, this.tilePos, working);
		} else if (this.carriedBlock != null && AVBlocks.FREEZER != null && AVBlocks.FREEZER_LIT != null) {
			this.carriedBlock.blockId = (working ? AVBlocks.FREEZER_LIT : AVBlocks.FREEZER).id();
		}
	}
}
