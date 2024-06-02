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
import net.minecraft.util.MetricsData;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.LongStream;

//#if MC >= 12006
//$$ import net.minecraft.util.profiler.MultiValueDebugSampleLogImpl;
//#endif

public class MetricsStatistic
{
	private final Type type;
	private final long[] buffer;
	private final List<NewSampleCallback> newSampleCallbacks;

	//#if MC >= 12006
	//$$ private MultiValueDebugSampleLogImpl
	//#else
	private MetricsData
	//#endif
			metricsData;

	private int index;

	public MetricsStatistic(Type type, int n)
	{
		this.type = type;
		this.buffer = new long[n];
		this.newSampleCallbacks = Lists.newArrayList();
		this.reset();
	}

	//#if MC >= 12006
	//$$ public MultiValueDebugSampleLogImpl
	//#else
	public MetricsData
	//#endif
	getMetricsData()
	{
		return this.metricsData;
	}

	public void reset()
	{
		//#if MC >= 12006
		//$$ this.metricsData = new MultiValueDebugSampleLogImpl(MultiValueDebugSampleLogImpl.LOG_SIZE);
		//#else
		this.metricsData = new MetricsData();
		//#endif
		this.index = 0;
	}

	public void addData(long value)
	{
		this.buffer[this.index++] = value;
		if (this.index == this.buffer.length)
		{
			this.harvestBuffer();
		}
	}

	public void addNewSampleCallback(NewSampleCallback callback)
	{
		this.newSampleCallbacks.add(callback);
	}

	private void harvestBuffer()
	{
		long sample = this.type.process(this.buffer);
		this.index = 0;
		this.metricsData.pushSample(sample);
		this.newSampleCallbacks.forEach(callback -> callback.onNewSample(sample));
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

	@FunctionalInterface
	public interface NewSampleCallback
	{
		void onNewSample(long sampleValue);
	}
}
