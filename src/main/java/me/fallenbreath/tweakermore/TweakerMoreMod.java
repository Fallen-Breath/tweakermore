package me.fallenbreath.tweakermore;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.interfaces.IInitializationHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class TweakerMoreMod implements ClientModInitializer
{
	public static final String MOD_ID = "tweakermore";
	public static String VERSION = "unknown";

	public static Identifier id(String path)
	{
		return new Identifier(MOD_ID, path);
	}

	@Override
	public void onInitializeClient()
	{
		VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).orElseThrow(RuntimeException::new).getMetadata().getVersion().getFriendlyString();
		InitializationHandler.getInstance().registerInitializationHandler(new InitHandler());
	}

	private static class InitHandler implements IInitializationHandler
	{
		@Override
		public void registerModHandlers()
		{
			// maybe
		}
	}
}
