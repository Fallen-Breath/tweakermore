/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.util.render;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import org.jetbrains.annotations.NotNull;
//#endif

//#if MC >= 11500
import com.mojang.blaze3d.systems.RenderSystem;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.util.math.Matrix4f;

//#if 11600 <= MC && MC < 11700
//$$ @SuppressWarnings("deprecation")
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
		//#if MC >= 11700
		//$$ this.matrixStack.push();
		//#elseif MC >= 11500
		RenderSystem.pushMatrix();
		//#else
		//$$ GlStateManager.pushMatrix();
		//#endif
	}

	public void popMatrix()
	{
		//#if MC >= 11700
		//$$ this.matrixStack.pop();
		//#elseif MC >= 11500
		RenderSystem.popMatrix();
		//#else
		//$$ GlStateManager.popMatrix();
		//#endif
	}

	public void translate(double x, double y, double z)
	{
		//#if MC >= 11700
		//$$ matrixStack.translate(x, y, z);
		//#elseif MC >= 11500
		RenderSystem.translated(x, y, z);
		//#else
		//$$ GlStateManager.translated(x, y, z);
		//#endif
	}

	public void scale(double x, double y, double z)
	{
		//#if MC >= 11700
		//$$ matrixStack.scale((float)x, (float)y, (float)z);
		//#elseif MC >= 11500
		RenderSystem.scaled(x, y, z);
		//#else
		//$$ GlStateManager.scaled(x, y, z);
		//#endif
	}

	public void multMatrix(Matrix4f matrix4f)
	{
		//#if MC >= 11800
		//$$ matrixStack.multiplyPositionMatrix(matrix4f);
		//#elseif MC >= 11700
		//$$ matrixStack.method_34425(matrix4f);
		//#elseif MC >= 11500
		RenderSystem.multMatrix(matrix4f);
		//#else
		//$$ GlStateManager.multMatrix(matrix4f);
		//#endif
	}

	public void enableDepthTest()
	{
		//#if MC >= 11500
		RenderSystem.enableDepthTest();
		//#else
		//$$ GlStateManager.enableDepthTest();
		//#endif
	}

	public void disableDepthTest()
	{
		//#if MC >= 11500
		RenderSystem.disableDepthTest();
		//#else
		//$$ GlStateManager.disableDepthTest();
		//#endif
	}

	//#if MC < 11904
	public void enableTexture()
	{
		//#if MC >= 11500
		RenderSystem.enableTexture();
		//#else
		//$$ GlStateManager.enableTexture();
		//#endif
	}
	//#endif

	//#if MC < 11700
	public void enableAlphaTest()
	{
		//#if MC >= 11500
		RenderSystem.enableAlphaTest();
		//#else
		//$$ GlStateManager.enableAlphaTest();
		//#endif
	}
	//#endif

	public void depthMask(boolean mask)
	{
		//#if MC >= 11500
		RenderSystem.depthMask(mask);
		//#else
		//$$ GlStateManager.depthMask(mask);
		//#endif
	}

	//#if MC < 11600
	public void color4f(float red, float green, float blue, float alpha)
	{
		//#if MC >= 11500
		RenderSystem.color4f(red, green, blue, alpha);
		//#else
		//$$ GlStateManager.color4f(red, green, blue, alpha);
		//#endif
	}
	//#endif

	public void enableBlend()
	{
		//#if MC >= 11500
		RenderSystem.enableBlend();
		//#else
		//$$ GlStateManager.enableBlend();
		//#endif
	}

	public void blendFunc(
			//#if MC >= 11500
			GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor
			//#else
			//$$ GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor
			//#endif
	)
	{
		//#if MC >= 11500
		RenderSystem.blendFunc(srcFactor, dstFactor);
		//#else
		//$$ GlStateManager.blendFunc(srcFactor, dstFactor);
		//#endif
	}

	//#if MC < 11700
	public void disableLighting()
	{
		//#if MC >= 11500
		RenderSystem.disableLighting();
		//#else
		//$$ GlStateManager.disableLighting();
		//#endif
	}
	//#endif
}
