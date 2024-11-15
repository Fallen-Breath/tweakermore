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

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.StringUtils;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Only works perfectly with 1 line tho
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.element.WidgetLabelMixin} for more details
 */
public class TweakerMoreOptionLabel extends WidgetLabel
{
	private final String[] originalLines;
	private final boolean showOriginalLines;

	public TweakerMoreOptionLabel(int x, int y, int width, int height, int textColor, String[] displayLines, String[] originalLines, Function<String, String> lineModifier)
	{
		super(x, y, width, height, textColor, displayLines);
		this.originalLines = originalLines;
		boolean showOriginalLines = false;
		for (int i = 0; i < this.originalLines.length; i++)
		{
			String linesToDisplay = this.labels.get(i);
			if (!this.originalLines[i].equals(StringUtils.removeFormattingCode(linesToDisplay)))
			{
				showOriginalLines = true;
			}
			this.labels.set(i, lineModifier.apply(linesToDisplay));
		}
		this.showOriginalLines = showOriginalLines;
		if (this.showOriginalLines != willShowOriginalLines(displayLines, originalLines))
		{
			TweakerMoreMod.LOGGER.warn("Inconsistent showOriginalLines result: {} {}", this.showOriginalLines, willShowOriginalLines(displayLines, originalLines));
		}
	}

	public static double getConfigOriginalNameScale()
	{
		return TweakerMoreConfigs.CONFIG_ORIGINAL_NAME_SCALE.getDoubleValue();
	}

	public static boolean willShowOriginalLines(String[] displayLines, String[] originalLines)
	{
		return !Arrays.equals(
				originalLines,
				Arrays.stream(displayLines).
						map(fi.dy.masa.malilib.util.StringUtils::translate).
						map(StringUtils::removeFormattingCode).
						toArray(String[]::new)
		);
	}

	public String[] getOriginalLines()
	{
		return this.originalLines;
	}

	public boolean shouldShowOriginalLines()
	{
		return this.showOriginalLines && getConfigOriginalNameScale() > 0;
	}
}
