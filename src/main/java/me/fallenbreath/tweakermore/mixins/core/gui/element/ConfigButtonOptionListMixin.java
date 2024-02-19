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

package me.fallenbreath.tweakermore.mixins.core.gui.element;

import fi.dy.masa.malilib.config.IConfigOptionList;
import fi.dy.masa.malilib.gui.button.ButtonGeneric;
import fi.dy.masa.malilib.gui.button.ConfigButtonOptionList;
import me.fallenbreath.tweakermore.gui.ConfigButtonOptionListHovering;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConfigButtonOptionList.class)
public abstract class ConfigButtonOptionListMixin extends ButtonGeneric implements ConfigButtonOptionListHovering
{
	public ConfigButtonOptionListMixin(int x, int y, int width, boolean rightAlign, String translationKey, Object... args)
	{
		super(x, y, width, rightAlign, translationKey, args);
	}

	@Shadow(remap = false) @Final private IConfigOptionList config;
	@Shadow(remap = false) public abstract void updateDisplayString();

	@Unique
	private boolean enableValueHovering = false;

	@Override
	public void setEnableValueHovering$TKM()
	{
		this.enableValueHovering = true;
		this.updateDisplayString();
	}

	@Inject(method = "updateDisplayString", at = @At("TAIL"), remap = false)
	private void makeSomeValueHovering$TKM(CallbackInfo ci)
	{
		if (this.enableValueHovering)
		{
			this.setHoverStrings(this.makeHoveringLines(this.config));
		}
	}
}
