/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.connectionSimulatedDelay;

import com.google.common.collect.Queues;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

class DelayedRunner
{
	private final Timer timer = new HashedWheelTimer(10, TimeUnit.MILLISECONDS);
	private final Queue<Runnable> queue = Queues.newConcurrentLinkedQueue();

	public void delayedRun(Function<Integer, Integer> delayTransformer, Runnable task)
	{
		int optionValue = TweakerMoreConfigs.CONNECTION_SIMULATED_DELAY.getIntegerValue();
		int delayMs = delayTransformer.apply(optionValue);

		if (delayMs > 0 && TweakerMoreConfigs.CONNECTION_SIMULATED_DELAY.isModified())
		{
			this.queue.add(task);
			this.timer.newTimeout(t -> this.queue.remove().run(), delayMs, TimeUnit.MILLISECONDS);
		}
		else
		{
			task.run();
		}
	}
}
