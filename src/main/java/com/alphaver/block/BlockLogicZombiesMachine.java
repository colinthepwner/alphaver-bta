package com.alphaver.block;

import com.alphaver.world.AVWorlds;
import com.alphaver.world.minigame.AVMinigames;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.material.Materials;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLogicZombiesMachine extends BlockLogic {

	public enum Kind {
		PERK_HEALTH_BOOST(true),
		PERK_ARMOR(true),
		PERK_DASH(true),
		PERK_QUICK_REVIVE(true),

		MACHINE_TOP(false),
		UPGRADER(true),
		GIVER(true);

		public final boolean interactive;

		Kind(boolean interactive) {
			this.interactive = interactive;
		}
	}

	private final Kind kind;

	public BlockLogicZombiesMachine(@NotNull Block<?> block, @NotNull Kind kind) {
		super(block, Materials.IRON);
		this.kind = kind;
	}

	@NotNull
	public Kind kind() {
		return this.kind;
	}

	@Nullable
	public static Kind kindOf(@Nullable Block<?> block) {
		return block != null && block.getLogic() instanceof BlockLogicZombiesMachine machine ? machine.kind : null;
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side,
	                            double xHit, double yHit) {
		if (!this.kind.interactive || !AVWorlds.isZombies(world)) {
			return false;
		}
		if (!world.isClientSide) {
			AVMinigames.interactAt(player, tilePos.x(), tilePos.y(), tilePos.z());
		}
		return true;
	}
}
