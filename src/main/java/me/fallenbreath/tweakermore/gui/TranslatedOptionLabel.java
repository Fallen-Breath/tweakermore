package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import me.fallenbreath.tweakermore.util.StringUtil;

import java.util.Arrays;
import java.util.function.Function;

/**
 * Only works perfectly with 1 line tho
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetLabelMixin} for more details
 */
public class TranslatedOptionLabel extends WidgetLabel
{
	public static final double TRANSLATION_SCALE = 0.65;
	private final String[] defaultNames;
	private final boolean showTranslation;

	public TranslatedOptionLabel(int x, int y, int width, int height, int textColor, String[] lines, Function<String, String> lineModifier)
	{
		super(x, y, width, height, textColor, lines);
		this.defaultNames = Arrays.stream(lines).map(StringUtil::removeTweakerMoreNameSpacePrefix).toArray(String[]::new);
		boolean showTranslation = false;
		for (int i = 0; i < this.defaultNames.length; i++)
		{
			String linesToDisplay = this.labels.get(i);
			if (!this.defaultNames[i].equals(linesToDisplay))
			{
				showTranslation = true;
			}
			this.labels.set(i, lineModifier.apply(linesToDisplay));
		}
		this.showTranslation = showTranslation;
	}

	public String[] getDefaultNames()
	{
		return this.defaultNames;
	}

	public boolean shouldShowTranslation()
	{
		return this.showTranslation;
	}
}
