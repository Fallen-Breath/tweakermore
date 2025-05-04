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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.itemTooltipHideUntilMouseMove;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.itemTooltipHideUntilMouseMove.ItemTooltipHideUntilMouseMoveHelper;
import net.minecraft.client.gui.screen.ingame.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 11600
//$$ import org.spongepowered.asm.mixin.injection.Coerce;
//#endif

@Mixin(ContainerScreen.class)
public abstract class ContainerScreenMixin
{
	@Unique
	private ItemTooltipHideUntilMouseMoveHelper tooltipHideHelper$TKM = null;

	@Inject(method = "drawMouseoverTooltip", at = @At("HEAD"), cancellable = true)
	private void itemTooltipHideUntilMouseMove_impl(
			//#if MC >= 11600
			//$$ @Coerce Object whatever,
			//#endif
			int mouseX, int mouseY, CallbackInfo ci
	)
	{
		if (TweakerMoreConfigs.ITEM_TOOLTIP_HIDE_UNTIL_MOUSE_MOVE.getBooleanValue())
		{
			if (this.tooltipHideHelper$TKM == null)
			{
				this.tooltipHideHelper$TKM = new ItemTooltipHideUntilMouseMoveHelper(mouseX, mouseY);
			}
			if (tooltipHideHelper$TKM.mouseHasMoved)
			{
				return;
			}

			if (this.tooltipHideHelper$TKM.mouseHasNotMoved(mouseX, mouseY))
			{
				ci.cancel();
			}
			else
			{
				this.tooltipHideHelper$TKM.mouseHasMoved = true;
			}
		}
	}
}
