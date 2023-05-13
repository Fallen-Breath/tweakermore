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

package me.fallenbreath.tweakermore.util.render;

import java.util.function.Consumer;

@SuppressWarnings("PointlessBitwiseExpression")
public class ColorHolder
{
	public int red;
	public int green;
	public int blue;
	public int alpha;

	private ColorHolder(int red, int green, int blue, int alpha)
	{
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public static ColorHolder of(int color)
	{
		return new ColorHolder((color >> 16) & 0xFF, (color >> 8) & 0xFF, (color >> 0) & 0xFF, (color >> 24) & 0xFF);
	}

	public static ColorHolder of(int red, int green, int blue, int alpha)
	{
		return new ColorHolder(red, green, blue, alpha);
	}

	public int pack()
	{
		int color = 0;
		color |= (this.alpha & 0xFF) << 24;
		color |= (this.red & 0xFF) << 16;
		color |= (this.green & 0xFF) << 8;
		color |= (this.blue & 0xFF) << 0;
		return color;
	}

	public ColorHolder modify(Consumer<ColorHolder> modifier)
	{
		modifier.accept(this);
		return this;
	}
}
