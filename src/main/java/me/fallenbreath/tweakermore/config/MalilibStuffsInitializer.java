package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;

public class MalilibStuffsInitializer
{
	public static void init()
	{
		InitializationHandler.getInstance().registerInitializationHandler(() -> {
			ConfigManager.getInstance().registerConfigHandler(TweakerMoreMod.MOD_ID, new TweakerMoreConfigStorage());

			InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());

			TweakerMoreConfigs.initCallbacks();
		});
	}
}
