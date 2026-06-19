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
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import me.fallenbreath.tweakermore.util.PositionUtils;
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContextImpl;
import me.fallenbreath.tweakermore.util.render.matrix.JomlMatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.StagedVertexBuffer;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;

public class TextRenderer
{
	public static final double DEFAULT_FONT_SCALE = 0.025;
	private static final double DEFAULT_LINE_HEIGHT_RATIO = 1.0 * RenderUtils.TEXT_LINE_HEIGHT / RenderUtils.TEXT_HEIGHT;
	private static final int FULL_BRIGHT_LIGHT = 0xF000F0;

	private final List<TextHolder> lines;
	private Vec3 pos;
	private double shiftX;
	private double shiftY;
	private double fontScale;
	private double lineHeightRatio = DEFAULT_LINE_HEIGHT_RATIO;
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

	private static WorldRenderContext createGlobalMatrixRenderContext()
	{
		return new WorldRenderContextImpl(new JomlMatrixStack(RenderSystem.getModelViewStack()));
	}

	/**
	 * Draw given lines with centered format
	 * Reference: {@link DebugRenderer#drawString(String, double, double, double, int, float, boolean, float, boolean)}
	 * Note:
	 * - shadow=true + seeThrough=false might result in weird rendering
	 */
	public void render()
	{
		if (this.lines.isEmpty())
		{
			return;
		}

		WorldRenderContext renderContext = createGlobalMatrixRenderContext();
		Minecraft mc = Minecraft.getInstance();
		InWorldPositionTransformer positionTransformer = new InWorldPositionTransformer(this.pos);
		positionTransformer.apply(renderContext);
		try
		{
			renderContext.scale(this.fontScale * RenderUtils.getSizeScalingXSign(), -this.fontScale, this.fontScale);

			if (this.seeThrough)
			{
				RenderGlobals.disableDepthTest();
			}
			else
			{
				RenderGlobals.enableDepthTest();
			}

			RenderGlobals.depthMask(true);

			int lineNum = this.lines.size();
			double maxTextWidth = this.lines.stream().mapToInt(TextHolder::getWidth).max().orElse(0);
			double totalTextWidth = maxTextWidth;
			double totalTextHeight = RenderUtils.TEXT_HEIGHT * lineNum + (this.lineHeightRatio - 1) * (lineNum - 1);
			renderContext.translate(this.horizontalAlignment.getTranslateX(totalTextWidth), this.verticalAlignment.getTranslateY(totalTextHeight), 0);
			renderContext.translate(this.shiftX, this.shiftY, 0);

			RenderGlobals.enableBlend();
			RenderGlobals.blendFuncForAlpha();

			Font.DisplayMode displayMode = this.seeThrough ? Font.DisplayMode.SEE_THROUGH : Font.DisplayMode.NORMAL;
			try (ImmediateTextDrawer drawer = new ImmediateTextDrawer(displayMode, FULL_BRIGHT_LIGHT))
			{
				for (int i = 0; i < lineNum; i++)
				{
					TextHolder holder = this.lines.get(i);
					float textX = (float)this.horizontalAlignment.getTextX(maxTextWidth, holder.getWidth());
					float textY = (float)(this.getLineHeight() * i);

					int backgroundColor = this.backgroundColor;
					while (true)
					{
						drawer.append(mc.font.prepareText(holder.text, textX, textY, this.color, this.shadow, false, backgroundColor));
						if (backgroundColor == 0)
						{
							break;
						}
						backgroundColor = 0;
					}
				}
				drawer.draw();
			}
		}
		finally
		{
			positionTransformer.restore();
		}
	}

	private TextRenderer addLines(TextHolder... lines)
	{
		Collections.addAll(this.lines, lines);
		return this;
	}

	private TextRenderer setLines(TextHolder... lines)
	{
		this.lines.clear();
		this.addLines(lines);
		return this;
	}

	public TextRenderer text(FormattedCharSequence text)
	{
		return this.setLines(TextHolder.of(text));
	}

	public TextRenderer text(String text)
	{
		return this.setLines(TextHolder.of(text));
	}

	public TextRenderer text(Component text)
	{
		return this.setLines(TextHolder.of(text));
	}

