/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.core.gui.panel.searchBar;

import fi.dy.masa.malilib.gui.button.ConfigButtonKeybind;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBar;
import fi.dy.masa.malilib.gui.widgets.WidgetSearchBarConfigs;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.mixins.core.gui.access.WidgetSearchBarConfigsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 1.21.10
//$$ import net.minecraft.client.input.CharacterEvent;
//#endif

@Mixin(WidgetSearchBar.class)
public abstract class WidgetSearchBarCharInputMixin
{
	@Inject(method = "onCharTypedImpl", at = @At("HEAD"), cancellable = true, remap = false)
	private void preventSearchTextInputWhileSettingHotkey(
			//#if MC >= 1.21.10
			//$$ CharacterEvent input,
			//#else
			char charIn, int modifiers,
			//#endif
			CallbackInfoReturnable<Boolean> cir
	)
	{
		WidgetSearchBar searchBar = (WidgetSearchBar)(Object)this;
		if (searchBar instanceof WidgetSearchBarConfigs && TweakerMoreConfigGui.isCurrentSearchBar(searchBar))
		{
			ConfigButtonKeybind button = ((WidgetSearchBarConfigsAccessor)searchBar).getButton();
			if (searchBar.isSearchOpen() && button.isSelected())
			{
				cir.setReturnValue(true);
			}
		}
	}
}
