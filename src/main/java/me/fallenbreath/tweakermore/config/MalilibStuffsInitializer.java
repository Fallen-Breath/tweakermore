package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.config.ConfigManager;
import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import fi.dy.masa.malilib.gui.GuiBase;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.impl.copySignTextToClipBoard.SignTextCopier;

public class MalilibStuffsInitializer
{
	public static void init()
	{
		InitializationHandler.getInstance().registerInitializationHandler(() -> {
			ConfigManager.getInstance().registerConfigHandler(TweakerMoreMod.MOD_ID, new TweakerMoreConfigs());

			InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());

			initCallbacks();
		});
	}

	private static void initCallbacks()
	{
		TweakerMoreConfigs.COPY_SIGN_TEXT_TO_CLIPBOARD.getKeybind().setCallback(SignTextCopier::copySignText);
		TweakerMoreConfigs.OPEN_TWEAKERMORE_CONFIG_GUI.getKeybind().setCallback((action, key) -> {
			GuiBase.openGui(new TweakerMoreConfigGui());
			return true;
		});
	}
}
