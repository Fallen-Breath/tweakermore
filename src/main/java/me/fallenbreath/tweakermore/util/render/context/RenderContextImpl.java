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

import me.fallenbreath.tweakermore.util.render.matrix.IMatrixStack;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.Matrix4f;
import org.jetbrains.annotations.NotNull;

//#if MC >= 12000
//$$ import org.jetbrains.annotations.Nullable;
//#endif

//#if MC >= 11903
//$$ import me.fallenbreath.tweakermore.util.render.matrix.JomlMatrixStack;
//$$ import org.joml.Matrix4fStack;
//#endif

//#if 11600 <= MC && MC < 11700
//$$ @SuppressWarnings("deprecation")
//#endif
/**
 * The implementation for mc [1.15, ~)
 * See subproject 1.14.4 or for implementation for other version range
 */
public class RenderContextImpl implements RenderContext
{
	//#if MC >= 12000
	//$$ @Nullable
	//$$ private final DrawContext drawContext;
	//#endif

	@NotNull
	private final IMatrixStack matrixStack;

	RenderContextImpl(
			//#if MC >= 12000
			//$$ @Nullable DrawContext drawContext,
			//#endif
			@NotNull IMatrixStack matrixStack
	)
	{
		//#if MC >= 12000
		//$$ this.drawContext = drawContext;
		//#endif
		this.matrixStack = matrixStack;
	}

	@NotNull
	public IMatrixStack getMatrixStack()
	{
		return this.matrixStack;
	}

	//#if MC >= 12000
	//$$ @Nullable
	//#else
	@NotNull
	//#endif
	@Override
	public DrawableHelper getGuiDrawer()
	{
		//#if MC >= 12000
		//$$ return this.drawContext;
		//#else
		return new DrawableHelper(){};
		//#endif
	}

	@Override
	public void pushMatrix()
	{
		this.matrixStack.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		this.matrixStack.popMatrix();
	}

	@Override
	public void translate(double x, double y, double z)
	{
		this.matrixStack.translate(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		this.matrixStack.scale(x, y, z);
	}

	@Override
	public void multMatrix(Matrix4f matrix4f)
	{
		this.matrixStack.mul(matrix4f);
	}
}
