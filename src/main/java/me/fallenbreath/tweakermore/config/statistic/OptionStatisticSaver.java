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

package me.fallenbreath.tweakermore.config.statistic;

import com.google.gson.JsonObject;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.TweakerMoreOption;
import me.fallenbreath.tweakermore.config.migration.ConfigRenameMigration;
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
			else
			{
				ConfigRenameMigration.newToOld(key).ifPresent(oldKey -> {
					if (jsonObject.has(oldKey))
					{
						tweakerMoreOption.getStatistic().loadFromJson(jsonObject.get(oldKey));
					}
				});
			}
		}
	}
}
