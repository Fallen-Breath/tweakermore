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

package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic.compat.carpettisaddition;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12006
//$$ import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
//#else
import net.minecraft.util.MetricsData;
//#endif

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(value = ModIds.carpet_tis_addition, versionPredicates = ">=1.41.0"))
@Pseudo
@Mixin(targets = "carpettisaddition.helpers.rule.syncServerMsptMetricsData.ServerMsptMetricsDataSyncer")
public abstract class ServerMsptMetricsDataSyncerMixin
{
	@Shadow(remap = false)
	private
	//#if MC >= 12006
	//$$ MultiValueDebugSampleLogImpl
	//#else
	MetricsData
	//#endif
			metricsData;

	@Inject(
			method = {"<init>", "reset"},
			at = @At("TAIL"),
			remap = false
	)
	private void serverMsptMetricsStatistic_hookMetricsData(CallbackInfo ci)
	{
		((MetricsDataWithRichStatistic)this.metricsData).enableRichStatistic$TKM();
	}
}
