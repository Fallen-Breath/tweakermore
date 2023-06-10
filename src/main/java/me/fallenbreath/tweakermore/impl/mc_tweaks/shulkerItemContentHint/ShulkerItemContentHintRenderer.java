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

package me.fallenbreath.tweakermore.impl.mc_tweaks.shulkerItemContentHint;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.InventoryUtil;
import me.fallenbreath.tweakermore.util.ItemUtil;
import me.fallenbreath.tweakermore.util.render.ColorHolder;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.RenderUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;

//#if MC >= 12000
//$$ import net.minecraft.client.render.RenderLayer;
//#endif

//#if MC >= 11904
//$$ import net.minecraft.client.gui.DrawableHelper;
//#else
import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.shulkerItemContentHint.ItemRendererAccessor;
import net.minecraft.client.render.BufferBuilder;
//#endif

//#if MC >= 11500
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
//#else
//$$ import com.mojang.blaze3d.platform.GlStateManager;
//#endif

public class ShulkerItemContentHintRenderer
{
	// the display width of an item slot
	private static final int SLOT_WIDTH = 16;
	private static final ThreadLocal<Boolean> isRendering = ThreadLocal.withInitial(() -> false);

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
		if (!TweakerMoreConfigs.SHULKER_ITEM_CONTENT_HINT.getBooleanValue())
		{
			return;
		}
		if (isRendering.get())  // no hint if it's rendering our hint item
		{
			return;
		}
		if (!ItemUtil.isShulkerBox(itemStack.getItem()))
		{
			return;
		}
		Optional<DefaultedList<ItemStack>> stackList = InventoryUtil.getStoredItems(itemStack);
		if (!stackList.isPresent())
		{
			return;
		}

		ItemStack std = null;
		boolean useQuestionMark = false;
		for (ItemStack stack : stackList.get())
		{
			if (!stack.isEmpty())
			{
				if (std == null)
				{
					std = stack;
				}
				// to be equal: item type equals, item nbt equals
				//#if MC >= 12000
				//$$ else if (!ItemStack.canCombine(stack, std))
				//#else
				else if (!(ItemStack.areItemsEqual(stack, std) && ItemStack.areTagsEqual(stack, std)))
				//#endif
				{
					useQuestionMark = true;
					break;
				}
			}
		}
		if (std == null)
		{
			return;
		}

		double scale = TweakerMoreConfigs.SHULKER_ITEM_CONTENT_HINT_SCALE.getDoubleValue();

		if (scale <= 0)
		{
			return;
		}

		//#if MC >= 11904
		//$$ MatrixStack textMatrixStack = matrices;
		//$$ textMatrixStack.push();
		//#elseif MC >= 11500
		MatrixStack textMatrixStack = new MatrixStack();
		//#endif

		RenderContext renderContext = RenderContext.of(
				//#if MC >= 12000
				//$$ drawContext
				//#elseif MC >= 11904
				//$$ textMatrixStack
				//#elseif MC >= 11700
				//$$ useQuestionMark ? textMatrixStack : RenderSystem.getModelViewStack()
				//#elseif MC >= 11600
				//$$ textMatrixStack
				//#endif
		);

		RenderUtil.Scaler scaler = RenderUtil.createScaler(x, y + SLOT_WIDTH, scale);
		scaler.apply(renderContext);

