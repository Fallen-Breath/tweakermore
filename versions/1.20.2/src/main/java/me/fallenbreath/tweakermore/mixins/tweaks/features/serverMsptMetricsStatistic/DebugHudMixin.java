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

package me.fallenbreath.tweakermore.mixins.tweaks.features.serverMsptMetricsStatistic;

import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.RichStatisticManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.DebugHud;
import net.minecraft.client.gui.hud.debug.TickChart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//#if MC >= 12006
//$$ import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
//#else
import net.minecraft.util.profiler.PerformanceLog;
//#endif

/**
 * <= mc1.20.1: subproject 1.15.2 (main project)
 * >= mc1.20.2: subproject 1.20.2        <--------
 */
@Mixin(DebugHud.class)
public abstract class DebugHudMixin
{
	// ============================ enabling ============================

	@Shadow @Final private
	//#if MC >= 12006
	//$$ MultiValueDebugSampleLogImpl
	//#else
	PerformanceLog
	//#endif
			tickNanosLog;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void enableRichStatisticForTickNanosLog(CallbackInfo ci)
	{
		((MetricsDataWithRichStatistic)this.tickNanosLog).enableRichStatistic();
	}

	// ============================ render ============================

	@Shadow @Final private TickChart tickChart;

	@Unique private
	//#if MC >= 12006
	//$$ MultiValueDebugSampleLogImpl
	//#else
	PerformanceLog
	//#endif
			originMetricsData$TKM = null;

	@Unique private int tickChartX$TKM = 0;
	@Unique private int tickChartWidth$TKM = 0;

	@ModifyArgs(
			method = "method_51746",  // lambda method in tickChart()
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/debug/TickChart;render(Lnet/minecraft/client/gui/DrawContext;II)V"
			)
	)
	private void serverMsptMetricsStatistic_modify(Args args)
	{
		DebugChartAccessor chart = (DebugChartAccessor)this.tickChart;
		//#if MC >= 12006
		//$$ var chartLog = chart.getLog();
		//$$ if (!(chartLog instanceof MultiValueDebugSampleLogImpl))
		//$$ {
		//$$ 	return;
		//$$ }
		//$$ var metricsData = (MultiValueDebugSampleLogImpl)chartLog;
		//#else
		PerformanceLog metricsData = chart.getLog();
		//#endif

		RichStatisticManager manager = ((MetricsDataWithRichStatistic)metricsData).getRichStatisticManager();
		if (manager != null)
		{
			this.originMetricsData$TKM = metricsData;
			this.tickChartX$TKM = args.get(1);
			this.tickChartWidth$TKM = args.get(2);
			chart.setLog(manager.modifyServerMsptMetricsStatistic(metricsData));
		}
	}

	@Inject(
			method = "method_51746",  // lambda method in tickChart()
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/gui/hud/debug/TickChart;render(Lnet/minecraft/client/gui/DrawContext;II)V",
					shift = At.Shift.AFTER
			)
	)
	private void serverMsptMetricsStatistic_renderExtra(DrawContext drawContext, CallbackInfo ci)
	{
		if (this.originMetricsData$TKM != null)
		{
			RichStatisticManager manager = ((MetricsDataWithRichStatistic)this.originMetricsData$TKM).getRichStatisticManager();
			if (manager != null)
			{
				manager.renderExtraOnDebugHud(drawContext, this.tickChartX$TKM, this.tickChartWidth$TKM);
			}

			DebugChartAccessor chart = (DebugChartAccessor)this.tickChart;
			chart.setLog(this.originMetricsData$TKM);
			this.originMetricsData$TKM = null;
		}
	}
}
