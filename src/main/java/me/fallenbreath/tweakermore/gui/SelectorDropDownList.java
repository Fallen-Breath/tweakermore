package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

/**
 * Compares to WidgetDropDownList:
 * - Accepts IStringValue as generic value only
 * - Added entry change listener hook (See this class)
 * - Use opaque background when rendering
 * - Show 1px left borderline
 * - Does not respond to key input
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetDropDownListMixin}
 */
public class SelectorDropDownList<T extends IStringValue> extends WidgetDropDownList<T>
{
	@Nullable
	protected Consumer<T> entryChangeListener = null;

	public SelectorDropDownList(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries, List<T> entries)
	{
		super(x, y, width, height, maxHeight, maxVisibleEntries, entries, IStringValue::getStringValue);
	}

	public void setEntryChangeListener(@Nullable Consumer<T> entryChangeListener)
	{
		this.entryChangeListener = entryChangeListener;
	}

	@Override
	protected void setSelectedEntry(int index)
	{
		super.setSelectedEntry(index);
		this.onEntryChanged();
	}

	@Override
	public WidgetDropDownList<T> setSelectedEntry(T entry)
	{
		WidgetDropDownList<T> ret = super.setSelectedEntry(entry);
		this.onEntryChanged();
		return ret;
	}

	private void onEntryChanged()
	{
		if (this.entryChangeListener != null)
		{
			this.entryChangeListener.accept(this.getSelectedEntry());
		}
	}

	@Override
	protected boolean onKeyTypedImpl(int keyCode, int scanCode, int modifiers)
	{
		return false;
	}

	@Override
	protected boolean onCharTypedImpl(char charIn, int modifiers)
	{
		return false;
	}

	@Override
	protected String getDisplayString(T entry)
	{
		if (entry == null)
		{
			return StringUtils.translate("tweakermore.gui.selector_drop_down_list.all");
		}
		return super.getDisplayString(entry);
	}
}
