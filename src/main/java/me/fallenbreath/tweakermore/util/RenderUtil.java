package me.fallenbreath.tweakermore.util;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

public class RenderUtil
{
	public static Scaler createScaler(int anchorX, int anchorY, double factor)
	{
		return new Scaler(anchorX, anchorY, factor);
	}

	public static class Scaler
	{
		private final int anchorX;
		private final int anchorY;
		private final double factor;

		private RenderContext renderContext;

		private Scaler(int anchorX, int anchorY, double factor)
		{
			this.anchorX = anchorX;
			this.anchorY = anchorY;
			if (factor <= 0)
			{
				throw new IllegalArgumentException("factor should be greater than 0, but " + factor + " found");
			}
			this.factor = factor;
		}

		public void apply(
				//#if MC >= 11600
				//$$ MatrixStack matrixStack
				//#endif
		)
		{
			this.renderContext = new RenderContext(
					//#if MC >= 11600
					//$$ matrixStack
					//#endif
			);
			this.renderContext.pushMatrix();
			this.renderContext.translated(-anchorX * factor, -anchorY * factor, 0);
			this.renderContext.scale(factor, factor, 1);
			this.renderContext.translated(anchorX / factor, anchorY / factor, 0);
		}

		public void restore()
		{
			if (this.renderContext == null)
			{
				throw new RuntimeException("RenderUtil.Scaler: Calling restore before calling apply");
			}
			this.renderContext.popMatrix();
		}
	}
}
