package vazkii.neat;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

public class NeatRenderType extends RenderStateShard {

	//https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
	public static final ResourceLocation HEALTH_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(NeatConfig.MOD_ID, "textures/ui/health_bar_texture.png");
	public static final RenderType BAR_RENDER_TYPE = getBarRenderType();
	public static final RenderType BACKGROUND_RENDER_TYPE = getBackgroundRenderType();

	private NeatRenderType(String string, Runnable r, Runnable r1) {
		super(string, r, r1);
	}

	private static RenderType getBackgroundRenderType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, false))
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return RenderType.create("neat_health_bar", 1536, true, false, RenderPipelines.ENTITY_TRANSLUCENT, renderTypeState);
	}

	private static RenderType getBarRenderType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, false))
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return RenderType.create("neat_health_bar", 1536, true, false, NeatRenderPipelines.ENTITY_ADDITIVE_TRANSLUCENT_PIPELINE, renderTypeState);
	}
}
