package me.fallenbreath.tweakermore;

import me.fallenbreath.tweakermore.config.MalilibStuffsInitializer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TweakerMoreMod implements ClientModInitializer
{
	public static final Logger LOGGER = LogManager.getLogger();

	public static final String MOD_NAME = "TweakerMore";
	public static final String MOD_ID = "tweakermore";
	public static String VERSION = "unknown";

	@Override
	public void onInitializeClient()
	{
		VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();

		MalilibStuffsInitializer.init();
	}
}
