package me.fallenbreath.tweakermore.util;

import me.fallenbreath.conditionalmixin.api.util.VersionChecker;
import net.fabricmc.loader.api.FabricLoader;

import java.util.Collection;

public class FabricUtil
{
	public static boolean isModLoaded(String modId)
	{
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean isDevelopmentEnvironment()
	{
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static boolean doesModFitsAnyPredicate(String modId, Collection<String> versionPredicates)
	{
		return FabricLoader.getInstance().getModContainer(modId).
				map(mod -> VersionChecker.doesVersionSatisfyPredicate(mod.getMetadata().getVersion(), versionPredicates)).
				orElse(false);
	}
}
