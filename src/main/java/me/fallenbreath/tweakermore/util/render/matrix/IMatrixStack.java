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

import net.minecraft.client.util.math.Matrix4f;

//#if MC >= 11500
import net.minecraft.client.util.math.MatrixStack;
//#endif

// TODO: split by RenderContextImpl (e.g. WorldImpl, GuiImpl)
public interface IMatrixStack
{
	//#if MC >= 11500
	MatrixStack asMcRaw();
	//#endif

	void pushMatrix();

	void popMatrix();

	void translate(double x, double y, double z);

	void scale(double x, double y, double z);

	void mul(Matrix4f matrix4f);
}
