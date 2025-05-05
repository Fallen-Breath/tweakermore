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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.itemTooltipHideUntilMouseMove.compat.malilib;

import fi.dy.masa.malilib.event.RenderEventHandler;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.itemTooltipHideUntilMouseMove.MalilibRenderTooltipLastCancelHelper;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * {@link fi.dy.masa.malilib.mixin.MixinInventoryScreen} uses default priority 1000
 * We need to mixin into its merged method, so here comes priority 2000
 */
@Restriction(require = @Condition(value = ModIds.malilib, versionPredicates = ">=0.22.0-sakura.4"))
@Mixin(value = RenderEventHandler.class, priority = 2000)
public abstract class RenderEventHandlerMixin
{
	@Inject(
			method = "onRenderTooltipLast",
			at = @At("HEAD"),
			cancellable = true,
			remap = false
	)
	private void itemTooltipHideUntilMouseMove_cancelMalilibRenderTooltipLastCall(CallbackInfo ci)
	{
		if (TweakerMoreConfigs.ITEM_TOOLTIP_HIDE_UNTIL_MOUSE_MOVE.getBooleanValue())
		{
			if (MalilibRenderTooltipLastCancelHelper.shouldCancel.get())
			{
				ci.cancel();
			}
		}
	}
}
