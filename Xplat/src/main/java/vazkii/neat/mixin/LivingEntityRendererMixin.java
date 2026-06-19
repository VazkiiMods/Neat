package vazkii.neat.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import vazkii.neat.HealthBarRenderer;
import vazkii.neat.NeatConfig;
import vazkii.neat.NeatRenderState;
import vazkii.neat.NeatRenderStateHandler;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

	@Inject(
		method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V",
		at = @At("TAIL")
	)
	public void neat$extractRenderState(LivingEntity entity, LivingEntityRenderState entityRenderState, float partialTicks, CallbackInfo ci) {
		var neatRenderState = (NeatRenderState) entityRenderState;
		boolean isBoss = HealthBarRenderer.isBoss(entity);
		boolean isFriendly = entity.getType().getCategory().isFriendly();
		boolean isIdBlacklisted = NeatConfig.instance.blacklist().contains(BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).toString());
		float health = entity.getHealth();
		float maxHealth = entity.getMaxHealth();
		neatRenderState.neat$setBoss(isBoss);
		neatRenderState.neat$setFriendly(isFriendly);
		neatRenderState.neat$setIdBlacklisted(isIdBlacklisted);
		neatRenderState.neat$setHealth(health);
		neatRenderState.neat$setMaxHealth(maxHealth);
		boolean isFullHealth = health >= maxHealth;

		var cameraEntity = Minecraft.getInstance().getCameraEntity(); //todo using this could re-introduce issue with real camera - test that
		if (cameraEntity != null) {
			boolean shouldShowPlate = NeatRenderStateHandler.shouldShowPlate(entity, cameraEntity, isBoss, isFriendly, isIdBlacklisted, isFullHealth);
			neatRenderState.neat$setShowPlate(shouldShowPlate);
		}

		@SuppressWarnings("DataFlowIssue") //getCustomName cannot be null if hasCustomName was called before
		FormattedCharSequence nameToRender = entity.hasCustomName()
				? entity.getCustomName().copy().withStyle(ChatFormatting.ITALIC).getVisualOrderText()
				: entity.getDisplayName().getVisualOrderText();
		neatRenderState.neat$setNameToRender(nameToRender);

		var typeIcon = NeatRenderStateHandler.getIcon(entity, isBoss);
		neatRenderState.neat$setTypeIcon(typeIcon);

		neatRenderState.neat$setArmorValue(entity.getArmorValue());
	}
}
