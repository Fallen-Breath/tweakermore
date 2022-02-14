package me.fallenbreath.tweakermore.util.dependency;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ModRestriction
{
	private final List<ModPredicate> requirements;
	private final List<ModPredicate> conflictions;
	private final boolean satisfied;

	private ModRestriction(Restriction restriction, Predicate<Condition> conditionPredicate)
	{
		this.requirements = generateRequirement(restriction.enableWhen(), conditionPredicate);
		this.conflictions = generateRequirement(restriction.disableWhen(), conditionPredicate);
		this.satisfied = this.requirements.stream().allMatch(ModPredicate::isSatisfied) && this.conflictions.stream().noneMatch(ModPredicate::isSatisfied);
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

	public boolean isSatisfied()
	{
		return this.satisfied;
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
