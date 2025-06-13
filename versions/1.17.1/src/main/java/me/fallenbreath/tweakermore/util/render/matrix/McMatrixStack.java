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

import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Matrix4f;

// see other impls in subproject 1.14.4, 1.15.2
public class McMatrixStack implements IMatrixStack
{
	private final MatrixStack matrixStack;

	public McMatrixStack(MatrixStack matrixStack)
	{
		this.matrixStack = matrixStack;
	}

	public MatrixStack asMcRaw()
	{
		return this.matrixStack;
	}

	@Override
	public void pushMatrix()
	{
		this.matrixStack.push();
	}

	@Override
	public void popMatrix()
	{
		this.matrixStack.pop();
	}

	@Override
	public void translate(double x, double y, double z)
	{
		this.matrixStack.translate(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		this.matrixStack.scale((float)x, (float)y, (float)z);
	}

	@Override
	public void mul(Matrix4f matrix4f)
	{
		this.matrixStack.method_34425(matrix4f);
	}
}
