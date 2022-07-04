package vazkii.neat.mixin;

import com.mojang.blaze3d.vertex.VertexFormat;

import net.minecraft.client.renderer.RenderType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RenderType.class)
public interface AccessorRenderType {
	@Invoker("create")
	static RenderType.CompositeRenderType neat_create(String name, VertexFormat format, VertexFormat.Mode mode, int bufSize, boolean affectsCrumbling, boolean sortOnUpload, RenderType.CompositeState glState) {
		throw new IllegalStateException("");
	}
}
