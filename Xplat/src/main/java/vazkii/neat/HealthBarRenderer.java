package vazkii.neat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public class HealthBarRenderer {
	private static final DecimalFormat HEALTH_FORMAT = new DecimalFormat("#.##");

	@Nullable
	private static Entity getEntityLookedAt(Entity e) {
		Entity foundEntity = null;
		final double finalDistance = 32;
		HitResult pos = raycast(e, finalDistance);
		Vec3 positionVector = e.getEyePosition();

		double distance = pos.getLocation().distanceTo(positionVector);

		Vec3 lookVector = e.getLookAngle();
		Vec3 reachVector = positionVector.add(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

		List<Entity> entitiesInBoundingBox = e.getLevel().getEntities(e,
				e.getBoundingBox().inflate(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance)
						.expandTowards(1F, 1F, 1F));
		double minDistance = distance;

		for (Entity entity : entitiesInBoundingBox) {
			Entity lookedEntity = null;
			if (entity.isPickable()) {
				AABB collisionBox = entity.getBoundingBoxForCulling();
				Optional<Vec3> interceptPosition = collisionBox.clip(positionVector, reachVector);

				if (collisionBox.contains(positionVector)) {
					if (0.0D < minDistance || minDistance == 0.0D) {
						lookedEntity = entity;
						minDistance = 0.0D;
					}
				} else if (interceptPosition.isPresent()) {
					double distanceToEntity = positionVector.distanceTo(interceptPosition.get());

					if (distanceToEntity < minDistance || minDistance == 0.0D) {
						lookedEntity = entity;
						minDistance = distanceToEntity;
					}
				}
			}

			if (lookedEntity != null && minDistance < distance) {
				foundEntity = lookedEntity;
			}
		}

		return foundEntity;
	}

	private static HitResult raycast(Entity e, double len) {
		return raycast(e.getEyePosition(), e.getLookAngle(), e, len);
	}

	private static HitResult raycast(Vec3 origin, Vec3 ray, Entity e, double len) {
		Vec3 next = origin.add(ray.normalize().scale(len));
		return e.level.clip(new ClipContext(origin, next, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
	}

	@Nonnull
	private static ItemStack getIcon(LivingEntity entity, boolean boss) {
		if (boss) {
			return new ItemStack(Items.NETHER_STAR);
		}
		MobType type = entity.getMobType();
		if (type == MobType.ARTHROPOD) {
			return new ItemStack(Items.SPIDER_EYE);
		} else if (type == MobType.UNDEAD) {
			return new ItemStack(Items.ROTTEN_FLESH);
		} else {
			return ItemStack.EMPTY;
		}
	}

	private static int getColor(LivingEntity entity, boolean colorByType, boolean boss) {
		if (colorByType) {
			int r = 0;
			int g = 255;
			int b = 0;
			if (boss) {
				r = 128;
				g = 0;
				b = 128;
			}
			if (entity instanceof Monster) {
				r = 255;
				g = 0;
				b = 0;
			}
			return 0xff000000 | r << 16 | g << 8 | b;
		} else {
			float health = Mth.clamp(entity.getHealth(), 0.0F, entity.getMaxHealth());
			float hue = Math.max(0.0F, (health / entity.getMaxHealth()) / 3.0F - 0.07F);
			return Mth.hsvToRgb(hue, 1.0F, 1.0F);
		}
	}

	private static boolean isBoss(Entity entity) {
		// TODO inaccurate
		return !entity.canChangeDimensions();
	}

	private static boolean shouldShowPlate(Entity entity, Entity cameraEntity) {
		if ((!NeatConfig.instance.renderInF1() && !Minecraft.renderNames()) || !NeatConfig.draw) {
			return false;
		}

		if (!(entity instanceof LivingEntity living)) {
			return false;
		}

		var id = BuiltInRegistries.ENTITY_TYPE.getKey(living.getType());
		if (NeatConfig.instance.blacklist().contains(id.toString())) {
			return false;
		}

		float distance = living.distanceTo(cameraEntity);
		if (distance > NeatConfig.instance.maxDistance()
				|| !living.hasLineOfSight(cameraEntity)
				|| living.isInvisible()) {
			return false;
		}
		if (!NeatConfig.instance.showOnBosses() && isBoss(living)) {
			return false;
		}
		if (!NeatConfig.instance.showOnPlayers() && living instanceof Player) {
			return false;
		}
		if (!NeatConfig.instance.showFullHealth() && living.getHealth() >= living.getMaxHealth()) {
			return false;
		}
		if (NeatConfig.instance.showOnlyFocused() && getEntityLookedAt(cameraEntity) != entity) {
			return false;
		}

		return true;
	}

	public static void hookRender(Entity entity, PoseStack poseStack, MultiBufferSource buffers,
			Quaternionf cameraOrientation) {
		final Minecraft mc = Minecraft.getInstance();

		if (!(entity instanceof LivingEntity living) || !entity.getPassengers().isEmpty()) {
			// TODO handle mob stacks properly
			return;
		}

		if (!shouldShowPlate(entity, mc.gameRenderer.getMainCamera().getEntity())) {
			return;
		}

		// Constants
		final int light = 0xF000F0;
		final float globalScale = 0.0267F;
		final float textScale = 0.5F;
		final int barHeight = NeatConfig.instance.barHeight();
		final boolean boss = isBoss(entity);
		final String name = entity.hasCustomName()
				? ChatFormatting.ITALIC + entity.getCustomName().getString()
				: entity.getDisplayName().getString();
		final float nameLen = mc.font.width(name) * textScale;
		final float halfSize = Math.max(NeatConfig.instance.plateSize(), nameLen / 2.0F + 10.0F);

		poseStack.pushPose();
		poseStack.translate(0, entity.getBbHeight() + NeatConfig.instance.heightAbove(), 0);
		poseStack.mulPose(cameraOrientation);

		// Plate background, bars, and text operate with globalScale, but icons don't
		poseStack.pushPose();
		poseStack.scale(-globalScale, -globalScale, globalScale);

		// Background
		if (NeatConfig.instance.drawBackground()) {
			float padding = NeatConfig.instance.backgroundPadding();
			int bgHeight = NeatConfig.instance.backgroundHeight();
			VertexConsumer builder = buffers.getBuffer(NeatRenderType.BAR_TEXTURE_TYPE);
			builder.vertex(poseStack.last().pose(), -halfSize - padding, -bgHeight, 0.01F).color(0, 0, 0, 64).uv(0.0F, 0.0F).uv2(light).endVertex();
			builder.vertex(poseStack.last().pose(), -halfSize - padding, barHeight + padding, 0.01F).color(0, 0, 0, 64).uv(0.0F, 0.5F).uv2(light).endVertex();
			builder.vertex(poseStack.last().pose(), halfSize + padding, barHeight + padding, 0.01F).color(0, 0, 0, 64).uv(1.0F, 0.5F).uv2(light).endVertex();
			builder.vertex(poseStack.last().pose(), halfSize + padding, -bgHeight, 0.01F).color(0, 0, 0, 64).uv(1.0F, 0.0F).uv2(light).endVertex();
		}

		// Health Bar
		{
			int argb = getColor(living, NeatConfig.instance.colorByType(), boss);
			int r = (argb >> 16) & 0xFF;
			int g = (argb >> 8) & 0xFF;
			int b = argb & 0xFF;
			float healthHalfSize = halfSize * (living.getHealth() / living.getMaxHealth());

			VertexConsumer builder = buffers.getBuffer(NeatRenderType.BAR_TEXTURE_TYPE);
			builder.vertex(poseStack.last().pose(), -halfSize, 0, 0.001F).color(r, g, b, 127).uv(0.0F, 0.75F).uv2(light).endVertex();
			builder.vertex(poseStack.last().pose(), -halfSize, barHeight, 0.001F).color(r, g, b, 127).uv(0.0F, 1.0F).uv2(light).endVertex();
			builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, barHeight, 0.001F).color(r, g, b, 127).uv(1.0F, 1.0F).uv2(light).endVertex();
			builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, 0, 0.001F).color(r, g, b, 127).uv(1.0F, 0.75F).uv2(light).endVertex();

			// Blank part of the bar
			if (healthHalfSize < halfSize) {
				builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, 0, 0.001F).color(0, 0, 0, 127).uv(0.0F, 0.5F).uv2(light).endVertex();
				builder.vertex(poseStack.last().pose(), -halfSize + 2 * healthHalfSize, barHeight, 0.001F).color(0, 0, 0, 127).uv(0.0F, 0.75F).uv2(light).endVertex();
				builder.vertex(poseStack.last().pose(), halfSize, barHeight, 0.001F).color(0, 0, 0, 127).uv(1.0F, 0.75F).uv2(light).endVertex();
				builder.vertex(poseStack.last().pose(), halfSize, 0, 0.001F).color(0, 0, 0, 127).uv(1.0F, 0.5F).uv2(light).endVertex();
			}
		}

		// Text
		{
			final int white = 0xFFFFFF;
			final int black = 0;

			// Name
			{
				poseStack.pushPose();
				poseStack.translate(-halfSize, -4.5F, 0F);
				poseStack.scale(textScale, textScale, textScale);
				mc.font.drawInBatch(name, 0, 0, white, false, poseStack.last().pose(), buffers, false, black, light);
				poseStack.popPose();
			}

			// Health values (and debug ID)
			{
				final float healthValueTextScale = 0.75F * textScale;
				poseStack.pushPose();
				poseStack.translate(-halfSize, -4.5F, 0F);
				poseStack.scale(healthValueTextScale, healthValueTextScale, healthValueTextScale);

				int h = NeatConfig.instance.hpTextHeight();

				if (NeatConfig.instance.showCurrentHP()) {
					String hpStr = HEALTH_FORMAT.format(living.getHealth());
					mc.font.drawInBatch(hpStr, 2, h, white, false, poseStack.last().pose(), buffers, false, black, light);
				}
				if (NeatConfig.instance.showMaxHP()) {
					String maxHpStr = ChatFormatting.BOLD + HEALTH_FORMAT.format(living.getMaxHealth());
					mc.font.drawInBatch(maxHpStr, (int) (halfSize / healthValueTextScale * 2) - mc.font.width(maxHpStr) - 2, h, white, false, poseStack.last().pose(), buffers, false, black, light);
				}
				if (NeatConfig.instance.showPercentage()) {
					String percStr = (int) (100 * living.getHealth() / living.getMaxHealth()) + "%";
					mc.font.drawInBatch(percStr, (int) (halfSize / healthValueTextScale) - mc.font.width(percStr) / 2.0F, h, white, false, poseStack.last().pose(), buffers, false, black, light);
				}
				if (NeatConfig.instance.enableDebugInfo() && mc.options.renderDebug) {
					var id = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType());
					mc.font.drawInBatch("ID: \"" + id + "\"", 0, h + 16, white, false, poseStack.last().pose(), buffers, false, black, light);
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
				var icon = getIcon(living, boss);
				renderIcon(icon, poseStack, buffers, globalScale, halfSize, iconOffset, zShift);
				iconOffset += 5F;
				zShift += zBump;
			}

			int armor = living.getArmorValue();
			if (armor > 0 && NeatConfig.instance.showArmor()) {
				int ironArmor = armor % 5;
				int diamondArmor = armor / 5;
				if (!NeatConfig.instance.groupArmor()) {
					ironArmor = armor;
					diamondArmor = 0;
				}

				var iron = new ItemStack(Items.IRON_CHESTPLATE);
				for (int i = 0; i < ironArmor; i++) {
					renderIcon(iron, poseStack, buffers, globalScale, halfSize, iconOffset, zShift);
					iconOffset += 1F;
					zShift += zBump;
				}

				var diamond = new ItemStack(Items.DIAMOND_CHESTPLATE);
				for (int i = 0; i < diamondArmor; i++) {
					renderIcon(diamond, poseStack, buffers, globalScale, halfSize, iconOffset, zShift);
					iconOffset += 1F;
					zShift += zBump;
				}
			}

			poseStack.popPose();
		}

		poseStack.popPose();
	}

	private static void renderIcon(ItemStack icon, PoseStack poseStack, MultiBufferSource buffers, float globalScale, float halfSize, float leftShift, float zShift) {
		if (!icon.isEmpty()) {
			final float iconScale = 0.12F;
			poseStack.pushPose();
			// halfSize and co. are units operating under the assumption of globalScale,
			// but in the icon rendering section we don't use globalScale, so we need
			// to manually multiply it in to ensure the units line up.
			float dx = (halfSize - leftShift) * globalScale;
			float dy = 3F * globalScale;
			float dz = zShift * globalScale;
			// Need to negate X due to our rotation below
			poseStack.translate(-dx, dy, dz);
			poseStack.scale(iconScale, iconScale, iconScale);
			poseStack.mulPose(Axis.YP.rotationDegrees(180F));
			Minecraft.getInstance().getItemRenderer()
					.renderStatic(icon, ItemTransforms.TransformType.NONE,
							0xF000F0, OverlayTexture.NO_OVERLAY, poseStack, buffers, 0);
			poseStack.popPose();
		}
	}
}
