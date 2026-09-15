package com.alphaver.client.gui;

import com.alphaver.client.AVDoorFinds;
import com.alphaver.entity.AVGamemodes;
import com.alphaver.mixin.client.BooleanGameRuleComponentAccessor;
import com.alphaver.world.AVGameRules;
import com.alphaver.world.travel.AVInvites;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.ScreenCreateWorld;
import net.minecraft.client.gui.paged.PageComponent;
import net.minecraft.client.gui.worldsettings.ScreenWorldSettings;
import net.minecraft.core.player.gamemode.Gamemode;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public final class CypressModesMenu {
	private CypressModesMenu() {}

	@Nullable
	private static PageComponent hiddenRule;
	private static int hiddenRuleIndex;

	public static void refresh() {
		if (AVGamemodes.CYPRESS_SURVIVAL == null || AVGamemodes.CYPRESS_FRAIL == null) {
			return;
		}
		boolean shown = !AVInvites.enforced() || AVDoorFinds.anyWorld(true);
		List<Gamemode> modes = ScreenCreateWorld.GAMEMODES;
		modes.remove(AVGamemodes.CYPRESS_SURVIVAL);
		modes.remove(AVGamemodes.CYPRESS_FRAIL);
		if (shown) {
			modes.add(AVGamemodes.CYPRESS_SURVIVAL);
			modes.add(AVGamemodes.CYPRESS_FRAIL);
		}
		showStartRule(shown);
	}

	private static void showStartRule(boolean shown) {
		List<PageComponent> components = ScreenWorldSettings.PAGE_GAME_RULES.getComponents();
		if (shown) {
			if (hiddenRule != null) {
				components.add(Math.min(hiddenRuleIndex, components.size()), hiddenRule);
				hiddenRule = null;
			}
			return;
		}
		if (hiddenRule != null || AVGameRules.START_IN_CYPRESS == null) {
			return;
		}
		for (int i = 0; i < components.size(); i++) {
			if (components.get(i) instanceof BooleanGameRuleComponentAccessor rule
				&& rule.alphaver$getGameRule() == AVGameRules.START_IN_CYPRESS) {
				hiddenRule = components.remove(i);
				hiddenRuleIndex = i;
				return;
			}
		}
	}
}
