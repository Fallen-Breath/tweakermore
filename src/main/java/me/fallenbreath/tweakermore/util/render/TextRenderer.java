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

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

//#if MC >= 11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//#endif

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//$$ import net.minecraft.text.OrderedText;
//#endif

//#if MC >= 11500
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.Rotation3;
//#endif

public class TextRenderer
{
	public static final double DEFAULT_FONT_SCALE = 0.025;

	private final List<TextHolder> lines;
	private Vec3d pos;
	private double shiftX;
	private double shiftY;
	private double fontScale;
	private double lineHeightRatio;
	private int color;
	private int backgroundColor;
	private boolean shadow;
	private boolean seeThrough;
	private HorizontalAlignment horizontalAlignment;
	private VerticalAlignment verticalAlignment;

	private TextRenderer()
	{
		this.lines = Lists.newArrayList();
		this.shiftX = this.shiftY = 0.0;
		this.fontScale = DEFAULT_FONT_SCALE;
		this.lineHeightRatio = 1.0 * RenderUtil.TEXT_LINE_HEIGHT / RenderUtil.TEXT_HEIGHT;
		this.color = 0xFFFFFFFF;
		this.backgroundColor = 0x00000000;
		this.shadow = false;
		this.seeThrough = false;
		this.horizontalAlignment = HorizontalAlignment.DEFAULT;
		this.verticalAlignment = VerticalAlignment.DEFAULT;
	}

	public static TextRenderer create()
	{
		return new TextRenderer();
	}

	/*
	 * ============================
	 *         Render Impl
	 * ============================
	 */

