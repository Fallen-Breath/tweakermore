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
import net.minecraft.client.gui.hud.spectator.TeleportToSpecificPlayerSpectatorCommand;
import net.minecraft.text.BaseText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TeleportToSpecificPlayerSpectatorCommand.class)
public abstract class TeleportToSpecificPlayerSpectatorCommandMixin implements CommandEntryWithSpectatorMark
{
	private boolean isSpectator$tkm = false;

	@Override
	public void setIsSpectator(boolean value)
	{
		this.isSpectator$tkm = value;
	}

	/**
	 * Make the spectator name have style like in player list hud (gray italic)
	 */
	@Inject(method = "getName", at = @At("TAIL"))
	private void spectatorTeleportMenuIncludeSpectator_modifyEntryNameForSpectator(CallbackInfoReturnable<Text> cir)
	{
		if (this.isSpectator$tkm)
		{
			BaseText text = (BaseText)cir.getReturnValue();
			Style style = text.getStyle();

			//#if MC >= 11600
			//$$ style = style.withColor(Formatting.GRAY);
			//$$ style = style.withItalic(true);
			//#else
			style.setColor(Formatting.GRAY);
			style.setItalic(true);
			//#endif

			text.setStyle(style);
		}
	}
}
