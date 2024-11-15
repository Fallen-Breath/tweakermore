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

package me.fallenbreath.tweakermore.util.mixin;

import me.fallenbreath.conditionalmixin.api.mixin.RestrictiveMixinConfigPlugin;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.ModIds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixins;

import java.util.List;
import java.util.Set;

public class TweakerMoreMixinConfigPlugin extends RestrictiveMixinConfigPlugin
{
	private final Logger LOGGER = LogManager.getLogger();

	@Override
	protected void onRestrictionCheckFailed(String mixinClassName, String reason)
	{
		LOGGER.debug("[TweakerMore] Disabled mixin {} due to {}", mixinClassName, reason);
	}

	@Override
	public String getRefMapperConfig()
	{
		return null;
	}

	@Override
	public void acceptTargets(Set<String> myTargets, Set<String> otherTargets)
	{
	}

	@Override
	public List<String> getMixins()
	{
		// Fabric ASM (mm) direct MixinConfigPlugin getMixins() ---mm:early_risers--> optiFabric OptifabricSetup
		// We need to wait until optifine loading stuffs to be done to add our optifine mixins
		if (FabricUtils.isModLoaded(ModIds.optifine))  // a rough check
		{
			LOGGER.info("[TweakerMore] loading optifine mixin");
			Mixins.addConfiguration("tweakermore.optifine_mixins.json");
		}
		return null;
	}
}
