package vazkii.neat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.neat.NeatConfig;
import vazkii.neat.NeatRenderState;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(
		method = "submit(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;submitNameDisplay(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V"), cancellable = true
	)
	private void neat_disableNameTag(EntityRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, CameraRenderState camera, CallbackInfo ci) {
		NeatConfig.NameTagRenderBehavior renderBehavior = NeatConfig.instance.nameTagRenderBehavior();
		if (renderBehavior != NeatConfig.NameTagRenderBehavior.ALWAYS &&
				(renderBehavior == NeatConfig.NameTagRenderBehavior.WHEN_NO_HEALTHBAR && neat$entityHasHealthbar(state)) ||
				renderBehavior == NeatConfig.NameTagRenderBehavior.NEVER) {
			ci.cancel();
		}
	}

	@Unique
	public boolean neat$entityHasHealthbar(EntityRenderState renderState) {
		if (renderState instanceof LivingEntityRenderState livingRenderState) {
			if (renderState instanceof AvatarRenderState && !NeatConfig.instance.showOnPlayers()) {
				return false;
			}
			if (((NeatRenderState) livingRenderState).neat$isBoss() && !NeatConfig.instance.showOnBosses()) {
				return false;
			}
			if (((NeatRenderState) livingRenderState).neat$isFriendly() && !NeatConfig.instance.showOnPassive()) {
				return false;
			}
			if ((!((NeatRenderState) livingRenderState).neat$isFriendly() && !((NeatRenderState) livingRenderState).neat$isBoss()) && !NeatConfig.instance.showOnHostile()) {
				return false;
			}
			return !((NeatRenderState) livingRenderState).neat$isIdBlacklisted() && NeatConfig.draw;

		}
		return false;
	}
}
