package me.fallenbreath.tweakermore.impl.serverDataSyncer;

import com.google.common.collect.Queues;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;

import java.util.Queue;

/**
 * Run tasks with limited amount each tick
 * Overflowed tasks will be queued to be executed in the next tick
 * Not multi-threading-proof
 */
public abstract class LimitedTaskRunner
{
	protected final Queue<Runnable> tasks = Queues.newArrayDeque();
	protected int taskExecutedThisRound = 0;
	private int cooldown = 0;

	protected LimitedTaskRunner()
	{
	}

	protected abstract int getMaxTaskPerTick();

	protected abstract int getTaskExecuteCooldown();

	private boolean runTask(Runnable runnable)
	{
		if (this.taskExecutedThisRound < this.getMaxTaskPerTick())
		{
			this.taskExecutedThisRound++;
			runnable.run();
			return true;
		}
		return false;
	}

	/**
	 * Add a task
	 * If the limit is not exceeded, execute the task immediately
	 * Otherwise the task will be queued
	 */
	public void addTask(Runnable runnable)
	{
		if (!this.runTask(runnable))
		{
			this.tasks.add(runnable);
		}
	}

	protected void tickTask()
	{
		if (!this.tasks.isEmpty() && TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
		{
			TweakerMoreMod.LOGGER.info("D size={} cooldown={}", this.tasks.size(), this.cooldown);
		}

		if (this.cooldown > 0)
		{
			this.cooldown--;
			return;
		}
		this.cooldown = this.getTaskExecuteCooldown();
		this.taskExecutedThisRound = 0;
		while (!this.tasks.isEmpty())
		{
			Runnable task = this.tasks.peek();
			if (this.runTask(task))
			{
				this.tasks.poll();
			}
			else
			{
				break;
			}
		}
	}
}
