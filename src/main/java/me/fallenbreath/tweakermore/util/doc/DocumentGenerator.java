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

import com.google.common.collect.ImmutableList;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.FabricUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.LanguageDefinition;
import net.minecraft.client.resource.language.LanguageManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DocumentGenerator
{
	private static CompletableFuture<Void> completableFuture = null;
	private static final List<String> LANGS = ImmutableList.of("en_us", "zh_cn");

	private static CompletableFuture<Void> setLanguage(String lang)
	{
		TweakerMoreMod.LOGGER.info("Setting client language to {}", lang);
		LanguageManager languageManager = MinecraftClient.getInstance().getLanguageManager();
		LanguageDefinition languageDefinition = languageManager.getLanguage(lang);
		if (languageDefinition != languageManager.getLanguage(  // what we want != current
				//#if MC >= 11904
				//$$ languageManager.getLanguage()
				//#endif
		))
		{
			languageManager.setLanguage(
					//#if MC >= 11904
					//$$ lang
					//#else
					languageDefinition
					//#endif
			);
			return MinecraftClient.getInstance().reloadResources();
		}
		return CompletableFuture.allOf();
	}

	private static CompletableFuture<Void> generateDoc(String lang)
	{
		return setLanguage(lang).thenRun(() -> DocumentPrinter.printDoc(lang));
	}

	@SuppressWarnings("unchecked")
	private static void generateDoc(boolean exit)
	{
		if (completableFuture == null)
		{
			completableFuture = new CompletableFuture<>();  // assign to any value first to prevent hotkey spam

			TweakerMoreMod.LOGGER.info("Generating doc...");
			String prevLang = MinecraftClient.getInstance().options.language;

			CompletableFuture<Void>[] futures = new CompletableFuture[LANGS.size() + 1];
			for (int i = 0; i < futures.length; i++)
			{
				futures[i] = new CompletableFuture<>();
			}
			for (int i = 0; i < LANGS.size(); i++)
			{
				final int finalI = i;
				futures[i].thenRun(
						() -> generateDoc(LANGS.get(finalI)).thenRun(
								() -> futures[finalI + 1].complete(null)
						)
				);
			}
			futures[LANGS.size()].thenRun(() -> {
				TweakerMoreMod.LOGGER.info("Restoring language back to {}", prevLang);
				setLanguage(prevLang).thenRun(() -> {
					completableFuture.complete(null);
					completableFuture = null;
					TweakerMoreMod.LOGGER.info("Doc generating done");
					if (exit)
					{
						TweakerMoreMod.LOGGER.info("Stopping Minecraft client");
						MinecraftClient.getInstance().scheduleStop();
					}
				});
			});

			futures[0].complete(null);  // start the logic
		}
	}

	public static void onHotKey()
	{
		generateDoc(false);
	}

	public static void onClientInitFinished()
	{
		// -Dtweakermore.gen_doc=true
		if (FabricUtil.isDevelopmentEnvironment() && "true".equals(System.getProperty(TweakerMoreMod.MOD_ID + ".gen_doc")))
		{
			TweakerMoreMod.LOGGER.info("Starting Tweakermore automatic doc generating");
			generateDoc(true);
		}
	}
}
