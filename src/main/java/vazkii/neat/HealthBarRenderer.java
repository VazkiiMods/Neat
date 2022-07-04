package vazkii.neat;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import net.minecraft.world.phys.Vec3;
import com.mojang.math.Vector3f;
import net.minecraft.ChatFormatting;
import net.minecraftforge.client.event.RenderLevelLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;

import java.awt.Color;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class HealthBarRenderer {

	public static Entity getEntityLookedAt(Entity e) {
		Entity foundEntity = null;
		final double finalDistance = 32;
		double distance = finalDistance;
		HitResult pos = raycast(e, finalDistance);
		Vec3 positionVector = e.position();

		if (e instanceof Player)
			positionVector = positionVector.add(0, e.getEyeHeight(e.getPose()), 0);

		if (pos != null)
			distance = pos.getLocation().distanceTo(positionVector);

		Vec3 lookVector = e.getLookAngle();
		Vec3 reachVector = positionVector.add(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

		Entity lookedEntity = null;
		List<Entity> entitiesInBoundingBox = e.getCommandSenderWorld().getEntities(e, e.getBoundingBox().inflate(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance).expandTowards(1F, 1F, 1F));
		double minDistance = distance;

		for (Entity entity : entitiesInBoundingBox) {
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

			if (lookedEntity != null && (minDistance < distance || pos == null))
				foundEntity = lookedEntity;
		}

		return foundEntity;
	}

	public static HitResult raycast(Entity e, double len) {
		Vec3 vec = new Vec3(e.getX(), e.getY(), e.getZ());
		if (e instanceof Player)
			vec = vec.add(new Vec3(0, e.getEyeHeight(e.getPose()), 0));

		Vec3 look = e.getLookAngle();
		if (look == null)
			return null;

		return raycast(vec, look, e, len);
	}

	public static HitResult raycast(Vec3 origin, Vec3 ray, Entity e, double len) {
		Vec3 next = origin.add(ray.normalize().scale(len));
		return e.level.clip(new ClipContext(origin, next, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, e));
	}

	@Nonnull
	public static ItemStack getIcon(LivingEntity entity, boolean boss) {
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

	public static int getColor(LivingEntity entity, boolean colorByType, boolean boss) {
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
			return Color.HSBtoRGB(hue, 1.0F, 1.0F);
		}
	}

	@SubscribeEvent
	public void onRenderLevelLast(RenderLevelLastEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if ((!NeatConfig.renderInF1 && !Minecraft.renderNames()) || !NeatConfig.draw)
			return;

		Camera camera = mc.gameRenderer.getMainCamera();
		PoseStack poseStack = event.getPoseStack();
		float partialTicks = event.getPartialTick();
		Entity cameraEntity = camera.getEntity() != null ? camera.getEntity() : mc.player;

		if (NeatConfig.showOnlyFocused) {
			Entity focused = getEntityLookedAt(mc.player);
			if (focused != null && focused instanceof LivingEntity && focused.isAlive()) {
				renderHealthBar((LivingEntity) focused, mc, poseStack, partialTicks, camera, cameraEntity);
			}
		} else {
			Vec3 cameraPos = camera.getPosition();
			final Frustum frustum = new Frustum(poseStack.last().pose(), event.getProjectionMatrix());
			frustum.prepare(cameraPos.x(), cameraPos.y(), cameraPos.z());

			ClientLevel client = mc.level;
			if (client != null) {
				for (Entity entity : client.entitiesForRendering()) {
					if (entity != null && entity instanceof LivingEntity && entity != cameraEntity && entity.isAlive() && !entity.getIndirectPassengers().iterator().hasNext() && entity.shouldRender(cameraPos.x(), cameraPos.y(), cameraPos.z()) && (entity.noCulling || frustum.isVisible(entity.getBoundingBox()))) {
						renderHealthBar((LivingEntity) entity, mc, poseStack, partialTicks, camera, cameraEntity);
					}
				}
			}
		}
	}

	public void renderHealthBar(LivingEntity passedEntity, Minecraft mc, PoseStack poseStack, float partialTicks, Camera camera, Entity viewPoint) {
		Stack<LivingEntity> ridingStack = new Stack<>();

		LivingEntity entity = passedEntity;
		ridingStack.push(entity);

		while (entity.getVehicle() != null && entity.getVehicle() instanceof LivingEntity) {
			entity = (LivingEntity) entity.getVehicle();
			ridingStack.push(entity);
		}

		ShaderInstance prevShader = RenderSystem.getShader();
		RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
		poseStack.pushPose();
		while (!ridingStack.isEmpty()) {
			entity = ridingStack.pop();
			boolean boss = !entity.canChangeDimensions();

			String entityID = entity.getType().getRegistryName().toString();
			if (NeatConfig.blacklist.contains(entityID))
				continue;

			processing:
			{
				float distance = passedEntity.distanceTo(viewPoint);
				if (distance > NeatConfig.maxDistance || !passedEntity.hasLineOfSight(viewPoint) || entity.isInvisible())
					break processing;
				if (!NeatConfig.showOnBosses && boss)
					break processing;
				if (!NeatConfig.showOnPlayers && entity instanceof Player)
					break processing;
				if (entity.getMaxHealth() <= 0)
					break processing;
				if (!NeatConfig.showFullHealth && entity.getHealth() == entity.getMaxHealth())
					break processing;

				double x = passedEntity.xo + (passedEntity.getX() - passedEntity.xo) * partialTicks;
				double y = passedEntity.yo + (passedEntity.getY() - passedEntity.yo) * partialTicks;
				double z = passedEntity.zo + (passedEntity.getZ() - passedEntity.zo) * partialTicks;

				EntityRenderDispatcher renderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
				Vec3 renderPos = renderDispatcher.camera.getPosition();

				poseStack.pushPose();
				poseStack.translate((float) (x - renderPos.x()), (float) (y - renderPos.y() + passedEntity.getBbHeight() + NeatConfig.heightAbove), (float) (z - renderPos.z()));
				MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
				ItemStack icon = getIcon(entity, boss);
				final int light = 0xF000F0;
				renderEntity(mc, poseStack, buffer, camera, entity, light, icon, boss);
				poseStack.popPose();

				poseStack.translate(0.0D, -(NeatConfig.backgroundHeight + NeatConfig.barHeight + NeatConfig.backgroundPadding), 0.0D);
			}
		}
		poseStack.popPose();
		//RenderSystem.setShader(() -> prevShader);

	}

	private void renderEntity(Minecraft mc, PoseStack poseStack, MultiBufferSource.BufferSource buffer, Camera camera, LivingEntity entity, int light, ItemStack icon, boolean boss) {
		Quaternion rotation = camera.rotation().copy();
		rotation.mul(-1.0F);
		poseStack.mulPose(rotation);
		float scale = 0.026666672F;
		poseStack.scale(-scale, -scale, scale);
		float health = Mth.clamp(entity.getHealth(), 0.0F, entity.getMaxHealth());
		float percent = (health / entity.getMaxHealth()) * 100.0F;
		float size = NeatConfig.plateSize;
		float textScale = 0.5F;

		String name = (entity.hasCustomName() ? entity.getCustomName() : entity.getDisplayName()).getString();
		if (entity.hasCustomName())
			name = ChatFormatting.ITALIC + name;

		float namel = mc.font.width(name) * textScale;
		if (namel + 20 > size * 2) {
			size = namel / 2.0F + 10.0F;
		}
		float healthSize = size * (health / entity.getMaxHealth());
		PoseStack.Pose pose = poseStack.last();
		Matrix4f modelViewMatrix = pose.pose();
		Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
		normal.transform(pose.normal());
		VertexConsumer builder = buffer.getBuffer(NeatRenderType.getHealthBarType(NeatRenderType.HEALTH_BAR_TEXTURE));
		float padding = NeatConfig.backgroundPadding;
		int bgHeight = NeatConfig.backgroundHeight;
		int barHeight = NeatConfig.barHeight;

		// Background
		if (NeatConfig.drawBackground) {
			builder.vertex(modelViewMatrix, -size - padding, -bgHeight, 0.01F).color(0, 0, 0, 64).uv(0.0F, 0.0F).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, -size - padding, barHeight + padding, 0.01F).color(0, 0, 0, 64).uv(0.0F, 0.5F).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, size + padding, barHeight + padding, 0.01F).color(0, 0, 0, 64).uv(1.0F, 0.5F).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, size + padding, -bgHeight, 0.01F).color(0, 0, 0, 64).uv(1.0F, 0.0F).uv2(light).endVertex();
		}

		// Health Bar
		int argb = getColor(entity, NeatConfig.colorByType, boss);
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;

		builder.vertex(modelViewMatrix, -size, 0, 0.001F).color(r, g, b, 127).uv(0.0F, 0.75F).uv2(light).endVertex();
		builder.vertex(modelViewMatrix, -size, barHeight, 0.001F).color(r, g, b, 127).uv(0.0F, 1.0F).uv2(light).endVertex();
		builder.vertex(modelViewMatrix, healthSize * 2 - size, barHeight, 0.001F).color(r, g, b, 127).uv(1.0F, 1.0F).uv2(light).endVertex();
		builder.vertex(modelViewMatrix, healthSize * 2 - size, 0, 0.001F).color(r, g, b, 127).uv(1.0F, 0.75F).uv2(light).endVertex();

		//Health bar background
		if (healthSize < size) {
			builder.vertex(modelViewMatrix, -size + healthSize * 2, 0, 0.001F).color(0, 0, 0, 127).uv(0.0F, 0.5F).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, -size + healthSize * 2, barHeight, 0.001F).color(0, 0, 0, 127).uv(0.0F, 0.75F).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, size, barHeight, 0.001F).color(0, 0, 0, 127).uv(1.0F, 0.75F).uv2(light).endVertex();
			builder.vertex(modelViewMatrix, size, 0, 0.001F).color(0, 0, 0, 127).uv(1.0F, 0.5F).uv2(light).endVertex();
		}


		{
			int white = 0xFFFFFF;
			int black = 0x000000;
			poseStack.translate(-size, -4.5F, 0F);
			poseStack.scale(textScale, textScale, textScale);
			modelViewMatrix = poseStack.last().pose();
			mc.font.drawInBatch(name, 0, 0, white, false, modelViewMatrix, buffer, false, black, light);

			float s1 = 0.75F;
			poseStack.pushPose();
			{
				poseStack.scale(s1, s1, s1);
				modelViewMatrix = poseStack.last().pose();

				int h = NeatConfig.hpTextHeight;
				String maxHpStr = ChatFormatting.BOLD + "" + Math.round(entity.getMaxHealth() * 100.0) / 100.0;
				String hpStr = "" + Math.round(health * 100.0) / 100.0;
				String percStr = (int) percent + "%";

				if (maxHpStr.endsWith(".00"))
					maxHpStr = maxHpStr.substring(0, maxHpStr.length() - 3);
				if (hpStr.endsWith(".00"))
					hpStr = hpStr.substring(0, hpStr.length() - 3);

				if (NeatConfig.showCurrentHP)
					mc.font.drawInBatch(hpStr, 2, h, white, false, modelViewMatrix, buffer, false, black, light);
				if (NeatConfig.showMaxHP)
					mc.font.drawInBatch(maxHpStr, (int) (size / (textScale * s1) * 2) - 2 - mc.font.width(maxHpStr), h, white, false, modelViewMatrix, buffer, false, black, light);
				if (NeatConfig.showPercentage)
					mc.font.drawInBatch(percStr, (int) (size / (textScale * s1)) - mc.font.width(percStr) / 2, h, white, false, modelViewMatrix, buffer, false, black, light);
				if (NeatConfig.enableDebugInfo && mc.options.renderDebug)
					mc.font.drawInBatch("ID: \"" + entity.getType().getRegistryName().toString() + "\"", 0, h + 16, white, false, modelViewMatrix, buffer, false, black, light);
			}
			poseStack.popPose();

			poseStack.pushPose();
			int off = 0;
			s1 = 0.5F;
			poseStack.scale(s1, s1, s1);
			poseStack.translate(size / (textScale * s1) * 2, 0F, 0F);
			mc.textureManager.bindForSetup(InventoryMenu.BLOCK_ATLAS);
			if (NeatConfig.showAttributes) {
				renderIcon(mc, off, 0, icon, poseStack, buffer, light);
				off -= 16;
			}

			int armor = entity.getArmorValue();
			if (armor > 0 && NeatConfig.showArmor) {
				int ironArmor = armor % 5;
				int diamondArmor = armor / 5;
				if (!NeatConfig.groupArmor) {
					ironArmor = armor;
					diamondArmor = 0;
				}

				icon = new ItemStack(Items.IRON_CHESTPLATE);
				for (int i = 0; i < ironArmor; i++) {
					renderIcon(mc, off, 0, icon, poseStack, buffer, light);
					off -= 4;
				}

				icon = new ItemStack(Items.DIAMOND_CHESTPLATE);
				for (int i = 0; i < diamondArmor; i++) {
					renderIcon(mc, off, 0, icon, poseStack, buffer, light);
					off -= 4;
				}
			}
			poseStack.popPose();
		}
	}

	private void renderIcon(Minecraft mc, int vertexX, int vertexY, @Nonnull ItemStack icon, PoseStack poseStack, MultiBufferSource buffer, int light) {
		poseStack.pushPose();
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90));
		poseStack.translate(vertexY - 16, vertexX - 16, 0.0D);
		poseStack.scale(16.0F, 16.0F, 1.0F);
		try {
			ResourceLocation registryName = icon.getItem().getRegistryName();
			Pair<ResourceLocation, ResourceLocation> pair = Pair.of(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(registryName.getNamespace(), "item/" + registryName.getPath()));
			TextureAtlasSprite sprite = mc.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
			PoseStack.Pose pose = poseStack.last();
			Matrix4f modelViewMatrix = pose.pose();
			Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
			normal.transform(pose.normal());
			if (icon.isEmpty()) { //Wonky workaround to make text stay in position & make empty icon not rendering
				VertexConsumer builder = buffer.getBuffer(NeatRenderType.getNoIconType());
				builder.vertex(modelViewMatrix, 0.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
				builder.vertex(modelViewMatrix, 0.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
				builder.vertex(modelViewMatrix, 1.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
				builder.vertex(modelViewMatrix, 1.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			} else {
				VertexConsumer builder = buffer.getBuffer(NeatRenderType.getHealthBarType(InventoryMenu.BLOCK_ATLAS));
				builder.vertex(modelViewMatrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, 255).uv(sprite.getU0(), sprite.getV1()).uv2(light).endVertex();
				builder.vertex(modelViewMatrix, 0.0F, 1.0F, 0.0F).color(255, 255, 255, 255).uv(sprite.getU1(), sprite.getV1()).uv2(light).endVertex();
				builder.vertex(modelViewMatrix, 1.0F, 1.0F, 0.0F).color(255, 255, 255, 255).uv(sprite.getU1(), sprite.getV0()).uv2(light).endVertex();
				builder.vertex(modelViewMatrix, 1.0F, 0.0F, 0.0F).color(255, 255, 255, 255).uv(sprite.getU0(), sprite.getV0()).uv2(light).endVertex();
			}
			//Wonky workaround for making corner icons stay in position
			VertexConsumer builder = buffer.getBuffer(NeatRenderType.getNoIconType());
			builder.vertex(modelViewMatrix, 0.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			builder.vertex(modelViewMatrix, 0.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			builder.vertex(modelViewMatrix, 1.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			builder.vertex(modelViewMatrix, 1.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
		} catch (Exception ignored) {
			poseStack.popPose();
			return;
		}
		poseStack.popPose();
	}
}
