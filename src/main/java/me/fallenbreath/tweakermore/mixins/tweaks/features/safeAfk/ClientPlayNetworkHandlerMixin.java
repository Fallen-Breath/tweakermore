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

package me.fallenbreath.tweakermore.mixins.tweaks.features.safeAfk;

import me.fallenbreath.tweakermore.impl.features.safeAfk.SafeAfkHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12002
//$$ import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
//$$ import net.minecraft.client.multiplayer.CommonListenerCookie;
//$$ import net.minecraft.network.Connection;
//#else
import org.spongepowered.asm.mixin.Shadow;
//#endif

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin
		//#if MC >= 12002
		//$$ extends ClientCommonPacketListenerImpl
		//#endif
{
	//#if MC >= 12002
	//$$ protected ClientPlayNetworkHandlerMixin(Minecraft client, Connection connection, CommonListenerCookie connectionState)
	//$$ {
	//$$ 	super(client, connection, connectionState);
	//$$ }
	//#else
	@Shadow private Minecraft minecraft;
	//#endif

	@Inject(method = "handleSetHealth", at = @At("TAIL"))
	private void tweakerMoreSafeAfkHook(CallbackInfo ci)
	{
		SafeAfkHelper.onHealthUpdate(this.minecraft);
	}

	@Inject(method = {"cleanup", "handleRespawn"}, at = @At("TAIL"))
	private void resetLastHurtGameTime(CallbackInfo ci)
	{
		SafeAfkHelper.resetHurtTime();
	}
}
