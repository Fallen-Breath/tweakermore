/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.render.context;

import com.mojang.blaze3d.opengl.GlConst;
import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;

/**
 * mc1.14           : subproject 1.14.4
 * mc1.15 ~ mc1.21.4: subproject 1.15.2 (main project)
 * mc1.21.5+        : subproject 1.21.5        <--------
 */
public class RenderGlobals
{
	private RenderGlobals() {}

	public static void enableDepthTest()
	{
		GlStateManager._enableDepthTest();
	}

	public static void disableDepthTest()
	{
		GlStateManager._disableDepthTest();
	}

	public static void depthMask(boolean mask)
	{
		GlStateManager._depthMask(mask);
	}

	public static void enableBlend()
	{
		GlStateManager._enableBlend();
	}

	public static void blendFunc(int srcFactorRGB, int dstFactorRgb, int srcFactorAlpha, int dstFactorAlpha)
	{
		GlStateManager._blendFuncSeparate(srcFactorRGB, dstFactorRgb, srcFactorAlpha, dstFactorAlpha);
	}

	/**
	 * references:
	 * - {@link com.mojang.blaze3d.pipeline.BlendFunction.TRANSLUCENT}
	 * - {@link net.minecraft.client.gl.GlResourceManager#setPipelineAndApplyState}
	 */
	public static void blendFuncForAlpha()
	{
		blendFunc(
				GlConst.toGl(SourceFactor.SRC_ALPHA),
				GlConst.toGl(DestFactor.ONE_MINUS_SRC_ALPHA),
				GlConst.toGl(SourceFactor.ONE),
				GlConst.toGl(DestFactor.ONE_MINUS_SRC_ALPHA)
		);
	}

	public static boolean isOnRenderThread()
	{
		return RenderSystem.isOnRenderThread();
	}
}
