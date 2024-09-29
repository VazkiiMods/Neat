package vazkii.neat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;

import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.neat.HealthBarRenderer;

@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {
	@Shadow
	public abstract Quaternionf cameraOrientation();

	/**
	 * Hooks right after the main entity renderer runs.
	 * Here we have a good GL state set up, the buffers are still available for fabulous mode, etc.
	 * It's a much better point to render our bars than something like RenderLevelLastEvent.
	 */
	@Inject(
		method = "render", at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;render(Lnet/minecraft/world/entity/Entity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
			shift = At.Shift.AFTER
		)
	)
	private void neat_renderHealthBar(Entity entity, double worldX, double worldY, double worldZ, float entityYRot, float partialTicks, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
		//HealthBarRenderer.hookRender(entity, poseStack, buffers, cameraOrientation());
	}
}
