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

package me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer.serverDataSyncer;

import com.google.common.collect.Queues;

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
