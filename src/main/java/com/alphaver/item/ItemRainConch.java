package com.alphaver.item;

import com.alphaver.net.AVSounds;
import net.minecraft.core.entity.player.Player;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.world.World;
import net.minecraft.core.world.weather.Weather;
import net.minecraft.core.world.weather.WeatherManager;
import net.minecraft.core.world.weather.Weathers;
import org.jetbrains.annotations.NotNull;

public class ItemRainConch extends Item {

	public ItemRainConch(@NotNull String name, @NotNull String namespaceId, int id) {
		super(name, namespaceId, id);
	}

	@Override
	public ItemStack onUse(@NotNull ItemStack selfStack, @NotNull World world, @NotNull Player player) {
		if (world.isClientSide || AVItems.ESSENCE == null) {
			return selfStack;
		}
		if (!player.inventory.consumeInventoryItem(AVItems.ESSENCE.id)) {
			return selfStack;
		}
		AVSounds.playFor(player, AVItemHooks.NOTIFICATION_SOUND, 1.0F, 1.0F / (world.rand.nextFloat() * 0.4F + 0.8F));

		WeatherManager weather = world.getWeatherManager();
		Weather current = weather.getCurrentWeather();
		boolean raining = current == Weathers.OVERWORLD_RAIN || current == Weathers.OVERWORLD_STORM;
		weather.overrideWeather(raining ? Weathers.OVERWORLD_CLEAR : Weathers.OVERWORLD_RAIN);
		return selfStack;
	}
}
