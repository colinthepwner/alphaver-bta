package com.alphaver.mixin;

import com.alphaver.world.AVWorlds;
import net.minecraft.core.block.BlockLogicFire;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.enums.EnumFireSpread;
import net.minecraft.core.world.World;
import net.minecraft.core.world.WorldSource;
import net.minecraft.core.world.pos.TilePos;
import net.minecraft.core.world.pos.TilePosc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Random;

@Mixin(value = BlockLogicFire.class, remap = false)
public abstract class BlockLogicFireCypressMixin {

	@Inject(method = "getFlammability(Lnet/minecraft/core/world/World;Lnet/minecraft/core/world/pos/TilePosc;I)I",
		at = @At("HEAD"), cancellable = true)
	private static void alphaver$onlyTntEncourages(World world, TilePosc tilePos, int currentFlameChance,
	                                               CallbackInfoReturnable<Integer> cir) {
		if (AVWorlds.isCypress(world) && world.getBlockType(tilePos) != Blocks.TNT) {
			cir.setReturnValue(currentFlameChance);
		}
	}

	@Inject(method = "canBurn(Lnet/minecraft/core/world/WorldSource;Lnet/minecraft/core/world/pos/TilePosc;)Z",
		at = @At("HEAD"), cancellable = true)
	private static void alphaver$onlyTntBurns(WorldSource source, TilePosc tilePos, CallbackInfoReturnable<Boolean> cir) {
		if (source instanceof World world && AVWorlds.isCypress(world) && world.getBlockType(tilePos) != Blocks.TNT) {
			cir.setReturnValue(false);
		}
	}

	@Inject(method = "checkBurn", at = @At("HEAD"), cancellable = true)
	private void alphaver$onlyTntCatches(World world, TilePos tilePos, int chance, Random random, int meta,
	                                     EnumFireSpread spreadRule, CallbackInfo ci) {
		if (AVWorlds.isCypress(world) && world.getBlockType(tilePos) != Blocks.TNT) {
			ci.cancel();
		}
	}
}
