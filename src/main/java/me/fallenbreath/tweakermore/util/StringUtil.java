package me.fallenbreath.tweakermore.util;

import net.minecraft.util.Formatting;

public class StringUtil
{
	public static String removeFormattingCode(String string)
	{
		return Formatting.strip(string);
	}
}
