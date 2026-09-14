package com.alphaver.client.render;

import com.alphaver.AlphaVer;
import com.alphaver.item.AVItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.item.model.ItemModelDispatcher;
import net.minecraft.client.render.item.model.ItemModelStandard;
import net.minecraft.core.item.Item;
import com.alphaver.item.ItemEssenceRifle;
import com.alphaver.item.ItemSpear;
import net.minecraft.core.item.tool.ItemTool;
import net.minecraft.core.item.tool.ItemToolSword;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public final class AVItemModels {
	private AVItemModels() {}

	private static final String DEFAULT_STAND_IN = "minecraft:item/paper";

	private static final Map<String, String> STAND_INS = new HashMap<>();

	static {
		STAND_INS.put("essence", "minecraft:item/dust_glowstone");
		STAND_INS.put("lace_agate", "minecraft:item/quartz");
		STAND_INS.put("clinohumite", "minecraft:item/rubyglass_crystal");
		STAND_INS.put("malachite", "minecraft:item/olivine");
		STAND_INS.put("pyrite", "minecraft:item/ingot_gold");
		STAND_INS.put("bismuth_ingot", "minecraft:item/ingot_iron");
		STAND_INS.put("granular_salt", "minecraft:item/dust_sugar");
		STAND_INS.put("frigid_bits", "minecraft:item/ammo_snowball");
		STAND_INS.put("observer_fur", "minecraft:item/leather");
		STAND_INS.put("pear", "minecraft:item/food_apple");
		STAND_INS.put("obsidian_pear", "minecraft:item/food_apple_gold");
		STAND_INS.put("tea_leaf", "minecraft:item/wheat");
		STAND_INS.put("tea_bucket", "minecraft:item/bucket_iron");
		STAND_INS.put("hours_long_past_1", "minecraft:item/book");
		STAND_INS.put("hours_long_past_2", "minecraft:item/book");
		STAND_INS.put("hours_long_past_3", "minecraft:item/book");
		STAND_INS.put("hours_long_past_4", "minecraft:item/book");
		STAND_INS.put("the_one_true_book", "minecraft:item/book");
		STAND_INS.put("rain_conch", "minecraft:item/jar");
		STAND_INS.put("flameberge", "minecraft:item/tool_sword_gold");
		STAND_INS.put("record_sandcastles", "minecraft:item/record_13");
		STAND_INS.put("record_rock_beetle", "minecraft:item/record_cat");
		STAND_INS.put("record_downbeat_uplink", "minecraft:item/record_blocks");
		STAND_INS.put("record_k2", "minecraft:item/record_chirp");
		STAND_INS.put("record_desambrier", "minecraft:item/record_far");
		STAND_INS.put("record_juhry", "minecraft:item/record_mall");
		STAND_INS.put("record_gyldan_sverd", "minecraft:item/record_mellohi");
		STAND_INS.put("obsidian_ingot", "minecraft:item/ingot_steel");
		STAND_INS.put("dye_black", "minecraft:item/dye");
		STAND_INS.put("dye_green", "minecraft:item/dye");
		STAND_INS.put("dye_blue", "minecraft:item/dye");
		STAND_INS.put("dye_pink", "minecraft:item/dye");
		STAND_INS.put("mycon_strand", "minecraft:item/string");
		STAND_INS.put("greenstone", "minecraft:item/dust_redstone");
		STAND_INS.put("fryshroom", "minecraft:item/food_stew_mushroom");
		STAND_INS.put("liquified_flame", "minecraft:item/ammo_fireball");
		STAND_INS.put("candy_ice", "minecraft:item/ammo_snowball");
		STAND_INS.put("obsidian_sword", "minecraft:item/tool_sword_diamond");
		STAND_INS.put("obsidian_shovel", "minecraft:item/tool_shovel_diamond");
		STAND_INS.put("obsidian_pickaxe", "minecraft:item/tool_pickaxe_diamond");
		STAND_INS.put("obsidian_axe", "minecraft:item/tool_axe_diamond");
		STAND_INS.put("obsidian_hoe", "minecraft:item/tool_hoe_diamond");
		STAND_INS.put("obsidian_helm", "minecraft:item/armor_helmet_diamond");
		STAND_INS.put("obsidian_chestplate", "minecraft:item/armor_chestplate_diamond");
		STAND_INS.put("obsidian_leggings", "minecraft:item/armor_leggings_diamond");
		STAND_INS.put("obsidian_boots", "minecraft:item/armor_boots_diamond");
		STAND_INS.put("mycon_sword", "minecraft:item/tool_sword_wood");
		STAND_INS.put("mycon_shovel", "minecraft:item/tool_shovel_wood");
		STAND_INS.put("mycon_pickaxe", "minecraft:item/tool_pickaxe_wood");
		STAND_INS.put("mycon_axe", "minecraft:item/tool_axe_wood");
		STAND_INS.put("mycon_hoe", "minecraft:item/tool_hoe_wood");
		STAND_INS.put("spear", "minecraft:item/ammo_arrow");
		STAND_INS.put("hearthen_mirror", "minecraft:item/tool_compass");
		STAND_INS.put("essence_rifle", "minecraft:item/tool_bow");
		STAND_INS.put("door_flamewood", "minecraft:item/door_oak");
		STAND_INS.put("door_highwood", "minecraft:item/door_oak");
		STAND_INS.put("door_mycon", "minecraft:item/door_oak");
		STAND_INS.put("door_tea", "minecraft:item/door_oak");
		STAND_INS.put("door_ice", "minecraft:item/door_glass");
	}

	public static void register(ItemModelDispatcher dispatcher) {
		int own = 0;
		for (Item item : AVItems.ALL) {
			boolean hasOwn = AVTextures.has(item.namespaceID.toString());
			ItemModelStandard model = new ItemModelStandard(item, hasOwn);
			if (hasOwn) {
				own++;
			} else {
				String path = item.namespaceID.value();
				String name = path.substring(path.lastIndexOf('/') + 1);
				model.setIcon(STAND_INS.getOrDefault(name, DEFAULT_STAND_IN));
			}
			if (item instanceof ItemToolSword || item instanceof ItemTool || item instanceof ItemSpear || item instanceof ItemEssenceRifle) {

				model.setDisplayPos("firstperson_righthand", ItemModelDispatcher.HANDHELD_FIRST_PERSON_RIGHT_HAND);
				model.setDisplayPos("firstperson_lefthand", ItemModelDispatcher.HANDHELD_FIRST_PERSON_LEFT_HAND);
				model.setDisplayPos("thirdperson_righthand", ItemModelDispatcher.HANDHELD_THIRD_PERSON_RIGHT_HAND);
				model.setDisplayPos("thirdperson_lefthand", ItemModelDispatcher.HANDHELD_THIRD_PERSON_LEFT_HAND);
			}
			dispatcher.addDispatch(model);
		}
		AlphaVer.LOGGER.info("Registered item models for {} AlphaVer items; {} drew Cypress's own icon, the rest a BTA "
			+ "stand-in.", AVItems.ALL.size(), own);
	}
}
