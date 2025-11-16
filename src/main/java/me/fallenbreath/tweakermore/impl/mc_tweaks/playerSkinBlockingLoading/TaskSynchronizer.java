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

package me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading;

import net.minecraft.client.Minecraft;

import java.util.concurrent.CompletableFuture;

public class TaskSynchronizer
{
	public static void runOnClientThread(Runnable runnable)
	{
		Minecraft.getInstance().tell(runnable);
	}

	/**
	 * Submit a runnable to the client, to make the client wait for the execution of the given task
	 * @param task a to-be-executed task
	 */
	public static Runnable createSyncedTask(Runnable task)
	{
		CompletableFuture<Void> future = new CompletableFuture<>();
		Runnable wrapper = () -> {
			task.run();
			future.complete(null);
		};
		runOnClientThread(future::join);
		return wrapper;
	}

	public static <T> CompletableFuture<T> createSyncedFuture(CompletableFuture<T> completableFuture)
	{
		CompletableFuture<Void> future = new CompletableFuture<>();
		runOnClientThread(future::join);
		return completableFuture.thenApply(v -> {
			future.complete(null);
			return v;
		});
	}
}
