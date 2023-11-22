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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.customPayloadMemoryLeakFix;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.util.PacketByteBuf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = "<1.20.2"))
@Mixin(CustomPayloadS2CPacket.class)
public abstract class CustomPayloadS2CPacketMixin
{
	@Shadow private PacketByteBuf data;

	@Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("TAIL"))
	private void customPayloadMemoryLeakFix_releaseTheNettyByteBuf(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.CUSTOM_PAYLOAD_MEMORY_LEAK_FIX.getBooleanValue())
		{
			if (this.data != null && this.data.refCnt() > 0)
			{
				this.data.release();
			}
		}
	}
}
