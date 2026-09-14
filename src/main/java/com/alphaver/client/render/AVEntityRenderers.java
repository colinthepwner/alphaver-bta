package com.alphaver.client.render;

import com.alphaver.entity.EntityEssenceShot;
import com.alphaver.entity.EntitySpear;
import com.alphaver.entity.MobAmoung;
import com.alphaver.entity.MobSpearmaster;
import com.alphaver.entity.MobCatbomb;
import com.alphaver.entity.MobObserver;
import com.alphaver.entity.MobPongormatron;
import com.alphaver.entity.MobRecruiter;
import com.alphaver.entity.MobSaltSven;
import com.alphaver.entity.MobSquib;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.EntityRendererDispatcher;
import net.minecraft.client.render.entity.MobRenderer;
import net.minecraft.client.render.entity.MobRendererBiped;
import net.minecraft.core.entity.Mob;
import net.minecraft.core.util.helper.MathHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.useless.dragonfly.models.entity.BoneTransform;
import org.useless.dragonfly.models.entity.StaticEntityModel;

@Environment(EnvType.CLIENT)
public final class AVEntityRenderers {
	private AVEntityRenderers() {}

	private static final String MODEL_KEY = "main";

	public static void register(EntityRendererDispatcher dispatcher) {
		dispatcher.assignRenderer(MobObserver.class, new Static<>("geometry.alphaver.observer", 0.5F));
		dispatcher.assignRenderer(MobCatbomb.class, new Static<>("geometry.alphaver.catbomb", 0.5F));
		dispatcher.assignRenderer(MobSquib.class, new Static<>("geometry.alphaver.squib", 0.5F));
		dispatcher.assignRenderer(MobPongormatron.class, new Static<>("geometry.alphaver.pongormatron", 0.5F));
		dispatcher.assignRenderer(MobAmoung.class, new Amoung());
		dispatcher.assignRenderer(MobRecruiter.class, new Biped<>());
		dispatcher.assignRenderer(MobSaltSven.class, new Biped<>());
		dispatcher.assignRenderer(MobSpearmaster.class, new Biped<>());
		dispatcher.assignRenderer(EntitySpear.class, new EntityRendererAlphaArrow<EntitySpear>(
			"/assets/alphaver/textures/entity/spear/spear.png", "alphaver:entity/spear/spear", false, spear -> spear.shake));
		dispatcher.assignRenderer(EntityEssenceShot.class, new EntityRendererAlphaArrow<EntityEssenceShot>(
			"/assets/alphaver/textures/entity/essence_shot/essence_shot.png", "alphaver:entity/essence_shot/essence_shot", true,
			shot -> 0));
	}

	public static final class Static<T extends Mob> extends MobRenderer<T> {
		public Static(String geometry, float shadow) {
			super(shadow);
			this.setModel(MODEL_KEY, geometry, 0.0);
		}

		@Nullable
		@Override
		protected StaticEntityModel getAndSetupModelForLayer(@NotNull T entity, float brightness, float partialTick, int layer) {
			StaticEntityModel model = this.getModel(MODEL_KEY);
			if (model != null) {
				model.resetBones();
			}
			return model;
		}
	}

	public static final class Amoung extends MobRenderer<MobAmoung> {
		public Amoung() {
			super(0.5F);
			this.setModel(MODEL_KEY, "geometry.alphaver.amoung", 0.0);
		}

		@Nullable
		@Override
		protected StaticEntityModel getAndSetupModelForLayer(@NotNull MobAmoung entity, float brightness, float partialTick, int layer) {
			StaticEntityModel model = this.getModel(MODEL_KEY);
			if (model == null) {
				return null;
			}
			model.resetBones();
			float swing = this.getLimbSwing(entity, partialTick);
			float amount = this.getLimbYaw(entity, partialTick);
			BoneTransform left = model.getTransform("legLeft");
			BoneTransform right = model.getTransform("legRight");
			if (left != null) {
				left.rotX = MathHelper.cos(swing * 0.6662F) * 1.4F * amount;
			}
			if (right != null) {
				right.rotX = MathHelper.cos(swing * 0.6662F + (float) Math.PI) * 1.4F * amount;
			}
			return model;
		}
	}

	public static final class Biped<T extends Mob> extends MobRendererBiped<T> {
		public Biped() {
			super(0.5F);
			this.setModel(MODEL_KEY, "geometry.zombie", 0.0);
		}

		@Nullable
		@Override
		protected StaticEntityModel getActiveModel(@NotNull T entity) {
			return this.getModel(MODEL_KEY);
		}
	}
}
