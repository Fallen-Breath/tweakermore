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

package me.fallenbreath.tweakermore.impl.mc_tweaks.connectionSimulatedDelay;

import com.google.common.collect.Queues;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class ChannelDelayer extends ChannelInboundHandlerAdapter
{
	private static final Timer TIMER = new HashedWheelTimer();
	private final Queue<Runnable> queue = Queues.newConcurrentLinkedQueue();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
	{
		Runnable task = () -> ctx.fireChannelRead(msg);
		if (TweakerMoreConfigs.CONNECTION_SIMULATED_DELAY.isModified() || !this.queue.isEmpty())
		{
			this.queue.add(task);
			int delayMs = TweakerMoreConfigs.CONNECTION_SIMULATED_DELAY.getIntegerValue();
			TIMER.newTimeout(this::actualChannelRead, delayMs, TimeUnit.MILLISECONDS);
		}
		else
		{
			task.run();
		}
	}

	private void actualChannelRead(Timeout timeout)
	{
		this.queue.remove().run();
	}
}
