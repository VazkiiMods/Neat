package vazkii.neat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;

public class NeatRenderType {
	//https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
	public static final Identifier HEALTH_BAR_TEXTURE = Identifier.fromNamespaceAndPath(NeatConfig.MOD_ID, "textures/ui/health_bar_texture.png");

	public static final RenderSetup BACKGROUND_SETUP = RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
			.withTexture("Sampler0", HEALTH_BAR_TEXTURE)
			.useLightmap()
			.useOverlay()
			//.bufferSize(1536)
			.affectsCrumbling()
			.createRenderSetup();

	public static final RenderSetup BAR_SETUP = RenderSetup.builder(NeatRenderPipelines.ENTITY_ADDITIVE_TRANSLUCENT_PIPELINE)
			.withTexture("Sampler0", HEALTH_BAR_TEXTURE)
			.useLightmap()
			.useOverlay()
			//.bufferSize(1536)
			.affectsCrumbling()
			.createRenderSetup();

	public static final RenderType BACKGROUND_RENDER_TYPE = RenderType.create("neat_health_bar_bg", BACKGROUND_SETUP);
	public static final RenderType BAR_RENDER_TYPE = RenderType.create("neat_health_bar", BAR_SETUP);

}
