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

package me.fallenbreath.tweakermore.impl.features.serverMsptMetricsStatistic;

import fi.dy.masa.malilib.config.IConfigOptionListEntry;
import fi.dy.masa.malilib.util.StringUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.listentries.ServerMsptMetricsStatisticType;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import net.minecraft.client.Minecraft;

//#if MC >= 12006
//$$ import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
//#else
import net.minecraft.util.FrameTimer;
//#endif


//#if MC >= 12000
//$$ import net.minecraft.client.gui.DrawContext;
//#elseif MC >= 11600
//$$ import net.minecraft.client.util.math.MatrixStack;
//#endif

public class RichStatisticManager
{
	private final MetricsStatistic statGameTick;
	private final MetricsStatistic statSecondAvg;
	private final MetricsStatistic statSecondMax;
	private final MetricsStatistic statMinuteAvg;
	private final MetricsStatistic statMinuteMax;

	public RichStatisticManager()
	{
		this.statGameTick = new MetricsStatistic(MetricsStatistic.Type.MAX, 1);
		this.statSecondAvg = new MetricsStatistic(MetricsStatistic.Type.AVG, 20);  // 20gt -> 1s
		this.statSecondMax = new MetricsStatistic(MetricsStatistic.Type.MAX, 20);  // 20gt -> 1s
		this.statMinuteAvg = new MetricsStatistic(MetricsStatistic.Type.AVG, 60);  // 60s -> 1min
		this.statMinuteMax = new MetricsStatistic(MetricsStatistic.Type.AVG, 60);  // 60s -> 1min

		this.statGameTick.addNewSampleCallback(this.statSecondAvg);
		this.statGameTick.addNewSampleCallback(this.statSecondMax);
		this.statSecondAvg.addNewSampleCallback(this.statMinuteAvg);
		this.statSecondMax.addNewSampleCallback(this.statMinuteMax);
	}

	public void recordGameTickMetrics(long msThisTick)
	{
		this.statGameTick.addData(msThisTick);
	}

	public void recordGameTickMetricsExtra(long ms, int column)
	{
		this.statGameTick.addDataExtra(ms, column);
	}

	public
	//#if MC >= 12006
	//$$ MultiValueDebugSampleLogImpl
	//#else
	FrameTimer
	//#endif
	modifyServerMsptMetricsStatistic(
			//#if MC >= 12006
			//$$ MultiValueDebugSampleLogImpl metricsData
			//#else
			FrameTimer metricsData
			//#endif
	)
	{
		switch ((ServerMsptMetricsStatisticType)TweakerMoreConfigs.SERVER_MSPT_METRICS_STATISTIC_TYPE.getOptionListValue())
		{
			case SECOND_AVG:
				return this.statSecondAvg.getMetricsData();
			case SECOND_MAX:
				return this.statSecondMax.getMetricsData();
			case MINUTE_AVG:
				return this.statMinuteAvg.getMetricsData();
			case MINUTE_MAX:
				return this.statMinuteMax.getMetricsData();

			case GAME_TICK:
			default:
				return metricsData;
		}
	}

	public void renderExtraOnDebugHud(
			//#if MC >= 12000
			//$$ DrawContext drawContext,
			//#elseif MC >= 11600
			//$$ MatrixStack matrices,
			//#endif
			int x, int width
	)
	{
		IConfigOptionListEntry value = TweakerMoreConfigs.SERVER_MSPT_METRICS_STATISTIC_TYPE.getOptionListValue();
		if (value == ServerMsptMetricsStatisticType.USE_VANILLA)
		{
			return;
		}

		String text = StringUtils.translate("tweakermore.impl.serverMsptMetricsStatisticTypeCycle.hint", value.getDisplayName());
		Minecraft mc = Minecraft.getInstance();
		int height =
				//#if MC >= 11500
				mc.getWindow().getGuiScaledHeight();
				//#else
				//$$ mc.window.getScaledHeight();
				//#endif

		//#if MC >= 12000
		//$$ drawContext.drawTextWithShadow(mc.textRenderer,
		//#else
		mc.font.draw(
				//#if MC >= 11600
				//$$ matrices,
				//#endif
		//#endif
				text,
				x + width - 2 - RenderUtils.getRenderWidth(text),
				height - 60 + 2,
				0x00E0E0E0
		);
	}
}
