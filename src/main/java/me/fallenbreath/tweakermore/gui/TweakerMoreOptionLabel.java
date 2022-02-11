package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import me.fallenbreath.tweakermore.util.StringUtil;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Only works perfectly with 1 line tho
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetLabelMixin} for more details
 */
public class TweakerMoreOptionLabel extends WidgetLabel
{
	public static final double TRANSLATION_SCALE = 0.65;
	private final String[] originalTexts;
	private final boolean showOriginalTexts;

	public TweakerMoreOptionLabel(int x, int y, int width, int height, int textColor, String[] lines, Function<String, String> lineModifier)
	{
		super(x, y, width, height, textColor, lines);
		this.originalTexts = Arrays.stream(lines).map(StringUtil::removeTweakerMoreNameSpacePrefix).toArray(String[]::new);
		boolean showOriginalNames = false;
		for (int i = 0; i < this.originalTexts.length; i++)
		{
			String linesToDisplay = this.labels.get(i);
			if (!this.originalTexts[i].equals(linesToDisplay))
			{
				showOriginalNames = true;
			}
			this.labels.set(i, lineModifier.apply(linesToDisplay));
		}
		this.showOriginalTexts = showOriginalNames;
	}

	public String[] getOriginalTexts()
	{
		return this.originalTexts;
	}

	public boolean shouldShowOriginalTexts()
	{
		return this.showOriginalTexts;
	}
}
