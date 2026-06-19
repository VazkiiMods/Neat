package vazkii.neat;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.OrderedSubmitNodeCollector;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.text.DecimalFormat;
import java.util.*;

public class HealthBarRenderer {

	private static int getColor(NeatRenderState entity, boolean colorByType, boolean boss) {
		if (colorByType) {
			int r = 0;
			int g = 255;
			int b = 0;
			if (boss) {
				r = 128;
				g = 0;
				b = 128;
			} else if (!entity.neat$isFriendly()) {
				r = 255;
				g = 0;
			}
			return 0xff000000 | r << 16 | g << 8 | b;
		} else {
			float health = Mth.clamp(entity.neat$getHealth(), 0.0F, entity.neat$getMaxHealth());
			float hue = Math.max(0.0F, (health / entity.neat$getMaxHealth()) / 3.0F - 0.07F);
			return Mth.hsvToRgb(hue, 1.0F, 1.0F);
		}
	}

	private static final TagKey<EntityType<?>> BOSS_TAG =
			TagKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath("c", "bosses"));

	public static boolean isBoss(Entity entity) {
		return entity.is(BOSS_TAG);
	}

	public static <S extends EntityRenderState> void hookRender(S renderState, PoseStack poseStack, SubmitNodeCollector nodeCollector, CameraRenderState camera) {
		final Minecraft mc = Minecraft.getInstance();
		if (!(renderState instanceof LivingEntityRenderState livingState) || !(livingState instanceof NeatRenderState neatRenderState)) {
			return;
		}
		//This was previously mc.gameRenderer.getMainCamera().getEntity() but that caused an incompatibility with RealCamera
		if (!neatRenderState.neat$shouldShowPlate()) {
			return;
		}

		// Constants
		final int light = 0xF000F0;
		final float globalScale = 0.0267F;
		final float textScale = 0.5F;
		final int barHeight = NeatConfig.instance.barHeight();
		final boolean boss = neatRenderState.neat$isBoss();
		final FormattedCharSequence name = neatRenderState.neat$nameToRender();
		final float nameLen = mc.font.width(name) * textScale;
		final float halfSize = Math.max(NeatConfig.instance.plateSize(), nameLen / 2.0F + 10.0F);

		Level level = mc.level; //todo check which level is best

		//Vec3 attachmentPoint = entity.getAttachments().get(EntityAttachment.NAME_TAG, 0, entity.getViewYRot(partialTicks));

		poseStack.pushPose();
		//poseStack.translate(x, y, z); since moving to dispatcher
		poseStack.translate(0, livingState.boundingBoxHeight + NeatConfig.instance.heightAbove(), 0);
		poseStack.mulPose(camera.orientation);
		poseStack.mulPose(Axis.YP.rotationDegrees(180));

		// Plate background, bars, and text operate with globalScale, but icons don't
		poseStack.pushPose();
		poseStack.scale(-globalScale, -globalScale, globalScale);

		// Background
		if (NeatConfig.instance.drawBackground()) {

			nodeCollector.submitCustomGeometry(poseStack, NeatRenderType.BACKGROUND_RENDER_TYPE, ((pose, consumer) -> {
				float padding = NeatConfig.instance.backgroundPadding();
				int bgHeight = NeatConfig.instance.backgroundHeight();
				if (!NeatConfig.instance.showEntityName()) {
					bgHeight -= (int) 4F;
				}

				consumer.addVertex(pose.pose(), -halfSize - padding, -bgHeight, 0.01F).setColor(0, 0, 0, 35).setUv(0.0F, 0.0F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				consumer.addVertex(pose.pose(), -halfSize - padding, barHeight + padding, 0.01F).setColor(0, 0, 0, 35).setUv(0.0F, 0.5F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				consumer.addVertex(pose.pose(), halfSize + padding, barHeight + padding, 0.01F).setColor(0, 0, 0, 35).setUv(1.0F, 0.5F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				consumer.addVertex(pose.pose(), halfSize + padding, -bgHeight, 0.01F).setColor(0, 0, 0, 35).setUv(1.0F, 0.0F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
			}));

		}

		// Health Bar
		{
			int argb = getColor(neatRenderState, NeatConfig.instance.colorByType(), boss);
			int r = (argb >> 16) & 0xFF;
			int g = (argb >> 8) & 0xFF;
			int b = argb & 0xFF;
			// There are scenarios in vanilla where the current health
			// can temporarily exceed the max health.
			float maxHealth = Math.max(neatRenderState.neat$getHealth(), neatRenderState.neat$getMaxHealth());
			float healthHalfSize = halfSize * (neatRenderState.neat$getHealth() / maxHealth);

			nodeCollector.submitCustomGeometry(poseStack, NeatRenderType.BAR_RENDER_TYPE, ((pose, consumer) -> {
				consumer.addVertex(pose.pose(), -halfSize, 0, 0.001F).setColor(r, g, b, 127).setUv(0.0F, 0.75F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				consumer.addVertex(pose.pose(), -halfSize, barHeight, 0.001F).setColor(r, g, b, 127).setUv(0.0F, 1.0F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				consumer.addVertex(pose.pose(), -halfSize + 2 * healthHalfSize, barHeight, 0.001F).setColor(r, g, b, 127).setUv(1.0F, 1.0F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				consumer.addVertex(pose.pose(), -halfSize + 2 * healthHalfSize, 0, 0.001F).setColor(r, g, b, 127).setUv(1.0F, 0.75F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);

				// Blank part of the bar
				if (healthHalfSize < halfSize) {
					consumer.addVertex(pose.pose(), -halfSize + 2 * healthHalfSize, 0, 0.001F).setColor(0, 0, 0, 127).setUv(0.0F, 0.5F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
					consumer.addVertex(pose.pose(), -halfSize + 2 * healthHalfSize, barHeight, 0.001F).setColor(0, 0, 0, 127).setUv(0.0F, 0.75F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
					consumer.addVertex(pose.pose(), halfSize, barHeight, 0.001F).setColor(0, 0, 0, 127).setUv(1.0F, 0.75F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
					consumer.addVertex(pose.pose(), halfSize, 0, 0.001F).setColor(0, 0, 0, 127).setUv(1.0F, 0.5F).setLight(light).setOverlay(OverlayTexture.NO_OVERLAY).setNormal(pose, 0.0F, 0.0F, 0.0F);
				}
			}));
		}

		// Text
		{
			String colorWithAlpha = "FF" + NeatConfig.instance.textColor();
			final int textColor = HexFormat.fromHexDigits(colorWithAlpha);

			final int black = 0;

			OrderedSubmitNodeCollector textCollector = nodeCollector.order(1); //sort after custom geometry

			// Name
			{

				if (NeatConfig.instance.showEntityName()) {

					poseStack.pushPose();
					poseStack.translate(-halfSize, -4.5F, 0F);
					poseStack.scale(textScale, textScale, textScale);
					textCollector.submitText(poseStack, 0, 0, name, false, Font.DisplayMode.NORMAL, light, textColor, black, black);
					poseStack.popPose();
				}
			}

			// Health values (and debug ID)
			{
				final float healthValueTextScale = 0.75F * textScale;
				poseStack.pushPose();
				poseStack.translate(-halfSize, -4.5F, 0F);
				poseStack.scale(healthValueTextScale, healthValueTextScale, healthValueTextScale);

				int h = NeatConfig.instance.hpTextHeight();
				DecimalFormat health_format = new DecimalFormat(NeatConfig.instance.decimalFormat());

				if (NeatConfig.instance.showCurrentHP()) {
					FormattedCharSequence hpStr = FormattedCharSequence.forward(health_format.format(neatRenderState.neat$getHealth()), Style.EMPTY);
					textCollector.submitText(poseStack, 2, h, hpStr, false, Font.DisplayMode.NORMAL, light, textColor, black, black);
				}
				if (NeatConfig.instance.showMaxHP()) {
					FormattedCharSequence maxHpStr = FormattedCharSequence.forward(health_format.format(neatRenderState.neat$getMaxHealth()), Style.EMPTY.withBold(true));
					textCollector.submitText(poseStack, (int) (halfSize / healthValueTextScale * 2) - mc.font.width(maxHpStr) - 2, h, maxHpStr, false, Font.DisplayMode.NORMAL, light, textColor, black, black);

				}
				if (NeatConfig.instance.showPercentage()) {
					FormattedCharSequence percStr = FormattedCharSequence.forward((int) (100 * neatRenderState.neat$getHealth() / neatRenderState.neat$getMaxHealth()) + "%", Style.EMPTY);
					textCollector.submitText(poseStack, (int) (halfSize / healthValueTextScale) - mc.font.width(percStr) / 2.0F, h, percStr, false, Font.DisplayMode.NORMAL, light, textColor, black, black);

				}
				if (NeatConfig.instance.enableDebugInfo() && mc.getDebugOverlay().showDebugScreen()) {
					Identifier id = BuiltInRegistries.ENTITY_TYPE.getKey(livingState.entityType);
					FormattedCharSequence idSequence = FormattedCharSequence.forward("ID: \"" + id + "\"", Style.EMPTY);
					textCollector.submitText(poseStack, 0, h + 16, idSequence, false, Font.DisplayMode.NORMAL, light, textColor, black, black);
				}
				poseStack.popPose();
			}
		}

		poseStack.popPose(); // Remove globalScale

		// Icons
		{
			final float zBump = -0.1F;
			poseStack.pushPose();

			float iconOffset = 2.85F;
			float zShift = 0F;
			if (NeatConfig.instance.showAttributes()) {
				var icon = neatRenderState.neat$getTypeIcon().getIcon();
				renderIcon(level, icon, poseStack,
						globalScale, halfSize, iconOffset, zShift);
				iconOffset += 5F;
				zShift += zBump;
			}

			int armor = neatRenderState.neat$getArmorValue();
			if (armor > 0 && NeatConfig.instance.showArmor()) {
				int ironArmor = armor % 5;
				int diamondArmor = armor / 5;
				if (!NeatConfig.instance.groupArmor()) {
					ironArmor = armor;
					diamondArmor = 0;
				}

				var iron = new ItemStack(Items.IRON_CHESTPLATE);
				for (int i = 0; i < ironArmor; i++) {
					renderIcon(level, iron, poseStack,
							globalScale, halfSize, iconOffset, zShift);
					iconOffset += 1F;
					zShift += zBump;
				}

				var diamond = new ItemStack(Items.DIAMOND_CHESTPLATE);
				for (int i = 0; i < diamondArmor; i++) {
					renderIcon(level, diamond, poseStack,
							globalScale, halfSize, iconOffset, zShift);
					iconOffset += 1F;
					zShift += zBump;
				}
			}

			poseStack.popPose();
		}

		poseStack.popPose();
	}

	private static void renderIcon(Level level, ItemStack icon, PoseStack poseStack, float globalScale, float halfSize, float leftShift, float zShift) { //todo use the node collector here too
		if (!icon.isEmpty()) {
			final float iconScale = 0.12F;
			poseStack.pushPose();
			// halfSize and co. are units operating under the assumption of globalScale,
			// but in the icon rendering section we don't use globalScale, so we need
			// to manually multiply it in to ensure the units line up.
			double dx = (halfSize - leftShift) * globalScale + NeatConfig.instance.iconOffsetX();
			double dy = 3F * globalScale;
			double dz = zShift * globalScale;
			// Need to negate X due to our rotation below
			poseStack.translate(-dx, dy + NeatConfig.instance.iconOffsetY(), dz);
			poseStack.scale(iconScale, iconScale, iconScale);
			poseStack.mulPose(Axis.YP.rotationDegrees(180F));
			ItemStackRenderState renderState = new ItemStackRenderState();
			Minecraft.getInstance().getItemModelResolver()
					.updateForTopItem(renderState, icon, ItemDisplayContext.NONE, level, null, 0);
			poseStack.popPose();
		}
	}
}
