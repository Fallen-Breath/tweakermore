/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.moveConnectedServerEntryToTop;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.moveConnectedServerEntryToTop.ServerListAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;

import java.util.List;

public class MoveConnectedServerEntryToTopHelper
{
	// should always on the "Renderer Thread"
	public static int selectedIndex = -1;

	public static void onJoinWorld()
	{
		if (!TweakerMoreConfigs.MOVE_CONNECTED_SERVER_ENTRY_TO_TOP.getBooleanValue())
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();
		ServerData currentEntry = mc.getCurrentServer();
		if (currentEntry == null)
		{
			return;
		}

		// ref: net.minecraft.client.options.ServerList#updateServerListEntry
		ServerList serverList = new ServerList(mc);
		serverList.load();

		List<ServerData> servers = ((ServerListAccessor)serverList).getServers();

		int idx = selectedIndex;
		boolean ok = false;
		if (0 <= idx && idx < servers.size())
		{
			// verify the fresh ServerData, in case the file got modified
			ServerData serverInfo = servers.get(idx);
			if (serverInfo.name.equals(currentEntry.name) && serverInfo.ip.equals(currentEntry.ip))
			{
				ok = true;
			}
		}

		if (!ok || idx == -1 || idx == 0)
		{
			return;
		}

		// move idx to the first
		ServerData serverInfo = servers.get(idx);
		for (int i = idx; i > 0; i--)
		{
			serverList.replace(i, servers.get(i - 1));
		}
		serverList.replace(0, serverInfo);

		serverList.save();
	}
}
