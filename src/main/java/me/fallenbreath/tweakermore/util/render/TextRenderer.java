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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Collections;
import java.util.List;

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
//#else
//$$ import net.minecraft.client.render.entity.EntityRenderDispatcher;
//#endif

public class TextRenderer
{
	private final List<TextHolder> lines;

	private Vec3d pos;
	private double fontSize;
	private double lineHeight;
	private int color;
	private int backgroundColor;
	private boolean shadow;
	private boolean seeThrough;

	private TextRenderer()
	{
		this.lines = Lists.newArrayList();
		this.fontSize = 0.02;
		// text with background has 1 extra height at the top and the bottom
		this.lineHeight = (RenderUtil.TEXT_HEIGHT + 1.0) / RenderUtil.TEXT_HEIGHT;
		this.color = 0xFFFFFFFF;
		this.backgroundColor = 0x00000000;
		this.shadow = false;
		this.seeThrough = false;
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
	 * - 1.14 doesn't support shadow = true
	 */
	@SuppressWarnings("UnnecessaryLocalVariable")
	public void render()
	{
		if (this.lines.isEmpty())
		{
			return;
		}
		MinecraftClient client = MinecraftClient.getInstance();
		Camera camera = client.gameRenderer.getCamera();
		if (camera.isReady() && client.getEntityRenderManager().gameOptions != null && client.player != null)
		{
			double x = this.pos.x;
			double y = this.pos.y;
			double z = this.pos.z;
			double camX = camera.getPos().x;
			double camY = camera.getPos().y;
			double camZ = camera.getPos().z;

			RenderContext renderContext = new RenderContext(
					//#if MC >= 11700
					//$$ RenderSystem.getModelViewStack()
					//#elseif MC >= 11600
					//$$ new MatrixStack()
					//#endif
			);

			renderContext.pushMatrix();
			renderContext.translate((float)(x - camX), (float)(y - camY), (float)(z - camZ));

			//#if MC >= 11500
			renderContext.multMatrix(
					//#if MC >= 11903
					//$$ new Matrix4f().rotation(camera.getRotation())
					//#else
					new Matrix4f(camera.getRotation())
					//#endif
			);
			//#endif

			renderContext.scale(this.fontSize, -this.fontSize, this.fontSize);

			//#if MC < 11500
			//$$ EntityRenderDispatcher entityRenderDispatcher = client.getEntityRenderManager();
			//$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraYaw, 0.0F, 1.0F, 0.0F);
			//$$ GlStateManager.rotatef(-entityRenderDispatcher.cameraPitch, 1.0F, 0.0F, 0.0F);
			//#endif

			//#if MC < 11700
			renderContext.disableLighting();
			//#endif

			if (this.seeThrough)
			{
				renderContext.disableDepthTest();
			}
			else
			{
				renderContext.enableDepthTest();
			}

			//#if MC < 11904
			renderContext.enableTexture();
			//#endif

			renderContext.depthMask(true);
			renderContext.scale(-1.0, 1.0, 1.0);

			int lineNum = this.lines.size();
			double maxTextWidth = this.lines.stream().mapToInt(TextHolder::getWidth).max().orElse(0);
			double totalTextX = maxTextWidth;
			double totalTextY = RenderUtil.TEXT_HEIGHT * lineNum + (this.lineHeight - 1) * (lineNum - 1);
			renderContext.translate(-totalTextX * 0.5, -totalTextY * 0.5, 0);

			//#if MC >= 11700
			//$$ RenderSystem.applyModelViewMatrix();
			//#else
			renderContext.enableAlphaTest();
			//#endif

			// enable transparent-able text rendering
			renderContext.enableBlend();
			renderContext.blendFunc(
					//#if MC >= 11500
					GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA
					//#else
					//$$ GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA
					//#endif
			);

			for (int i = 0; i < lineNum; i++)
			{
				TextHolder holder = this.lines.get(i);
				float textX = (float)((maxTextWidth - holder.getWidth()) / 2);
				float textY = (float)(RenderUtil.TEXT_HEIGHT * this.lineHeight * i);

				//#if MC >= 11500
				int backgroundColor = this.backgroundColor;
				while (true)
				{
					VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
					Matrix4f matrix4f = Rotation3.identity().getMatrix();
					client.textRenderer.draw(
							holder.text, textX, textY, this.color, this.shadow, matrix4f, immediate,
							//#if MC >= 11904
							//$$ this.seeThrough ? net.minecraft.client.font.TextRenderer.TextLayerType.NORMAL : net.minecraft.client.font.TextRenderer.TextLayerType.SEE_THROUGH,
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
				//$$ client.textRenderer.draw(holder.text, textX, textY, this.color);
				//#endif
			}

			//#if MC < 11600
			renderContext.color4f(1.0F, 1.0F, 1.0F, 1.0F);
			//#endif

			renderContext.enableDepthTest();
			renderContext.popMatrix();
		}
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

	public TextRenderer lineHeight(double lineHeight)
	{
		this.lineHeight = lineHeight;
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

	public TextRenderer fontSize(double fontSize)
	{
		this.fontSize = fontSize;
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
}
