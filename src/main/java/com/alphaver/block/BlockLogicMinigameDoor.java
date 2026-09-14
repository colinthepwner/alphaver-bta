package com.alphaver.block;

import com.alphaver.world.minigame.AVMinigames;
import com.alphaver.world.minigame.MinigameKind;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;

public class BlockLogicMinigameDoor extends BlockLogicAlphaVerDoor {

	private final MinigameKind kind;

	public BlockLogicMinigameDoor(@NotNull Block<?> block, @NotNull MinigameKind kind) {
		super(block, java.util.Objects.requireNonNull(kind.dimension(), "minigame dimensions are created before the doors"), Half.BOTH);
		this.kind = kind;
	}

	@NotNull
	public MinigameKind kind() {
		return this.kind;
	}

	@Override
	public void onEntityCollision(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Entity entity) {
		if (entity instanceof Player player) {
			AVMinigames.useDoor(world, tilePos, player, this.kind);
		}
	}
}
