/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.core.gui.access;

import fi.dy.masa.malilib.gui.GuiTextFieldGeneric;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import me.fallenbreath.tweakermore.gui.WidgetSearchBarSearchOpenStateAccess;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

// Remove this hack before 1.21.9 release?
// -- nope, cuz malilib 1.21.8-0.25.6 is released with the `setSearchOpen` signature change issue
@Mixin(WidgetSearchBar.class)
public abstract class WidgetSearchBarMixin implements WidgetSearchBarSearchOpenStateAccess
{
	@Shadow(remap = false)
	protected boolean searchOpen;

	@Shadow(remap = false)
	@Final
	protected GuiTextFieldGeneric searchBox;

	@Override
	public void setSearchOpen$TKM(boolean isOpen)
	{
		this.searchOpen = isOpen;

		if (this.searchOpen)
		{
			this.searchBox.setFocused(true);
		}
	}
}