	public TextRenderer addLine(FormattedCharSequence text)
	{
		return this.addLines(TextHolder.of(text));
	}

	public TextRenderer addLine(String text)
	{
		return this.addLines(TextHolder.of(text));
	}

	public TextRenderer addLine(Component text)
	{
		return this.addLines(TextHolder.of(text));
	}

	public TextRenderer lineHeightRatio(double lineHeightRatio)
	{
		this.lineHeightRatio = lineHeightRatio;
		return this;
	}

	public TextRenderer at(Vec3 vec3d)
	{
		this.pos = vec3d;
		return this;
	}

	public TextRenderer at(double x, double y, double z)
	{
		return this.at(new Vec3(x, y, z));
	}

	public TextRenderer atCenter(BlockPos blockPos)
	{
		return this.at(PositionUtils.centerOf(blockPos));
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

	public double getLineHeight()
	{
		return RenderUtils.TEXT_HEIGHT * this.lineHeightRatio;
	}

	public Vec3 getPos()
	{
		return this.pos;
	}

	private static class ImmediateTextDrawer implements Font.GlyphVisitor, AutoCloseable
	{
		private static final Matrix4fc IDENTITY_MATRIX = new Matrix4f();

		private final Font.DisplayMode displayMode;
		private final int lightCoords;
		private final StagedVertexBuffer stagedBuffer = new StagedVertexBuffer(() -> "TweakerMore TextRenderer", 65536);
		private final Map<RenderType, StagedVertexBuffer.Draw> draws = new LinkedHashMap<>();

		private ImmediateTextDrawer(Font.DisplayMode displayMode, int lightCoords)
		{
			this.displayMode = displayMode;
			this.lightCoords = lightCoords;
		}

		public void append(Font.PreparedText preparedText)
		{
			preparedText.visit(this);
		}

		@Override
		public void acceptRenderable(TextRenderable renderable)
		{
			RenderType renderType = renderable.renderType(this.displayMode);
			StagedVertexBuffer.Draw draw = this.draws.computeIfAbsent(renderType, this::createDraw);
			renderable.render(IDENTITY_MATRIX, this.stagedBuffer.getVertexBuilder(draw), this.lightCoords, false);
		}

		public void draw()
		{
			if (this.draws.isEmpty())
			{
				return;
			}

			this.stagedBuffer.upload();
			this.draws.forEach((renderType, draw) -> {
				StagedVertexBuffer.ExecuteInfo executeInfo = this.stagedBuffer.getExecuteInfo(draw);
				if (executeInfo != null)
				{
					renderType.prepare().drawFromBuffer(executeInfo);
				}
			});
			this.stagedBuffer.endDraw();
		}

		@Override
		public void close()
		{
			this.stagedBuffer.close();
		}

		private StagedVertexBuffer.Draw createDraw(RenderType renderType)
		{
			return this.stagedBuffer.appendDraw(
					renderType.format(),
					renderType.primitiveTopology(),
					renderType.sortOnUpload() ? RenderSystem.getProjectionType().vertexSorting() : null
			);
		}
	}

	private static class TextHolder
	{
		public final FormattedCharSequence text;

		private TextHolder(FormattedCharSequence text)
		{
			this.text = text;
		}

		public static TextHolder of(FormattedCharSequence text)
		{
			return new TextHolder(text);
		}

		public static TextHolder of(String text)
		{
			return of(TextRenderingUtil.string2orderedText(text));
		}

		public static TextHolder of(Component text)
		{
			return new TextHolder(text.getVisualOrderText());
		}

		public int getWidth()
		{
			return RenderUtils.getRenderWidth(this.text);
		}
	}

	public enum HorizontalAlignment
	{
		LEFT(w -> 0.0, (w, tw) -> 0.0),
		RIGHT(w -> -w, (w, tw) -> w - tw),
		CENTER(w -> -0.5 * w, (w, tw) -> 0.5 * (w - tw));

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
		TOP(h -> 0.0),
		BOTTOM(h -> -h),
		CENTER(h -> -0.5 * h);

		public static final VerticalAlignment DEFAULT = CENTER;

		private final Function<Double, Double> trMapper;

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
