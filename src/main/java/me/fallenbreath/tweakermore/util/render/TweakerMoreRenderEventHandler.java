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
import me.fallenbreath.tweakermore.util.render.context.RenderContext;
import net.minecraft.client.MinecraftClient;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

import java.util.List;

public abstract class TweakerMoreRenderEventHandler
{
	private static final List<TweakerMoreIRenderer> renderers = Lists.newArrayList();

	public static void register(TweakerMoreIRenderer renderer)
	{
		renderers.add(renderer);
	}

	public static void dispatchRenderWorldPostEvent(
			MinecraftClient mc
			//#if MC >= 11600
			//$$ , MatrixStack matrixStack
			//#endif
	)
	{
		mc.getProfiler().swap("TweakerMore_WorldRenderPostHook");
		RenderContext renderContext = RenderContext.of(
				//#if MC >= 11600
				//$$ matrixStack
				//#endif
		);
		renderers.forEach(renderer -> renderer.onRenderWorldLast(renderContext));
		mc.getProfiler().pop();
	}
}
