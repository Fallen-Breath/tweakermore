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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModRestriction
{
	private final List<ModPredicate> requirements;
	private final List<ModPredicate> conflictions;
	private final boolean requirementsSatisfied;
	private final boolean noConfliction;

	private ModRestriction(Restriction restriction, Predicate<Condition> conditionPredicate)
	{
		this.requirements = generateRequirement(restriction.require(), conditionPredicate);
		this.conflictions = generateRequirement(restriction.conflict(), conditionPredicate);
		this.requirementsSatisfied = this.requirements.stream().allMatch(ModPredicate::isSatisfied);
		this.noConfliction = this.conflictions.stream().noneMatch(ModPredicate::isSatisfied);
	}

	public static ModRestriction of(Restriction restriction, Predicate<Condition> conditionPredicate)
	{
		return new ModRestriction(restriction, conditionPredicate);
	}

	public static ModRestriction of(Restriction restriction)
	{
		return new ModRestriction(restriction, c -> true);
	}

	private static List<ModPredicate> generateRequirement(Condition[] conditions, Predicate<Condition> conditionPredicate)
	{
		return Arrays.stream(conditions).
				filter(c -> c.type() == Condition.Type.MOD).
				filter(conditionPredicate).
				map(ModPredicate::of).
				collect(Collectors.toList());
	}

	public boolean isRequirementsSatisfied()
	{
		return this.requirementsSatisfied;
	}

	public boolean isNoConfliction()
	{
		return this.noConfliction;
	}

	public boolean isSatisfied()
	{
		return this.isRequirementsSatisfied() && this.isNoConfliction();
	}

	public List<ModPredicate> getRequirements()
	{
		return this.requirements;
	}

	public List<ModPredicate> getConflictions()
	{
		return this.conflictions;
	}
}
