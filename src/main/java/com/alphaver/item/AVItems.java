package com.alphaver.item;

import com.alphaver.AlphaVer;
import com.alphaver.block.AVBlocks;
import net.minecraft.core.enums.HumanArmorShape;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemArmor;
import net.minecraft.core.item.ItemDiscMusic;
import net.minecraft.core.item.ItemDoor;
import net.minecraft.core.item.ItemFood;
import net.minecraft.core.item.ItemPlaceable;
import net.minecraft.core.item.material.ArmorMaterial;
import net.minecraft.core.item.material.ToolMaterial;
import net.minecraft.core.item.tool.ItemToolAxe;
import net.minecraft.core.item.tool.ItemToolHoe;
import net.minecraft.core.item.tool.ItemToolPickaxe;
import net.minecraft.core.item.tool.ItemToolShovel;
import net.minecraft.core.item.tool.ItemToolSword;
import net.minecraft.core.util.collection.NamespaceID;
import net.minecraft.core.util.helper.DamageType;
import turniplabs.halplibe.helper.ItemBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AVItems {
	private AVItems() {}

	public static final int BASE_ID = 26000;

	private static final List<Item> REGISTERED = new ArrayList<>();

	public static final List<Item> ALL = Collections.unmodifiableList(REGISTERED);

	public static void hideFromCreativeMenu() {
		for (Item item : ALL) {
			item.withTags(net.minecraft.core.item.tag.ItemTags.NOT_IN_CREATIVE_MENU);
		}
		AlphaVer.LOGGER.info("Hid {} AlphaVer items from the creative inventory.", ALL.size());
	}

	public static Item ESSENCE;
	public static Item LACE_AGATE;
	public static Item CLINOHUMITE;
	public static Item MALACHITE;
	public static Item PYRITE;
	public static Item BISMUTH_INGOT;
	public static Item GRANULAR_SALT;
	public static Item FRIGID_BITS;
	public static Item OBSERVER_FUR;
	public static Item OBSIDIAN_INGOT;
	public static Item DYE_BLACK;
	public static Item DYE_GREEN;
	public static Item DYE_BLUE;
	public static Item DYE_PINK;
	public static Item MYCON_STRAND;
	public static Item GREENSTONE;

	public static Item PEAR;
	public static Item OBSIDIAN_PEAR;
	public static Item TEA_LEAF;
	public static Item TEA_BUCKET;
	public static Item FRYSHROOM;
	public static Item LIQUIFIED_FLAME;
	public static Item CANDY_ICE;

	public static Item HOURS_LONG_PAST_1;
	public static Item HOURS_LONG_PAST_2;
	public static Item HOURS_LONG_PAST_3;
	public static Item HOURS_LONG_PAST_4;
	public static Item THE_ONE_TRUE_BOOK;

	public static Item RAIN_CONCH;
	public static Item FLAMEBERGE;
	public static Item RECORD_SANDCASTLES;
	public static Item RECORD_ROCK_BEETLE;
	public static Item RECORD_DOWNBEAT_UPLINK;
	public static Item RECORD_K2;
	public static Item RECORD_DESAMBRIER;
	public static Item RECORD_JUHRY;
	public static Item RECORD_GYLDAN_SVERD;
	public static Item RECORD_LEMURIA;
	public static Item RECORD_HIDDEN_DEN;

	public static Item OBSIDIAN_SWORD;
	public static Item OBSIDIAN_SHOVEL;
	public static Item OBSIDIAN_PICKAXE;
	public static Item OBSIDIAN_AXE;
	public static Item OBSIDIAN_HOE;
	public static Item OBSIDIAN_HELM;
	public static Item OBSIDIAN_CHESTPLATE;
	public static Item OBSIDIAN_LEGGINGS;
	public static Item OBSIDIAN_BOOTS;
	public static Item MYCON_SWORD;
	public static Item MYCON_SHOVEL;
	public static Item MYCON_PICKAXE;
	public static Item MYCON_AXE;
	public static Item MYCON_HOE;
	public static Item SPEAR;
	public static Item HEARTHEN_MIRROR;
	public static Item ESSENCE_RIFLE;
	public static Item DOOR_FLAMEWOOD;
	public static Item DOOR_HIGHWOOD;
	public static Item DOOR_MYCON;
	public static Item DOOR_TEA;
	public static Item DOOR_ICE;
	public static Item GRAY_GUN;
	public static Item ERASER;
	public static Item STYLISH_VISOR;
	public static Item STYLISH_CHESTPLATE;
	public static Item STYLISH_SHORTS;
	public static Item STYLISH_SHOES;
	public static Item SUNGLASSES;

	private static int id(int cypressId) {
		return BASE_ID + cypressId;
	}

	private static String texture(String name) {
		return AlphaVer.MOD_ID + ":item/" + name;
	}

	public static void register() {
		ESSENCE = add(new Item("essence", texture("essence"), id(109)), 64);
		LACE_AGATE = add(new Item("lace_agate", texture("lace_agate"), id(134)), 64);
		CLINOHUMITE = add(new Item("clinohumite", texture("clinohumite"), id(135)), 64);
		MALACHITE = add(new Item("malachite", texture("malachite"), id(136)), 64);
		PYRITE = add(new Item("pyrite", texture("pyrite"), id(137)), 64);
		BISMUTH_INGOT = add(new Item("bismuth_ingot", texture("bismuth_ingot"), id(122)), 64);
		GRANULAR_SALT = add(new Item("granular_salt", texture("granular_salt"), id(129)), 64);
		FRIGID_BITS = add(new Item("frigid_bits", texture("frigid_bits"), id(141)), 64);
		OBSERVER_FUR = add(new Item("observer_fur", texture("observer_fur"), id(148)), 64);
		OBSIDIAN_INGOT = add(new Item("obsidian_ingot", texture("obsidian_ingot"), id(99)), 64);

		DYE_BLACK = add(new Item("dye_black", texture("dye_black"), id(100)), 64);
		DYE_GREEN = add(new Item("dye_green", texture("dye_green"), id(101)), 64);
		DYE_BLUE = add(new Item("dye_blue", texture("dye_blue"), id(102)), 64);
		DYE_PINK = add(new Item("dye_pink", texture("dye_pink"), id(103)), 64);
		MYCON_STRAND = add(new Item("mycon_strand", texture("mycon_strand"), id(123)), 64);

		GREENSTONE = add(new ItemPlaceable("greenstone", texture("greenstone"), id(75), AVBlocks.GREENSTONE_WIRE), 64);

		PEAR = add(new ItemFood("pear", texture("pear"), id(114), 4, 8, false, 1), 1);
		OBSIDIAN_PEAR = add(new ItemFood("obsidian_pear", texture("obsidian_pear"), id(115), 42, 2, false, 1), 1);
		TEA_LEAF = add(new ItemFood("tea_leaf", texture("tea_leaf"), id(131), 1, 8, false, 1), 1);
		TEA_BUCKET = add(new ItemFood("tea_bucket", texture("tea_bucket"), id(132), 21, 4, false, 1), 1);
		FRYSHROOM = add(new ItemFood("fryshroom", texture("fryshroom"), id(104), 6, 6, false, 1), 1);

		LIQUIFIED_FLAME = add(new ItemLiquifiedFlame("liquified_flame", texture("liquified_flame"), id(105), 2, 8, 1), 1);
		CANDY_ICE = add(new ItemFood("candy_ice", texture("candy_ice"), id(142), 10, 4, false, 1), 1);

		HOURS_LONG_PAST_1 = add(new ItemCypressLore("hours_long_past_1", texture("hours_long_past_1"), id(138),
			"Hours Long Past I", 801), 1);
		HOURS_LONG_PAST_2 = add(new ItemCypressLore("hours_long_past_2", texture("hours_long_past_2"), id(144),
			"Hours Long Past II", 802), 1);
		HOURS_LONG_PAST_3 = add(new ItemCypressLore("hours_long_past_3", texture("hours_long_past_3"), id(145),
			"Hours Long Past III", 803), 1);

		HOURS_LONG_PAST_4 = add(new ItemCypressLore("hours_long_past_4", texture("hours_long_past_4"), id(146),
			"Hours Long Past IV", 804), 1);
		THE_ONE_TRUE_BOOK = add(new ItemCypressLore("the_one_true_book", texture("the_one_true_book"), id(147),
			"The One True Book", 805), 1);

		RAIN_CONCH = add(new ItemRainConch("rain_conch", texture("rain_conch"), id(140)), 1);
		FLAMEBERGE = add(new ItemFlameberge("flameberge", texture("flameberge"), id(106)), 1);

		RECORD_SANDCASTLES = add(new ItemDiscMusic("record_sandcastles", texture("record_sandcastles"), id(2004),
			AlphaVer.MOD_ID + ":record.sandcastles", null), 1);
		RECORD_ROCK_BEETLE = add(new ItemRecordRockBeetle("record_rock_beetle", texture("record_rock_beetle"), id(2006),
			AlphaVer.MOD_ID + ":record.rock_beetle"), 1);

		RECORD_LEMURIA = add(new ItemDiscMusic("record_lemuria", texture("record_lemuria"), id(2000),
			AlphaVer.MOD_ID + ":record.lemuria", null), 1);
		RECORD_HIDDEN_DEN = add(new ItemDiscMusic("record_hidden_den", texture("record_hidden_den"), id(2001),
			AlphaVer.MOD_ID + ":record.hidden_den", null), 1);

		RECORD_DOWNBEAT_UPLINK = add(new ItemDiscMusic("record_downbeat_uplink", texture("record_downbeat_uplink"), id(2003),
			AlphaVer.MOD_ID + ":record.downbeat_uplink", null), 1);
		RECORD_K2 = add(new ItemDiscMusic("record_k2", texture("record_k2"), id(2005),
			AlphaVer.MOD_ID + ":record.k2", null), 1);
		RECORD_DESAMBRIER = add(new ItemDiscMusic("record_desambrier", texture("record_desambrier"), id(2007),
			AlphaVer.MOD_ID + ":record.desambrier", null), 1);
		RECORD_JUHRY = add(new ItemDiscMusic("record_juhry", texture("record_juhry"), id(2008),
			AlphaVer.MOD_ID + ":record.juhry", null), 1);
		RECORD_GYLDAN_SVERD = add(new ItemDiscMusic("record_gyldan_sverd", texture("record_gyldan_sverd"), id(2009),
			AlphaVer.MOD_ID + ":record.gyldan_sverd", null), 1);

		registerToolsAndArmour();
		SPEAR = add(new ItemSpear("spear", texture("spear"), id(113)), 8);
		HEARTHEN_MIRROR = add(new ItemHearthenMirror("hearthen_mirror", texture("hearthen_mirror"), id(139)), 1);
		ESSENCE_RIFLE = add(new ItemEssenceRifle("essence_rifle", texture("essence_rifle"), id(111)), 1);
		GRAY_GUN = add(new ItemGrayGun("gray_gun", texture("gray_gun"), id(130)), 1);
		ERASER = add(new ItemEraser("eraser", texture("eraser"), id(110)), 1);

		DOOR_FLAMEWOOD = add(new ItemDoor("door_flamewood", texture("door_flamewood"), id(108),
			AVBlocks.DOOR_FLAMEWOOD_BOTTOM, AVBlocks.DOOR_FLAMEWOOD_TOP), 1);
		DOOR_HIGHWOOD = add(new ItemDoor("door_highwood", texture("door_highwood"), id(116),
			AVBlocks.DOOR_HIGHWOOD_BOTTOM, AVBlocks.DOOR_HIGHWOOD_TOP), 1);
		DOOR_MYCON = add(new ItemDoor("door_mycon", texture("door_mycon"), id(117),
			AVBlocks.DOOR_MYCON_BOTTOM, AVBlocks.DOOR_MYCON_TOP), 1);
		DOOR_TEA = add(new ItemDoor("door_tea", texture("door_tea"), id(133),
			AVBlocks.DOOR_TEA_BOTTOM, AVBlocks.DOOR_TEA_TOP), 1);
		DOOR_ICE = add(new ItemDoor("door_ice", texture("door_ice"), id(143),
			AVBlocks.DOOR_ICE_BOTTOM, AVBlocks.DOOR_ICE_TOP), 1);

		AlphaVer.LOGGER.info("Registered {} AlphaVer items.", REGISTERED.size());
	}

	private static void registerToolsAndArmour() {
		ToolMaterial obsidian = new ToolMaterial()
			.setDurability(1536)
			.setEfficiency(21.0F, 45.0F)
			.setMiningLevel(3)
			.setDamage(5)
			.setBlockHitDelay(4);
		OBSIDIAN_SWORD = add(new ItemToolSword("obsidian_sword", texture("obsidian_sword"), id(94), obsidian), 1);
		OBSIDIAN_SHOVEL = add(new ItemToolShovel("obsidian_shovel", texture("obsidian_shovel"), id(95), obsidian), 1);
		OBSIDIAN_PICKAXE = add(new ItemObsidianPickaxe("obsidian_pickaxe", texture("obsidian_pickaxe"), id(96), obsidian), 1);
		OBSIDIAN_AXE = add(new ItemToolAxe("obsidian_axe", texture("obsidian_axe"), id(97), obsidian), 1);
		OBSIDIAN_HOE = add(new ItemToolHoe("obsidian_hoe", texture("obsidian_hoe"), id(98), obsidian), 1);

		ArmorMaterial obsidianArmour = ArmorMaterial.register(
			new ArmorMaterial(NamespaceID.getPermanent(AlphaVer.MOD_ID, "obsidian"), 1600)
				.withProtectionPercentage(DamageType.COMBAT, 66.0F)
				.withProtectionPercentage(DamageType.BLAST, 124.0F)
				.withProtectionPercentage(DamageType.FIRE, 124.0F)
				.withProtectionPercentage(DamageType.FALL, 66.0F));
		OBSIDIAN_HELM = add(new ItemArmor<>("obsidian_helm", texture("obsidian_helm"), id(90), obsidianArmour, HumanArmorShape.HEAD), 1);
		OBSIDIAN_CHESTPLATE = add(new ItemArmor<>("obsidian_chestplate", texture("obsidian_chestplate"), id(91), obsidianArmour,
			HumanArmorShape.CHEST), 1);
		OBSIDIAN_LEGGINGS = add(new ItemArmor<>("obsidian_leggings", texture("obsidian_leggings"), id(92), obsidianArmour,
			HumanArmorShape.LEGS), 1);
		OBSIDIAN_BOOTS = add(new ItemArmor<>("obsidian_boots", texture("obsidian_boots"), id(93), obsidianArmour, HumanArmorShape.BOOTS), 1);

		ArmorMaterial stylish = ArmorMaterial.register(chainmailLike("stylish"));
		STYLISH_VISOR = add(new ItemArmor<>("stylish_visor", texture("stylish_visor"), id(118), stylish, HumanArmorShape.HEAD), 1);
		STYLISH_CHESTPLATE = add(new ItemArmor<>("stylish_chestplate", texture("stylish_chestplate"), id(119), stylish,
			HumanArmorShape.CHEST), 1);
		STYLISH_SHORTS = add(new ItemArmor<>("stylish_shorts", texture("stylish_shorts"), id(120), stylish, HumanArmorShape.LEGS), 1);
		STYLISH_SHOES = add(new ItemArmor<>("stylish_shoes", texture("stylish_shoes"), id(121), stylish, HumanArmorShape.BOOTS), 1);
		ArmorMaterial fashion = ArmorMaterial.register(chainmailLike("fashion"));
		SUNGLASSES = add(new ItemArmor<>("sunglasses", texture("sunglasses"), id(107), fashion, HumanArmorShape.HEAD), 1);

		ToolMaterial mycon = new ToolMaterial()
			.setDurability(64)
			.setEfficiency(2.0F, 4.0F)
			.setMiningLevel(0);
		MYCON_SWORD = add(new ItemToolSword("mycon_sword", texture("mycon_sword"), id(124), mycon), 1);
		MYCON_SHOVEL = add(new ItemToolShovel("mycon_shovel", texture("mycon_shovel"), id(125), mycon), 1);
		MYCON_PICKAXE = add(new ItemToolPickaxe("mycon_pickaxe", texture("mycon_pickaxe"), id(126), mycon), 1);
		MYCON_AXE = add(new ItemToolAxe("mycon_axe", texture("mycon_axe"), id(127), mycon), 1);
		MYCON_HOE = add(new ItemToolHoe("mycon_hoe", texture("mycon_hoe"), id(128), mycon), 1);
	}

	private static ArmorMaterial chainmailLike(String name) {
		return new ArmorMaterial(NamespaceID.getPermanent(AlphaVer.MOD_ID, name), 240)
			.withProtectionPercentage(DamageType.COMBAT, 120.0F)
			.withProtectionPercentage(DamageType.BLAST, 35.0F)
			.withProtectionPercentage(DamageType.FIRE, 35.0F)
			.withProtectionPercentage(DamageType.FALL, 35.0F);
	}

	private static <T extends Item> T add(T item, int stackSize) {
		new ItemBuilder(AlphaVer.MOD_ID).setStackSize(stackSize).build(item);
		REGISTERED.add(item);
		return item;
	}
}
