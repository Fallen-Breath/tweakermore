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

package me.fallenbreath.tweakermore.tests;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranslationOrderTest extends TestCase
{
	// only test our maintaining languages
	private static final Set<String> TESTING_LANGUAGES = ImmutableSet.of("en_us", "zh_cn");

	private final Map<String, Translation> translationStorage = Maps.newLinkedHashMap();

	@Override
	protected void setUp()
	{
		for (String lang : TESTING_LANGUAGES)
		{
			String fileName = String.format("assets/tweakermore/lang/%s.json", lang);
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(fileName);

			if (inputStream == null)
			{
				throw new RuntimeException(String.format("language file %s for %s not found", fileName, lang));
			}
			try
			{
				String fileContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
				this.translationStorage.put(lang, new Gson().fromJson(fileContent, Translation.class));
			}
			catch (IOException e)
			{
				throw new RuntimeException(String.format("Failed to read translation for %s", lang));
			}
		}
	}

	public void testTranslationConsistency()
	{
		// lang -> list of keys
		Map<String, List<String>> translationKeys = Maps.newLinkedHashMap();
		for (String lang : TESTING_LANGUAGES)
		{
			translationKeys.put(lang, Lists.newArrayList(this.translationStorage.get(lang).keySet()));
		}
		System.out.printf("Testing %d languages: %s\n", translationKeys.size(), translationKeys.keySet());

		if (translationKeys.size() >= 1)
		{
			String stdLang = translationKeys.keySet().iterator().next();
			List<String> stdKeys = translationKeys.get(stdLang);
			int stdSize = stdKeys.size();

			// translation size
			for (Map.Entry<String, List<String>> entry : translationKeys.entrySet())
			{
				String testLang = entry.getKey();
				int testSize = entry.getValue().size();
				assertEquals(String.format("STD language %s has size %d, but language %s has size %d", stdLang, stdSize, testLang, testSize), stdSize, testSize);
			}

			// translation key order
			for (int i = 0; i < stdKeys.size(); i++)
			{
				String stdKey = stdKeys.get(i);
				for (Map.Entry<String, List<String>> entry : translationKeys.entrySet())
				{
					String testLang = entry.getKey();
					String testKey = entry.getValue().get(i);
					assertEquals(String.format("Key #%d, excepted %s in %s, found %s in %s\n", i, stdKey, stdLang, testKey, testLang), stdKey, testKey);
				}
			}
		}
	}

	private static class Translation extends LinkedHashMap<String, String>
	{
	}
}
