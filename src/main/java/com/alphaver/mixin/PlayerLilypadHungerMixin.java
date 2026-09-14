package com.alphaver.mixin;

import com.alphaver.AlphaVer;
import com.alphaver.entity.AVLilypadHungerData;
import com.alphaver.entity.LilypadHunger;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Player.class, remap = false)
public abstract class PlayerLilypadHungerMixin implements AVLilypadHungerData {

	@Unique
	private static boolean alphaver$reportedHungerFailure;

	@Unique
	private int alphaver$hunger;
	@Unique
	private boolean alphaver$hungerSynced;
	@Unique
	private int alphaver$sentHungerStage;
	@Unique
	private World alphaver$sentHungerWorld;

	@Override
	public int alphaver$hunger() {
		return this.alphaver$hunger;
	}

	@Override
	public void alphaver$setHunger(int hunger) {
		this.alphaver$hunger = hunger;
	}

	@Override
	public boolean alphaver$hungerSynced() {
		return this.alphaver$hungerSynced;
	}

	@Override
	public int alphaver$sentHungerStage() {
		return this.alphaver$sentHungerStage;
	}

	@Override
	public @Nullable World alphaver$sentHungerWorld() {
		return this.alphaver$sentHungerWorld;
	}

	@Override
	public void alphaver$markHungerSent(int stage, @Nullable World world) {
		this.alphaver$hungerSynced = true;
		this.alphaver$sentHungerStage = stage;
		this.alphaver$sentHungerWorld = world;
	}

	@Inject(method = "tick", at = @At("TAIL"))
	private void alphaver$lilypadHunger(CallbackInfo ci) {
		try {
			LilypadHunger.tick((Player) (Object) this);
		} catch (RuntimeException e) {
			if (!alphaver$reportedHungerFailure) {
				alphaver$reportedHungerFailure = true;
				AlphaVer.LOGGER.error("Lilypad hunger failed; further failures this session are not logged.", e);
			}
		}
	}
}
