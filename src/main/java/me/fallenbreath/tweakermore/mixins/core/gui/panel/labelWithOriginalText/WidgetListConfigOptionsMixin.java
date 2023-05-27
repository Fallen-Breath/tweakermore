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

package me.fallenbreath.tweakermore.mixins.core.gui.panel.labelWithOriginalText;

import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.gui.TweakerMoreOptionLabel;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(WidgetListConfigOptions.class)
public abstract class WidgetListConfigOptionsMixin extends WidgetListConfigOptionsBase<GuiConfigsBase.ConfigOptionWrapper, WidgetConfigOption>
{
	@Shadow(remap = false) @Final protected GuiConfigsBase parent;

	public WidgetListConfigOptionsMixin(int x, int y, int width, int height, int configWidth)
	{
		super(x, y, width, height, configWidth);
	}

	@Inject(
			method = "getMaxNameLengthWrapped",
			at = @At(
					value = "INVOKE",
					target = "Ljava/util/List;iterator()Ljava/util/Iterator;",
					remap = false
			),
			cancellable = true,
			remap = false
	)
	private void useCustomMaxNameLengthIfUsingTweakerMoreOptionLabelAndShowsOriginalText(List<GuiConfigsBase.ConfigOptionWrapper> wrappers, CallbackInfoReturnable<Integer> cir)
	{
		if (this.parent instanceof TweakerMoreConfigGui || TweakerMoreConfigs.APPLY_TWEAKERMORE_OPTION_LABEL_GLOBALLY.getBooleanValue())
		{
			int maxWidth = 0;
			for (GuiConfigsBase.ConfigOptionWrapper wrapper : wrappers)
			{
				if (wrapper.getType() == GuiConfigsBase.ConfigOptionWrapper.Type.CONFIG)
				{
					IConfigBase config = Objects.requireNonNull(wrapper.getConfig());
					maxWidth = Math.max(maxWidth, this.getStringWidth(config.getConfigGuiDisplayName()));
					if (TweakerMoreOptionLabel.willShowOriginalLines(new String[]{config.getConfigGuiDisplayName()}, new String[]{config.getName()}))
					{
						maxWidth = Math.max(maxWidth, (int)Math.ceil(this.getStringWidth(config.getName()) * TweakerMoreOptionLabel.TRANSLATION_SCALE));
					}
				}
			}
			cir.setReturnValue(maxWidth);
		}
	}
}
