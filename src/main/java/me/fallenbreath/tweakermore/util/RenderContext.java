package me.fallenbreath.tweakermore.util;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import org.jetbrains.annotations.NotNull;
//#elseif MC >= 11500
import com.mojang.blaze3d.systems.RenderSystem;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif

public class RenderContext
{
	//#if MC >= 11600
	//$$ @NotNull
	//$$ private final MatrixStack matrixStack;
	//#endif

	public RenderContext(
			//#if MC >= 11600
			//$$ @NotNull MatrixStack matrixStack
			//#endif
	)
	{
		//#if MC >= 11600
		//$$ this.matrixStack = matrixStack;
		//#endif
	}

	//#if MC >= 11600
	//$$ public MatrixStack getMatrixStack()
	//$$ {
	//$$ 	return this.matrixStack;
	//$$ }
	//#endif

	public void pushMatrix()
	{
		//#if MC >= 11600
		//$$ this.matrixStack.push();
		//#elseif MC >= 11500
		RenderSystem.pushMatrix();
		//#else
		//$$ GlStateManager.pushMatrix();
		//#endif
	}

	public void popMatrix()
	{
		//#if MC >= 11600
		//$$ this.matrixStack.pop();
		//#elseif MC >= 11500
		RenderSystem.popMatrix();
		//#else
		//$$ GlStateManager.popMatrix();
		//#endif
	}

	public void translated(double x, double y, double z)
	{
		//#if MC >= 11600
		//$$ matrixStack.translate(x, y, z);
		//#elseif MC >= 11500
		RenderSystem.translated(x, y, z);
		//#else
		//$$ GlStateManager.translated(x, y, z);
		//#endif
	}

	public void scale(double x, double y, double z)
	{
		//#if MC >= 11600
		//$$ matrixStack.scale((float)x, (float)y, (float)z);
		//#elseif MC >= 11500
		RenderSystem.scaled(x, y, z);
		//#else
		//$$ GlStateManager.scaled(x, y, z);
		//#endif
	}
}
