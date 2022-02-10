package me.fallenbreath.tweakermore.util;

import me.fallenbreath.tweakermore.TweakerMoreMod;

public class StringUtil
{
	public static final String TWEAKERMORE_NAMESPACE_PREFIX = TweakerMoreMod.MOD_ID + ".";

	public static boolean inTweakerMoreNameSpace(String name)
	{
		return name.startsWith(TWEAKERMORE_NAMESPACE_PREFIX);
	}

	public static String removeTweakerMoreNameSpacePrefix(String name)
	{
		if (inTweakerMoreNameSpace(name))
		{
			name = name.substring(TWEAKERMORE_NAMESPACE_PREFIX.length());
		}
		return name;
	}
}
