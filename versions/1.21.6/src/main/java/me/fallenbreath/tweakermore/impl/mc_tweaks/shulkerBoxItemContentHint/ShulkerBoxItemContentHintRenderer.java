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
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderGlobals;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/**
 * mc1.21.6-: subproject 1.15.2 (main project)
 * mc1.21.6+: subproject 1.21.6        <--------
 */
public class ShulkerBoxItemContentHintRenderer
{
	private static final int SLOT_WIDTH = 16;
	static final ThreadLocal<Boolean> isRendering = ThreadLocal.withInitial(() -> false);

	public static void render(DrawContext drawContext, ItemStack itemStack, int x, int y)
	{
		ShulkerBoxItemContentHintCommon.Info info = ShulkerBoxItemContentHintCommon.prepareInformation(itemStack);
		if (!info.enabled)
		{
			return;
		}

		var renderContext = RenderContext.of(drawContext);
		var scaler = RenderUtils.createScaler(x, y + SLOT_WIDTH, info.scale);
		scaler.apply(renderContext);

		if (info.allItemSame || info.allItemSameIgnoreNbt)
		{
			renderMiniItem(renderContext, info, x, y);
		}
		if (!info.allItemSame)
		{
			renderText(renderContext, info, x, y);
		}
		scaler.restore();

		boolean mixedBox = !info.allItemSameIgnoreNbt && !info.allItemSame;
		if (
				(!mixedBox || TweakerMoreConfigs.SHULKER_BOX_ITEM_CONTENT_HINT_SHOW_BAR_ON_MIXED.getBooleanValue())
				&& (0 < info.fillRatio && info.fillRatio < 1)
		)
		{
			renderBar(renderContext, info.fillRatio, x, y);
		}
	}

	private static void renderMiniItem(RenderContext renderContext, ShulkerBoxItemContentHintCommon.Info info, int x, int y)
	{
		ItemStack stack = info.stack;
		if (stack.isEmpty())
		{
			return;
		}

		isRendering.set(true);
		try
		{
			renderContext.getGuiDrawer().drawItem(stack, x, y);
		}
		finally
		{
			isRendering.set(false);
		}
	}

	private static void renderText(RenderContext renderContext, ShulkerBoxItemContentHintCommon.Info info, int x, int y)
	{
		String text = info.allItemSameIgnoreNbt ? "*" : "...";
		boolean putTextOnRight = info.allItemSameIgnoreNbt && info.scale <= 0.75;
		float width = RenderUtils.getRenderWidth(text);
		float height = RenderUtils.TEXT_HEIGHT;
		float textX = putTextOnRight ? x + SLOT_WIDTH + 0.5F : x + (SLOT_WIDTH - width) * 0.5F;
		float textY = putTextOnRight ? y + (SLOT_WIDTH - height) * 0.5F : y + SLOT_WIDTH - height - 3;
		double textScale = SLOT_WIDTH / height * 0.7 * (putTextOnRight ? 0.9 : 1);
		int textColor = 0xFFDDDDDD;

		var scaler = RenderUtils.createScaler(textX + width * 0.5, textY + height * 0.5, textScale);
		scaler.apply(renderContext);

		var textRenderer = MinecraftClient.getInstance().textRenderer;
		renderContext.translate(textX, textY, 0);
		renderContext.getGuiDrawer().drawText(textRenderer, text, 0, 0, textColor, true);

		scaler.restore();
	}

	@FunctionalInterface
	private interface GuiQuadDrawer
	{
		void draw(int x, int y, int width, int height, int color);
	}

	private static void renderBar(RenderContext renderContext, double fillRatio, int x, int y)
	{
		final int HEIGHT = SLOT_WIDTH / 2;
		final int WIDTH = 1;

		x = x + SLOT_WIDTH - WIDTH;
		y = y + SLOT_WIDTH - HEIGHT;

		// ====== [begin] ref: net.minecraft.client.render.item.ItemRenderer#renderGuiItemOverlay ======
		// see net.minecraft.client.gui.DrawContext#drawItemBar

		RenderGlobals.disableDepthTest();

		int h = (int)Math.round(fillRatio * HEIGHT);
		int color = MathHelper.hsvToRgb((float)(fillRatio / 3), 1.0F, 1.0F);
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

		GuiQuadDrawer drawer = (x_, y_, width_, height_, color_) -> {
			renderContext.getGuiDrawer().fill(RenderPipelines.GUI, x_, y_, x_ + width_, y_ + height_, color_ | 0xFF000000);
		};

		drawer.draw( x, y, WIDTH, HEIGHT, 0x040404);
		drawer.draw(x, y + HEIGHT - h, WIDTH, h, color);

		RenderGlobals.enableDepthTest();

		// ====== [end] ref: net.minecraft.client.render.item.ItemRenderer#renderGuiItemOverlay ======
	}
}
