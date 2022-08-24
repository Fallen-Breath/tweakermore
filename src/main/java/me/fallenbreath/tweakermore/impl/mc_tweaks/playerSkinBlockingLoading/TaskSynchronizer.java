package me.fallenbreath.tweakermore.impl.mc_tweaks.playerSkinBlockingLoading;

import net.minecraft.client.MinecraftClient;

import java.util.concurrent.CompletableFuture;

public class TaskSynchronizer
{
	public static void runOnClientThread(Runnable runnable)
	{
		MinecraftClient.getInstance().send(runnable);
	}

	/**
	 * Submit a runnable to the client , to make the client wait for the execution of the given task
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
}
