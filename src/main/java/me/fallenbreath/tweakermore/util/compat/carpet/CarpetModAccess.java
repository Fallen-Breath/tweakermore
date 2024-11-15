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

package me.fallenbreath.tweakermore.util.compat.carpet;

import com.google.common.collect.Lists;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.ReflectionUtils;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class CarpetModAccess
{
	private static final List<Supplier<Optional<Boolean>>> TICK_FROZEN_FLAG_GETTERS = Lists.newArrayList(
			CarpetModAccess::getIsTickFrozenOld,
			CarpetModAccess::getIsTickFrozenNew
	);

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	private static boolean checkCarpetVersion(String predicate)
	{
		return FabricLoader.getInstance().getModContainer("carpet").
				map(ModContainer::getMetadata).
				map(ModMetadata::getVersion).
				map(version -> FabricUtils.doesVersionSatisfyPredicate(version, predicate)).
				orElse(false);
	}

	// works in mc < 1.20 (fabric-carpet < 1.4.108)
	private static Optional<Boolean> getIsTickFrozenOld()
	{
		if (!checkCarpetVersion("<1.4.108"))
		{
			return Optional.empty();
		}

		Optional<Class<?>> tickSpeedOpt = ReflectionUtils.getClass("carpet.helpers.TickSpeed");
		if (!tickSpeedOpt.isPresent())
		{
			TweakerMoreMod.LOGGER.warn("failed to get field carpet.helpers.TickSpeed: {}", tickSpeedOpt);
			return Optional.empty();
		}

		Class<?> tickSpeedClass = tickSpeedOpt.get();
		ReflectionUtils.ValueWrapper<Boolean> flagWrapper = ReflectionUtils.getStaticField(tickSpeedClass, "process_entities");
		if (!flagWrapper.isPresent())
		{
			TweakerMoreMod.LOGGER.warn("failed to get field carpet.helpers.TickSpeed#process_entities: {}", flagWrapper);
			return Optional.empty();
		}

		// !TickSpeed.process_entities --> tick freezing
		return Optional.of(!flagWrapper.get());
	}

	// works in mc >= 1.20 (fabric-carpet >= 1.4.108)
	private static Optional<Boolean> getIsTickFrozenNew()
	{
		if (!checkCarpetVersion(">=1.4.108"))
		{
			return Optional.empty();
		}

		// carpet.fakes.LevelInterface gets mixed-in into ClientWorld
		ClientWorld world = MinecraftClient.getInstance().world;
		if (world == null)
		{
			return Optional.of(false);  // not in world, tick is not frozen ofc
		}

		ReflectionUtils.ValueWrapper<Object> trmWrapper = ReflectionUtils.invoke(world.getClass(), "tickRateManager", world);
		if (!trmWrapper.isPresent())
		{
			TweakerMoreMod.LOGGER.warn("failed to invoked carpet.fakes.LevelInterface#tickRateManager() from a ClientWorld: {}", trmWrapper);
			return Optional.empty();
		}
		Object trm = trmWrapper.get();

		ReflectionUtils.ValueWrapper<Boolean> runsNormally = ReflectionUtils.invoke(trm.getClass(), "runsNormally", trm);
		if (!runsNormally.isPresent())
		{
			TweakerMoreMod.LOGGER.warn("failed to invoked carpet.helpers.TickRateManager#runsNormally(): {}", runsNormally);
			return Optional.empty();
		}

		// !TickRateManager#runsNormally() --> tick freezing
		return Optional.of(!runsNormally.get());
	}

	/**
	 * Returns false when carpet mod is not present —— no tick freezing without carpet ofc
	 */
	public static boolean isTickFrozen()
	{
		for (Supplier<Optional<Boolean>> flagGetter : TICK_FROZEN_FLAG_GETTERS)
		{
			Optional<Boolean> frozen = flagGetter.get();
			if (frozen.isPresent())
			{
				return frozen.get();
			}
		}
		return false;
	}
}
