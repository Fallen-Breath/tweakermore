package me.fallenbreath.tweakermore.config.statistic;

import com.google.gson.JsonObject;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.util.JsonSaveAble;

public class OptionStatisticSaver implements JsonSaveAble
{
	@Override
	public void dumpToJson(JsonObject jsonObject)
	{
		for (TweakerMoreOption tweakerMoreOption : TweakerMoreConfigs.getAllOptions())
		{
			jsonObject.add(tweakerMoreOption.getConfig().getName(), tweakerMoreOption.getStatistic().toJson());
		}
	}

	@Override
	public void loadFromJson(JsonObject jsonObject)
	{
		for (TweakerMoreOption tweakerMoreOption : TweakerMoreConfigs.getAllOptions())
		{
			String key = tweakerMoreOption.getConfig().getName();
			if (jsonObject.has(key))
			{
				tweakerMoreOption.getStatistic().loadFromJson(jsonObject.get(key));
			}
		}
	}
}
