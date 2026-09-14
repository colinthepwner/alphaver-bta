package com.alphaver.mixin.client;

import com.alphaver.client.render.AVLookController;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.container.ScreenContainerAbstract;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value = Gui.class, remap = false)
public abstract class GuiContainerLabelMixin {

	private static final int BTA_LABEL_GREY = 4210752;
	private static final int CYPRESS_LABEL_WHITE = 0xFFFFFF;

	@ModifyVariable(method = "drawStringNoShadow", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private int alphaver$containerLabel(int argb) {
		return this.alphaver$swap(argb);
	}

	@ModifyVariable(method = "drawStringCenteredNoShadow", at = @At("HEAD"), argsOnly = true, ordinal = 2)
	private int alphaver$containerLabelCentered(int argb) {
		return this.alphaver$swap(argb);
	}

	private int alphaver$swap(int argb) {
		if (argb == BTA_LABEL_GREY && (Object) this instanceof ScreenContainerAbstract && AVLookController.isApplied()) {
			return CYPRESS_LABEL_WHITE;
		}
		return argb;
	}
}
