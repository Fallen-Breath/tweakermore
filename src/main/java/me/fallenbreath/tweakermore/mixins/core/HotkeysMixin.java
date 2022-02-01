package me.fallenbreath.tweakermore.mixins.core;

import fi.dy.masa.malilib.config.options.ConfigHotkey;
import fi.dy.masa.tweakeroo.config.Hotkeys;
import me.fallenbreath.tweakermore.config.Config;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(Hotkeys.class)
public abstract class HotkeysMixin
{
	@Mutable
	@Shadow(remap = false) @Final public static List<ConfigHotkey> HOTKEY_LIST;

	static
	{
		HOTKEY_LIST = TweakerMoreConfigs.updateOptionList(HOTKEY_LIST, Config.Type.HOTKEY);
	}
}
