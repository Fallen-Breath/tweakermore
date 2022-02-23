package me.fallenbreath.tweakermore.util;

import net.fabricmc.loader.api.FabricLoader;

public class FabricUtil
{
	public static boolean isDevelopmentEnvironment()
	{
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}
}
