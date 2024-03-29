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

import fi.dy.masa.malilib.interfaces.IRenderer;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;

//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#endif

//#if MC >= 11700
//$$ import com.mojang.blaze3d.systems.RenderSystem;
//$$ import net.minecraft.util.math.Matrix4f;
//#endif

//#if MC >= 11500
import net.minecraft.client.util.math.MatrixStack;
//#endif

public interface TweakerMoreIRenderer extends IRenderer
{
	/**
	 * Deprecated: Go register to {@link me.fallenbreath.tweakermore.util.render.TweakerMoreRenderEventHandler}
	 * instead of malilib's {@link fi.dy.masa.malilib.event.RenderEventHandler}
	 */
	@Deprecated
	@Override
	default void onRenderWorldLast(
			//#if MC >= 11700
			//$$ MatrixStack matrixStack, Matrix4f positionMatrix
			//#elseif MC >= 11500
			float partialTicks, MatrixStack matrixStack
			//#else
			//$$ float partialTicks
			//#endif
	)
	{
		this.onRenderWorldLast(
				RenderContext.of(
						//#if MC >= 11600
						//$$ matrixStack
						//#endif
				)
		);
		//#if MC >= 11700
		//$$ // to prevent rain rendering got messed up
		//$$ RenderSystem.applyModelViewMatrix();
		//#endif
	}

	@Deprecated
	@Override
	default void onRenderGameOverlayPost(
			//#if MC >= 12000
			//$$ DrawContext matrixStackOrDrawContext
			//#elseif MC >= 11700
			//$$ MatrixStack matrixStackOrDrawContext
			//#elseif MC >= 11600
			//$$ float partialTicks, MatrixStack matrixStackOrDrawContext
			//#else
			float partialTicks
			//#endif
	)
	{
		this.onRenderGameOverlayPost(
				RenderContext.of(
						//#if MC >= 11600
						//$$ matrixStackOrDrawContext
						//#endif
				)
		);
	}

	default void onRenderWorldLast(RenderContext context) {}
	default void onRenderGameOverlayPost(RenderContext context) {}
}
