package vazkii.neat;

import com.google.common.collect.ImmutableMap;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.vertex.VertexFormatElement;
import net.minecraft.resources.ResourceLocation;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

import static net.minecraft.client.renderer.RenderStateShard.TextureStateShard;
import static net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard;

public class NeatRenderType extends RenderStateShard {

	//https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
	public static final ResourceLocation HEALTH_BAR_TEXTURE = new ResourceLocation(Neat.MOD_ID, "textures/ui/health_bar_texture.png");

	//public static final VertexFormat POSITION_TEX_COLOR_NORMAL_LIGHTMAP = new VertexFormat(ImmutableMap.<String, VertexFormatElement>builder().put("Position", ELEMENT_POSITION).put("UV0", ELEMENT_UV0).put("Color", ELEMENT_COLOR).put("Normal", ELEMENT_NORMAL).put("UV2", ELEMENT_UV2).put("Padding", ELEMENT_PADDING).build());

	public NeatRenderType(String string, Runnable r, Runnable r1) {
		super(string, r, r1);
	}

	public static RenderType getNoIconType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TransparencyStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(true);
		return RenderType.create("neat_icon", POSITION_COLOR, VertexFormat.Mode.QUADS, 0, false, false, renderTypeState);
	}

	public static RenderType getHealthBarType(ResourceLocation location) {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER).setTextureState(new TextureStateShard(location, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(true);
		return RenderType.create("neat_health_bar", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, renderTypeState);
	}
}
