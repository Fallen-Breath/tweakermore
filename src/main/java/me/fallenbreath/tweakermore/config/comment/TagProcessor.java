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

package me.fallenbreath.tweakermore.config.comment;

import com.google.common.collect.ImmutableMap;
import fi.dy.masa.malilib.gui.GuiBase;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagProcessor
{
	private static final Map<String, Transformer> TRANSFORMERS = ImmutableMap.of(
			"tr", TagProcessor::transformTranslation,
			"option", TagProcessor::transformOption
	);

	@FunctionalInterface
	private interface Transformer
	{
		String transform(String value);
	}

	/**
	 * Tag Syntax:
	 * - "@tr#my.translation.key@": translation with the given key
	 * - "@option#myOption@": translated and colored name of the given tweakermore option
	 */
	public static String processReferences(String comment)
	{
		String patternString = "@([a-zA-Z0-9]+)#([a-zA-Z0-9.]+)@";
		Pattern pattern = Pattern.compile(patternString);
		Matcher matcher = pattern.matcher(comment);

		StringBuffer sb = new StringBuffer();
		while (matcher.find())
		{
			String type = matcher.group(1);
			String value = matcher.group(2);
			Transformer transformer = TRANSFORMERS.getOrDefault(type, s -> matcher.group());
			matcher.appendReplacement(sb, transformer.transform(value));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	private static String transformTranslation(String translationKey)
	{
		return StringUtils.translate(translationKey);
	}

	private static String transformOption(String optionName)
	{
		return TweakerMoreConfigs.getOptionByName(optionName).
				map(config -> {
					String displayName = config.getConfig().getConfigGuiDisplayName();
					return GuiBase.TXT_YELLOW + displayName + GuiBase.TXT_RST;
				}).
				orElse(optionName);
	}
}
