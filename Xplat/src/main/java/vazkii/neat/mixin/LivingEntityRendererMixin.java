package vazkii.neat.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

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
		Minecraft mc = Minecraft.getInstance();
		Level level = entity.level();
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

		var cameraEntity = mc.getCameraEntity();
		if (cameraEntity != null) {
			boolean shouldShowPlate = NeatRenderStateHandler.shouldShowPlate(entity, cameraEntity, isBoss, isFriendly, isIdBlacklisted, isFullHealth);
			neatRenderState.neat$setShowPlate(shouldShowPlate);
		}

		@SuppressWarnings("DataFlowIssue") //getCustomName cannot be null if hasCustomName was called before
		FormattedCharSequence nameToRender = entity.hasCustomName()
				? entity.getCustomName().copy().withStyle(ChatFormatting.ITALIC).getVisualOrderText()
				: entity.getDisplayName().getVisualOrderText();
		neatRenderState.neat$setNameToRender(nameToRender);

		if (NeatConfig.instance.showAttributes()) {
			var typeIcon = NeatRenderStateHandler.getIcon(entity, isBoss);
			ItemStackRenderState renderState = new ItemStackRenderState();
			mc.getItemModelResolver().updateForTopItem(renderState, typeIcon.getIcon(), ItemDisplayContext.NONE, level, null, 0);
			neatRenderState.neat$setTypeIconState(renderState);
		}

		int armor = entity.getArmorValue();
		neatRenderState.neat$setArmorValue(armor);

		if (armor > 0 && NeatConfig.instance.showArmor()) {
			int ironArmor = armor % 5;
			int diamondArmor = armor / 5;
			if (!NeatConfig.instance.groupArmor()) {
				ironArmor = armor;
				diamondArmor = 0;
			}

			var iron = new ItemStack(Items.IRON_CHESTPLATE);
			for (int i = 0; i < ironArmor; i++) {
				ItemStackRenderState renderState = new ItemStackRenderState();
				mc.getItemModelResolver().updateForTopItem(renderState, iron, ItemDisplayContext.NONE, level, null, 0);
				neatRenderState.neat$addIronArmorIcons(renderState);

			}

			var diamond = new ItemStack(Items.DIAMOND_CHESTPLATE);
			for (int i = 0; i < diamondArmor; i++) {
				ItemStackRenderState renderState = new ItemStackRenderState();
				mc.getItemModelResolver().updateForTopItem(renderState, diamond, ItemDisplayContext.NONE, level, null, 0);
				neatRenderState.neat$addDiamondArmorIcons(renderState);
			}
		}
	}
}
