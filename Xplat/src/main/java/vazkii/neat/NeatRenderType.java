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
	public static final RenderType BACKGROUND_RENDER_TYPE = getBarRenderType();

	private NeatRenderType(String string, Runnable r, Runnable r1) {
		super(string, r, r1);
	}

	/*
	private static RenderType getBarRenderType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
				.setTextureState(new TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, TriState.FALSE, false))
				.setTransparencyState(ADDITIVE_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return AccessorRenderType.neat_create("neat_health_bar", NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, renderTypeState);
	}

	private static RenderType getBackgroundRenderType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_SHADER)
				.setTextureState(new TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, TriState.FALSE, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return AccessorRenderType.neat_create("neat_health_bar_bg", NEW_ENTITY, VertexFormat.Mode.QUADS, 1536, true, false, renderTypeState);
	}

	 */

	private static RenderType getBarRenderType() { //TODO
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setTextureState(new RenderStateShard.TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, TriState.FALSE, false))
				.setLightmapState(LIGHTMAP)
				.setOverlayState(OVERLAY)
				.createCompositeState(false);
		return RenderType.create("neat_health_bar", 1536, true, false, RenderPipelines.ENTITY_TRANSLUCENT, renderTypeState);
	}
}
