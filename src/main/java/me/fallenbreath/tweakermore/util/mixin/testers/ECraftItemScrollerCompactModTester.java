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

package me.fallenbreath.tweakermore.util.mixin.testers;

import me.fallenbreath.conditionalmixin.api.mixin.ConditionTester;
import me.fallenbreath.tweakermore.util.ModIds;
import net.fabricmc.loader.api.FabricLoader;

public class ECraftItemScrollerCompactModTester implements ConditionTester
{
	// see also: me.fallenbreath.tweakermore.config.TweakerMoreConfigs#ECRAFT_ITEM_SCROLLER_COMPACT
	@Override
	public boolean isSatisfied(String mixinClassName)
	{
		boolean c1 = FabricLoader.getInstance().isModLoaded(ModIds.itemscroller);
		boolean c2 = FabricLoader.getInstance().isModLoaded(ModIds.easier_crafting) || FabricLoader.getInstance().isModLoaded(ModIds.easier_crafting_updated);
		return c1 && c2;
	}
}
