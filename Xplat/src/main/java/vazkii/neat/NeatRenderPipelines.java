package vazkii.neat;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.renderer.BindGroupLayouts;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class NeatRenderPipelines {

	public static final RenderPipeline ENTITY_ADDITIVE_TRANSLUCENT_PIPELINE = RenderPipelines.register(
			RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
					.withLocation(Identifier.parse("neat:pipeline/entity_additive_translucent"))
					.withShaderDefine("ALPHA_CUTOUT", 0.01F)
                    .withBindGroupLayout(BindGroupLayouts.SAMPLER1)
					.withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
					.withCull(false)
					.build()
	);
}
