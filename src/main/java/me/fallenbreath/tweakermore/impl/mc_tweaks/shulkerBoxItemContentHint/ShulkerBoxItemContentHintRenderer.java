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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerBoxItemContentHint;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.render.ColorHolder;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import me.fallenbreath.tweakermore.util.render.context.GuiRenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;

//#if MC >= 12000
//$$ import net.minecraft.client.render.RenderLayer;
//#endif

//#if MC >= 11904
//$$ import net.minecraft.client.gui.DrawableHelper;
//#else
import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.shulkerItemContentHint.ItemRendererAccessor;
import com.mojang.blaze3d.vertex.BufferBuilder;
//#endif

//#if MC >= 11500
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.MultiBufferSource;
import com.mojang.blaze3d.vertex.PoseStack;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif

/**
 * mc1.21.6-: subproject 1.15.2 (main project)        <--------
 * mc1.21.6+: subproject 1.21.8
 */
public class ShulkerBoxItemContentHintRenderer
{
	// the display width of an item slot
	private static final int SLOT_WIDTH = 16;
	static final ThreadLocal<Boolean> isRendering = ThreadLocal.withInitial(() -> false);

	public static void render(
			//#if MC >= 11904
			//$$ MatrixStack matrices,
			//#endif
			//#if MC >= 12000
			//$$ DrawContext drawContext,
			//#else
			ItemRenderer itemRenderer,
			//#endif
			ItemStack itemStack, int x, int y
	)
	{
		ShulkerBoxItemContentHintCommon.Info info = ShulkerBoxItemContentHintCommon.prepareInformation(itemStack);
		if (!info.enabled)
		{
			return;
		}

		//#if MC >= 11904
		//$$ MatrixStack textMatrixStack = matrices;
		//$$ textMatrixStack.push();
		//#elseif MC >= 11500
		PoseStack textMatrixStack = new PoseStack();
		//#endif

		GuiRenderContext renderContext = RenderContext.gui(
				//#if MC >= 12000
				//$$ drawContext
				//#elseif MC >= 11904
				//$$ textMatrixStack
				//#elseif MC >= 11700
				//$$ info.allItemSame ? RenderSystem.getModelViewStack() : textMatrixStack
				//#elseif MC >= 11600
				//$$ textMatrixStack
				//#endif
		);

		RenderUtils.Scaler scaler = RenderUtils.createScaler(x, y + SLOT_WIDTH, info.scale);
		scaler.apply(renderContext);

		if (info.allItemSame || info.allItemSameIgnoreNbt)
		{
			renderMiniItem(
					renderContext,
					//#if MC >= 12000
					//$$ drawContext,
					//#else
					itemRenderer,
					//#endif
					info, x, y
			);
		}
		if (!info.allItemSame)
		{
			renderText(
					//#if MC >= 11500
					textMatrixStack,
					//#endif
					//#if MC >= 12000
					//$$ drawContext,
					//#endif
					//#if MC >= 11904
					//$$ 150 + 10,
					//#else
					itemRenderer.blitOffset + 150,
					//#endif
					info, x, y
			);
		}

		scaler.restore();
		//#if MC >= 11700 && MC < 11904
		//$$ RenderSystem.applyModelViewMatrix();
		//#endif

		boolean mixedBox = !info.allItemSameIgnoreNbt && !info.allItemSame;
		if (
				(!mixedBox || TweakerMoreConfigs.SHULKER_BOX_ITEM_CONTENT_HINT_SHOW_BAR_ON_MIXED.getBooleanValue())
				&& (0 < info.fillRatio && info.fillRatio < 1)
		)
		{
			renderBar(
					renderContext,
					//#if MC < 12000
					itemRenderer,
					//#endif
					info.fillRatio, x, y
			);
		}

		//#if MC >= 11904
		//$$ textMatrixStack.pop();
		//#endif
	}

