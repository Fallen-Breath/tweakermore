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

package me.fallenbreath.tweakermore.impl.mc_tweaks.yeetServerIpReversedDnsLookup;

import com.google.common.net.InetAddresses;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class InetAddressPatcher
{
	@SuppressWarnings("UnstableApiUsage")
	public static InetAddress patch(String hostName, InetAddress addr) throws UnknownHostException
	{
		if (TweakerMoreConfigs.YEET_SERVER_IP_REVERSED_DNS_LOOKUP.getBooleanValue())
		{
			if (InetAddresses.isInetAddress(hostName))
			{
				InetAddress patched = InetAddress.getByAddress(addr.getHostAddress(), addr.getAddress());
				TweakerMoreMod.LOGGER.debug("Patching ip-only InetAddresses from {} to {}", addr, patched);
				addr = patched;
			}
		}
		return addr;
	}
}
