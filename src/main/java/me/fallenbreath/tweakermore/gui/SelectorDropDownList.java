package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Compares to WidgetDropDownList:
 * - Accepts IStringValue as generic value only
 * - Added entry change listener hook (See this class)
 * - Use opaque background when rendering
 * - Show 1px left borderline
 * - Does not respond to key input
 * - Supports custom null entry display
 * - Supports hover text, display hover text when the drop-down list is not opened
 * See {@link me.fallenbreath.tweakermore.mixins.core.gui.WidgetDropDownListMixin}
 */
public class SelectorDropDownList<T extends IStringValue> extends WidgetDropDownList<T>
{
	@Nullable
	protected Consumer<T> entryChangeListener = null;
	@Nullable
	private IStringValue nullEntry = null;
	@Nullable
	private IStringValue hoverText = null;

	public SelectorDropDownList(int x, int y, int width, int height, int maxHeight, int maxVisibleEntries, List<T> entries)
	{
		super(x, y, width, height, maxHeight, maxVisibleEntries, entries, IStringValue::getStringValue);
	}

	public void setEntryChangeListener(@Nullable Consumer<T> entryChangeListener)
	{
		this.entryChangeListener = entryChangeListener;
	}

	public void setNullEntry(@Nullable IStringValue nullEntry)
	{
		this.nullEntry = nullEntry;
	}

	public void setHoverText(@Nullable IStringValue hoverText)
	{
		this.hoverText = hoverText;
	}

	public void setHoverText(String translationKey, Object... args)
	{
		this.setHoverText(() -> StringUtils.translate(translationKey, args));
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
		if (entry == null && this.nullEntry != null)
		{
			return this.nullEntry.getStringValue();
		}
		return super.getDisplayString(entry);
	}

	@Override
	public void postRenderHovered(int mouseX, int mouseY, boolean selected)
	{
		super.postRenderHovered(mouseX, mouseY, selected);
		// reference: fi.dy.masa.malilib.gui.button.ButtonBase.postRenderHovered
		if (this.hoverText != null && this.isMouseOver(mouseX, mouseY) && !this.isOpen)
		{
			RenderUtils.drawHoverText(mouseX, mouseY, Collections.singletonList(this.hoverText.getStringValue()));
			//#if MC >= 11500
			RenderUtils.disableDiffuseLighting();
			//#else
			//$$ RenderUtils.disableItemLighting();
			//#endif
		}
	}
}
