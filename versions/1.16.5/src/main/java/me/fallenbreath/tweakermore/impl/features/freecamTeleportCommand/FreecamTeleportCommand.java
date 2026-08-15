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

package me.fallenbreath.tweakermore.impl.features.freecamTeleportCommand;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.spectatorTeleportCommand.EntitySelectorHack;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.Messenger;
import me.fallenbreath.tweakermore.util.compat.tweakeroo.TweakerooAccess;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Arrays;
import java.util.Collection;

import static net.minecraft.commands.arguments.EntityArgument.entity;

//#if MC >= 11900
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#endif

public class FreecamTeleportCommand
{
	private static boolean inited = false;

	public static void init()
	{
		if (inited)
		{
			return;
		}
		inited = true;

		String fapiModId = "fabric-command-api-v" +
				//#if MC >= 11900
				//$$ 2;
				//#else
				1;
				//#endif
		if (!FabricUtils.isModLoaded(fapiModId))
		{
			TweakerMoreMod.LOGGER.warn("{} does not exist, FreecamTeleportCommand init skipped", fapiModId);
			return;
		}

		//#if MC >= 11900
		//$$ ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> registerCommand(dispatcher));
		//#else
		registerCommand(ClientCommandManager.DISPATCHER);
		//#endif
	}

	private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
	{
		String prefix = TweakerMoreConfigs.FREECAM_TELEPORT_COMMAND_PREFIX.getStringValue();
		LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal(prefix)
				.requires(s -> TweakerMoreConfigs.FREECAM_TELEPORT_COMMAND.getBooleanValue())
				.then(ClientCommandManager.argument("location", FreecamPositionArgument.position())
						.executes(c -> teleport(c.getSource(), getPosition(c, "location"))))
				.then(ClientCommandManager.argument("target", entity())
						.executes(c -> teleport(c.getSource(), getEntitySelector(c, "target"))));
		dispatcher.register(root);
		TweakerMoreMod.LOGGER.debug("(freecamTeleportCommand) Registered client-side command with prefix '{}'", prefix);
	}

	private static FreecamPosition getPosition(CommandContext<FabricClientCommandSource> context, String name)
	{
		return context.getArgument(name, FreecamPosition.class);
	}

	private static EntitySelector getEntitySelector(CommandContext<FabricClientCommandSource> context, String name)
	{
		return context.getArgument(name, EntitySelector.class);
	}

	private static int teleport(FabricClientCommandSource source, FreecamPosition position)
	{
		LocalPlayer freecam = TweakerooAccess.getFreecamEntity();
		if (freecam == null)
		{
			source.sendError(Messenger.tr("tweakermore.impl.freecamTeleportCommand.need_freecam"));
			return 0;
		}

		Vec3 destination = position.resolve(getPosition(freecam));
		freecam.setPos(destination.x, destination.y, destination.z);
		TweakerMoreMod.LOGGER.info("Teleported freecam to {}", destination);
		return 1;
	}

	private static int teleport(FabricClientCommandSource source, EntitySelector selector) throws CommandSyntaxException
	{
		LocalPlayer freecam = TweakerooAccess.getFreecamEntity();
		if (freecam == null)
		{
			source.sendError(Messenger.tr("tweakermore.impl.freecamTeleportCommand.need_freecam"));
			return 0;
		}

		Entity target = EntitySelectorHack.getSingleEntity(selector, source, getPosition(freecam));
		Vec3 destination = getPosition(target);
		freecam.setPos(destination.x, destination.y, destination.z);
		TweakerMoreMod.LOGGER.info("Teleported freecam to entity {} at {}", target.getUUID(), destination);
		return 1;
	}

	private static Vec3 getPosition(Entity entity)
	{
		return new Vec3(entity.getX(), entity.getY(), entity.getZ());
	}

	private static class FreecamPositionArgument implements ArgumentType<FreecamPosition>
	{
		private static final Collection<String> EXAMPLES = Arrays.asList("0 0 0", "~ ~ ~", "~1 ~ ~-2");

		public static FreecamPositionArgument position()
		{
			return new FreecamPositionArgument();
		}

		@Override
		public FreecamPosition parse(StringReader reader) throws CommandSyntaxException
		{
			int start = reader.getCursor();
			WorldCoordinate x = WorldCoordinate.parseDouble(reader, true);
			if (!hasNextCoordinate(reader))
			{
				reader.setCursor(start);
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(reader);
			}
			reader.skipWhitespace();
			WorldCoordinate y = WorldCoordinate.parseDouble(reader, false);
			if (!hasNextCoordinate(reader))
			{
				reader.setCursor(start);
				throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerExpectedDouble().createWithContext(reader);
			}
			reader.skipWhitespace();
			WorldCoordinate z = WorldCoordinate.parseDouble(reader, true);
			return new FreecamPosition(x, y, z);
		}

		private static boolean hasNextCoordinate(StringReader reader)
		{
			return reader.canRead() && reader.peek() == ' ';
		}

		@Override
		public Collection<String> getExamples()
		{
			return EXAMPLES;
		}
	}

	private static class FreecamPosition
	{
		private final WorldCoordinate x;
		private final WorldCoordinate y;
		private final WorldCoordinate z;

		private FreecamPosition(WorldCoordinate x, WorldCoordinate y, WorldCoordinate z)
		{
			this.x = x;
			this.y = y;
			this.z = z;
		}

		private Vec3 resolve(Vec3 origin)
		{
			return new Vec3(this.x.get(origin.x), this.y.get(origin.y), this.z.get(origin.z));
		}
	}
}
