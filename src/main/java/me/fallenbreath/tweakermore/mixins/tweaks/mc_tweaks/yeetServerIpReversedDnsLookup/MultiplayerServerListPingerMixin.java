/*
 * This file is part of the Carpet TIS Addition project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Carpet TIS Addition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Carpet TIS Addition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Carpet TIS Addition.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.yeetServerIpReversedDnsLookup;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.fallenbreath.tweakermore.impl.mc_tweaks.yeetServerIpReversedDnsLookup.InetAddressPatcher;
import net.minecraft.client.multiplayer.ServerStatusPinger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.net.InetAddress;
import java.net.UnknownHostException;

// used in mc < 1.17
@Mixin(ServerStatusPinger.class)
public abstract class MultiplayerServerListPingerMixin
{
	@WrapOperation(
			method = "pingServer",
			at = @At(
					value = "INVOKE",
					target = "Ljava/net/InetAddress;getByName(Ljava/lang/String;)Ljava/net/InetAddress;"
			)
	)
	private InetAddress setHostnameToIpAddressToAvoidReversedDnsLookupOnGetHostname_ping(String hostName, Operation<InetAddress> original) throws UnknownHostException
	{
		return InetAddressPatcher.patch(hostName, original.call(hostName));
	}
}
