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

import me.fallenbreath.tweakermore.util.render.context.GuiRenderContext;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

import java.util.Objects;

//#if MC >= 11904
//$$ import org.joml.Matrix3x2f;
//#endif

//#if MC >= 12100
//$$ import net.minecraft.client.render.RenderLayer;
//$$ import com.mojang.blaze3d.vertex.ByteBufferBuilder;
//#endif

//#if MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//$$ import net.minecraft.util.FormattedCharSequence;
//#endif

//#if MC >= 11500
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.MultiBufferSource;
//#endif

public class RenderUtils
{
	private static final Font TEXT_RENDERER = Minecraft.getInstance().font;

	public static final int TEXT_HEIGHT = TEXT_RENDERER.lineHeight;
	// text with background has 1 extra height at the top
	public static final int TEXT_LINE_HEIGHT = RenderUtils.TEXT_HEIGHT + 1;
	public static float tickDelta = 1.0F;

	public static int getRenderWidth(String text)
	{
		return TEXT_RENDERER.width(text);
	}

	public static int getSizeScalingXSign()
	{
		// stupid change in 24w21a
		//#if MC >= 12100
		//$$ return 1;
		//#else
		return -1;
		//#endif
	}

	//#if MC >= 11600
	//$$ public static int getRenderWidth(FormattedCharSequence text)
	//$$ {
	//$$ 	return TEXT_RENDERER.getWidth(text);
	//$$ }
	//#endif

	//#if MC >= 11500
	public static MultiBufferSource.BufferSource getVertexConsumer()
	{
		//#if MC >= 12100
		//$$ return Minecraft.getInstance().getBufferBuilders().getEntityVertexConsumers();
		//#else
		return MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
		//#endif
	}
	//#endif

	public static Scaler createScaler(double anchorX, double anchorY, double factor)
	{
		return new Scaler(anchorX, anchorY, factor);
	}

	public static class Scaler
	{
		private final double anchorX;
		private final double anchorY;
		private final double factor;

		private Runnable restoreFunc;

		private Scaler(double anchorX, double anchorY, double factor)
		{
			this.anchorX = anchorX;
			this.anchorY = anchorY;
			if (factor <= 0)
			{
				throw new IllegalArgumentException("factor should be greater than 0, but " + factor + " found");
			}
			this.factor = factor;
		}

		public double getScaleFactor()
		{
			return this.factor;
		}

		public void apply(WorldRenderContext renderContext)
		{
			renderContext.pushMatrix();
			renderContext.translate(-anchorX * factor, -anchorY * factor, 0);
			renderContext.scale(factor, factor, 1);
			renderContext.translate(anchorX / factor, anchorY / factor, 0);
			this.restoreFunc = renderContext::popMatrix;
		}

		public void apply(GuiRenderContext renderContext)
		{
			renderContext.pushMatrix();
			renderContext.translate(-anchorX * factor, -anchorY * factor);
			renderContext.scale(factor, factor);
			renderContext.translate(anchorX / factor, anchorY / factor);
			this.restoreFunc = renderContext::popMatrix;
		}

		//#if MC >= 11904
		//$$ public void apply(Matrix3x2f matrix)
		//$$ {
		//$$ 	matrix.translate((float)(-anchorX * factor), (float)(-anchorY * factor));
		//$$ 	matrix.scale((float)factor, (float)factor);
		//$$ 	matrix.translate((float)(anchorX / factor), (float)(anchorY / factor));
		//$$ }
		//#endif

		public void restore()
		{
			if (this.restoreFunc == null)
			{
				throw new RuntimeException("RenderUtils.Scaler: Calling restore before calling apply");
			}
			this.restoreFunc.run();
			this.restoreFunc = null;
		}
	}
}
