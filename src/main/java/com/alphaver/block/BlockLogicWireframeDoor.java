package com.alphaver.block;

import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.AVMinigames;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.entity.TileEntity;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.monster.MobZombie;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.enums.EnumDropCause;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicWireframeDoor extends BlockLogic {

	public BlockLogicWireframeDoor(@NotNull Block<?> block) {
		super(block, Materials.IRON);
	}

	@Override
	public boolean isSolidRender() {
		return false;
	}

	@Override
	public boolean collidesWithEntity(@NotNull Entity entity, @NotNull World world, @NotNull TilePosc tilePos) {
		return !(entity instanceof MobZombie && AVWorlds.isZombies(world));
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side,
	                            double xHit, double yHit) {
		if (!AVWorlds.isZombies(world)) {
			return false;
		}
		if (!world.isClientSide) {
			AVMinigames.interactAt(player, tilePos.x(), tilePos.y(), tilePos.z());
		}
		return true;
	}

	@Override
	public ItemStack[] getBreakResult(@NotNull World world, @NotNull EnumDropCause dropCause, int data, @Nullable TileEntity tileEntity) {
		return null;
	}
}
