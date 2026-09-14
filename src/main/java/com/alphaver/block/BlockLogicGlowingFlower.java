package com.alphaver.block;

import com.alphaver.item.AVItems;
import net.minecraft.core.block.Block;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.Items;
import net.minecraft.core.sound.SoundCategory;
import net.minecraft.core.util.helper.Side;
import net.minecraft.core.world.World;
import net.minecraft.core.world.pos.TilePosc;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.WeakHashMap;

public class BlockLogicGlowingFlower extends BlockLogicCypressPlant {

	private static final int HEAL_INTERVAL = 20;
	private static final String INFUSE_SOUND = "alphaver:ext.infuse";

	private static final Map<Player, Long> NEXT_HEAL = new WeakHashMap<>();

	public BlockLogicGlowingFlower(@NotNull Block<?> block) {
		super(block, Soil.FLOATS);
	}

	@Override
	public void onEntityCollision(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Entity entity) {
		if (world.isClientSide || !(entity instanceof Player player) || player.getHealth() <= 0) {
			return;
		}
		long now = world.getWorldTime();
		synchronized (NEXT_HEAL) {
			Long next = NEXT_HEAL.get(player);
			if (next != null && now < next && next - now <= HEAL_INTERVAL) {
				return;
			}
			NEXT_HEAL.put(player, now + HEAL_INTERVAL);
		}
		player.heal(1);
	}

	@Override
	public boolean onInteracted(@NotNull World world, @NotNull TilePosc tilePos, @NotNull Player player, @Nullable Side side,
	                            double xHit, double yHit) {
		ItemStack held = player.inventory.getCurrentItem();
		if (held == null) {
			return false;
		}
		Block<?> infused;
		float pitch;
		if (AVBlocks.CELESTIAL_FLAME != null && held.itemID == AVBlocks.CELESTIAL_FLAME.id()) {
			infused = AVBlocks.LILY_FLAME;
			pitch = 1.0F;
		} else if (held.itemID == Items.INGOT_GOLD.id) {
			infused = AVBlocks.LILY_GOLD;
			pitch = 0.7F;
		} else if (AVItems.OBSIDIAN_INGOT != null && held.itemID == AVItems.OBSIDIAN_INGOT.id) {
			infused = AVBlocks.LILY_OBSIDIAN;
			pitch = 0.3F;
		} else {
			return false;
		}
		if (infused == null) {
			return false;
		}
		if (!world.isClientSide) {
			world.setBlockTypeNotify(tilePos, infused);
			world.playSoundEffect(null, SoundCategory.WORLD_SOUNDS, tilePos.x() + 0.5, tilePos.y() + 0.5, tilePos.z() + 0.5,
				INFUSE_SOUND, 1.0F, pitch);
			held.consumeItem(player);
		}
		return true;
	}
}
