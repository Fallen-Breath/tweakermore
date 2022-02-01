package me.fallenbreath.tweakermore.config;

import fi.dy.masa.malilib.hotkeys.IHotkey;
import fi.dy.masa.malilib.hotkeys.IKeybindManager;
import fi.dy.masa.malilib.hotkeys.IKeybindProvider;
import me.fallenbreath.tweakermore.TweakerMoreMod;

import java.util.List;
import java.util.stream.Collectors;

public class KeybindProvider implements IKeybindProvider
{
	private static final List<IHotkey> ALL_CUSTOM_HOTKEYS = TweakerMoreConfigs.getOptions(Config.Type.CONFIG).stream().
			filter(option -> option instanceof IHotkey).
			map(option -> (IHotkey)option).
			collect(Collectors.toList());

	@Override
	public void addKeysToMap(IKeybindManager manager)
	{
		ALL_CUSTOM_HOTKEYS.forEach(iHotkey -> manager.addKeybindToMap(iHotkey.getKeybind()));
	}

	@Override
	public void addHotkeys(IKeybindManager manager)
	{
		manager.addHotkeysForCategory(TweakerMoreMod.MOD_NAME, "tweakermore.hotkeys.category.config_hotkeys", ALL_CUSTOM_HOTKEYS);
	}
}
