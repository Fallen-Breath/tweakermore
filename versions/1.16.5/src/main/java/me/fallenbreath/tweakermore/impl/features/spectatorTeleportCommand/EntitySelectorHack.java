/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.features.spectatorTeleportCommand;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.mixins.tweaks.features.spectatorTeleportCommand.EntitySelectorAccessor;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Reference: {@link EntitySelector}
 */
public class EntitySelectorHack
{
	public static UUID getSingleEntityUuid(EntitySelector entitySelector, FabricClientCommandSource source) throws CommandSyntaxException
	{
		EntitySelectorAccessor selector = (EntitySelectorAccessor)entitySelector;
		Minecraft mc = source.getClient();
		ClientLevel world = source.getWorld();
		ClientPacketListener networkHandler = mc.getConnection();
		if (networkHandler == null)
		{
			throw EntityArgument.NO_ENTITIES_FOUND.create();
		}

		if (selector.getPlayerName() != null)
		{
			for (PlayerInfo entry : networkHandler.getOnlinePlayers())
			{
				if (entry.getProfile().getName().equalsIgnoreCase(selector.getPlayerName()))
				{
					return entry.getProfile().getId();
				}
			}
			throw EntityArgument.NO_PLAYERS_FOUND.create();
		}
		else if (selector.getUuid() != null)
		{
			boolean found = false;
			for (Entity entity : world.entitiesForRendering())
			{
				if (entity.getUUID().equals(selector.getUuid()))
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				TweakerMoreMod.LOGGER.warn("Entity with uuid '{}' not found in the client world, spectator teleport might fail", selector.getUuid());
			}
			return selector.getUuid();
		}
		else
		{
			Vec3 pos = selector.getPositionOffset().apply(source.getPosition());
			Predicate<Entity> predicate = selector.invokeGetPositionPredicate(
					pos
					//#if MC >= 12100
					//$$ , selector.invokeGetOffsetBox(pos), source.enabledFeatures()
					//#endif
			);
			if (selector.getSenderOnly())
			{
				if (source.getEntity() != null && predicate.test(source.getEntity()))
				{
					return source.getEntity().getUUID();
				}
			}
			else
			{
				List<Entity> candidates = getEntitiesFromWorld(selector, world, pos, predicate);
				if (candidates.size() > 1)
				{
					selector.getSorter().accept(pos, candidates);
				}
				if (!candidates.isEmpty())
				{
					return candidates.get(0).getUUID();
				}
			}
			throw EntityArgument.NO_ENTITIES_FOUND.create();
		}
	}

	@SuppressWarnings("unchecked")
	private static List<Entity> getEntitiesFromWorld(EntitySelectorAccessor selector, ClientLevel world, Vec3 pos, Predicate<Entity> predicate)
	{
		if (selector.getBox() != null)
		{
			return (List<Entity>)world.getEntities(
					//#if MC >= 11700
					//$$ selector.getEntityFilter(),
					//#else
					selector.getType(),
					//#endif
					selector.getBox().move(pos),
					predicate
			);
		}
		else
		{
			List<Entity> entities = Lists.newArrayList();
			for (Entity entity : world.entitiesForRendering())
			{
				if (
						//#if MC >= 11700
						//$$ (selector.getEntityFilter() == null || selector.getEntityFilter().tryCast(entity) != null)
						//#else
						(selector.getType() == null || entity.getType() == selector.getType())
						//#endif
						&& predicate.test(entity)
				)
				{
					entities.add(entity);
				}
			}
			return entities;
		}
	}
}
