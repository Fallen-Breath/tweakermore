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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.spectatorTeleportMenuIncludeSpectator;

import me.fallenbreath.tweakermore.impl.mc_tweaks.spectatorTeleportMenuIncludeSpectator.CommandEntryWithSpectatorMark;
import net.minecraft.client.gui.spectator.PlayerMenuItem;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerMenuItem.class)
public abstract class TeleportToSpecificPlayerSpectatorCommandMixin implements CommandEntryWithSpectatorMark
{
	@Unique
	private boolean isSpectator = false;

	@Override
	public void setIsSpectator$TKM(boolean value)
	{
		this.isSpectator = value;
	}

	/**
	 * Make the spectator name have style like in player list hud (gray italic)
	 */
	@Inject(method = "getName", at = @At("TAIL"))
	private void spectatorTeleportMenuIncludeSpectator_modifyEntryNameForSpectator(CallbackInfoReturnable<Component> cir)
	{
		if (this.isSpectator)
		{
			BaseComponent text = (BaseComponent)cir.getReturnValue();
			Style style = text.getStyle();

			//#if MC >= 11600
			//$$ style = style.withColor(Formatting.GRAY);
			//$$ style = style.withItalic(true);
			//#else
			style.setColor(ChatFormatting.GRAY);
			style.setItalic(true);
			//#endif

			text.setStyle(style);
		}
	}
}
