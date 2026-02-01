/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.lowerExplosionSoundLimitPerTick;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;

import java.util.Objects;

public class LowerExplosionSoundLimitPerTickHelper
{
	private static final Object2IntOpenHashMap<Coord> explosionSoundCounts = new Object2IntOpenHashMap<>();

	public static void onClientTick(Minecraft mc)
	{
		explosionSoundCounts.clear();
	}

	public static int increaseAndGetExplosionSoundCount(double x, double y, double z)
	{
		return explosionSoundCounts.addTo(new Coord(x, y, z), 1) + 1;
	}

	private static class Coord
	{
		private final double x;
		private final double y;
		private final double z;

		public Coord(double x, double y, double z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		@Override
		public boolean equals(Object o)
		{
			if (o == null || getClass() != o.getClass()) return false;
			Coord coord = (Coord)o;
			return Double.compare(x, coord.x) == 0 && Double.compare(y, coord.y) == 0 && Double.compare(z, coord.z) == 0;
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(x, y, z);
		}
	}
}
