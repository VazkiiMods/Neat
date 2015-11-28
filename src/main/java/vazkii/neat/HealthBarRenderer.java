package vazkii.neat;

import java.awt.Color;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class HealthBarRenderer {

	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		Minecraft mc = Minecraft.getMinecraft();

		if(!NeatConfig.renderInF1 && !Minecraft.isGuiEnabled()) 
			return;

		EntityLivingBase cameraEntity = mc.renderViewEntity;
		Vec3 renderingVector = cameraEntity.getPosition(event.partialTicks);
		Frustrum frustrum = new Frustrum();

		double viewX = cameraEntity.lastTickPosX + (cameraEntity.posX - cameraEntity.lastTickPosX) * event.partialTicks;
		double viewY = cameraEntity.lastTickPosY + (cameraEntity.posY - cameraEntity.lastTickPosY) * event.partialTicks;
		double viewZ = cameraEntity.lastTickPosZ + (cameraEntity.posZ - cameraEntity.lastTickPosZ) * event.partialTicks;
		frustrum.setPosition(viewX, viewY, viewZ);

		WorldClient client = mc.theWorld;
		Set<Entity> entities = ReflectionHelper.getPrivateValue(WorldClient.class, client, new String[] { "entityList", "field_73032_d", "J" });

		for(Entity entity : entities)
			if(entity != null && entity instanceof EntityLiving && entity.isInRangeToRender3d(renderingVector.xCoord, renderingVector.yCoord, renderingVector.zCoord) && (entity.ignoreFrustumCheck || frustrum.isBoundingBoxInFrustum(entity.boundingBox)) && entity.isEntityAlive()) 
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
				
				GL11.glPushMatrix();
				GL11.glTranslatef((float) (x - RenderManager.renderPosX), (float) (y - RenderManager.renderPosY + passedEntity.height + NeatConfig.heightAbove), (float) (z - RenderManager.renderPosZ));
				GL11.glNormal3f(0.0F, 1.0F, 0.0F);
				GL11.glRotatef(-RenderManager.instance.playerViewY, 0.0F, 1.0F, 0.0F);
				GL11.glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
				GL11.glScalef(-scale, -scale, scale);
				GL11.glDisable(GL11.GL_LIGHTING);
				GL11.glDepthMask(false);
				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				Tessellator tessellator = Tessellator.instance;

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
				
				GL11.glTranslatef(0F, pastTranslate, 0F);
				
				float s = 0.5F;
				String name = StatCollector.translateToLocal("entity." + EntityList.getEntityString(entity) + ".name");
				if(entity instanceof EntityLiving && ((EntityLiving) entity).hasCustomNameTag())
					name = EnumChatFormatting.ITALIC + ((EntityLiving) entity).getCustomNameTag();
				float namel = mc.fontRenderer.getStringWidth(name) * s;
				if(namel + 20 > size * 2)
					size = namel / 2F + 10F;
				float healthSize = size * (health / maxHealth);
				
				// Background
				if(NeatConfig.drawBackground) {
					tessellator.startDrawingQuads();
					tessellator.setColorRGBA(0, 0, 0, 64);
					tessellator.addVertex(-size - padding, -bgHeight, 0.0D);
					tessellator.addVertex(-size - padding, barHeight + padding, 0.0D);
					tessellator.addVertex(size + padding, barHeight + padding, 0.0D);
					tessellator.addVertex(size + padding, -bgHeight, 0.0D);
					tessellator.draw();
				}

				// Gray Space
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA(127, 127, 127, 127);
				tessellator.addVertex(-size, 0, 0.0D);
				tessellator.addVertex(-size, barHeight, 0.0D);
				tessellator.addVertex(size, barHeight, 0.0D);
				tessellator.addVertex(size, 0, 0.0D);
				tessellator.draw();

				// Health Bar
				tessellator.startDrawingQuads();
				tessellator.setColorRGBA(r, g, b, 127);
				tessellator.addVertex(-size, 0, 0.0D);
				tessellator.addVertex(-size, barHeight, 0.0D);
				tessellator.addVertex(healthSize * 2 - size, barHeight, 0.0D);
				tessellator.addVertex(healthSize * 2 - size, 0, 0.0D);
				tessellator.draw();

				GL11.glEnable(GL11.GL_TEXTURE_2D);
				
				GL11.glPushMatrix();
				GL11.glTranslatef(-size, -4.5F, 0F);
				GL11.glScalef(s, s, s);
				mc.fontRenderer.drawString(name, 0, 0, 0xFFFFFF);

				GL11.glPushMatrix();
				float s1 = 0.75F;
				GL11.glScalef(s1, s1, s1);
				
				int h = NeatConfig.hpTextHeight;
				String maxHpStr = EnumChatFormatting.BOLD + "" + Math.round(maxHealth * 100.0) / 100.0;
				String hpStr = "" + Math.round(health * 100.0) / 100.0;
				String percStr = (int) percent + "%";
				
				if(maxHpStr.endsWith(".0"))
					maxHpStr = maxHpStr.substring(0, maxHpStr.length() - 2);
				if(hpStr.endsWith(".0"))
					hpStr = hpStr.substring(0, hpStr.length() - 2);
				
				if(NeatConfig.showCurrentHP)
					mc.fontRenderer.drawString(hpStr, 2, h, 0xFFFFFF);
				if(NeatConfig.showMaxHP)
					mc.fontRenderer.drawString(maxHpStr, (int) (size / (s * s1) * 2) - 2 - mc.fontRenderer.getStringWidth(maxHpStr), h, 0xFFFFFF);
				if(NeatConfig.showPercentage)
					mc.fontRenderer.drawString(percStr, (int) (size / (s * s1)) - mc.fontRenderer.getStringWidth(percStr) / 2, h, 0xFFFFFFFF);
 				GL11.glPopMatrix();
 				
 				GL11.glColor4f(1F, 1F, 1F, 1F);
				int off = 0;

				s1 = 0.5F;
				GL11.glScalef(s1, s1, s1);
				GL11.glTranslatef(size / (s * s1) * 2 - 16, 0F, 0F);
				mc.renderEngine.bindTexture(TextureMap.locationItemsTexture);
				if(stack != null && NeatConfig.showAttributes) {
					RenderItem.getInstance().renderIcon(off, 0, stack.getIconIndex(), 16, 16);
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
						RenderItem.getInstance().renderIcon(off, 0, stack.getIconIndex(), 16, 16);
						off -= 4;
					}
					
					stack = new ItemStack(Items.diamond_chestplate);
					for(int i = 0; i < diamondArmor; i++) {
						RenderItem.getInstance().renderIcon(off, 0, stack.getIconIndex(), 16, 16);
						off -= 4;
					}
				}

				GL11.glPopMatrix();

				GL11.glEnable(GL11.GL_DEPTH_TEST);
				GL11.glDepthMask(true);
				GL11.glEnable(GL11.GL_LIGHTING);
				GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
				GL11.glPopMatrix();
				
				pastTranslate = -(bgHeight + barHeight + padding);
			}

			Entity riddenBy = entity.riddenByEntity;
			if(riddenBy instanceof EntityLivingBase)
				entity = (EntityLivingBase) riddenBy;
			else return;
		}
	}


}
