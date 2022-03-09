package me.fallenbreath.tweakermore.util.condition;

import com.google.common.base.Joiner;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.tweakermore.util.FabricUtil;

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
		this.satisfied = FabricUtil.isModLoaded(this.modId) && FabricUtil.doesModFitsAnyPredicate(this.modId, this.versionPredicates);
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
		return this.versionPredicates.isEmpty() ? "" : " " + Joiner.on(" || ").join(this.versionPredicates);
	}

	@Override
	public String toString()
	{
		return this.modId + this.getVersionPredicatesString();
	}
}
