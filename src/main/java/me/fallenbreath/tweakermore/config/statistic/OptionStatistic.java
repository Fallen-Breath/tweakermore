package me.fallenbreath.tweakermore.config.statistic;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import me.fallenbreath.tweakermore.TweakerMoreMod;

public class OptionStatistic
{
	private static final int USE_AMOUNT_INCREASE_COOLDOWN = 1000;  // ms
	public long lastUsedTime;
	public long useAmount;

	public OptionStatistic()
	{
		this.reset();
	}

	public void reset()
	{
		this.lastUsedTime = 0;
		this.useAmount = 0;
	}

	public void loadFromJson(JsonElement jsonElement)
	{
		try
		{
			if (jsonElement.isJsonObject())
			{
				JsonObject obj = jsonElement.getAsJsonObject();
				this.lastUsedTime = obj.get("lastUsedTime").getAsLong();
				this.useAmount = obj.get("useAmount").getAsLong();
			}
		}
		catch (Exception e)
		{
			TweakerMoreMod.LOGGER.warn("Failed to load OptionStatistic from json '{}'", jsonElement);
		}
	}

	public JsonElement toJson()
	{
		JsonObject obj = new JsonObject();
		obj.add("lastUsedTime", new JsonPrimitive(this.lastUsedTime));
		obj.add("useAmount", new JsonPrimitive(this.useAmount));
		return obj;
	}

	public void onConfigUsed()
	{
		long currentTime = System.currentTimeMillis();
		if (currentTime - this.lastUsedTime > USE_AMOUNT_INCREASE_COOLDOWN)
		{
			this.useAmount += 1;
		}
		this.lastUsedTime = currentTime;
	}
}
