package me.fallenbreath.tweakermore.util;

import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.Nullable;

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
		@Nullable
		private MatrixStack matrixStack = null;

		private Scaler(int anchorX, int anchorY, double factor)
		{
			this.anchorX = anchorX;
			this.anchorY = anchorY;
			if (factor <= 0)
			{
				throw new RuntimeException("factor should be greater than 0, but " + factor + " found");
			}
			this.factor = factor;
		}

		public void apply(MatrixStack matrixStack)
		{
			this.matrixStack = matrixStack;
			matrixStack.push();
			matrixStack.translate(-anchorX * factor, -anchorY * factor, 0);
			matrixStack.scale((float)factor, (float) factor, 1);
			matrixStack.translate(anchorX / factor, anchorY / factor, 0);
		}

		public void restore()
		{
			if (this.matrixStack != null)
			{
				this.matrixStack.pop();
			}
			else
			{
				throw new RuntimeException("RenderUtil.Scaler: Calling restore before calling apply");
			}
			this.matrixStack = null;
		}
	}
}
