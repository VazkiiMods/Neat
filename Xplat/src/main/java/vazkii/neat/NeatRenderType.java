package vazkii.neat;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;

import vazkii.neat.mixin.AccessorRenderType;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

public class NeatRenderType extends RenderStateShard {

	//https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
	public static final ResourceLocation HEALTH_BAR_TEXTURE = ResourceLocation.fromNamespaceAndPath(NeatConfig.MOD_ID, "textures/ui/health_bar_texture.png");
	public static final RenderType BAR_TEXTURE_TYPE = getHealthBarType();

	private NeatRenderType(String string, Runnable r, Runnable r1) {
		super(string, r, r1);
	}

	private static RenderType getHealthBarType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder()
				.setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER)
				.setTextureState(new TextureStateShard(NeatRenderType.HEALTH_BAR_TEXTURE, TriState.FALSE, false))
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setCullState(NO_CULL)
				.setLightmapState(LIGHTMAP)
				.createCompositeState(false);
		return AccessorRenderType.neat_create("neat_health_bar", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, false, renderTypeState);
	}
}