	private static void renderMiniItem(
			GuiRenderContext renderContext,
			//#if MC >= 12000
			//$$ DrawContext drawContext,
			//#else
			ItemRenderer itemRenderer,
			//#endif
			ShulkerBoxItemContentHintCommon.Info info, int x, int y)
	{
		isRendering.set(true);

		//#if MC < 11904
		float zOffset = itemRenderer.blitOffset;
		//#endif

		try
		{
			//#if MC >= 11904
			//$$ renderContext.pushMatrix();
			//$$ renderContext.translateDirect(0, 0, 10);
			//#else
			itemRenderer.blitOffset += 10;
			// scale the z axis, so the lighting of the item can render correctly
			// see net.minecraft.client.render.item.ItemRenderer.renderGuiItemModel for z offset applying
			renderContext.scaleDirect(1, 1, info.scale);
			renderContext.translateDirect(0, 0, (100.0F + itemRenderer.blitOffset) * (1 / info.scale - 1));
			//#endif

			//#if MC >= 12000
			//$$ drawContext.drawItemWithoutEntity(info.stack, x, y);
			//#else
			// we do this manually so no need to care about extra z-offset modification of itemRenderer in its ItemRenderer#renderGuiItem
			itemRenderer.renderGuiItem(
					//#if MC >= 11904
					//$$ renderContext.getMcRawMatrixStack(),
					//#endif
					info.stack, x, y
			);
			//#endif
		}
		finally
		{
			isRendering.set(false);

			//#if MC >= 11904
			//$$ renderContext.popMatrix();
			//#else
			itemRenderer.blitOffset = zOffset;
			//#endif
		}
	}

	private static void renderText(
			//#if MC >= 11500
			PoseStack textMatrixStack,
			//#endif
			//#if MC >= 12000
			//$$ DrawContext drawContext,
			//#endif
			double zOffset, ShulkerBoxItemContentHintCommon.Info info, int x, int y
	)
	{
		String text = info.allItemSameIgnoreNbt ? "*" : "...";
		boolean putTextOnRight = info.allItemSameIgnoreNbt && info.scale <= 0.75;
		float width = RenderUtils.getRenderWidth(text);
		float height = RenderUtils.TEXT_HEIGHT;
		float textX = putTextOnRight ? x + SLOT_WIDTH + 0.5F : x + (SLOT_WIDTH - width) * 0.5F;
		float textY = putTextOnRight ? y + (SLOT_WIDTH - height) * 0.5F : y + SLOT_WIDTH - height - 3;
		double textScale = SLOT_WIDTH / height * 0.7 * (putTextOnRight ? 0.9 : 1);
		int textColor = 0xDDDDDD;

		RenderUtils.Scaler textScaler = RenderUtils.createScaler(textX + width * 0.5, textY + height * 0.5, textScale);
		textScaler.apply(RenderContext.gui(
				//#if MC >= 12000
				//$$ drawContext,
				//#endif
				//#if MC >= 11600
				//$$ textMatrixStack
				//#endif
		));
		Font textRenderer = Minecraft.getInstance().font;

		//#if MC >= 11500
		textMatrixStack.translate(0.0, 0.0, zOffset);
		MultiBufferSource.BufferSource immediate = RenderUtils.getVertexConsumer();
		textRenderer.drawInBatch(
				text,
				textX,
				textY,
				textColor,
				true,
				// TODO: check why this doesn't get remapped
				//#if MC >= 11800
				//$$ textMatrixStack.peek().getPositionMatrix(),
				//#else
				textMatrixStack.last().pose(),
				//#endif
				immediate,
				//#if MC >= 11904
				//$$ TextRenderer.TextLayerType.NORMAL,
				//#else
				false,
				//#endif
				0,
				0xF000F0
		);
		immediate.endBatch();
		//#else
		//$$ GlStateManager.disableLighting();
		//$$ GlStateManager.disableDepthTest();
		//$$ GlStateManager.disableBlend();
		//$$ textRenderer.drawWithShadow(text, textX, textY, textColor);
		//$$ GlStateManager.enableBlend();
		//$$ GlStateManager.enableLighting();
		//$$ GlStateManager.enableDepthTest();
		//#endif

		textScaler.restore();
	}

