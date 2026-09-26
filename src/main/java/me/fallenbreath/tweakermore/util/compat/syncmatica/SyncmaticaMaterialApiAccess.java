/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.util.compat.syncmatica;

import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.ModIds;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SyncmaticaMaterialApiAccess
{
	private static final String API_CLASS_NAME = "cn.net.rms.syncmatica_r.api.SyncmaticaMaterialApi";

	public static Optional<List<ClaimedMaterialRequirement>> getClaimedMaterialRequirements(UUID playerId)
	{
		if (!FabricUtils.isModLoaded(ModIds.syncmatica_r))
		{
			return Optional.empty();
		}

		try
		{
			Class<?> apiClass = Class.forName(API_CLASS_NAME);
			Method apiMethod = apiClass.getMethod("getClaimedMaterialRequirements", UUID.class);
			Object result = apiMethod.invoke(null, playerId);
			return Optional.of(decodeRequirements(result));
		}
		catch (ReflectiveOperationException | RuntimeException e)
		{
			TweakerMoreMod.LOGGER.error("Failed to query Syncmatica_r material requirements", e);
			return Optional.empty();
		}
	}

	static List<ClaimedMaterialRequirement> decodeRequirements(Object result) throws ReflectiveOperationException
	{
		if (!(result instanceof List<?>))
		{
			throw new IllegalArgumentException("Syncmatica_r material API returned a non-list result");
		}

		List<ClaimedMaterialRequirement> requirements = new ArrayList<>();
		for (Object entry : (List<?>)result)
		{
			Class<?> entryClass = entry.getClass();
			String itemId = (String)entryClass.getMethod("itemId").invoke(entry);
			String variant = (String)entryClass.getMethod("variant").invoke(entry);
			int missingAmount = (Integer)entryClass.getMethod("missingAmount").invoke(entry);
			requirements.add(new ClaimedMaterialRequirement(itemId, variant, missingAmount));
		}
		return requirements;
	}
}
