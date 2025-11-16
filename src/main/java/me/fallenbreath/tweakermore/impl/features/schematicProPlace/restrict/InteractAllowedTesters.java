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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace.restrict;

import fi.dy.masa.malilib.util.StringUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.Optional;

public class InteractAllowedTesters
{
	private InteractAllowedTesters() {}

	public interface Impl
	{
		boolean test(Player player, BlockState worldState, BlockState schematicState);
	}

	private static String msg(String id)
	{
		return StringUtils.translate("tweakermore.impl.schematicBlockPlacementRestriction.info.interact_not_allow." + id);
	}

	public static InteractAllowedTester of(String id, Impl testerImpl)
	{
		return (player, worldState, schematicState) ->
				testerImpl.test(player, worldState, schematicState) ?
						/* success */ Optional.empty() :
						/* failed */ Optional.of(msg(id));
	}

	/**
	 * Simple InteractAllowedTester factory that you don't need to care about the failure message
	 * The failure message is: not allowed
	 */
	public static InteractAllowedTester simple(Impl testerImpl)
	{
		return of("not_allowed", testerImpl);
	}

	/**
	 * Always not-allowed interaction
	 */
	public static InteractAllowedTester notAllowed()
	{
		return simple((player, worldState, schematicState) -> false);
	}

	/**
	 * Interaction is allowed only when the block unmatched or given property unmatched
	 */
	public static InteractAllowedTester unequalProperty(Property<?> property)
	{
		return of("property_protection", (player, worldState, schematicState) ->
				worldState.getBlock() != schematicState.getBlock() || worldState.getValue(property) != schematicState.getValue(property)
		);
	}
}
