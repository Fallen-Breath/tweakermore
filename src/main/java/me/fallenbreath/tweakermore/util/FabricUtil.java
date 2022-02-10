package me.fallenbreath.tweakermore.util;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.version.VersionPredicate;

import java.lang.reflect.Method;

public class FabricUtil
{
	public static boolean isModLoaded(String modId)
	{
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	public static boolean doesVersionFitsPredicate(Version version, String versionPredicate)
	{
		try
		{
			// fabric loader >=0.12
			return VersionPredicate.parse(versionPredicate).test(version);
		}
		catch (NoClassDefFoundError e)
		{
			// fabric loader >=0.10.4 <0.12
			try
			{
				Class<?> clazz = Class.forName("net.fabricmc.loader.util.version.VersionPredicateParser");
				Method matches = clazz.getMethod("matches", Version.class, String.class);
				return (boolean)matches.invoke(null, version, versionPredicate);
			}
			catch (Exception ex)
			{
				TweakerMoreMod.LOGGER.error("Failed to invoke VersionPredicateParser#matches", ex);
			}
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.error("Failed to parse version or version predicate {} {}: {}", version.getFriendlyString(), versionPredicate, e);
		}
		return false;
	}
}
