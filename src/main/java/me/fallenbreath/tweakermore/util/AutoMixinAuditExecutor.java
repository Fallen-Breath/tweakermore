package me.fallenbreath.tweakermore.util;

import fi.dy.masa.malilib.event.TickHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.impl.setting.tweakmDebug.TweakerMoreDebugHelper;
import net.fabricmc.loader.api.FabricLoader;

public class AutoMixinAuditExecutor
{
	private static final String KEYWORD_PROPERTY = "tweakermore.mixin_audit";

	public static void run()
	{
		if (FabricLoader.getInstance().isDevelopmentEnvironment() && "true".equals(System.getProperty(KEYWORD_PROPERTY)))
		{
			TickHandler.getInstance().registerClientTickHandler(mc -> {
				TweakerMoreMod.LOGGER.info("Triggered auto mixin audit");
				TweakerMoreDebugHelper.forceLoadAllMixins();
				System.exit(0);
			});
		}
	}
}
