package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;

import java.util.function.Function;

/**
 * Only works perfectly with 1 line tho
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetLabelMixin} for more details
 */
public class TweakerMoreOptionLabel extends WidgetLabel
{
	public static final double TRANSLATION_SCALE = 0.65;
	private final String[] originalLines;
	private final boolean showOriginalLines;

	public TweakerMoreOptionLabel(int x, int y, int width, int height, int textColor, String[] displayLines, String[] originalLines, Function<String, String> lineModifier)
	{
		super(x, y, width, height, textColor, displayLines);
		this.originalLines = originalLines;
		boolean showOriginalLines = false;
		for (int i = 0; i < this.originalLines.length; i++)
		{
			String linesToDisplay = this.labels.get(i);
			if (!this.originalLines[i].equals(linesToDisplay))
			{
				showOriginalLines = true;
			}
			this.labels.set(i, lineModifier.apply(linesToDisplay));
		}
		this.showOriginalLines = showOriginalLines;
	}

	public String[] getOriginalLines()
	{
		return this.originalLines;
	}

	public boolean shouldShowOriginalLines()
	{
		return this.showOriginalLines;
	}
}
