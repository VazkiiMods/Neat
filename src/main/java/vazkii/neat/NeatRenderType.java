package vazkii.neat;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

public class NeatRenderType extends RenderStateShard {

	//https://github.com/UpcraftLP/Orderly/blob/master/src/main/resources/assets/orderly/textures/ui/default_health_bar.png
	public static final ResourceLocation HEALTH_BAR_TEXTURE = new ResourceLocation(Neat.MOD_ID, "textures/ui/health_bar_texture.png");
	public static final RenderType NO_ICON_TYPE = getNoIconType();
	public static final RenderType BAR_TEXTURE_TYPE = getHealthBarType(HEALTH_BAR_TEXTURE);
	public static final RenderType BAR_ATLAS_TYPE = getHealthBarType(InventoryMenu.BLOCK_ATLAS);

	private NeatRenderType(String string, Runnable r, Runnable r1) {
		super(string, r, r1);
	}

	private static RenderType getNoIconType() {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_SHADER).setTransparencyState(TransparencyStateShard.TRANSLUCENT_TRANSPARENCY).createCompositeState(true);
		return RenderType.create("neat_icon", POSITION_COLOR, VertexFormat.Mode.QUADS, 0, false, false, renderTypeState);
	}

	private static RenderType getHealthBarType(ResourceLocation location) {
		RenderType.CompositeState renderTypeState = RenderType.CompositeState.builder().setShaderState(POSITION_COLOR_TEX_LIGHTMAP_SHADER).setTextureState(new TextureStateShard(location, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setLightmapState(LIGHTMAP).createCompositeState(true);
		return RenderType.create("neat_health_bar", POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, renderTypeState);
	}
}
