package me.fallenbreath.tweakermore.impl.connectionSimulatedDelay;

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
