package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import org.jetbrains.annotations.Nullable;

public class OverrideImpl
{
	public final byte[] cfg;
	public final byte @Nullable [] model;
	public final byte @Nullable [] texture;

	public OverrideImpl(byte[] cfg, byte @Nullable [] model, byte @Nullable [] texture)
	{
		this.cfg = cfg;
		this.model = model;
		this.texture = texture;
	}
}
