package me.fallenbreath.tweakermore.impl.mc_tweaks.fixHoverTextScale;

public class RenderTooltipArgs
{
	public final int xBase;
	public final int yBase;
	public final int maxWidth;
	public final int totalHeight;

	public RenderTooltipArgs(int xBase, int yBase, int maxWidth, int totalHeight)
	{
		this.xBase = xBase;
		this.yBase = yBase;
		this.maxWidth = maxWidth;
		this.totalHeight = totalHeight;
	}
}
