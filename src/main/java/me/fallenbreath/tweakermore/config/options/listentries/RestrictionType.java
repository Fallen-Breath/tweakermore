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
