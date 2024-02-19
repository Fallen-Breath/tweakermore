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

package me.fallenbreath.tweakermore.impl.features.infoView;

import fi.dy.masa.malilib.config.IConfigBoolean;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigOptionList;
import me.fallenbreath.tweakermore.config.options.listentries.InfoViewRenderStrategy;

import java.util.function.Supplier;

public abstract class AbstractInfoViewer implements InfoViewer
{
	protected final IConfigBoolean switchConfig;
	protected final Supplier<InfoViewRenderStrategy> renderStrategySupplier;

	public AbstractInfoViewer(IConfigBoolean switchConfig, Supplier<InfoViewRenderStrategy> renderStrategySupplier)
	{
		this.switchConfig = switchConfig;
		this.renderStrategySupplier = renderStrategySupplier;
	}
	public AbstractInfoViewer(IConfigBoolean switchConfig, TweakerMoreConfigOptionList renderStrategyOption)
	{
		this(switchConfig, () -> (InfoViewRenderStrategy)renderStrategyOption.getOptionListValue());
	}

	@Override
	public boolean isRenderEnabled()
	{
		if (this.switchConfig.getBooleanValue())
		{
			switch (this.renderStrategySupplier.get())
			{
				case HOTKEY_HELD:
					return TweakerMoreConfigs.INFO_VIEW_RENDERING_KEY.isKeybindHeld();
				case ALWAYS:
					return true;
			}
		}
		return false;
	}
}
