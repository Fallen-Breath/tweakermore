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
import fi.dy.masa.malilib.event.RenderEventHandler;
import fi.dy.masa.malilib.interfaces.IRenderer;
import me.fallenbreath.tweakermore.util.render.context.GuiRenderContext;
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.util.profiling.ProfilerFiller;

//#if MC >= 12103
//$$ import net.minecraft.util.profiling.Profiler;
//#endif

//#if MC >= 12006
//$$ import org.joml.Matrix4fStack;
//#endif

//#if MC >= 12000
//$$ import net.minecraft.client.gui.GuiGraphics;
//#endif

//#if MC >= 11600
//$$ import com.mojang.blaze3d.vertex.PoseStack;
//#endif

import java.util.List;

public abstract class TweakerMoreRenderEventHandler
{
	private static final List<TweakerMoreIRenderer> renderers = Lists.newArrayList();

	public static void register(TweakerMoreIRenderer renderer)
	{
		renderers.add(renderer);
	}

	public static void init()
	{
		RenderEventHandler.getInstance().registerGameOverlayRenderer(new MalilibRendererHook());
	}

	public static void dispatchRenderWorldPostEvent(
			Minecraft mc
			//#if MC >= 12006
			//$$ , Matrix4fStack matrixStack
			//#elseif MC >= 11600
			//$$ , PoseStack matrixStack
			//#endif
	)
	{
		//#if MC >= 12103
		//$$ ProfilerFiller profiler = Profiler.get();
		//#else
		ProfilerFiller profiler = mc.getProfiler();
		//#endif

		profiler.popPush("TweakerMore_WorldRenderPostHook");
		WorldRenderContext renderContext = RenderContext.world(
				//#if MC >= 11600
				//$$ matrixStack
				//#endif
		);
		renderers.forEach(renderer -> renderer.onRenderWorldLast(renderContext));
		profiler.pop();
	}

	private static void dispatchRenderGameOverlayPost(GuiRenderContext renderContext)
	{
		renderers.forEach(renderer -> renderer.onRenderGameOverlayPost(renderContext));
	}

	private static class MalilibRendererHook implements IRenderer
	{
		@Override
		public void onRenderGameOverlayPost(
				//#if MC >= 12000
				//$$ GuiGraphics matrixStackOrDrawContext
				//#elseif MC >= 11700
				//$$ PoseStack matrixStackOrDrawContext
				//#elseif MC >= 11600
				//$$ float partialTicks, PoseStack matrixStackOrDrawContext
				//#else
				float partialTicks
				//#endif
		)
		{
			dispatchRenderGameOverlayPost(
					RenderContext.gui(
							//#if MC >= 11600
							//$$ matrixStackOrDrawContext
							//#endif
					)
			);
		}
	}
}
