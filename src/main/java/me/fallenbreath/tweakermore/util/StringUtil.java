package me.fallenbreath.tweakermore.util;

import fi.dy.masa.malilib.gui.GuiBase;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import net.minecraft.util.Formatting;

public class StringUtil
{
	public static String removeFormattingCode(String string)
	{
		return Formatting.strip(string);
	}

	public static String configsToListLines(Iterable<? extends TweakerMoreIConfigBase> configs)
	{
		StringBuilder builder = new StringBuilder();
		boolean isFirst = true;
		for (TweakerMoreIConfigBase config : configs)
		{
			String id = config.getName();
			String name = config.getConfigGuiDisplayName();

			if (!isFirst)
			{
				builder.append("\n");
			}
			isFirst = false;

			builder.append(GuiBase.TXT_GRAY).append("- ");
			builder.append(GuiBase.TXT_RST).append(name);

			if (!id.equals(name))
			{
				builder.append(GuiBase.TXT_GRAY).append(" (").append(id).append(")").append(GuiBase.TXT_RST);
			}
		}
		return builder.toString();
	}
}
