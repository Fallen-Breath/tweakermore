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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import org.jetbrains.annotations.NotNull;

public interface JsonSaveAble
{
	default JsonObject dumpToJson()
	{
		JsonObject jsonObject = new JsonObject();
		this.dumpToJson(jsonObject);
		return jsonObject;
	}

	void dumpToJson(JsonObject jsonObject);

	void loadFromJson(JsonObject jsonObject);

	/**
	 * Just like {@link #loadFromJson}, but exception proof
	 */
	default void loadFromJsonSafe(JsonObject jsonObject)
	{
		try
		{
			this.loadFromJson(jsonObject);
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.warn("Failed to load data of {} from json object {}: {}", this.getClass().getSimpleName(), jsonObject, e);
		}
	}

	@SuppressWarnings("unchecked")
	default <T extends Enum<T>> T getEnumSafe(JsonObject jsonObject, String key, @NotNull T fallbackValue)
	{
		JsonElement jsonElement = jsonObject.get(key);
		if (jsonElement != null && jsonElement.isJsonPrimitive())
		{
			String jsonName = jsonElement.getAsString();
			try
			{
				return Enum.valueOf((Class<T>)fallbackValue.getClass(), jsonName);
			}
			catch (Exception e)
			{
				TweakerMoreMod.LOGGER.warn("Failed to load data of {} from json object {}: {}", this.getClass().getSimpleName(), jsonObject, e);
			}
		}
		return fallbackValue;
	}
}
