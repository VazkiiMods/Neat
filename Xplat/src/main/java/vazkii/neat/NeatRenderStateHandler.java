package vazkii.neat;

import net.minecraft.client.Minecraft;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Team;

import java.util.List;
import java.util.Optional;

public class NeatRenderStateHandler {

	public static boolean shouldShowPlate(LivingEntity living, Entity cameraEntity, boolean isBoss, boolean isFriendly, boolean isIdBlacklisted, boolean isFullHealth) {
		if (living == cameraEntity) {
			return false;
		}
        boolean hudHidden = Minecraft.getInstance().gameRenderer.gameRenderState().guiRenderState.isHudHidden;
		if ((!NeatConfig.instance.renderInF1() && hudHidden) || !NeatConfig.draw) {
			return false;
		}

		if (isIdBlacklisted) {
			return false;
		}

		float distance = living.distanceTo(cameraEntity);
		if (distance > NeatConfig.instance.maxDistance()
				|| (distance > NeatConfig.instance.maxDistanceWithoutLineOfSight()
						&& !living.hasLineOfSight(cameraEntity))) {
			return false;
		}
		if (!NeatConfig.instance.showOnBosses() && isBoss) {
			return false;
		}
		if (!NeatConfig.instance.showOnPlayers() && living instanceof Player) {
			return false;
		}
		if (!NeatConfig.instance.showFullHealth() && isFullHealth) {
			return false;
		}
		if (NeatConfig.instance.showOnlyFocused() && getEntityLookedAt(cameraEntity) != living) {
			return false;
		}
		if (!NeatConfig.instance.showOnPassive() && isFriendly) {
			return false;
		}
		if (!NeatConfig.instance.showOnHostile() && (!isFriendly && !isBoss)) {
			return false;
		}

		if (living.hasPassenger(cameraEntity)) {
			return false;
		}

		boolean visible = true;
		if (cameraEntity instanceof Player cameraPlayer
				&& living.isInvisibleTo(cameraPlayer)) {
			boolean wearingThings = false;
			for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.ARMOR) {
				ItemStack armor = living.getItemBySlot(equipmentSlot);
				if (!armor.isEmpty()) {
					wearingThings = true;
				}
			}

			for (EquipmentSlot equipmentSlot : EquipmentSlotGroup.HAND) {
				ItemStack hand = living.getItemBySlot(equipmentSlot);
				if (!hand.isEmpty()) {
					wearingThings = true;
				}
			}

			if (!wearingThings) {
				visible = false;
			}
		}
		Team livingTeam = living.getTeam();
		Team cameraTeam = cameraEntity.getTeam();
		if (livingTeam != null) {
			return switch (livingTeam.getNameTagVisibility()) {
				case ALWAYS -> visible;
				case NEVER -> false;
				case HIDE_FOR_OTHER_TEAMS -> cameraTeam == null ? visible : livingTeam.isAlliedTo(cameraTeam) && (livingTeam.canSeeFriendlyInvisibles() || visible);
				case HIDE_FOR_OWN_TEAM -> cameraTeam == null ? visible : !livingTeam.isAlliedTo(cameraTeam) && visible;
			};
		}

		return visible;
	}

	private static Entity getEntityLookedAt(Entity e) {
		Entity foundEntity = null;
		final double finalDistance = 32;
		HitResult pos = raycast(e, finalDistance);
		Vec3 positionVector = e.getEyePosition();

		double distance = pos.getLocation().distanceTo(positionVector);

		Vec3 lookVector = e.getLookAngle();
		Vec3 reachVector = positionVector.add(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

		List<Entity> entitiesInBoundingBox = e.level().getEntities(e,
				e.getBoundingBox().inflate(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance)
						.expandTowards(1F, 1F, 1F));
		double minDistance = distance;

		for (Entity entity : entitiesInBoundingBox) {
			Entity lookedEntity = null;
			if (entity.isPickable()) {
				AABB collisionBox = entity.getBoundingBox();
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
		Vec3 origin = e.getEyePosition();
		Vec3 ray = e.getLookAngle();
		Vec3 next = origin.add(ray.normalize().scale(len));
		return e.level().clip(new ClipContext(origin, next, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
	}

	public static NeatTypeIcon getIcon(LivingEntity entity, boolean boss) {
		if (boss) {
			return NeatTypeIcon.BOSS;
		}
		if (entity.is(EntityTypeTags.ARTHROPOD)) {
			return NeatTypeIcon.ARTHROPOD;
		} else if (entity.is(EntityTypeTags.UNDEAD)) {
			return NeatTypeIcon.UNDEAD;
		} else if (entity.is(EntityTypeTags.ILLAGER)) {
			return NeatTypeIcon.ILLAGER;
		} else {
			return NeatTypeIcon.NONE;
		}
	}
}
