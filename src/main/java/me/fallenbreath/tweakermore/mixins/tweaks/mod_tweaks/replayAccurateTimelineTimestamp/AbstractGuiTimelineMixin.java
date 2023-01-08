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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.replayAccurateTimelineTimestamp;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.replay_mod))
@Pseudo
@Mixin(targets = "com.replaymod.lib.de.johni0702.minecraft.gui.element.advanced.AbstractGuiTimeline")
public abstract class AbstractGuiTimelineMixin
{
	@Inject(method = "getTooltipText", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, remap = false)
	private void replayFlySpeedLimit(@Coerce Object renderInfo, CallbackInfoReturnable<String> cir, int ms, int s, int m)
	{
		if (TweakerMoreConfigs.REPLAY_ACCURATE_TIMELINE_TIMESTAMP.getBooleanValue())
		{
			cir.setReturnValue(String.format("%s:%03d", cir.getReturnValue(), ms % 1000));
		}
	}
}
