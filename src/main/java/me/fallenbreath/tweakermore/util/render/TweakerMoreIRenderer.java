package me.fallenbreath.tweakermore.util.render;

import fi.dy.masa.malilib.interfaces.IRenderer;

//#if MC >= 11700
//$$ import net.minecraft.util.math.Matrix4f;
//#endif

//#if MC >= 11500
import net.minecraft.client.util.math.MatrixStack;
//#endif

public interface TweakerMoreIRenderer extends IRenderer
{
	@Deprecated
	@Override
	default void onRenderWorldLast(
			//#if MC >= 11700
			//$$ MatrixStack matrixStack, Matrix4f positionMatrix
			//#elseif MC >= 11500
			float partialTicks, MatrixStack matrixStack
			//#else
			//$$ float partialTicks
			//#endif
	)
	{
		this.onRenderWorldLast(
				new RenderContext(
						//#if MC >= 11600
						//$$ matrixStack
						//#endif
				)
		);
	}

	@Deprecated
	@Override
	default void onRenderGameOverlayPost(
			//#if MC >= 11700
			//$$ MatrixStack matrixStack
			//#elseif MC >= 11600
			//$$ float partialTicks, MatrixStack matrixStack
			//#else
			float partialTicks
			//#endif
	)
	{
		this.onRenderGameOverlayPost(
				new RenderContext(
						//#if MC >= 11600
						//$$ matrixStack
						//#endif
				)
		);
	}

	default void onRenderWorldLast(RenderContext context) {}
	default void onRenderGameOverlayPost(RenderContext context) {}
}