		if (useQuestionMark)
		{
			String text = "...";
			float width = RenderUtil.getRenderWidth(text);
			float height = RenderUtil.TEXT_HEIGHT;
			float textX = x + (SLOT_WIDTH - width) * 0.5F;
			float textY = y + SLOT_WIDTH - height - 3;
			int color = 0xDDDDDD;

			RenderUtil.Scaler textScaler = RenderUtil.createScaler(textX + width * 0.5, textY + height * 0.5, SLOT_WIDTH / height * 0.7);
			textScaler.apply(RenderContext.of(
					//#if MC >= 11600
					//$$ textMatrixStack
					//#endif
			));
			TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

			//#if MC >= 11500
			textMatrixStack.translate(
					0.0, 0.0,
					//#if MC >= 11904
					//$$ 150 + 10
					//#else
					itemRenderer.zOffset + 150
					//#endif
			);
			VertexConsumerProvider.Immediate immediate = VertexConsumerProvider.immediate(Tessellator.getInstance().getBuffer());
			textRenderer.draw(
					text,
					textX,
					textY,
					color,
					true,
					// TODO: check why this doesn't get remapped
					//#if MC >= 11800
					//$$ textMatrixStack.peek().getPositionMatrix(),
					//#else
					textMatrixStack.peek().getModel(),
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
			immediate.draw();
			//#else
			//$$ GlStateManager.disableLighting();
			//$$ GlStateManager.disableDepthTest();
			//$$ GlStateManager.disableBlend();
			//$$ textRenderer.drawWithShadow(text, textX, textY, color);
			//$$ GlStateManager.enableBlend();
			//$$ GlStateManager.enableLighting();
			//$$ GlStateManager.enableDepthTest();
			//#endif

			textScaler.restore();
		}
		else
		{
			isRendering.set(true);

			//#if MC < 11904
			float zOffset = itemRenderer.zOffset;
			//#endif

			try
			{
				//#if MC >= 11904
				//$$ matrices.push();
				//$$ matrices.translate(0, 0, 10);
				//#else
				itemRenderer.zOffset += 10;
				// scale the z axis, so the lighting of the item can render correctly
				// see net.minecraft.client.render.item.ItemRenderer.renderGuiItemModel for z offset applying
				renderContext.scale(1, 1, scale);
				renderContext.translate(0, 0, (100.0F + itemRenderer.zOffset) * (1 / scale - 1));
				//#endif

				//#if MC >= 12000
				//$$ drawContext.drawItemWithoutEntity(std, x, y);
				//#else
				// we do this manually so no need to care about extra z-offset modification of itemRenderer in its ItemRenderer#renderGuiItem
				itemRenderer.renderGuiItemIcon(
						//#if MC >= 11904
						//$$ matrices,
						//#endif
						std, x, y
				);
				//#endif
			}
			finally
			{
				isRendering.set(false);

				//#if MC >= 11904
				//$$ matrices.pop();
				//#else
				itemRenderer.zOffset = zOffset;
				//#endif
			}
		}

		scaler.restore();
		//#if MC >= 11700 && MC < 11904
		//$$ RenderSystem.applyModelViewMatrix();
		//#endif

		if (!useQuestionMark)
		{
			renderBar(
					renderContext,
					//#if MC < 12000
					itemRenderer,
					//#endif
					itemStack, x, y, stackList.get()
			);
		}

		//#if MC >= 11904
		//$$ textMatrixStack.pop();
		//#endif
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
			RenderContext renderContext,
			//#if MC < 12000
			ItemRenderer itemRenderer,
			//#endif
			ItemStack shulkerItem, int x, int y, DefaultedList<ItemStack> stackList)
	{
		// 1. checks & calc

		int slotAmount = InventoryUtil.getInventorySlotAmount(shulkerItem);
		if (slotAmount == -1)
		{
			return;
		}
		double sum = 0;
		for (ItemStack stack : stackList)
		{
			sum += 1.0D * stack.getCount() / stack.getMaxCount();
		}

		double ratio = sum / slotAmount;
		if (ratio <= 0 || ratio >= 1)
		{
			return;
		}

		// 2. render

		final int HEIGHT = SLOT_WIDTH / 2;
		final int WIDTH = 1;

		x = x + SLOT_WIDTH - WIDTH;
		y = y + SLOT_WIDTH - HEIGHT;

		// ====== [begin] ref: net.minecraft.client.render.item.ItemRenderer#renderGuiItemOverlay ======
		// (mc1.20+) net.minecraft.client.gui.DrawContext.drawItemInSlot

		//#if MC >= 11500

		RenderSystem.disableDepthTest();
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

		int h = (int)Math.round(ratio * HEIGHT);
		int color = MathHelper.hsvToRgb((float)(ratio / 3), 1.0F, 1.0F);
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
		//$$ 	DrawableHelper.fill(renderContext.getMatrixStack(), x_, y_, x_ + width_, y_ + height_, color_ | 0xFF000000);
		//$$ };
		//#else
		ItemRendererAccessor accessor = (ItemRendererAccessor)itemRenderer;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		GuiQuadDrawer drawer = (x_, y_, width_, height_, color_) -> {
			accessor.invokeRenderGuiQuad(bufferBuilder, x_, y_, width_ , height_,color_ >> 16 & 0xFF, color_ >> 8 & 0xFF, color_ & 0xFF, 0xFF);
		};
		//#endif

		drawer.draw( x, y, WIDTH, HEIGHT, 0x040404);
		drawer.draw(x, y + HEIGHT - h, WIDTH, h, color);

		//#if MC >= 11500

		RenderSystem.enableDepthTest();
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
