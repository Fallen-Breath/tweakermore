/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.render.matrix;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.util.math.Matrix4f;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

// see other impls in subproject 1.14.4, 1.17.1
//#if 11600 <= MC && MC < 11700
//$$ @SuppressWarnings("deprecation")
//#endif
public class McMatrixStack implements IMatrixStack
{
	//#if MC >= 11600
	//$$ private final MatrixStack matrixStack;
	//$$
	//$$ public McMatrixStack(MatrixStack matrixStack)
	//$$ {
	//$$ 	this.matrixStack = matrixStack;
	//$$ }
	//#endif

	//#if MC >= 11600
	//$$ public MatrixStack asMcRaw()
	//$$ {
	//$$ 	return this.matrixStack;
	//$$ }
	//#endif

	@Override
	public void pushMatrix()
	{
		RenderSystem.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		RenderSystem.popMatrix();
	}

	@Override
	public void translate(double x, double y, double z)
	{
		RenderSystem.translated(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		RenderSystem.scaled(x, y, z);
	}

	@Override
	public void mul(Matrix4f matrix4f)
	{
		RenderSystem.multMatrix(matrix4f);
	}
}
