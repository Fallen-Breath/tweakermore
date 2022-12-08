package me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale;

import org.jetbrains.annotations.Nullable;

public interface ScaleableHoverTextRenderer
{
	/**
	 * @param scale null: no scale; other: scale value
	 */
	void setHoverTextScale(@Nullable Double scale);
}
