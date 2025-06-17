package vazkii.neat;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

public class NeatRenderPipelines {

	public static final RenderPipeline ENTITY_ADDITIVE_TRANSLUCENT_PIPELINE = RenderPipelines.register(
			RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
					.withLocation(ResourceLocation.parse("neat:pipeline/entity_additive_translucent"))
					.withShaderDefine("ALPHA_CUTOUT", 0.1F)
					.withSampler("Sampler1")
					.withBlend(BlendFunction.ADDITIVE)
					.withCull(false)
					.build()
	);
}
