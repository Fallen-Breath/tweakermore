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

package me.fallenbreath.tweakermore.config.options.listentries;

import java.util.function.BiPredicate;

public enum RestrictionType implements EnumOptionEntry
{
	NONE,
	WHITELIST,
	BLACKLIST;

	public <T> boolean test(T target, Iterable<T> collection, BiPredicate<T, T> tester)
	{
		if (this == NONE)
		{
			throw new UnsupportedOperationException();
		}
		for (T item : collection)
		{
			boolean matches = tester.test(target, item);
			if (matches && this == WHITELIST)
			{
				return true;
			}
			if (matches && this == BLACKLIST)
			{
				return false;
			}
		}
		// not in the list
		switch (this)
		{
			case WHITELIST:
				return false;
			case BLACKLIST:
				return true;
			default:
				throw new UnsupportedOperationException();
		}
	}

	public <T> boolean testEquality(T target, Iterable<T> collection)
	{
		return this.test(target, collection, T::equals);
	}

	public <T> boolean testReference(T target, Iterable<T> collection)
	{
		return this.test(target, collection, (a, b) -> a == b);
	}

	@Override
	public EnumOptionEntry[] getAllValues()
	{
		return values();
	}

	@Override
	public EnumOptionEntry getDefault()
	{
		return NONE;
	}

	@Override
	public String getTranslationPrefix()
	{
		return "tweakermore.list_entry.restriction_type.";
	}
}
