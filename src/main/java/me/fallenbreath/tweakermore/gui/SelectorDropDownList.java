/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * TweakerMore is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * TweakerMore is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with TweakerMore.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.gui;

import fi.dy.masa.malilib.gui.widgets.WidgetDropDownList;
import fi.dy.masa.malilib.interfaces.IStringValue;
import fi.dy.masa.malilib.render.RenderUtils;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.mixins.core.gui.element.WidgetDropDownListMixin;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

//#if MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

/**
 * Compares to WidgetDropDownList:
 * - Accepts IStringValue as generic value only
 * - Added entry change listener hook (See this class)
 * - Use opaque background when rendering
 * - Show 1px left borderline
 * - Does not respond to key input
 * - Supports custom null entry display
 * - Supports hover text, display hover text when the drop-down list is not opened
 * See {@link WidgetDropDownListMixin}
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

	/**
	 * Hover text rendering logic reference: {@link fi.dy.masa.malilib.gui.button.ButtonBase#postRenderHovered}
	 */
	@Override
	public void postRenderHovered(
			int mouseX, int mouseY, boolean selected
			//#if MC >= 11600
			//$$ , MatrixStack matrixStack
			//#endif
	)
	{
		super.postRenderHovered(
				mouseX, mouseY, selected
				//#if MC >= 11600
				//$$ , matrixStack
				//#endif
		);

		if (this.hoverText != null && this.isMouseOver(mouseX, mouseY) && !this.isOpen)
		{
			RenderUtils.drawHoverText(
					mouseX, mouseY, Collections.singletonList(this.hoverText.getStringValue())
					//#if MC >= 11600
					//$$ , matrixStack
					//#endif
			);
			//#if MC >= 11500
			RenderUtils.disableDiffuseLighting();
			//#else
			//$$ RenderUtils.disableItemLighting();
			//#endif
		}
	}
}
