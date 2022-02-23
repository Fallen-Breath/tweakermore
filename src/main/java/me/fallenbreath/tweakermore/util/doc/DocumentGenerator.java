package me.fallenbreath.tweakermore.util.doc;

import com.google.common.collect.ImmutableList;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
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
		if (languageDefinition != languageManager.getLanguage())
		{
			languageManager.setLanguage(languageDefinition);
			return MinecraftClient.getInstance().reloadResources();
		}
		return CompletableFuture.allOf();
	}

	private static CompletableFuture<Void> generateDoc(String lang)
	{
		return setLanguage(lang).thenRun(() -> DocumentPrinter.printDoc(lang));
	}

	@SuppressWarnings("unchecked")
	private static void generateDoc()
	{
		if (completableFuture == null)
		{
			completableFuture = new CompletableFuture<>();  // assign to any value first to prevent hotkey spam

			InfoUtils.showGuiMessage(Message.MessageType.INFO, "Generating doc...");
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
				completableFuture = setLanguage(prevLang);
				completableFuture.thenRun(() -> {
					completableFuture = null;
					TweakerMoreMod.LOGGER.info("Doc generating done");
				});
			});
			futures[0].complete(null);
		}
	}

	public static boolean onHotKey(KeyAction keyAction, IKeybind iKeybind)
	{
		generateDoc();
		return true;
	}
}
