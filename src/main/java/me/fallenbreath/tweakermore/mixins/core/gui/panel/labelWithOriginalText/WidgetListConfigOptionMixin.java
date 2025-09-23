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

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.malilib.config.IConfigBase;
import fi.dy.masa.malilib.gui.GuiConfigsBase;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOption;
import fi.dy.masa.malilib.gui.widgets.WidgetConfigOptionBase;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptions;
import fi.dy.masa.malilib.gui.widgets.WidgetListConfigOptionsBase;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreIConfigBase;
import me.fallenbreath.tweakermore.gui.TweakerMoreConfigGui;
import me.fallenbreath.tweakermore.gui.TweakerMoreOptionLabel;
import me.fallenbreath.tweakermore.mixins.core.gui.access.WidgetListConfigOptionsAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.function.Function;

@Mixin(WidgetConfigOption.class)
public abstract class WidgetListConfigOptionMixin extends WidgetConfigOptionBase<GuiConfigsBase.ConfigOptionWrapper>
{
	public WidgetListConfigOptionMixin(int x, int y, int width, int height, WidgetListConfigOptionsBase<?, ?> parent, GuiConfigsBase.ConfigOptionWrapper entry, int listIndex)
	{
		super(x, y, width, height, parent, entry, listIndex);
	}

	@Unique
	private boolean isTweakerMoreConfigGui()
	{
		return this.parent instanceof WidgetListConfigOptions && ((WidgetListConfigOptionsAccessor)this.parent).getParent() instanceof TweakerMoreConfigGui;
	}

	@Unique
	private boolean showOriginalTextsThisTime;

	@WrapWithCondition(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addLabel(IIIII[Ljava/lang/String;)V",
					remap = false
			),
			remap = false
	)
	private boolean useMyBetterOptionLabelForTweakerMore(
			WidgetConfigOption self, int x, int y, int width, int height, int textColor, String[] lines,
			@Local(argsOnly = true) IConfigBase config
	)
	{
		if (isTweakerMoreConfigGui() || TweakerMoreConfigs.APPLY_TWEAKERMORE_OPTION_LABEL_GLOBALLY.getBooleanValue())
		{
			if (lines != null && lines.length == 1)
			{
				TweakerMoreOptionLabel label = this.createTweakerMoreOptionLabel(config, x, y, width, height, textColor, lines);
				this.showOriginalTextsThisTime = label.shouldShowOriginalLines();
				return false;
			}
		}

		this.showOriginalTextsThisTime = false;
		return true;
	}

	@Unique
	private TweakerMoreOptionLabel createTweakerMoreOptionLabel(IConfigBase config, int x, int y, int width, int height, int textColor, String[] lines)
	{
		Function<String, String> modifier = s -> s;
		if (config instanceof TweakerMoreIConfigBase)
		{
			modifier = ((TweakerMoreIConfigBase)config).getGuiDisplayLineModifier();
		}

		TweakerMoreOptionLabel label = new TweakerMoreOptionLabel(x, y, width, height, textColor, lines, new String[]{config.getName()}, modifier);
		this.addWidget(label);
		return label;
	}

	@ModifyArg(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addConfigComment(IIIILjava/lang/String;)V",
					remap = false
			),
			index = 1,
			remap = false
	)
	private int tweaksCommentHeight_minY(int y)
	{
		if (this.showOriginalTextsThisTime)
		{
			y -= 4;
		}
		return y;
	}

	@ModifyArg(
			method = "addConfigOption",
			at = @At(
					value = "INVOKE",
					target = "Lfi/dy/masa/malilib/gui/widgets/WidgetConfigOption;addConfigComment(IIIILjava/lang/String;)V",
					remap = false
			),
			index = 3,
			remap = false
	)
	private int tweaksCommentHeight_height(int height)
	{
		if (this.showOriginalTextsThisTime)
		{
			height += 6;
		}
		return height;
	}
}
