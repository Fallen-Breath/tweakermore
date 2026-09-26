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

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.MetricsDataWithRichStatistic;
import me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic.RichStatisticManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.client.gui.components.debugchart.TpsDebugChart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

//#if MC >= 12006
//$$ import net.minecraft.util.debugchart.LocalSampleLogger;
//#else
import net.minecraft.util.SampleLogger;
//#endif

/**
 * <= mc1.20.1: subproject 1.15.2 (main project)
 * >= mc1.20.2: subproject 1.20.2        <--------
 */
@Mixin(DebugScreenOverlay.class)
public abstract class DebugHudMixin
{
	// ============================ enabling ============================

	@Shadow @Final private
	//#if MC >= 12006
	//$$ LocalSampleLogger
	//#else
	SampleLogger
	//#endif
			tickTimeLogger;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void enableRichStatisticForTickNanosLog(CallbackInfo ci)
	{
		((MetricsDataWithRichStatistic)this.tickTimeLogger).enableRichStatistic$TKM();
	}

	// ============================ render ============================

	@Shadow @Final private TpsDebugChart tpsChart;

	@ModifyArgs(
			//#if MC >= 26.1
			//$$ method = "extractRenderState",
			//#elseif MC >= 12103
			//$$ method = "render",
			//#else
			method = "method_51746",  // lambda method in render()
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 26.3
					//$$ target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;III)V"
					//#elseif MC >= 26.1
					//$$ target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"
					//#else
					target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;drawChart(Lnet/minecraft/client/gui/GuiGraphics;II)V"
					//#endif
			)
	)
	private void serverMsptMetricsStatistic_modify(
			Args args,
			@Share("originMetricsData") LocalRef<Object> originMetricsData,
			@Share("tickChartX") LocalIntRef tickChartX,
			@Share("tickChartWidth") LocalIntRef tickChartWidth
	)
	{
		DebugChartAccessor chart = (DebugChartAccessor)this.tpsChart;
		//#if MC >= 12006
		//$$ var chartLog = chart.getLog();
		//$$ if (!(chartLog instanceof LocalSampleLogger))
		//$$ {
		//$$ 	return;
		//$$ }
		//$$ var metricsData = (LocalSampleLogger)chartLog;
		//#else
		SampleLogger metricsData = chart.getLog();
		//#endif

		RichStatisticManager manager = ((MetricsDataWithRichStatistic)metricsData).getRichStatisticManager$TKM();
		if (manager != null)
		{
			originMetricsData.set(metricsData);
			tickChartX.set(args.get(1));
			tickChartWidth.set(args.get(2));
			chart.setLog(manager.modifyServerMsptMetricsStatistic(metricsData));
		}
	}

	@Inject(
			//#if MC >= 26.1
			//$$ method = "extractRenderState",
			//#elseif MC >= 12103
			//$$ method = "render",
			//#else
			method = "method_51746",  // lambda method in render()
			//#endif
			at = @At(
					value = "INVOKE",
					//#if MC >= 26.3
					//$$ target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;III)V",
					//#elseif MC >= 26.1
					//$$ target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V",
					//#else
					target = "Lnet/minecraft/client/gui/components/debugchart/TpsDebugChart;drawChart(Lnet/minecraft/client/gui/GuiGraphics;II)V",
					//#endif
					shift = At.Shift.AFTER
			)
	)
	private void serverMsptMetricsStatistic_renderExtra(
			GuiGraphics drawContext,
			CallbackInfo ci,
			@Share("originMetricsData") LocalRef<Object> originMetricsData,
			@Share("tickChartX") LocalIntRef tickChartX,
			@Share("tickChartWidth") LocalIntRef tickChartWidth
	)
	{
		if (
				originMetricsData.get() instanceof
				//#if MC >= 12006
				//$$ LocalSampleLogger
				//#else
				SampleLogger
				//#endif
				omd
		)
		{
			RichStatisticManager manager = ((MetricsDataWithRichStatistic)omd).getRichStatisticManager$TKM();
			if (manager != null)
			{
				manager.renderExtraOnDebugHud(drawContext, tickChartX.get(), tickChartWidth.get());
			}

			DebugChartAccessor chart = (DebugChartAccessor)this.tpsChart;
			chart.setLog(omd);
			originMetricsData.set(null);
		}
	}
}