	/**
	 * Draw given lines with centered format
	 * Reference: {@link DebugRenderer#drawString(String, double, double, double, int, float, boolean, float, boolean)}
	 * Note:
	 * - shadow=true + seeThrough=false might result in weird rendering
	 */
	@SuppressWarnings("UnnecessaryLocalVariable")
	public void render()
	{
		if (this.lines.isEmpty())
		{
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		RenderContext renderContext = RenderContext.of(
				//#if MC >= 11700
				//$$ RenderSystem.getModelViewStack()
				//#elseif MC >= 11600
				//$$ new MatrixStack()
				//#endif
		);

		InWorldPositionTransformer positionTransformer = new InWorldPositionTransformer(this.pos);
		positionTransformer.apply(renderContext);
		{
			renderContext.scale(-this.fontScale, -this.fontScale, this.fontScale);

			//#if MC < 11700
			RenderGlobals.disableLighting();
			//#endif

			if (this.seeThrough)
			{
				RenderGlobals.disableDepthTest();
			}
			else
			{
				RenderGlobals.enableDepthTest();
			}

			//#if MC < 11904
			RenderGlobals.enableTexture();
			//#endif

			RenderGlobals.depthMask(true);

			int lineNum = this.lines.size();
			double maxTextWidth = this.lines.stream().mapToInt(TextHolder::getWidth).max().orElse(0);
			double totalTextWidth = maxTextWidth;
			double totalTextHeight = RenderUtil.TEXT_HEIGHT * lineNum + (this.lineHeightRatio - 1) * (lineNum - 1);
			renderContext.translate(this.horizontalAlignment.getTranslateX(totalTextWidth), this.verticalAlignment.getTranslateY(totalTextHeight), 0);
			renderContext.translate(this.shiftX, this.shiftY, 0);

			//#if MC >= 11700
			//$$ RenderSystem.applyModelViewMatrix();
			//#else
			RenderGlobals.enableAlphaTest();
			//#endif

			// enable transparent-able text rendering
			RenderGlobals.enableBlend();
			RenderGlobals.blendFunc(
					//#if MC >= 11500
					GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
					//#else
					//$$ GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
					//#endif
			);

			for (int i = 0; i < lineNum; i++)
			{
				TextHolder holder = this.lines.get(i);
				float textX = (float)this.horizontalAlignment.getTextX(maxTextWidth, holder.getWidth());
				float textY = (float)(this.getLineHeight() * i);

				//#if MC >= 11500
				int backgroundColor = this.backgroundColor;
				while (true)
				{
					VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
					Matrix4f matrix4f = Rotation3.identity().getMatrix();
					client.textRenderer.draw(
							holder.text, textX, textY, this.color, this.shadow, matrix4f, immediate,
							//#if MC >= 11904
							//$$ this.seeThrough ? net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH : net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL,
							//#else
							this.seeThrough,
							//#endif
							backgroundColor, 0xF000F0
					);
					immediate.draw();

					// draw twice when having background
					if (backgroundColor == 0)
					{
						break;
					}
					else
					{
						backgroundColor = 0;
					}
				}
				//#else
				//$$ if (this.shadow)
				//$$ {
				//$$ 	client.textRenderer.drawWithShadow(holder.text, textX, textY, this.color);
				//$$ }
				//$$ else
				//$$ {
				//$$ 	client.textRenderer.draw(holder.text, textX, textY, this.color);
				//$$ }
				//#endif
			}

			//#if MC < 11600
			RenderGlobals.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			//#endif
			//TODO check color4f, see if it can replace blendFunc

			//#if MC < 11904
			RenderGlobals.enableDepthTest();
			//#endif
		}
		positionTransformer.restore();
		//#if MC >= 11700
		//$$ RenderSystem.applyModelViewMatrix();
		//#endif
	}

	/**
	 * ============================
	 *        Field Setters
	 * ============================
	 */

	private TextRenderer addLines(TextHolder ...lines)
	{
		Collections.addAll(this.lines, lines);
		return this;
	}

	private TextRenderer setLines(TextHolder ...lines)
	{
		this.lines.clear();
		this.addLines(lines);
		return this;
	}

	//#if MC >= 11600
	//$$ public TextRenderer text(OrderedText text)
	//$$ {
	//$$	return this.setLines(TextHolder.of(text));
	//$$ }
	//#endif

	public TextRenderer text(String text)
	{
		return this.setLines(TextHolder.of(text));
	}

	public TextRenderer text(Text text)
	{
		return this.setLines(TextHolder.of(text));
	}

	//#if MC >= 11600
	//$$ public TextRenderer addLine(OrderedText text)
	//$$ {
	//$$ 	return this.addLines(TextHolder.of(text));
	//$$ }
	//#endif

	public TextRenderer addLine(String text)
	{
		return this.addLines(TextHolder.of(text));
	}

	public TextRenderer addLine(Text text)
	{
		return this.addLines(TextHolder.of(text));
	}

	public TextRenderer lineHeightRatio(double lineHeightRatio)
	{
		this.lineHeightRatio = lineHeightRatio;
		return this;
	}

	public TextRenderer at(Vec3d vec3d)
	{
		this.pos = vec3d;
		return this;
	}

	public TextRenderer at(double x, double y, double z)
	{
		return this.at(new Vec3d(x, y, z));
	}

	public TextRenderer atCenter(BlockPos blockPos)
	{
		return this.at(PositionUtil.centerOf(blockPos));
	}

	/**
	 * Shift the text in the render length unit
	 */
	public TextRenderer shift(double x, double y)
	{
		this.shiftX = x;
		this.shiftY = y;
		return this;
	}

	public TextRenderer fontScale(double fontScale)
	{
		this.fontScale = fontScale;
		return this;
	}

	/**
	 * @param color the text color in the 0xAARRGGBB format
	 */
	public TextRenderer color(int color)
	{
		this.color = color;
		return this;
	}

	/**
	 * @param backgroundColor the background color in the 0xAARRGGBB format
	 */
	public TextRenderer bgColor(int backgroundColor)
	{
		this.backgroundColor = backgroundColor;
		return this;
	}

	/**
	 * @param color the text color in the 0xAARRGGBB format
	 * @param backgroundColor the background color in the 0xAARRGGBB format
	 */
	public TextRenderer color(int color, int backgroundColor)
	{
		this.color(color);
		this.bgColor(backgroundColor);
		return this;
	}

	public TextRenderer shadow(boolean shadow)
	{
		this.shadow = shadow;
		return this;
	}

	public TextRenderer shadow()
	{
		return this.shadow(true);
	}

	public TextRenderer seeThrough(boolean seeThrough)
	{
		this.seeThrough = seeThrough;
		return this;
	}

	public TextRenderer seeThrough()
	{
		return this.seeThrough(true);
	}

	public TextRenderer align(HorizontalAlignment horizontalAlignment)
	{
		this.horizontalAlignment = horizontalAlignment;
		return this;
	}

	public TextRenderer align(VerticalAlignment verticalAlignment)
	{
		this.verticalAlignment = verticalAlignment;
		return this;
	}

	/**
	 * ============================
	 *           Getters
	 * ============================
	 */

	public double getLineHeight()
	{
		return RenderUtil.TEXT_HEIGHT * this.lineHeightRatio;
	}

	/**
	 * ============================
	 */

	private static class TextHolder
	{
		//#if MC >= 11600
		//$$ public final OrderedText text;
		//#else
		public final String text;
		//#endif

		private TextHolder(
				//#if MC >= 11600
				//$$ OrderedText text
				//#else
				String text
				//#endif
		)
		{
			this.text = text;
		}

		public static TextHolder of(
				//#if MC >= 11600
				//$$ OrderedText text
				//#else
				String text
				//#endif
		)
		{
			return new TextHolder(text);
		}

		//#if MC >= 11600
		//$$ public static TextHolder of(String text)
		//$$ {
		//$$ 	return of(TextRenderingUtil.string2orderedText(text));
		//$$ }
		//#endif

		public static TextHolder of(Text text)
		{
			//#if MC >= 11600
			//$$ return new TextHolder(text.asOrderedText());
			//#else
			return new TextHolder(text.asFormattedString());
			//#endif
		}

		public int getWidth()
		{
			return RenderUtil.getRenderWidth(this.text);
		}
	}

	public enum HorizontalAlignment
	{
		LEFT(w -> 0.0, (w, tw) -> 0.0),					// [-x]  ^Text  [+x]
		RIGHT(w -> -w, (w, tw) -> w - tw),				// [-x]  Text^  [+x]
		CENTER(w -> -0.5 * w, (w, tw) -> 0.5 * (w - tw));	// [-x]  Te^xt  [+x]

		public static final HorizontalAlignment DEFAULT = CENTER;

		private final Function<Double, Double> trMapper;
		private final BiFunction<Double, Double, Double> posMapper;

		HorizontalAlignment(Function<Double, Double> trMapper, BiFunction<Double, Double, Double> posMapper)
		{
			this.trMapper = trMapper;
			this.posMapper = posMapper;
		}

		public double getTranslateX(double width)
		{
			return this.trMapper.apply(width);
		}

		public double getTextX(double width, double textWidth)
		{
			return this.posMapper.apply(width, textWidth);
		}
	}

	public enum VerticalAlignment
	{
		TOP(h -> 0.0),			// [-y]  ^Text  [+y]
		BOTTOM(h -> -h),			// [-y]  Text^  [+y]
		CENTER(h -> -0.5 * h);	// [-y]  Te^xt  [+y]

		private final Function<Double, Double> trMapper;

		public static final VerticalAlignment DEFAULT = CENTER;

		VerticalAlignment(Function<Double, Double> trMapper)
		{
			this.trMapper = trMapper;
		}

		public double getTranslateY(double height)
		{
			return this.trMapper.apply(height);
		}
	}
}
