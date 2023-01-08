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

package me.fallenbreath.tweakermore.util.doc;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import fi.dy.masa.malilib.config.*;
import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.config.options.IHotkeyWithSwitch;
import me.fallenbreath.tweakermore.config.options.IOptionListHotkeyed;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.util.StringUtil;
import me.fallenbreath.tweakermore.util.condition.ModPredicate;
import me.fallenbreath.tweakermore.util.condition.ModRestriction;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class DocumentPrinter
{
	private static final Path DOC_DIRECTORY = FabricLoader.getInstance().getGameDir().getParent().resolve("docs");
	private static final String ASSETS_DIRECTORY_NAME = "assets";
	private static final Path ASSETS_DIRECTORY = DOC_DIRECTORY.resolve(ASSETS_DIRECTORY_NAME);

	private static String codeBlock(String text)
	{
		return "`" + text + "`";
	}

	private static String italic(String text)
	{
		return "*" + text + "*";
	}

	private static String prettyPredicate(ModPredicate modPredicate)
	{
		String ret = String.format("%s (`%s`)", StringUtils.translate("tweakermore.util.mod." + modPredicate.modId), modPredicate.modId);
		String predicate = modPredicate.getVersionPredicatesString();
		if (!predicate.isEmpty())
		{
			ret += " " + codeBlock(predicate);
		}
		return ret;
	}

	private static String getConfigType(IConfigBase config)
	{
		String id;
		if (config instanceof IHotkeyTogglable)
		{
			id = "hotkey_togglable";
		}
		else if (config instanceof IHotkeyWithSwitch)
		{
			id = "hotkey_with_switch";
		}
		else if (config instanceof IOptionListHotkeyed)
		{
			id = "option_list_hotkeyed";
		}
		else
		{
			id = config.getType().name().toLowerCase();
		}
		return StringUtils.translate("tweakermore.doc_gen.type." + id);
	}

	private static String getComment(TweakerMoreIConfigBase config)
	{
		return StringUtil.removeFormattingCode(config.getCommentNoFooter());
	}

	private static String getDefaultValue(IConfigBase config)
	{
		String hotkey = "";
		if (config instanceof IHotkey)
		{
			hotkey = ((IHotkey)config).getKeybind().getDefaultStringValue();
			if (hotkey.isEmpty())
			{
				hotkey = italic(StringUtils.translate("tweakermore.doc_gen.value.no_hotkey"));
			}
			else
			{
				hotkey = codeBlock(hotkey);
			}
		}

		if (config instanceof IHotkeyTogglable)
		{
			return hotkey + ", " + codeBlock(String.valueOf(((IHotkeyTogglable)config).getDefaultBooleanValue()));
		}
		else if (config instanceof IConfigStringList)
		{
			return codeBlock(((IConfigStringList)config).getDefaultStrings().toString());
		}
		else if (config instanceof IConfigOptionList)  // IOptionListHotkeyed here too
		{
			return codeBlock(((IConfigOptionList)config).getDefaultOptionListValue().getDisplayName());
		}
		else if (config instanceof IHotkey)
		{
			return hotkey;
		}
		else if (config instanceof IStringRepresentable)
		{
			return codeBlock(((IStringRepresentable) config).getDefaultStringValue());
		}
		TweakerMoreMod.LOGGER.warn("Unknown type found in getDefaultValue: {}", config.getClass());
		return italic("unknown type: " + config.getClass().getName());
	}

	private static Optional<Number> getMinValue(IConfigBase config)
	{
		if (config instanceof IConfigInteger)
		{
			return Optional.of(((IConfigInteger) config).getMinIntegerValue());
		}
		else if (config instanceof IConfigDouble)
		{
			return Optional.of(((IConfigDouble) config).getMinDoubleValue());
		}
		return Optional.empty();
	}

	private static Optional<Number> getMaxValue(IConfigBase config)
	{
		if (config instanceof IConfigInteger)
		{
			return Optional.of(((IConfigInteger)config).getMaxIntegerValue());
		}
		else if (config instanceof IConfigDouble)
		{
			return Optional.of(((IConfigDouble) config).getMaxDoubleValue());
		}
		return Optional.empty();
	}

	private static Optional<List<String>> getOptionListValueNames(IConfigBase config)
	{
		if (config instanceof IConfigOptionList)
		{
			IConfigOptionListEntry firstValue = ((IConfigOptionList)config).getDefaultOptionListValue();
			List<IConfigOptionListEntry> values = Lists.newArrayList();
			boolean allEnum = true;
			IConfigOptionListEntry value = firstValue;
			do
			{
				allEnum &= value instanceof Enum;
				values.add(value);
				value = value.cycle(true);
			}
			while (value != firstValue);
			if (allEnum)
			{
				values.sort(Comparator.comparingInt(v -> ((Enum<?>)v).ordinal()));
			}
			return Optional.of(values.stream().map(IConfigOptionListEntry::getDisplayName).map(DocumentPrinter::codeBlock).collect(Collectors.toList()));
		}
		return Optional.empty();
	}

	private static Optional<String> getScreenShotFileName(String configId, String lang)
	{
		String fileName = String.format("%s-%s.png", configId, lang);
		if (ASSETS_DIRECTORY.resolve(fileName).toFile().isFile())
		{
			return Optional.of(fileName);
		}
		fileName = String.format("%s.png", configId);
		if (ASSETS_DIRECTORY.resolve(fileName).toFile().isFile())
		{
			return Optional.of(fileName);
		}
		return Optional.empty();
	}

	/**
	 * Used in printOption
	 */
	private static String tr(String key)
	{
		return StringUtils.translate("tweakermore.doc_gen.text." + key);
	}

	private static void printOption(Consumer<String> writeln, TweakerMoreOption tweakerMoreOption, String lang)
	{
		TweakerMoreIConfigBase config = tweakerMoreOption.getConfig();
		String configId = config.getName();

		String title = config.getConfigGuiDisplayName();
		if (!title.equals(configId))
		{
			title += String.format(" (%s)", configId);
		}
		writeln.accept("### " + title);
		writeln.accept("");

		writeln.accept(getComment(config).replace("\n", "\n\n"));
		writeln.accept("");
		writeln.accept(String.format("- %s: %s", tr("category"), tweakerMoreOption.getCategory().getDisplayName()));
		writeln.accept(String.format("- %s: %s", tr("type"), getConfigType(config)));
		writeln.accept(String.format("- %s: %s", tr("default_value"), getDefaultValue(config)));

		getMinValue(config).ifPresent(min -> writeln.accept(String.format("- %s: `%s`", tr("minimum_value"), min)));
		getMaxValue(config).ifPresent(max -> writeln.accept(String.format("- %s: `%s`", tr("maximum_value"), max)));
		getOptionListValueNames(config).ifPresent(values -> writeln.accept(String.format("- %s: %s", tr("options"), Joiner.on(", ").join(values))));

		List<ModRestriction> modRestrictions = tweakerMoreOption.getModRestrictions();
		if (!modRestrictions.isEmpty())
		{
			writeln.accept(String.format("- %s:", tr("mod_restrictions")));
			boolean first = true;
			for (ModRestriction modRestriction : modRestrictions)
			{
				if (!first)
				{
					writeln.accept("");
					writeln.accept(String.format("  *%s*", StringUtils.translate("tweakermore.gui.mod_relation_footer.or")));
					writeln.accept("");
				}
				first = false;
				if (!modRestriction.getRequirements().isEmpty())
				{
					writeln.accept(String.format("  - %s:", tr("requirements")));
					modRestriction.getRequirements().forEach(req -> writeln.accept(String.format("    - %s", prettyPredicate(req))));
				}
				if (!modRestriction.getConflictions().isEmpty())
				{
					writeln.accept(String.format("  - %s:", tr("conflictions")));
					modRestriction.getConflictions().forEach(cfl -> writeln.accept(String.format("    - %s", prettyPredicate(cfl))));
				}
			}
		}
		writeln.accept("");
		getScreenShotFileName(configId, lang).ifPresent(fileName -> {
			writeln.accept(String.format("![%s](%s/%s)", configId, ASSETS_DIRECTORY_NAME, fileName));
			writeln.accept("");
		});
		writeln.accept("");
	}

	public static void printDoc(String lang)
	{
		TweakerMoreMod.LOGGER.info("Dumping language with language {}", lang);
		StringBuffer buffer = new StringBuffer();
		Consumer<String> writeln = line -> buffer.append(line).append("\n");
		for (Config.Category category : Config.Category.values())
		{
			if (category == Config.Category.ALL)
			{
				continue;
			}
			writeln.accept("## " + category.getDisplayName());
			writeln.accept("");
			writeln.accept(category.getDescription());
			writeln.accept("");
			List<TweakerMoreOption> options = Lists.newArrayList(TweakerMoreConfigs.getOptions(category));
			options.sort((a, b) -> a.getConfig().getName().compareToIgnoreCase(b.getConfig().getName()));
			for (TweakerMoreOption tweakerMoreOption : options)
			{
				if (tweakerMoreOption.isDebug() || tweakerMoreOption.isDevOnly())
				{
					continue;
				}
				printOption(writeln, tweakerMoreOption, lang);
			}
		}
		Path filePath = DOC_DIRECTORY.resolve(String.format("document-%s.md", lang));
		TweakerMoreMod.LOGGER.info("Doc file path: {}", filePath);
		try
		{
			if (!Files.exists(filePath.getParent()))
			{
				Files.createDirectories(filePath.getParent());
			}
			try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8))
			{
				writer.write(buffer.toString());
			}
		}
		catch (IOException e)
		{
			TweakerMoreMod.LOGGER.error(e);
		}
	}
}
