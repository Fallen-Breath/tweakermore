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

package me.fallenbreath.tweakermore.impl.mod_tweaks.lmRemoveEntityCommand;

import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.tree.CommandNode;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.LitematicaRemoveEntityCommandPolicy;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.server.command.CommandSource;

import java.util.Collections;
import java.util.Optional;

public class LitematicaRemoveEntityCommandOverrider
{
	public static final String DEFAULT_COMMAND = "kill";

	public static void onCommandOverrideChanged(ConfigString configString)
	{
		assert configString == TweakerMoreConfigs.LM_REMOVE_ENTITY_COMMAND;
		String command = TweakerMoreConfigs.LM_REMOVE_ENTITY_COMMAND.getStringValue();
		if (!command.isEmpty())
		{
			isCommandValid(command).ifPresent(valid -> {
				if (!valid)
				{
					InfoUtils.showGuiMessage(Message.MessageType.WARNING, "tweakermore.impl.lmRemoveEntityCommand.invalid_command", command);
				}
			});
		}
	}

	/**
	 * @return true / false: if is valid; null: unknown
	 */
	private static Optional<Boolean> isCommandValid(String command)
	{
		ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
		if (networkHandler != null)
		{
			//#if MC >= 12106
			//$$ var node =  // the type is CommandNode<ClientCommandSource>
			//#else
			CommandNode<CommandSource> node =
			//#endif
					networkHandler.getCommandDispatcher().findNode(Collections.singleton(command));
			return Optional.of(node != null && node.canUse(networkHandler.getCommandSource()));
		}
		return Optional.empty();
	}

	public static String applyOverride(String commandFormatter)
	{
		if (TweakerMoreConfigs.LM_REMOVE_ENTITY_COMMAND.isModified())
		{
			if (commandFormatter.contains(DEFAULT_COMMAND))
			{
				String command = TweakerMoreConfigs.LM_REMOVE_ENTITY_COMMAND.getStringValue();
				if (isCommandValid(command).orElse(false) || TweakerMoreConfigs.LM_REMOVE_ENTITY_COMMAND_POLICY.getOptionListValue() == LitematicaRemoveEntityCommandPolicy.ALWAYS)
				{
					commandFormatter = commandFormatter.replaceFirst(DEFAULT_COMMAND, command);
				}
			}
			else
			{
				TweakerMoreMod.LOGGER.warn("Command formatter {} doesn't contain \"{}\"", commandFormatter, DEFAULT_COMMAND);
			}
		}
		return commandFormatter;
	}
}
