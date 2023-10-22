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

package me.fallenbreath.tweakermore.impl.mc_tweaks.connectionProxy;

import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.config.options.ConfigBoolean;
import fi.dy.masa.malilib.config.options.ConfigString;
import fi.dy.masa.malilib.gui.Message;
import fi.dy.masa.malilib.util.InfoUtils;
import io.netty.channel.Channel;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.jetbrains.annotations.Nullable;

import java.net.InetSocketAddress;

public class ConnectionProxyImpl
{
	public static Pair<@Nullable ProxyHandler, String> createProxyHandler(String proxyUrl)
	{
		if (proxyUrl.isEmpty())
		{
			return Pair.of(null, "proxy url is empty");
		}

		// http://user:password@hostname:8080
		try
		{
			String[] s = proxyUrl.split("://");
			if (s.length != 2)
			{
				return Pair.of(null, "bad '://' usage");
			}
			String protocol = s[0];

			s = s[1].split("@");
			String username = null, password = null;
			String rest;
			if (s.length == 2)
			{
				rest = s[1];
				s = s[0].split(":");
				if (s.length != 2)
				{
					return Pair.of(null, "bad ':' usage in the address");
				}
				username = s[0];
				password = s[1];
			}
			else if (s.length == 1)
			{
				rest = s[0];
			}
			else
			{
				return Pair.of(null, "too many '@'");
			}

			s = rest.split(":");
			if (s.length != 2)
			{
				return Pair.of(null, "bad ':' usage in the address");
			}
			String hostname = s[0], port = s[1];


			InetSocketAddress address;
			try
			{
				address = new InetSocketAddress(hostname, Integer.parseInt(port));
			}
			catch (IllegalArgumentException e)
			{
				return Pair.of(null, String.format("bad proxy address, hostname '%s', port '%s'", hostname, port));
			}

			ProxyHandler proxyHandler;
			switch (protocol)
			{
				case "sock4":
					proxyHandler = new Socks4ProxyHandler(address);
					break;
				case "sock5":
					if (username != null && password != null)
					{
						proxyHandler = new Socks5ProxyHandler(address, username, password);
					}
					else
					{
						proxyHandler = new Socks5ProxyHandler(address);
					}
					break;
				case "http":
					if (username != null && password != null)
					{
						proxyHandler = new HttpProxyHandler(address, username, password);
					}
					else
					{
						proxyHandler = new HttpProxyHandler(address);
					}
					break;
				default:
					return Pair.of(null, String.format("unsupported proxy protocol '%s'", protocol));
			}
			return Pair.of(proxyHandler, "");
		}
		catch (Exception exception)
		{
			return Pair.of(null, String.format("unexpected exception '%s'", exception));
		}
	}

	public static void addProxyFor(Channel channel)
	{
		if (!TweakerMoreConfigs.CONNECTION_PROXY.getBooleanValue())
		{
			return;
		}

		String proxyUrl = TweakerMoreConfigs.CONNECTION_PROXY_URL.getStringValue();
		String configName = TweakerMoreConfigs.CONNECTION_PROXY_URL.getName();
		Pair<ProxyHandler, String> pair = createProxyHandler(proxyUrl);
		if (pair.getFirst() != null)
		{
			TweakerMoreMod.LOGGER.info("[{}] Connecting with proxy '{}'", configName, proxyUrl);
			channel.pipeline().addLast(pair.getFirst());
		}
		else
		{
			TweakerMoreMod.LOGGER.error("[{}] Create connection proxy '{}' failed: {}", configName, proxyUrl, pair.getSecond());
		}
	}

	public static void onConfigValueChanged(ConfigString configString)
	{
		Pair<ProxyHandler, String> pair = createProxyHandler(configString.getStringValue());
		if (pair.getFirst() == null)
		{
			InfoUtils.showGuiMessage(Message.MessageType.ERROR, "Bad proxy url: %s", pair.getSecond());
		}
	}
}
