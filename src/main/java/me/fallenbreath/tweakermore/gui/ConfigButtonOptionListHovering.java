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

package me.fallenbreath.tweakermore.gui;

import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.gui.GuiBase;

import java.util.Comparator;
import java.util.List;

/**
 * See class {@link fi.dy.masa.malilib.gui.button.ConfigButtonOptionList} for the implementation details
 */
public interface ConfigButtonOptionListHovering
{
	void setEnableValueHovering$TKM();

	default List<String> makeHoveringLines(IConfigOptionList config)
	{
		List<String> lines = Lists.newArrayList();
		IConfigOptionListEntry head = config.getDefaultOptionListValue();
		IConfigOptionListEntry current = config.getOptionListValue();
		int cnt = 0;

		// in case it loops forever
		final int MAX_ENTRIES = 128;

		lines.add("tweakermore.gui.element.config_button_option_list_hovering.title");
		List<IConfigOptionListEntry> entries = Lists.newArrayList();
		boolean allEnum = true;
		for (IConfigOptionListEntry entry = head; (cnt == 0 || entry != head) && cnt < MAX_ENTRIES && entry != null; entry = entry.cycle(true))
		{
			cnt++;
			entries.add(entry);
			allEnum &= entry instanceof Enum<?>;
		}

		if (allEnum)
		{
			entries.sort(Comparator.comparingInt(e -> ((Enum<?>)e).ordinal()));
		}

		for (IConfigOptionListEntry entry : entries)
		{
			String line = "  " + entry.getDisplayName();
			if (entry.equals(current))
			{
				line += GuiBase.TXT_DARK_GRAY + "     <---" + GuiBase.TXT_RST;
			}
			lines.add(line);
		}
		return lines;
	}
}
