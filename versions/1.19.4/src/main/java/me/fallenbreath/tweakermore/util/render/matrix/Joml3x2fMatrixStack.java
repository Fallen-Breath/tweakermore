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

package me.fallenbreath.tweakermore.util.render.matrix;

import org.joml.Matrix3x2fStack;
import org.joml.Matrix4f;

// useful in mc1.21.6+
public class Joml3x2fMatrixStack implements IMatrixStack
{
	private final Matrix3x2fStack matrixStack;

	public Joml3x2fMatrixStack(Matrix3x2fStack matrixStack)
	{
		this.matrixStack = matrixStack;
	}

	public Matrix3x2fStack getRaw()
	{
		return this.matrixStack;
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
		if (z != 0)
		{
			throw new IllegalArgumentException("z must be 0 in Joml3x2fMatrixStack#translate");
		}
		this.matrixStack.translate((float)x, (float)y);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		this.matrixStack.scale((float)x, (float)y);
	}

	@Override
	public void mul(Matrix4f matrix4f)
	{
		throw new RuntimeException("Joml3x2fMatrixStack does not support mul()");
	}
}
