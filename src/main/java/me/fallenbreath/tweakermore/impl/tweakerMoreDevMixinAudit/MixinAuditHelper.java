package me.fallenbreath.tweakermore.impl.tweakerMoreDevMixinAudit;

import fi.dy.masa.malilib.hotkeys.IKeybind;
import fi.dy.masa.malilib.hotkeys.KeyAction;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class MixinAuditHelper
{
	public static void forceLoadAllMixins()
	{
		MixinEnvironment.getCurrentEnvironment().audit();
	}

	public static boolean onHotKey(KeyAction keyAction, IKeybind iKeybind)
	{
		forceLoadAllMixins();
		return true;
	}
}
