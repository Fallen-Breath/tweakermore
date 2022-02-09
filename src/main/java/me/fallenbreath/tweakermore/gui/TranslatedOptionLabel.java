package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetLabel;

import java.util.stream.IntStream;

/**
 * Only works perfectly with 1 line tho
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetLabelMixin} for more details
 */
public class TranslatedOptionLabel extends WidgetLabel
{
	public static final double TRANSLATION_SCALE = 0.65;
	private final String[] translationKeys;
	private final boolean showTranslation;

	public TranslatedOptionLabel(int x, int y, int width, int height, int textColor, String[] lines)
	{
		super(x, y, width, height, textColor, lines);
		this.translationKeys = lines;
		this.showTranslation = IntStream.range(0, lines.length).anyMatch(i -> !lines[i].equals(this.labels.get(i)));
	}

	public String[] getTranslationKeys()
	{
		return translationKeys;
	}

	public boolean shouldShowTranslation()
	{
		return showTranslation;
	}
}
