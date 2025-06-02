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

package me.fallenbreath.tweakermore.util.render.context;

import com.mojang.blaze3d.platform.GlStateManager;

/**
 * mc1.14           : subproject 1.14.4        <--------
 * mc1.15 ~ mc1.21.4: subproject 1.15.2 (main project)
 * mc1.21.5+        : subproject 1.21.5
 */
public class RenderGlobals
{
	private RenderGlobals() {}

	public static void enableDepthTest()
	{
		GlStateManager.enableDepthTest();
	}

	public static void disableDepthTest()
	{
		GlStateManager.disableDepthTest();
	}

	public static void enableTexture()
	{
		GlStateManager.enableTexture();
	}

	public static void enableAlphaTest()
	{
		GlStateManager.enableAlphaTest();
	}

	public static void depthMask(boolean mask)
	{
		GlStateManager.depthMask(mask);
	}

	public static void color4f(float red, float green, float blue, float alpha)
	{
		GlStateManager.color4f(red, green, blue, alpha);
	}

	public static void enableBlend()
	{
		GlStateManager.enableBlend();
	}

	public static void blendFunc(GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor)
	{
		GlStateManager.blendFunc(srcFactor, dstFactor);
	}

	public static void blendFuncForAlpha()
	{
		blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
	}

	public static void disableLighting()
	{
		GlStateManager.disableLighting();
	}
}
