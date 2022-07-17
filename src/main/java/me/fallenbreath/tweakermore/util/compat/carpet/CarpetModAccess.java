package me.fallenbreath.tweakermore.util.compat.carpet;

import me.fallenbreath.tweakermore.util.ReflectionUtil;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CarpetModAccess
{
	@Nullable
	private static final Supplier<Boolean> cmProcessEntities = ReflectionUtil.getClass("carpet.helpers.TickSpeed").
			map(clazz -> ReflectionUtil.<Boolean>getStaticFieldGetter(clazz, "process_entities")).
			orElse(null);

	/**
	 * Returns false when carpet mod is not present —— no tick freezing without carpet ofc
	 */
	public static boolean isTickFrozen()
	{
		// !TickSpeed.process_entities --> tick freezing
		return cmProcessEntities != null && !cmProcessEntities.get();
	}
}
