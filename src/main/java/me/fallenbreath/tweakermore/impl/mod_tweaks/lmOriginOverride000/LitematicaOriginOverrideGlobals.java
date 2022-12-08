package me.fallenbreath.tweakermore.impl.mod_tweaks.lmOriginOverride000;

import net.minecraft.util.math.BlockPos;

public class LitematicaOriginOverrideGlobals
{
	public static final String ORIGIN_OVERRIDE_FLAG = "OriginOverride000";
	public static final BlockPos POS_000 = new BlockPos(0, 0, 0);

	public static final ThreadLocal<Boolean> IS_USER_LOADING_SCHEMATIC = ThreadLocal.withInitial(() -> false);
}
