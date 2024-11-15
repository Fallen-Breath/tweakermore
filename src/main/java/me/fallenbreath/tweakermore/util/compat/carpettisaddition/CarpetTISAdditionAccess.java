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

package me.fallenbreath.tweakermore.util.compat.carpettisaddition;

import me.fallenbreath.tweakermore.util.ReflectionUtils;

import java.util.Optional;

public class CarpetTISAdditionAccess
{
	public static Optional<Boolean> getBooleanRule(String ruleName)
	{
		return ReflectionUtils.getClass("carpettisaddition.CarpetTISAdditionSettings")
				.map(clazz -> ReflectionUtils.getStaticField(clazz, ruleName))
				.map(vw -> vw.isPresent() ? vw.get() : null)
				.map(value -> value instanceof Boolean ? (Boolean)value : null);
	}
}