	@FunctionalInterface
	private interface GuiQuadDrawer
	{
		void draw(int x, int y, int width, int height, int color);
	}

	//#if 11600 <= MC && MC < 11700
	//$$ @SuppressWarnings("deprecation")
	//#endif
	private static void renderBar(
			GuiRenderContext renderContext,
			//#if MC < 12000
			ItemRenderer itemRenderer,
			//#endif
			double fillRatio, int x, int y)
	{
		final int HEIGHT = SLOT_WIDTH / 2;
		final int WIDTH = 1;

		x = x + SLOT_WIDTH - WIDTH;
		y = y + SLOT_WIDTH - HEIGHT;

		// ====== [begin] ref: net.minecraft.client.render.item.ItemRenderer#renderGuiItemOverlay ======
		// (mc1.20+) net.minecraft.client.gui.DrawContext.drawItemInSlot

		//#if MC >= 11500

		RenderGlobals.disableDepthTest();
		//#if MC < 11700
		RenderSystem.disableAlphaTest();
		//#endif
		//#if MC < 11904
		RenderSystem.disableBlend();
		RenderSystem.disableTexture();
		//#endif

		//#else
		//$$ GlStateManager.disableLighting();
		//$$ GlStateManager.disableDepthTest();
		//$$ GlStateManager.disableTexture();
		//$$ GlStateManager.disableAlphaTest();
		//$$ GlStateManager.disableBlend();
		//#endif

		int h = (int)Math.round(fillRatio * HEIGHT);
		int color = Mth.hsvToRgb((float)(fillRatio / 3), 1.0F, 1.0F);
		if (h == 0)
		{
			// make sure h > 0 so it's visible enough
			h = 1;
			// make the color darker
			ColorHolder holder = ColorHolder.of(color);
			holder.red /= 2;
			holder.green /= 2;
			holder.blue /= 2;
			color = holder.pack();
		}

		//#if MC >= 12000
		//$$ GuiQuadDrawer drawer = (x_, y_, width_, height_, color_) -> {
		//$$ 	// see net.minecraft.client.gui.DrawContext#drawItemInSlot
		//$$ 	renderContext.getGuiDrawer().fill(RenderLayer.getGuiOverlay(), x_, y_, x_ + width_, y_ + height_, color_ | 0xFF000000);
		//$$ };
		//#elseif MC >= 11904
		//$$ GuiQuadDrawer drawer = (x_, y_, width_, height_, color_) -> {
		//$$ 	DrawableHelper.fill(renderContext.getMcRawMatrixStack(), x_, y_, x_ + width_, y_ + height_, color_ | 0xFF000000);
		//$$ };
		//#else
		ItemRendererAccessor accessor = (ItemRendererAccessor)itemRenderer;
		Tesselator tessellator = Tesselator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuilder();
		GuiQuadDrawer drawer = (x_, y_, width_, height_, color_) -> {
			accessor.invokeRenderGuiQuad(bufferBuilder, x_, y_, width_ , height_,color_ >> 16 & 0xFF, color_ >> 8 & 0xFF, color_ & 0xFF, 0xFF);
		};
		//#endif

		drawer.draw( x, y, WIDTH, HEIGHT, 0x040404);
		drawer.draw(x, y + HEIGHT - h, WIDTH, h, color);

		//#if MC >= 11500

		RenderGlobals.enableDepthTest();
		//#if MC < 11700
		RenderSystem.enableAlphaTest();
		//#endif
		//#if MC < 11904
		RenderSystem.enableBlend();
		RenderSystem.enableTexture();
		//#endif

		//#else
		//$$ GlStateManager.enableBlend();
		//$$ GlStateManager.enableAlphaTest();
		//$$ GlStateManager.enableTexture();
		//$$ GlStateManager.enableLighting();
		//$$ GlStateManager.enableDepthTest();
		//#endif

		// ====== [end] ref: net.minecraft.client.render.item.ItemRenderer#renderGuiItemOverlay ======
	}
}
