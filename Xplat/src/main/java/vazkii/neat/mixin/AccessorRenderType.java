package vazkii.neat.mixin;

import com.mojang.blaze3d.pipeline.RenderPipeline;

import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface AccessorRenderType {
	@Invoker("create")
	static RenderType.CompositeRenderType neat_create(String name, int bufferSize, boolean affectsCrumbling, boolean sortOnUpload, RenderPipeline renderPipeline, RenderType.CompositeState state) {
		throw new IllegalStateException("");
	}
}
