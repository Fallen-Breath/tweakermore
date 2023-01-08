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

package me.fallenbreath.tweakermore.impl.mod_tweaks.ofPlayerExtraModelOverride;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.List;

public class OverrideDefinitions
{
	private static final String RESOURCES_ROOT = "assets/tweakermore/tweak/ofPlayerExtraModel/";

	// TODO: when there are more than 1 overriders, try to make all of them work together -> try to merge those activated configs
	public static final List<OverrideDefinition> OVERRIDE_DEFS = Lists.newArrayList(
			OverrideDefinition.create(TweakerMoreConfigs.OF_WITCH_HAT).
					cfg(RESOURCES_ROOT + "hat_jingy/config.json").
					model(RESOURCES_ROOT + "hat_jingy/model.json").
					texture(RESOURCES_ROOT + "hat_jingy/texture.png"),
			OverrideDefinition.create(TweakerMoreConfigs.OF_SANTA_HAT).
					cfg(RESOURCES_ROOT + "hat_santa/config.json").
					model(RESOURCES_ROOT + "hat_santa/model.json").
					texture(RESOURCES_ROOT + "hat_santa/texture.png")
	);
}
