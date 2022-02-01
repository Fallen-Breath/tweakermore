package me.fallenbreath.tweakermore;

import fi.dy.masa.malilib.event.InitializationHandler;
import fi.dy.masa.malilib.event.InputEventHandler;
import me.fallenbreath.tweakermore.config.KeybindProvider;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.Identifier;

public class TweakerMoreMod implements ClientModInitializer
{
	public static final String MOD_NAME = "TweakerMore";
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
		InitializationHandler.getInstance().registerInitializationHandler(() -> {
			TweakerMoreConfigs.initCallbacks();
			InputEventHandler.getKeybindManager().registerKeybindProvider(new KeybindProvider());
		});
	}
}
