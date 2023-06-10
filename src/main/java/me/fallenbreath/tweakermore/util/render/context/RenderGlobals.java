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

//#if MC >= 11500
import com.mojang.blaze3d.systems.RenderSystem;
//#endif

//#if 11600 <= MC && MC < 11700
//$$ @SuppressWarnings("deprecation")
//#endif
public class RenderGlobals
{
	private RenderGlobals() {}

	public static void enableDepthTest()
	{
		//#if MC >= 11500
		RenderSystem.enableDepthTest();
		//#else
		//$$ GlStateManager.enableDepthTest();
		//#endif
	}

	public static void disableDepthTest()
	{
		//#if MC >= 11500
		RenderSystem.disableDepthTest();
		//#else
		//$$ GlStateManager.disableDepthTest();
		//#endif
	}

	//#if MC < 11904
	public static void enableTexture()
	{
		//#if MC >= 11500
		RenderSystem.enableTexture();
		//#else
		//$$ GlStateManager.enableTexture();
		//#endif
	}
	//#endif

	//#if MC < 11700
	public static void enableAlphaTest()
	{
		//#if MC >= 11500
		RenderSystem.enableAlphaTest();
		//#else
		//$$ GlStateManager.enableAlphaTest();
		//#endif
	}
	//#endif

	public static void depthMask(boolean mask)
	{
		//#if MC >= 11500
		RenderSystem.depthMask(mask);
		//#else
		//$$ GlStateManager.depthMask(mask);
		//#endif
	}

	public static void color4f(float red, float green, float blue, float alpha)
	{
		//#if MC >= 11700
		//$$ RenderSystem.setShaderColor(red, green, blue, alpha);
		//#elseif MC >= 11500
		RenderSystem.color4f(red, green, blue, alpha);
		//#else
		//$$ GlStateManager.color4f(red, green, blue, alpha);
		//#endif
	}

	public static void enableBlend()
	{
		//#if MC >= 11500
		RenderSystem.enableBlend();
		//#else
		//$$ GlStateManager.enableBlend();
		//#endif
	}

	public static void blendFunc(
			//#if MC >= 11500
			GlStateManager.SrcFactor srcFactor, GlStateManager.DstFactor dstFactor
			//#else
			//$$ GlStateManager.SourceFactor srcFactor, GlStateManager.DestFactor dstFactor
			//#endif
	)
	{
		//#if MC >= 11500
		RenderSystem.blendFunc(srcFactor, dstFactor);
		//#else
		//$$ GlStateManager.blendFunc(srcFactor, dstFactor);
		//#endif
	}

	//#if MC < 11700
	public static void disableLighting()
	{
		//#if MC >= 11500
		RenderSystem.disableLighting();
		//#else
		//$$ GlStateManager.disableLighting();
		//#endif
	}
	//#endif
}
