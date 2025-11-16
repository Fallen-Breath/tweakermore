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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.maxChatHudHeight;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.components.ChatComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ChatComponent.class)
public abstract class ChatHudMixin
{
	@ModifyConstant(
			method = "getHeight(D)I",
			constant = @Constant(doubleValue = 160.0D),
			require = 0
	)
	private static double maxChatHudHeight(double maxHeight)
	{
		if (TweakerMoreConfigs.MAX_CHAT_HUD_HEIGHT.isModified())
		{
			maxHeight = TweakerMoreConfigs.MAX_CHAT_HUD_HEIGHT.getIntegerValue();
		}
		return maxHeight;
	}
}
