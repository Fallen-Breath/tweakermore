/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.util;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

import java.lang.reflect.Method;
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
				map(mod -> {
					Version version = mod.getMetadata().getVersion();
					return versionPredicates.isEmpty() || versionPredicates.stream().anyMatch(vp -> doesVersionSatisfyPredicate(version, vp));
				}).
				orElse(false);
	}

	public static boolean doesVersionSatisfyPredicate(Version version, String versionPredicate)
	{
		try
		{
			// fabric loader >=0.12
			return net.fabricmc.loader.api.metadata.version.VersionPredicate.parse(versionPredicate).test(version);
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
