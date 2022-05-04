package me.fallenbreath.tweakermore.util;

import com.google.gson.JsonObject;
import me.fallenbreath.tweakermore.TweakerMoreMod;

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
}
