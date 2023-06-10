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
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.Matrix4f;

/**
 * The implementation for mc (~, 1.14]
 * See subproject 1.15.2 or 1.17.1 for implementation for other version range
 */
public class RenderContextImpl implements RenderContext
{
	RenderContextImpl() {}

	@Override
	public DrawableHelper getGuiDrawer()
	{
		return new DrawableHelper(){};
	}

	@Override
	public void pushMatrix()
	{
		GlStateManager.pushMatrix();
	}

	@Override
	public void popMatrix()
	{
		GlStateManager.popMatrix();
	}

	@Override
	public void translate(double x, double y, double z)
	{
		GlStateManager.translated(x, y, z);
	}

	@Override
	public void scale(double x, double y, double z)
	{
		GlStateManager.scaled(x, y, z);
	}

	@Override
	public void multMatrix(Matrix4f matrix4f)
	{
		GlStateManager.multMatrix(matrix4f);
	}
}
