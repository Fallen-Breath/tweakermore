package me.fallenbreath.tweakermore.impl.tweakmDebug;

import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.MixinEnvironment;

public class TweakerMoreDebugHelper
{
	public static void resetAllConfigStatistic()
	{
		TweakerMoreConfigs.getAllOptions().forEach(option -> option.getStatistic().reset());
	}

	public static void forceLoadAllMixins()
	{
		MixinEnvironment.getCurrentEnvironment().audit();
		InfoUtils.showGuiOrInGameMessage(Message.MessageType.SUCCESS, "tweakermore.config.tweakerMoreDevMixinAudit.success");
	}
}
