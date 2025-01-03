package vazkii.neat.mixin;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.neat.HealthBarRenderer;
import vazkii.neat.NeatConfig;
import vazkii.neat.NeatRenderState;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
		at = @At("TAIL")
	)
	public void neat$extractRenderState(LivingEntity entity, LivingEntityRenderState entityRenderState, float partialTick, CallbackInfo ci) {
		((NeatRenderState) entityRenderState).neat$setBoss(HealthBarRenderer.isBoss(entity));
		((NeatRenderState) entityRenderState).neat$setFriendly(entity.getType().getCategory().isFriendly());
		((NeatRenderState) entityRenderState).neat$setIdBlacklisted(NeatConfig.instance.blacklist().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString()));
	}
}
