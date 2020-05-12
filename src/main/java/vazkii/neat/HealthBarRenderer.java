package vazkii.neat;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class HealthBarRenderer {

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getInstance();

		if ((!NeatConfig.renderInF1 && !Minecraft.isGuiEnabled()) || !NeatConfig.draw)
			return;

		ActiveRenderInfo renderInfo = mc.gameRenderer.getActiveRenderInfo();
		MatrixStack matrixStack = event.getMatrixStack();
		float partialTicks = event.getPartialTicks();
		Entity cameraEntity = renderInfo.getRenderViewEntity() != null ? renderInfo.getRenderViewEntity() : mc.player;

		if (NeatConfig.showOnlyFocused) {
			Entity focused = getEntityLookedAt(mc.player);
			if (focused != null && focused instanceof LivingEntity && focused.isAlive()) {
				renderHealthBar((LivingEntity) focused, mc, matrixStack, partialTicks, renderInfo, cameraEntity);
			}
		} else {
			Vec3d cameraPos = renderInfo.getProjectedView();
			final ClippingHelperImpl clippingHelper = new ClippingHelperImpl(matrixStack.getLast().getMatrix(), event.getProjectionMatrix());
			clippingHelper.setCameraPosition(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ());

			ClientWorld client = mc.world;
			if (client != null) {
				for (Entity entity : client.getAllEntities()) {
					if (entity != null && entity instanceof LivingEntity && entity != cameraEntity && entity.isAlive() && entity.getRecursivePassengers().isEmpty() && entity.isInRangeToRender3d(cameraPos.getX(), cameraPos.getY(), cameraPos.getZ()) && (entity.ignoreFrustumCheck || clippingHelper.isBoundingBoxInFrustum(entity.getBoundingBox()))) {
						renderHealthBar((LivingEntity) entity, mc, matrixStack, partialTicks, renderInfo, cameraEntity);
					}
				}
			}
		}
	}

	public void renderHealthBar(LivingEntity passedEntity, Minecraft mc, MatrixStack matrixStack, float partialTicks, ActiveRenderInfo renderInfo, Entity viewPoint) {
		Stack<LivingEntity> ridingStack = new Stack<>();

		LivingEntity entity = passedEntity;
		ridingStack.push(entity);

		while (entity.getRidingEntity() != null && entity.getRidingEntity() instanceof LivingEntity) {
			entity = (LivingEntity) entity.getRidingEntity();
			ridingStack.push(entity);
		}

		matrixStack.push();
		while (!ridingStack.isEmpty()) {
			entity = ridingStack.pop();
			boolean boss = !entity.isNonBoss();

			String entityID = entity.getEntityString();
			if (NeatConfig.blacklist.contains(entityID))
				continue;

			processing:
			{
				float distance = passedEntity.getDistance(viewPoint);
				if (distance > NeatConfig.maxDistance || !passedEntity.canEntityBeSeen(viewPoint) || entity.isInvisible())
					break processing;
				if (!NeatConfig.showOnBosses && !boss)
					break processing;
				if (!NeatConfig.showOnPlayers && entity instanceof PlayerEntity)
					break processing;
				if (entity.getMaxHealth() <= 0)
					break processing;
				if(!NeatConfig.showFullHealth && entity.getHealth() == entity.getMaxHealth())
					break processing;
				
				double x = passedEntity.prevPosX + (passedEntity.getPosX() - passedEntity.prevPosX) * partialTicks;
				double y = passedEntity.prevPosY + (passedEntity.getPosY() - passedEntity.prevPosY) * partialTicks;
				double z = passedEntity.prevPosZ + (passedEntity.getPosZ() - passedEntity.prevPosZ) * partialTicks;

				EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
				Vec3d renderPos = renderManager.info.getProjectedView();

				matrixStack.push();
				matrixStack.translate((float) (x - renderPos.getX()), (float) (y - renderPos.getY() + passedEntity.getHeight() + NeatConfig.heightAbove), (float) (z - renderPos.getZ()));
				IRenderTypeBuffer.Impl buffer = mc.getRenderTypeBuffers().getBufferSource();
				ItemStack icon = getIcon(entity, boss);
				final int light = 0xF000F0;
				renderEntity(mc, matrixStack, buffer, renderInfo, entity, light, icon, boss);
				matrixStack.pop();

				matrixStack.translate(0.0D, -(NeatConfig.backgroundHeight + NeatConfig.barHeight + NeatConfig.backgroundPadding), 0.0D);
			}
		}
		matrixStack.pop();
	}

	private void renderEntity(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer.Impl buffer, ActiveRenderInfo renderInfo, LivingEntity entity, int light, ItemStack icon, boolean boss) {
		Quaternion rotation = renderInfo.getRotation().copy();
		rotation.multiply(-1.0F);
		matrixStack.rotate(rotation);
		float scale = 0.026666672F;
		matrixStack.scale(-scale, -scale, scale);
		float health = MathHelper.clamp(entity.getHealth(), 0.0F, entity.getMaxHealth());
		float percent = (health / entity.getMaxHealth()) * 100.0F;
		float size = NeatConfig.plateSize;
		float textScale = 0.5F;

		String name = (entity.hasCustomName() ? entity.getCustomName().applyTextStyle(TextFormatting.ITALIC) : entity.getDisplayName()).getFormattedText();
		float namel = mc.fontRenderer.getStringWidth(name) * textScale;
		if (namel + 20 > size * 2) {
			size = namel / 2.0F + 10.0F;
		}
		float healthSize = size * (health / entity.getMaxHealth());
		MatrixStack.Entry entry = matrixStack.getLast();
		Matrix4f modelViewMatrix = entry.getMatrix();
		Vector3f normal = new Vector3f(0.0F, 1.0F, 0.0F);
		normal.transform(entry.getNormal());
		IVertexBuilder builder = buffer.getBuffer(NeatRenderType.getHealthBarType());
		float padding = NeatConfig.backgroundPadding;
		int bgHeight = NeatConfig.backgroundHeight;
		int barHeight = NeatConfig.barHeight;

		// Background
		if (NeatConfig.drawBackground) {
			builder.pos(modelViewMatrix, -size - padding, -bgHeight, 0.0F).tex(0.0F, 0.0F).color(0, 0, 0, 64).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			builder.pos(modelViewMatrix, -size - padding, barHeight + padding, 0.0F).tex(0.0F, 0.5F).color(0, 0, 0, 64).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			builder.pos(modelViewMatrix, size + padding, barHeight + padding, 0.0F).tex(1.0F, 0.5F).color(0, 0, 0, 64).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
			builder.pos(modelViewMatrix, size + padding, -bgHeight, 0.0F).tex(1.0F, 0.0F).color(0, 0, 0, 64).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		}

		// Gray Space
		builder.pos(modelViewMatrix, -size, 0, -0.001F).tex(0.0F, 0.5F).color(0, 0, 0, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.pos(modelViewMatrix, -size, barHeight, -0.001F).tex(0.0F, 0.75F).color(0, 0, 0, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.pos(modelViewMatrix, size, barHeight, -0.001F).tex(1.0F, 0.75F).color(0, 0, 0, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.pos(modelViewMatrix, size, 0, -0.001F).tex(1.0F, 0.5F).color(0, 0, 0, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

		// Health Bar
		int argb = getColor(entity, NeatConfig.colorByType, boss);
		int r = (argb >> 16) & 0xFF;
		int g = (argb >> 8) & 0xFF;
		int b = argb & 0xFF;
		builder.pos(modelViewMatrix, -size, 0, -0.002F).tex(0.0F, 0.75F).color(r, g, b, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.pos(modelViewMatrix, -size, barHeight, -0.002F).tex(0.0F, 1.0F).color(r, g, b, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.pos(modelViewMatrix, healthSize * 2 - size, barHeight, -0.002F).tex(1.0F, 1.0F).color(r, g, b, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();
		builder.pos(modelViewMatrix, healthSize * 2 - size, 0, -0.002F).tex(1.0F, 0.75F).color(r, g, b, 127).normal(normal.getX(), normal.getY(), normal.getZ()).endVertex();

		{
			int white = 0xFFFFFF;
			int black = 0x000000;
			matrixStack.translate(-size, -4.5F, 0F);
			matrixStack.scale(textScale, textScale, textScale);
			modelViewMatrix = matrixStack.getLast().getMatrix();
			mc.fontRenderer.renderString(name, 0, 0, white, false, modelViewMatrix, buffer, false, black, light);

			float s1 = 0.75F;
			matrixStack.push();
			{
				matrixStack.scale(s1, s1, s1);
				modelViewMatrix = matrixStack.getLast().getMatrix();

				int h = NeatConfig.hpTextHeight;
				String maxHpStr = TextFormatting.BOLD + "" + Math.round(entity.getMaxHealth() * 100.0) / 100.0;
				String hpStr = "" + Math.round(health * 100.0) / 100.0;
				String percStr = (int) percent + "%";

				if (maxHpStr.endsWith(".00"))
					maxHpStr = maxHpStr.substring(0, maxHpStr.length() - 3);
				if (hpStr.endsWith(".00"))
					hpStr = hpStr.substring(0, hpStr.length() - 3);

				if (NeatConfig.showCurrentHP)
					mc.fontRenderer.renderString(hpStr, 2, h, white, false, modelViewMatrix, buffer, false, black, light);
				if (NeatConfig.showMaxHP)
					mc.fontRenderer.renderString(maxHpStr, (int) (size / (textScale * s1) * 2) - 2 - mc.fontRenderer.getStringWidth(maxHpStr), h, white, false, modelViewMatrix, buffer, false, black, light);
				if (NeatConfig.showPercentage)
					mc.fontRenderer.renderString(percStr, (int) (size / (textScale * s1)) - mc.fontRenderer.getStringWidth(percStr) / 2, h, white, false, modelViewMatrix, buffer, false, black, light);
				if (NeatConfig.enableDebugInfo && mc.gameSettings.showDebugInfo)
					mc.fontRenderer.renderString("ID: \"" + Registry.ENTITY_TYPE.getId(entity.getType()) + "\"", 0, h + 16, white, false, modelViewMatrix, buffer, false, black, light);
			}
			matrixStack.pop();

			matrixStack.push();
			int off = 0;
			s1 = 0.5F;
			matrixStack.scale(s1, s1, s1);
			matrixStack.translate(size / (textScale * s1) * 2, 0F, 0F);
			mc.textureManager.bindTexture(AtlasTexture.LOCATION_BLOCKS_TEXTURE);
			if (NeatConfig.showAttributes) {
				renderIcon(mc, off, 0, icon, matrixStack, buffer, renderInfo);
				off -= 16;
			}

			int armor = entity.getTotalArmorValue();
			if (armor > 0 && NeatConfig.showArmor) {
				int ironArmor = armor % 5;
				int diamondArmor = armor / 5;
				if (!NeatConfig.groupArmor) {
					ironArmor = armor;
					diamondArmor = 0;
				}

				icon = new ItemStack(Items.IRON_CHESTPLATE);
				for (int i = 0; i < ironArmor; i++) {
					renderIcon(mc, off, 0, icon, matrixStack, buffer, renderInfo);
					off -= 4;
				}

				icon = new ItemStack(Items.DIAMOND_CHESTPLATE);
				for (int i = 0; i < diamondArmor; i++) {
					renderIcon(mc, off, 0, icon, matrixStack, buffer, renderInfo);
					off -= 4;
				}
			}
		}
	}

	private void renderIcon(Minecraft mc, int vertexX, int vertexY, @Nonnull ItemStack icon, MatrixStack matrixStack, IRenderTypeBuffer buffer, ActiveRenderInfo renderInfo) {
		matrixStack.push();
		matrixStack.rotate(Vector3f.ZP.rotationDegrees(-90));
		matrixStack.translate(vertexY - 16, vertexX - 16, 0.0D);
		matrixStack.scale(16.0F, 16.0F, 1.0F);
		try {
			ResourceLocation registryName = icon.getItem().getRegistryName();
			Pair<ResourceLocation, ResourceLocation> pair = Pair.of(PlayerContainer.LOCATION_BLOCKS_TEXTURE, new ResourceLocation(registryName.getNamespace(), "item/" + registryName.getPath()));
			TextureAtlasSprite sprite = mc.getAtlasSpriteGetter(pair.getFirst()).apply(pair.getSecond());
			Matrix4f modelViewMatrix = matrixStack.getLast().getMatrix();
			if (icon == null || icon.isEmpty()) { //Wonky workaround to make text stay in position & make empty icon not rendering
				IVertexBuilder builder = buffer.getBuffer(NeatRenderType.getNoIconType());
				builder.pos(0.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
				builder.pos(0.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
				builder.pos(1.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
				builder.pos(1.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			} else {
				IVertexBuilder builder = buffer.getBuffer(NeatRenderType.getIconType(pair.getSecond()));
				builder.pos(modelViewMatrix, 0.0F, 0.0F, 0.0F).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMaxV()).endVertex();
				builder.pos(modelViewMatrix, 0.0F, 1.0F, 0.0F).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMaxV()).endVertex();
				builder.pos(modelViewMatrix, 1.0F, 1.0F, 0.0F).color(255, 255, 255, 255).tex(sprite.getMaxU(), sprite.getMinV()).endVertex();
				builder.pos(modelViewMatrix, 1.0F, 0.0F, 0.0F).color(255, 255, 255, 255).tex(sprite.getMinU(), sprite.getMinV()).endVertex();
			}
			//Wonky workaround for making corner icons stay in position
			IVertexBuilder builder = buffer.getBuffer(NeatRenderType.getNoIconType());
			builder.pos(0.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			builder.pos(0.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			builder.pos(1.0F, 1.0F, 0.0F).color(0, 0, 0, 0).endVertex();
			builder.pos(1.0F, 0.0F, 0.0F).color(0, 0, 0, 0).endVertex();
		} catch (Exception ignored) {
		}
		matrixStack.pop();
	}

	public static Entity getEntityLookedAt(Entity e) {
		Entity foundEntity = null;
		final double finalDistance = 32;
		double distance = finalDistance;
		RayTraceResult pos = raycast(e, finalDistance);
		Vec3d positionVector = e.getPositionVector();

		if (e instanceof PlayerEntity)
			positionVector = positionVector.add(0, e.getEyeHeight(e.getPose()), 0);

		if (pos != null)
			distance = pos.getHitVec().distanceTo(positionVector);

		Vec3d lookVector = e.getLookVec();
		Vec3d reachVector = positionVector.add(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance);

		Entity lookedEntity = null;
		List<Entity> entitiesInBoundingBox = e.getEntityWorld().getEntitiesWithinAABBExcludingEntity(e, e.getBoundingBox().grow(lookVector.x * finalDistance, lookVector.y * finalDistance, lookVector.z * finalDistance).expand(1F, 1F, 1F));
		double minDistance = distance;

		for (Entity entity : entitiesInBoundingBox) {
			if (entity.canBeCollidedWith()) {
				AxisAlignedBB collisionBox = entity.getRenderBoundingBox();
				Optional<Vec3d> interceptPosition = collisionBox.rayTrace(positionVector, reachVector);

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

	public static RayTraceResult raycast(Entity e, double len) {
		Vec3d vec = new Vec3d(e.getPosX(), e.getPosY(), e.getPosZ());
		if (e instanceof PlayerEntity)
			vec = vec.add(new Vec3d(0, e.getEyeHeight(e.getPose()), 0));

		Vec3d look = e.getLookVec();
		if (look == null)
			return null;

		return raycast(vec, look, e, len);
	}

	public static RayTraceResult raycast(Vec3d origin, Vec3d ray, Entity e, double len) {
		Vec3d next = origin.add(ray.normalize().scale(len));
		return e.world.rayTraceBlocks(new RayTraceContext(origin, next, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, e));
	}

	@Nonnull
	public static ItemStack getIcon(LivingEntity entity, boolean boss) {
		if (boss) {
			return new ItemStack(Items.NETHER_STAR);
		}
		CreatureAttribute attr = entity.getCreatureAttribute();
		if (attr == CreatureAttribute.ARTHROPOD) {
			return new ItemStack(Items.SPIDER_EYE);
		} else if (attr == CreatureAttribute.UNDEAD) {
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
			if (entity instanceof MonsterEntity) {
				r = 255;
				g = 0;
				b = 0;
			}
			return 0xff000000 | r << 16 | g << 8 | b;
		} else {
			float health = MathHelper.clamp(entity.getHealth(), 0.0F, entity.getMaxHealth());
			float hue = Math.max(0.0F, (health / entity.getMaxHealth()) / 3.0F - 0.07F);
			return Color.HSBtoRGB(hue, 1.0F, 1.0F);
		}
	}
}