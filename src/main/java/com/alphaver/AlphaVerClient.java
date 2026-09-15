package com.alphaver;

import com.alphaver.block.machine.AVScreens;
import com.alphaver.client.MinigameClient;
import com.alphaver.client.gui.CypressLoreToasts;
import com.alphaver.client.gui.ScreenEssenceCloner;
import com.alphaver.client.gui.ScreenEssenceTransformer;
import com.alphaver.client.gui.ScreenFreezer;
import com.alphaver.client.net.AVClientNetwork;
import com.alphaver.client.render.AVBlockColors;
import com.alphaver.client.render.AVBlockModels;
import com.alphaver.client.render.AVEntityRenderers;
import com.alphaver.client.render.AVItemModels;
import com.alphaver.client.render.AVParticles;
import com.alphaver.client.render.ParticleGreenstoneDust;
import com.alphaver.client.render.WorldTypeFXCypress;
import com.alphaver.client.render.WorldTypeFXHub;
import com.alphaver.item.AVItemHooks;
import net.fabricmc.api.ClientModInitializer;
import org.lwjgl.input.Keyboard;
import turniplabs.halplibe.event.defs.ClientEvents;
import turniplabs.halplibe.util.dependency.Key;

public class AlphaVerClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ClientEvents.AFTER_CLIENT_START.listen(Key.of(AlphaVer.MOD_ID), this::afterClientStart);

		ClientEvents.BLOCK_MODEL_RELOAD.listen(Key.of(AlphaVer.MOD_ID), AVBlockModels::register);

		ClientEvents.ITEM_MODEL_RELOAD.listen(Key.of(AlphaVer.MOD_ID), AVItemModels::register);

		ClientEvents.ENTITY_RENDERER_RELOAD.listen(Key.of(AlphaVer.MOD_ID), AVEntityRenderers::register);

		ClientEvents.BLOCK_COLOR_RELOAD.listen(Key.of(AlphaVer.MOD_ID), AVBlockColors::register);

		AVItemHooks.setLoreDisplay(CypressLoreToasts::push);

		AVScreens.setFreezerOpener(ScreenFreezer::open);
		AVScreens.setTransformerOpener(ScreenEssenceTransformer::open);
		AVScreens.setClonerOpener(ScreenEssenceCloner::open);

		AVClientNetwork.install();

		AVItemHooks.setMessageDisplay(CypressLoreToasts::pushText);

		AVItemHooks.setRightShift(() -> Keyboard.isKeyDown(Keyboard.KEY_RSHIFT));

		MinigameClient.install();
	}

	private void afterClientStart() {

		WorldTypeFXCypress.register();
		WorldTypeFXHub.register();

		ParticleGreenstoneDust.register();

		AVParticles.register();
	}
}
