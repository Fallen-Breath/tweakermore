package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;
import me.fallenbreath.tweakermore.util.StringUtil;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Only works perfectly with 1 line tho
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetLabelMixin} for more details
 */
public class TranslatedOptionLabel extends WidgetLabel
{
	public static final double TRANSLATION_SCALE = 0.65;
	private final String[] defaultNames;
	private final boolean showTranslation;

	public TranslatedOptionLabel(int x, int y, int width, int height, int textColor, String[] lines)
	{
		super(x, y, width, height, textColor, lines);
		this.defaultNames = Arrays.stream(lines).map(StringUtil::removeTweakerMoreNameSpacePrefix).toArray(String[]::new);
		this.showTranslation = IntStream.range(0, this.defaultNames.length).anyMatch(i -> !this.defaultNames[i].equals(this.labels.get(i)));
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
