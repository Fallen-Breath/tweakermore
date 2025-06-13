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

//#if MC >= 11600
//$$ import me.fallenbreath.tweakermore.util.render.matrix.McMatrixStack;
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

/**
 * For those who needs in-game transformation and guiDrawer drawing (mc1.21.6+) (very hacky)
 */
public abstract class MixedRenderContext
{
	// see full impl in mc1.21.6+

	public static RenderContext create()
	{
		//#if MC >= 12000
		//$$ var matrixStack = new MatrixStack();
		//$$ return new RenderContextImpl(RenderContextUtils.createDrawContext(new MatrixStack()), new McMatrixStack(matrixStack));
		//#elseif MC >= 11600
		//$$ return new RenderContextImpl(new McMatrixStack(new MatrixStack()));
		//#else
		return RenderContext.of();
		//#endif
	}
}
