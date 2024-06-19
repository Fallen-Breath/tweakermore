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

import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;

import java.util.Objects;

//#if MC >= 12100
//$$ import net.minecraft.client.render.RenderLayer;
//$$ import net.minecraft.client.util.BufferAllocator;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.text.OrderedText;
//#endif

//#if MC >= 11500
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
//#endif

public class RenderUtil
{
	private static final TextRenderer TEXT_RENDERER = MinecraftClient.getInstance().textRenderer;

	public static final int TEXT_HEIGHT = TEXT_RENDERER.fontHeight;
	// text with background has 1 extra height at the top
	public static final int TEXT_LINE_HEIGHT = RenderUtil.TEXT_HEIGHT + 1;
	public static float tickDelta = 1.0F;

	public static int getRenderWidth(String text)
	{
		return TEXT_RENDERER.getStringWidth(text);
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
	//$$ public static int getRenderWidth(OrderedText text)
	//$$ {
	//$$ 	return TEXT_RENDERER.getWidth(text);
	//$$ }
	//#endif

	//#if MC >= 11500
	public static VertexConsumerProvider.Immediate getVertexConsumer()
	{
		//#if MC >= 12100
		//$$ return MinecraftClient.getInstance().getBufferBuilders().getEntityVertexConsumers();
		//#else
		return VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
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

		private RenderContext renderContext;

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

		public void apply(RenderContext renderContext)
		{
			this.renderContext = renderContext;
			this.renderContext.pushMatrix();
			this.renderContext.translate(-anchorX * factor, -anchorY * factor, 0);
			this.renderContext.scale(factor, factor, 1);
			this.renderContext.translate(anchorX / factor, anchorY / factor, 0);
		}

		public void restore()
		{
			if (this.renderContext == null)
			{
				throw new RuntimeException("RenderUtil.Scaler: Calling restore before calling apply");
			}
			this.renderContext.popMatrix();
		}

		public RenderContext getRenderContext()
		{
			return Objects.requireNonNull(this.renderContext);
		}
	}
}
