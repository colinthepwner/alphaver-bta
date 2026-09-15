package com.alphaver.mixin.client;

import com.alphaver.client.render.CypressShader;
import net.minecraft.client.render.shader.ShadersRenderer;
import net.minecraft.client.render.shader.framebuffer.FrameBuffer;
import net.minecraft.client.render.shader.framebuffer.FrameBufferAttachment;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ShadersRenderer.class, remap = false)
public abstract class ShadersRendererCypressShaderMixin {

	@Shadow
	@Nullable
	protected FrameBuffer worldFrameBuffer;

	@Unique
	@Nullable
	private FrameBufferAttachment alphaver$cypressColor;

	@Inject(method = "endRenderWorld", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/shader/framebuffer/FrameBuffer;blit()V", shift = At.Shift.AFTER))
	private void alphaver$cypressPass(float partialTicks, CallbackInfo ci) {
		this.alphaver$cypressColor = CypressShader.run(this.worldFrameBuffer);
	}

	@Redirect(method = "endRenderWorld", at = @At(value = "INVOKE",
		target = "Lnet/minecraft/client/render/shader/framebuffer/FrameBuffer;getAttachment(Lnet/minecraft/client/render/shader/framebuffer/FrameBuffer$AttachmentType;)Lnet/minecraft/client/render/shader/framebuffer/FrameBufferAttachment;"))
	private FrameBufferAttachment alphaver$cypressColorForPost(FrameBuffer buffer, FrameBuffer.AttachmentType type) {
		FrameBufferAttachment cypress = this.alphaver$cypressColor;
		if (type == FrameBuffer.AttachmentType.COLOR && cypress != null) {
			return cypress;
		}
		return buffer.getAttachment(type);
	}

	@Inject(method = "reload", at = @At("HEAD"))
	private void alphaver$reloadCypressPass(CallbackInfo ci) {
		CypressShader.reload();
	}

	@Inject(method = "delete", at = @At("HEAD"))
	private void alphaver$deleteCypressPass(CallbackInfo ci) {
		this.alphaver$cypressColor = null;
		CypressShader.release();
	}
}
