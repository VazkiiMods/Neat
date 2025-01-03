package vazkii.neat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.neat.NeatConfig;
import vazkii.neat.NeatRenderState;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), cancellable = true)
	private void neat_disableNameTag(EntityRenderState renderState, PoseStack $$1, MultiBufferSource $$2, int $$3, CallbackInfo ci) {
		if (NeatConfig.instance.disableNameTag() && (!NeatConfig.instance.disableNameTagIfHealthbar() || neat$allowNameTagDisable(renderState))) {
			ci.cancel();
		}
	}

	@Unique
	public boolean neat$allowNameTagDisable(EntityRenderState renderState) {
		if (renderState instanceof LivingEntityRenderState livingRenderState) {
			if (renderState instanceof PlayerRenderState && !NeatConfig.instance.showOnPlayers()) {
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
