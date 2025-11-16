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

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.FabricUtils;
import me.fallenbreath.tweakermore.util.Messenger;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v1.FabricClientCommandSource;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.protocol.game.ServerboundTeleportToEntityPacket;

import java.util.UUID;

import static net.minecraft.commands.arguments.EntityArgument.entity;

//#if MC >= 11900
//$$ import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
//#endif

public class SpectatorTeleportCommand
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
			TweakerMoreMod.LOGGER.warn("{} does not exist, SpectatorTeleportCommand init skipped", fapiModId);
			return;
		}

		//#if MC >= 11900
		//$$ ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
		//$$ 	registerCommand(dispatcher);
		//$$ });
		//#else
		registerCommand(ClientCommandManager.DISPATCHER);
		//#endif
	}

	private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher)
	{
		String prefix = TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND_PREFIX.getStringValue();
		LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommandManager.literal(prefix)
				.requires(s -> TweakerMoreConfigs.SPECTATOR_TELEPORT_COMMAND.getBooleanValue())
				.then(ClientCommandManager.argument("target", entity())
						.executes(c -> doSpectatorTeleport(c.getSource(), getEntity(c, "target")))
				);
		dispatcher.register(root);
		TweakerMoreMod.LOGGER.debug("(spectatorTeleportCommand) Registered client-side command with prefix '{}'", prefix);
	}

	public static UUID getEntity(CommandContext<FabricClientCommandSource> context, String target) throws CommandSyntaxException
	{
		EntitySelector selector = context.getArgument(target, EntitySelector.class);
		return EntitySelectorHack.getSingleEntityUuid(selector, context.getSource());
	}

	private static int doSpectatorTeleport(FabricClientCommandSource source, UUID target)
	{
		LocalPlayer player = source.getPlayer();
		if (!player.isSpectator())
		{
			source.sendError(Messenger.tr("tweakermore.impl.spectatorTeleportCommand.need_spectator"));
			return 0;
		}

		TweakerMoreMod.LOGGER.info("Performing spectator teleport to entity {}", target);
		player.connection.send(new ServerboundTeleportToEntityPacket(target));
		return 1;
	}
}
