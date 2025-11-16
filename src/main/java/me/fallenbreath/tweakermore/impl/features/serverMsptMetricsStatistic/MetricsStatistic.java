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

import com.google.common.collect.Lists;
import net.minecraft.util.FrameTimer;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

//#if MC >= 12006
//$$ import net.minecraft.util.debugchart.LocalSampleLogger;
//$$ import net.minecraft.util.debugchart.TpsDebugDimensions;
//#endif

public class MetricsStatistic
{
	private final Type aggregationType;
	private final int aggregationLen;
	private final long[][] buffer;
	private final List<MetricsStatistic> newSampleChildren;

	private static final int COLUMN_NUM =
			//#if MC >= 12006
			//$$ TpsDebugDimensions.values().length;
			//#else
			4;
			//#endif

	//#if MC >= 12006
	//$$ private LocalSampleLogger
	//#else
	private FrameTimer
	//#endif
			metricsData;

	private int index;

	public MetricsStatistic(Type aggregationType, int aggregationLen)
	{
		this.aggregationType = aggregationType;
		this.aggregationLen = aggregationLen;
		this.buffer = new long[COLUMN_NUM][aggregationLen];
		this.newSampleChildren = Lists.newArrayList();
		this.reset();
	}

	//#if MC >= 12006
	//$$ public LocalSampleLogger
	//#else
	public FrameTimer
	//#endif
	getMetricsData()
	{
		return this.metricsData;
	}

	public void reset()
	{
		//#if MC >= 12006
		//$$ this.metricsData = new LocalSampleLogger(COLUMN_NUM);
		//#else
		this.metricsData = new FrameTimer();
		//#endif
		this.index = 0;
	}

	public void addData(long ms)
	{
		this.buffer[0][this.index++] = ms;
		if (this.index == this.aggregationLen)
		{
			this.harvestBuffer();
		}
	}

	public void addDataExtra(long ms, int column)
	{
		if (1 <= column && column < COLUMN_NUM)
		{
			this.buffer[column][this.index] = ms;
		}
	}

	public void addNewSampleCallback(MetricsStatistic child)
	{
		this.newSampleChildren.add(child);
	}

	private void harvestBuffer()
	{
		long[] samples = new long[COLUMN_NUM];
		for (int cl = 0; cl < samples.length; cl++)
		{
			samples[cl] = this.aggregationType.process(this.buffer[cl]);
			Arrays.fill(this.buffer[cl], 0L);
		}
		this.index = 0;

		//#if MC >= 12006
		//$$ for (int cl = 1; cl < COLUMN_NUM; cl++)
		//$$ {
		//$$ 	this.metricsData.push(samples[cl], cl);
		//$$ }
		//#endif
		this.metricsData.logFrameDuration(samples[0]);

		this.newSampleChildren.forEach(child -> {
			for (int cl = 1; cl < samples.length; cl++)
			{
				child.addDataExtra(samples[cl], cl);
			}
			child.addData(samples[0]);
		});
	}

	public enum Type
	{
		AVG(s -> (long)s.average().orElse(0)),
		MAX(s -> s.max().orElse(0));

		private final Function<LongStream, Long> harvester;

		Type(Function<LongStream, Long> harvester)
		{
			this.harvester = harvester;
		}

		public long process(long[] buffer)
		{
			return this.harvester.apply(Arrays.stream(buffer));
		}
	}
}
