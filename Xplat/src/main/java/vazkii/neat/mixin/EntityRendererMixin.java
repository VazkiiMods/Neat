package vazkii.neat.mixin;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.neat.HealthBarRenderer;
import vazkii.neat.NeatConfig;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

	@Shadow @Final protected EntityRenderDispatcher entityRenderDispatcher;

	@Inject(method = "render(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/entity/EntityRenderer;renderNameTag(Lnet/minecraft/client/renderer/entity/state/EntityRenderState;Lnet/minecraft/network/chat/Component;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V"), cancellable = true)
	private void neat_disableNameTag(EntityRenderState renderState, PoseStack $$1, MultiBufferSource $$2, int $$3, CallbackInfo ci) {
		if (NeatConfig.instance.disableNameTag() /*&& (!NeatConfig.instance.disableNameTagIfHealthbar() TODO || neat$allowNameTagDisable(renderState)))*/ ) {
			ci.cancel();
		}
	}

	@Unique
	public boolean neat$allowNameTagDisable(Entity entity) {
		if (!(entity instanceof LivingEntity))
			return false;
		if (entity instanceof Player && !NeatConfig.instance.showOnPlayers())
			return false;
		if (HealthBarRenderer.isBoss(entity) && !NeatConfig.instance.showOnBosses())
			return false;
		if (entity.getType().getCategory().isFriendly() && !NeatConfig.instance.showOnPassive())
			return false;
		if ((!entity.getType().getCategory().isFriendly() && !HealthBarRenderer.isBoss(entity)) && !NeatConfig.instance.showOnHostile())
			return false;

		var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
		return !NeatConfig.instance.blacklist().contains(id.toString()) && NeatConfig.draw;
	}
}
