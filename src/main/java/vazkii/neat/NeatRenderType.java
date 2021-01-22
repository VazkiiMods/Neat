package vazkii.neat;

import com.google.common.collect.ImmutableList;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.*;

public class NeatRenderType extends RenderState {

	//https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
	public static final ResourceLocation HEALTH_BAR_TEXTURE = new ResourceLocation(Neat.MOD_ID, "textures/ui/health_bar_texture.png");

	public static final VertexFormat POSITION_TEX_COLOR_NORMAL_LIGHTMAP_OVERLAY = new VertexFormat(ImmutableList.<VertexFormatElement>builder().add(POSITION_3F).add(TEX_2F).add(COLOR_4UB).add(NORMAL_3B).add(TEX_2SB).add(TEX_2S).add(PADDING_1B).build());

	public NeatRenderType(String string, Runnable r, Runnable r1) {
		super(string, r, r1);
	}

	public static RenderType getNoIconType() {
		RenderType.State renderTypeState = RenderType.State.getBuilder().transparency(TransparencyState.TRANSLUCENT_TRANSPARENCY).build(true);
		return RenderType.makeType("neat_icon", DefaultVertexFormats.POSITION_COLOR, 0, 0, false, false, renderTypeState);
	}

	public static RenderType getHealthBarType(ResourceLocation location) {
		RenderType.State renderTypeState = RenderType.State.getBuilder().texture(new TextureState(location, false, false)).transparency(TRANSLUCENT_TRANSPARENCY).diffuseLighting(RenderState.DIFFUSE_LIGHTING_DISABLED).alpha(DEFAULT_ALPHA).cull(CULL_DISABLED).lightmap(LIGHTMAP_ENABLED).overlay(RenderState.OVERLAY_ENABLED).build(true);
		return RenderType.makeType("neat_health_bar", POSITION_TEX_COLOR_NORMAL_LIGHTMAP_OVERLAY, 7, 256, true, true, renderTypeState);
	}
}