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

package me.fallenbreath.tweakermore.util;

import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class IdentifierUtils
{
	/**
	 * @throws InvalidIdentifierException if the identifier string is invalid
	 */
	public static Identifier of(String id)
	{
		//#if MC >= 12100
		//$$ return Identifier.of(id);
		//#else
		return new Identifier(id);
		//#endif
	}

	/**
	 * @throws InvalidIdentifierException if the identifier string is invalid
	 */
	public static Identifier of(String namespace, String path)
	{
		//#if MC >= 12100
		//$$ return Identifier.of(namespace, path);
		//#else
		return new Identifier(namespace, path);
		//#endif
	}

	public static Optional<Identifier> tryParse(String id)
	{
		try
		{
			return Optional.of(of(id));
		}
		catch (InvalidIdentifierException e)
		{
			return Optional.empty();
		}
	}

	@Nullable
	public static Identifier tryParseOrNull(String id)
	{
		return tryParse(id).orElse(null);
	}
}
