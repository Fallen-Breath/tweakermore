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

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.itemTooltipHideUntilMouseMove.ContainerScreenWithToolTipHideHelper;
import me.fallenbreath.tweakermore.impl.mc_tweaks.itemTooltipHideUntilMouseMove.MalilibRenderTooltipLastCancelHelper;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * See {@link fi.dy.masa.malilib.mixin.MixinInventoryScreen#malilib_onPostInventoryStatusEffects}
 * We need to wrap its injection and update the shouldCancel flag.
 * <p>
 * Notes for the injection order:
 * 1. `InventoryScreenMixins$Start`      (priority 500)
 * 2. malilib's `MixinInventoryScreen`  (priority 1000)
 * 3. `InventoryScreenMixins$End`         (priority 2000)
 */
public abstract class InventoryScreenMixins
{
	private static final String SHARE_NS = "me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.itemTooltipHideUntilMouseMove.compat.malilib.InventoryScreenMixin";

	@Restriction(require = @Condition(value = ModIds.malilib, versionPredicates = ">=0.22.0-sakura.4"))
	@Mixin(value = InventoryScreen.class, priority = 500)
	public static abstract class Start
	{
		@Inject(method = "render", at = @At("TAIL"))
		private void itemTooltipHideUntilMouseMove_malilibHackBegin(
				CallbackInfo ci,
				@Local(ordinal = 0, argsOnly = true) int mouseX,
				@Local(ordinal = 1, argsOnly = true) int mouseY,
				@Share(value = "flagSet", namespace = SHARE_NS) LocalBooleanRef flagSet
		)
		{
			if (TweakerMoreConfigs.ITEM_TOOLTIP_HIDE_UNTIL_MOUSE_MOVE.getBooleanValue())
			{
				InventoryScreen self = (InventoryScreen)(Object)this;
				if (self instanceof ContainerScreenWithToolTipHideHelper withHelper)
				{
					var helper = withHelper.getToolTipHideHelper$TKM();
					if (helper != null && helper.shouldHide(mouseX, mouseY))
					{
						MalilibRenderTooltipLastCancelHelper.shouldCancel.set(true);
						flagSet.set(true);
					}
				}
			}
		}
	}

	@Restriction(require = @Condition(value = ModIds.malilib, versionPredicates = ">=0.22.0-sakura.4"))
	@Mixin(value = InventoryScreen.class, priority = 2000)
	public static abstract class End
	{
		@Inject(method = "render", at = @At("TAIL"))
		private void itemTooltipHideUntilMouseMove_malilibHackEnd(
				CallbackInfo ci,
				@Share(value = "flagSet", namespace = SHARE_NS) LocalBooleanRef flagSet
		)
		{
			if (flagSet.get())
			{
				MalilibRenderTooltipLastCancelHelper.shouldCancel.remove();
			}
		}
	}
}
