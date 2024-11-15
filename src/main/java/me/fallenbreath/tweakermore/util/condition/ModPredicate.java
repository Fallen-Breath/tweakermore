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

package me.fallenbreath.tweakermore.util.condition;

import com.google.common.base.Joiner;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.tweakermore.util.FabricUtils;

import java.util.Arrays;
import java.util.List;

public class ModPredicate
{
	public final String modId;
	public final List<String> versionPredicates;
	private final boolean satisfied;

	private ModPredicate(String modId, List<String> versionPredicates)
	{
		this.modId = modId;
		this.versionPredicates = versionPredicates;
		this.satisfied = FabricUtils.isModLoaded(this.modId) && FabricUtils.doesModFitsAnyPredicate(this.modId, this.versionPredicates);
	}

	public static ModPredicate of(Condition condition)
	{
		if (condition.type() != Condition.Type.MOD)
		{
			throw new IllegalArgumentException("Only MOD condition type is accepted");
		}
		return new ModPredicate(condition.value(), Arrays.asList(condition.versionPredicates()));
	}

	public boolean isSatisfied()
	{
		return this.satisfied;
	}

	public String getVersionPredicatesString()
	{
		return Joiner.on(" || ").join(this.versionPredicates);
	}

	@Override
	public String toString()
	{
		return this.modId + (this.versionPredicates.isEmpty() ? "" : " " + this.getVersionPredicatesString());
	}
}
