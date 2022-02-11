package me.fallenbreath.tweakermore.util.dependency;

import com.google.common.base.Joiner;
import me.fallenbreath.tweakermore.util.FabricUtil;

import java.util.Arrays;
import java.util.List;

public class ModPredicate
{
	public final String modId;
	public final List<String> versionPredicates;

	private ModPredicate(String modId, List<String> versionPredicates)
	{
		this.modId = modId;
		this.versionPredicates = versionPredicates;
	}

	public static ModPredicate of(Condition condition)
	{
		if (condition.type() != Condition.Type.MOD)
		{
			throw new RuntimeException("Only MOD condition type is accepted");
		}
		return new ModPredicate(condition.value(), Arrays.asList(condition.versionPredicates()));
	}

	public boolean satisfies()	{
		return FabricUtil.isModLoaded(this.modId) && (this.versionPredicates.isEmpty() || FabricUtil.doesModFitsAnyPredicate(this.modId, this.versionPredicates));
	}

	public String getVersionPredicatesString()
	{
		return this.versionPredicates.isEmpty() ? "" : " " + Joiner.on(" || ").join(this.versionPredicates);
	}
}
