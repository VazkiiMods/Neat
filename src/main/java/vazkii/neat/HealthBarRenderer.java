package vazkii.neat;

import java.awt.Color;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import org.lwjgl.opengl.GL11;

public class HealthBarRenderer {

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if(!NeatConfig.renderInF1 && !Minecraft.isGuiEnabled()) 
			return;

		Entity cameraEntity = mc.getRenderViewEntity();
		BlockPos renderingVector = cameraEntity.getPosition();
		Frustum frustum = new Frustum();

		double viewX = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * event.partialTicks;
		double viewY = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * event.partialTicks;
		double viewZ = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * event.partialTicks;
		frustum.setPosition(viewX, viewY, viewZ);

		WorldClient client = mc.theWorld;
		Set<Entity> entities = ReflectionHelper.getPrivateValue(WorldClient.class, client, new String[] { "entityList", "field_73032_d", "J" });

		for(Entity entity : entities)
			if(entity != null && entity instanceof EntityLiving && entity.isInRangeToRender3d(renderingVector.getX(), renderingVector.getY(), renderingVector.getZ()) && (entity.ignoreFrustumCheck || frustum.isBoundingBoxInFrustum(entity.getEntityBoundingBox())) && entity.isEntityAlive()) 
				renderHealthBar((EntityLiving) entity, event.partialTicks, cameraEntity);
	}

	public void renderHealthBar(EntityLivingBase passedEntity, float partialTicks, Entity viewPoint) {
		if(passedEntity.riddenByEntity != null)
			return;
		
		EntityLivingBase entity = passedEntity;
		while(entity.ridingEntity != null && entity.ridingEntity instanceof EntityLivingBase)
			entity = (EntityLivingBase) entity.ridingEntity;

		Minecraft mc = Minecraft.getMinecraft();
		
		float pastTranslate = 0F;
		while(entity != null) {
			processing: {
				float distance = passedEntity.getDistanceToEntity(viewPoint);
				if(distance > NeatConfig.maxDistance || !passedEntity.canEntityBeSeen(viewPoint) || entity.isInvisible()) 
					break processing;
				if(!NeatConfig.showOnBosses && entity instanceof IBossDisplayData)
					break processing;
				if(!NeatConfig.showOnPlayers && entity instanceof EntityPlayer)
					break processing;

				double x = passedEntity.lastTickPosX + (passedEntity.posX - passedEntity.lastTickPosX) * partialTicks;
				double y = passedEntity.lastTickPosY + (passedEntity.posY - passedEntity.lastTickPosY) * partialTicks;
				double z = passedEntity.lastTickPosZ + (passedEntity.posZ - passedEntity.lastTickPosZ) * partialTicks;

				float scale = 0.026666672F;
				float maxHealth = entity.getMaxHealth();
				float health = Math.min(maxHealth, entity.getHealth());
				
				if(maxHealth <= 0)
					break processing;

				float percent = (int) ((health / maxHealth) * 100F);
				RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
				
				GlStateManager.pushMatrix();
				GlStateManager.translate((float) (x - renderManager.viewerPosX), (float) (y - renderManager.viewerPosY + passedEntity.height + NeatConfig.heightAbove), (float) (z - renderManager.viewerPosZ));
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
				GlStateManager.scale(-scale, -scale, scale);
				GlStateManager.disableLighting();
				GlStateManager.depthMask(false);
				GlStateManager.disableDepth();
				GlStateManager.disableTexture2D();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				Tessellator tessellator = Tessellator.getInstance();
				WorldRenderer worldRenderer = tessellator.getWorldRenderer();

				float padding = NeatConfig.backgroundPadding;
				int bgHeight = NeatConfig.backgroundHeight;
				int barHeight = NeatConfig.barHeight;
				float size = NeatConfig.plateSize;

				int r = 0;
				int g = 255;
				int b = 0;

				ItemStack stack = null;

				if(entity instanceof IMob) {
					r = 255;
					g = 0;
					EnumCreatureAttribute attr = entity.getCreatureAttribute();
					switch(attr) {
					case ARTHROPOD:
						stack = new ItemStack(Items.spider_eye);
						break;
					case UNDEAD:
						stack = new ItemStack(Items.rotten_flesh);
						break;
					default:
						stack = new ItemStack(Items.skull, 1, 4);
					}
				}

				if(entity instanceof IBossDisplayData) {
					stack = new ItemStack(Items.skull);
					size = NeatConfig.plateSizeBoss;
					r = 128;
					g = 0;
					b = 128;
				}
				
				int armor = entity.getTotalArmorValue();

				boolean useHue = !NeatConfig.colorByType;
				if(useHue) {
					float hue = Math.max(0F, (health / maxHealth) / 3F - 0.07F);
					Color color = Color.getHSBColor(hue, 1F, 1F);
					r = color.getRed();
					g = color.getGreen();
					b = color.getBlue();
				}
				
				GlStateManager.translate(0F, pastTranslate, 0F);
				
				float s = 0.5F;
				String name = StatCollector.translateToLocal("entity." + EntityList.getEntityString(entity) + ".name");
				if(entity instanceof EntityLiving && ((EntityLiving) entity).hasCustomName())
					name = EnumChatFormatting.ITALIC + ((EntityLiving) entity).getCustomNameTag();
				float namel = mc.fontRendererObj.getStringWidth(name) * s;
				if(namel + 20 > size * 2)
					size = namel / 2F + 10F;
				float healthSize = size * (health / maxHealth);
				
				// Background
				if(NeatConfig.drawBackground) {
					worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
					worldRenderer.pos(-size - padding, -bgHeight, 0.0D).color(0, 0, 0, 64).endVertex();
					worldRenderer.pos(-size - padding, barHeight + padding, 0.0D).color(0, 0, 0, 64).endVertex();
					worldRenderer.pos(size + padding, barHeight + padding, 0.0D).color(0, 0, 0, 64).endVertex();
					worldRenderer.pos(size + padding, -bgHeight, 0.0D).color(0, 0, 0, 64).endVertex();
					tessellator.draw();
				}

				// Gray Space
				worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
				worldRenderer.pos(-size, 0, 0.0D).color(127, 127, 127, 127).endVertex();
				worldRenderer.pos(-size, barHeight, 0.0D).color(127, 127, 127, 127).endVertex();
				worldRenderer.pos(size, barHeight, 0.0D).color(127, 127, 127, 127).endVertex();
				worldRenderer.pos(size, 0, 0.0D).color(127, 127, 127, 127).endVertex();
				tessellator.draw();

				// Health Bar
				worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
				worldRenderer.pos(-size, 0, 0.0D).color(r, g, b, 127).endVertex();
				worldRenderer.pos(-size, barHeight, 0.0D).color(r, g, b, 127).endVertex();
				worldRenderer.pos(healthSize * 2 - size, barHeight, 0.0D).color(r, g, b, 127).endVertex();
				worldRenderer.pos(healthSize * 2 - size, 0, 0.0D).color(r, g, b, 127).endVertex();
				tessellator.draw();

				GlStateManager.enableTexture2D();
				
				GlStateManager.pushMatrix();
				GlStateManager.translate(-size, -4.5F, 0F);
				GlStateManager.scale(s, s, s);
				mc.fontRendererObj.drawString(name, 0, 0, 0xFFFFFF);

				GlStateManager.pushMatrix();
				float s1 = 0.75F;
				GlStateManager.scale(s1, s1, s1);
				
				int h = NeatConfig.hpTextHeight;
				String maxHpStr = EnumChatFormatting.BOLD + "" + Math.round(maxHealth * 100.0) / 100.0;
				String hpStr = "" + Math.round(health * 100.0) / 100.0;
				String percStr = (int) percent + "%";
				
				if(maxHpStr.endsWith(".0"))
					maxHpStr = maxHpStr.substring(0, maxHpStr.length() - 2);
				if(hpStr.endsWith(".0"))
					hpStr = hpStr.substring(0, hpStr.length() - 2);
				
				if(NeatConfig.showCurrentHP)
					mc.fontRendererObj.drawString(hpStr, 2, h, 0xFFFFFF);
				if(NeatConfig.showMaxHP)
					mc.fontRendererObj.drawString(maxHpStr, (int) (size / (s * s1) * 2) - 2 - mc.fontRendererObj.getStringWidth(maxHpStr), h, 0xFFFFFF);
				if(NeatConfig.showPercentage)
					mc.fontRendererObj.drawString(percStr, (int) (size / (s * s1)) - mc.fontRendererObj.getStringWidth(percStr) / 2, h, 0xFFFFFFFF);
 				GlStateManager.popMatrix();
 				
 				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				int off = 0;

				s1 = 0.5F;
				GlStateManager.scale(s1, s1, s1);
				GlStateManager.translate(size / (s * s1) * 2 - 16, 0F, 0F);
				mc.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
				if(stack != null && NeatConfig.showAttributes) {
					renderIcon(off, 0, stack, 16, 16);
					off -= 16;
				}
				
				if(armor > 0 && NeatConfig.showArmor) {
					int ironArmor = armor % 5;
					int diamondArmor = armor / 5;
					if(!NeatConfig.groupArmor) {
						ironArmor = armor;
						diamondArmor = 0;
					}
					
					stack = new ItemStack(Items.iron_chestplate);
					for(int i = 0; i < ironArmor; i++) {
						renderIcon(off, 0, stack, 16, 16);
						off -= 4;
					}
					
					stack = new ItemStack(Items.diamond_chestplate);
					for(int i = 0; i < diamondArmor; i++) {
						renderIcon(off, 0, stack, 16, 16);
						off -= 4;
					}
				}

				GlStateManager.popMatrix();

				GlStateManager.enableDepth();
				GlStateManager.depthMask(true);
				GlStateManager.enableLighting();
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.popMatrix();
				
				pastTranslate = -(bgHeight + barHeight + padding);
			}

			Entity riddenBy = entity.riddenByEntity;
			if(riddenBy instanceof EntityLivingBase)
				entity = (EntityLivingBase) riddenBy;
			else return;
		}
	}
	
	private void renderIcon(int vertexX, int vertexY, ItemStack stack, int intU, int intV) {
		try {
			IBakedModel iBakedModel = Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(stack);
			TextureAtlasSprite textureAtlasSprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(iBakedModel.getParticleTexture().getIconName());
			Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldRenderer = tessellator.getWorldRenderer();
			worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
			worldRenderer.pos((double)(vertexX), 		(double)(vertexY + intV), 	0.0D).tex((double) textureAtlasSprite.getMinU(), (double) textureAtlasSprite.getMaxV()).endVertex();
			worldRenderer.pos((double)(vertexX + intU), (double)(vertexY + intV),	0.0D).tex((double) textureAtlasSprite.getMaxU(), (double) textureAtlasSprite.getMaxV()).endVertex();
			worldRenderer.pos((double)(vertexX + intU), (double)(vertexY), 			0.0D).tex((double) textureAtlasSprite.getMaxU(), (double) textureAtlasSprite.getMinV()).endVertex();
			worldRenderer.pos((double)(vertexX), 		(double)(vertexY), 			0.0D).tex((double) textureAtlasSprite.getMinU(), (double) textureAtlasSprite.getMinV()).endVertex();
			tessellator.draw();
		} catch (Exception e) {}
	}
}
