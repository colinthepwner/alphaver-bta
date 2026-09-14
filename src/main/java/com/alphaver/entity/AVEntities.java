package com.alphaver.entity;

import com.alphaver.AlphaVer;
import net.minecraft.core.entity.Entity;
import net.minecraft.core.entity.EntityDispatcher;
import net.minecraft.core.entity.factories.EntityFactory;
import net.minecraft.core.util.collection.NamespaceID;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class AVEntities {
	private AVEntities() {}

	public static final List<String> REGISTERED_IDS = new ArrayList<>();

	private static final Set<Class<?>> REGISTERED_CLASSES = new HashSet<>();

	private static <T extends Entity> void register(Class<T> entityClass, String name, EntityFactory<T> factory) {
		EntityDispatcher.getInstance().addMapping(entityClass, NamespaceID.getPermanent(AlphaVer.MOD_ID, name), factory,
			"guidebook.section.mob." + name + ".name");
		REGISTERED_IDS.add(name);
		REGISTERED_CLASSES.add(entityClass);
	}

	public static boolean isAlphaVer(Entity entity) {
		for (Class<?> type = entity.getClass(); type != null && type != Entity.class; type = type.getSuperclass()) {
			if (REGISTERED_CLASSES.contains(type)) {
				return true;
			}
		}
		return false;
	}

	public static void register() {

		register(MobObserver.class, "observer", MobObserver::new);
		register(MobColossus.class, "colossus", MobColossus::new);
		register(MobRecruiter.class, "recruiter", MobRecruiter::new);

		register(MobAmoung.class, "amoung", MobAmoung::new);
		register(MobCatbomb.class, "catbomb", MobCatbomb::new);
		register(MobSquib.class, "squib", MobSquib::new);
		register(MobPongormatron.class, "pongormatron", MobPongormatron::new);
		register(MobSaltSven.class, "salt_sven", MobSaltSven::new);
		register(MobSpearmaster.class, "spearmaster", MobSpearmaster::new);
		register(EntitySpear.class, "spear", EntitySpear::new);
		register(EntityEssenceShot.class, "essence_shot", EntityEssenceShot::new);
		register(EntityGrayGunShot.class, "gray_gun_shot", EntityGrayGunShot::new);
		AlphaVer.LOGGER.info("Registered {} AlphaVer entities: {}", REGISTERED_IDS.size(), REGISTERED_IDS);
	}
}
