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

package me.fallenbreath.tweakermore.impl.mod_tweaks.serverDataSyncer;

import com.google.common.collect.Maps;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.serverDataSyncer.DoubleInventoryAccessor;
import me.fallenbreath.tweakermore.util.EntityUtils;
import me.fallenbreath.tweakermore.util.compat.carpettisaddition.CarpetTISAdditionAccess;
import me.fallenbreath.tweakermore.util.event.TweakerMoreEvents;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.CompoundContainer;
import net.minecraft.world.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

//#if MC >= 12106
//$$ import net.minecraft.world.level.storage.TagValueInput;
//$$ import net.minecraft.util.ProblemReporter;
//#endif

public class ServerDataSyncer extends LimitedTaskRunner implements IClientTickHandler
{
	private static final ServerDataSyncer INSTANCE = new ServerDataSyncer();

	private final Map<BlockPos, CompletableFuture<CompoundTag>> syncBlockEntityPos = Maps.newHashMap();
	private final Map<Integer, CompletableFuture<CompoundTag>> syncedEntityId = Maps.newHashMap();
	private final DataQueryHandlerPro queryHandler = new DataQueryHandlerPro();

	private ServerDataSyncer()
	{
		TweakerMoreEvents.registerDimensionChangedCallback(this::onDimensionChanged);
	}

	@Override
	protected int getMaxTaskPerTick()
	{
		// default 512
		return TweakerMoreConfigs.SERVER_DATA_SYNCER_QUERY_LIMIT.getIntegerValue();
	}

	@Override
	protected int getTaskExecuteCooldown()
	{
		// default 0
		return TweakerMoreConfigs.SERVER_DATA_SYNCER_QUERY_INTERVAL.getIntegerValue() - 1;
	}

	public static ServerDataSyncer getInstance()
	{
		return INSTANCE;
	}

	public DataQueryHandlerPro getQueryHandler()
	{
		return this.queryHandler;
	}

	public static boolean hasEnoughPermission()
	{
		Minecraft mc = Minecraft.getInstance();
		if (mc.player != null && mc.player.hasPermissions(2))
		{
			return true;
		}
		return CarpetTISAdditionAccess.getBooleanRule("debugNbtQueryNoPermission").orElse(false);
	}

	public void resetSyncLimiter()
	{
		this.syncBlockEntityPos.clear();
		this.syncedEntityId.clear();
	}

	@Override
	public void onClientTick(Minecraft mc)
	{
		this.resetSyncLimiter();
		this.tickTask();
	}

	public void onDimensionChanged()
	{
		this.resetSyncLimiter();
		this.queryHandler.clearCallbacks();
	}

	/**
	 * Fetch nbt of the given entity from the server
	 *
	 * @return An optional nbt future that produces a nullable nbt of the entity.
	 * The future completes on main thread.
	 * If the operation fails, return {@code Optional.empty()}
	 */
	public Optional<CompletableFuture<@Nullable CompoundTag>> fetchEntity(Entity entity)
	{
		if (hasEnoughPermission())
		{
			return Optional.of(this.syncedEntityId.computeIfAbsent(
					entity.getId(),
					entityId -> {
						CompletableFuture<CompoundTag> future = new CompletableFuture<>();
						this.runOrEnqueueTask(() -> {
							TweakerMoreMod.LOGGER.debug("Fetching entity data of {}", entity);
							this.queryHandler.queryEntityNbt(entityId, future::complete);
						});
						return future;
					}
			));
		}
		return Optional.empty();
	}

	/**
	 * Fetch nbt of the given block entity from the server
	 *
	 * @return An optional nbt future that produces a nullable nbt of the block entity.
	 * The future completes on main thread.
	 * If the operation fails, return {@code Optional.empty()}
	 */
	public Optional<CompletableFuture<@Nullable CompoundTag>> fetchBlockEntity(BlockEntity blockEntity)
	{
		if (hasEnoughPermission())
		{
			return Optional.of(this.syncBlockEntityPos.computeIfAbsent(
					blockEntity.getBlockPos(),
					pos -> {
						CompletableFuture<CompoundTag> future = new CompletableFuture<>();
						this.runOrEnqueueTask(() -> {
							TweakerMoreMod.LOGGER.debug("Syncing block entity data at {}", pos);
							this.queryHandler.queryBlockNbt(blockEntity.getBlockPos(), future::complete);
						});
						return future;
					}
			));
		}
		return Optional.empty();
	}

	/**
	 * Sync the given entity data from the server
	 *
	 * @return a future indicating when the sync operation completes
	 *   - If succeeded: future completes when syncing done
	 *   - If failed: future completes immediately
	 */
	public CompletableFuture<Void> syncEntity(Entity entity, boolean syncMotionState)
	{
		Optional<CompletableFuture<CompoundTag>> opt = fetchEntity(entity);
		if (opt.isPresent())
		{
			return opt.get().thenAccept(nbt -> {
				if (nbt != null)
				{
					EntityMotionStateRestorer restorer = null;
					if (!syncMotionState)
					{
						restorer = new EntityMotionStateRestorer(entity);
					}

					// net.minecraft.entity.Entity.fromTag is designed to be used server-side
					// so things like casting e.g. (ServerWorld)this.world might happen
					// that will definitely cause an exception since this.world is a ClientWorld
					// so a try-catch is required here
					try
					{
						//#if MC >= 12106
						//$$ try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(entity.problemPath(), TweakerMoreMod.LOGGER))
						//$$ {
						//$$ 	entity.load(TagValueInput.create(logging, entity.registryAccess(), nbt));
						//$$ }
						//#else
						entity.load(nbt);
						//#endif
						if (restorer != null)
						{
							restorer.restore();
						}
						TweakerMoreMod.LOGGER.debug("Synced entity data of {}", entity);
					}
					catch (Exception exception)
					{
						TweakerMoreMod.LOGGER.warn("Failed to sync entity data of {}: {}", entity, exception);
						if (TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
						{
							TweakerMoreMod.LOGGER.warn("[TweakerMore Debug] ServerDataSyncer#syncEntity", exception);
						}
					}
				}
			});
		}
		else
		{
			return CompletableFuture.allOf();
		}
	}
	public CompletableFuture<Void> syncEntity(Entity entity)
	{
		return this.syncEntity(entity, true);
	}

	/**
	 * Sync the given block entity data from the server
	 *
	 * @return a future indicating when the sync operation completes
	 *   - If succeeded: future completes when syncing done
	 *   - If failed: future completes immediately
	 */
	public CompletableFuture<Void> syncBlockEntity(BlockEntity blockEntity)
	{
		if (hasEnoughPermission())
		{
			Optional<CompletableFuture<CompoundTag>> opt = fetchBlockEntity(blockEntity);
			Level world = blockEntity.getLevel();
			if (opt.isPresent() && world != null)
			{
				return opt.get().thenAccept(nbt -> {
					if (nbt != null)
					{
						BlockPos pos = blockEntity.getBlockPos();
						try
						{
							//#if MC >= 12106
							//$$ try (ProblemReporter.ScopedCollector logging = new ProblemReporter.ScopedCollector(blockEntity.problemPath(), TweakerMoreMod.LOGGER))
							//$$ {
							//$$ 	blockEntity.loadWithComponents(TagValueInput.create(logging, world.registryAccess(), nbt));
							//$$ }
							//#elseif MC >= 12006
							//$$ blockEntity.loadWithComponents(nbt, blockEntity.getLevel().registryAccess());
							//#elseif MC >= 11700
							//$$ blockEntity.load(nbt);
							//#elseif MC >= 11600
							//$$ blockEntity.load(blockEntity.getBlockState(), nbt);
							//#else
							blockEntity.load(nbt);
							//#endif
							TweakerMoreMod.LOGGER.debug("Synced block entity data at {}", pos);
						}
						catch (Exception exception)
						{
							TweakerMoreMod.LOGGER.warn("Failed to sync block entity data at {}: {}", pos, exception);
							if (TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
							{
								TweakerMoreMod.LOGGER.warn("[TweakerMore Debug] ServerDataSyncer#syncBlockEntity", exception);
							}
						}
					}
				});
			}
		}
		return CompletableFuture.allOf();
	}

	/**
	 * Sync the data of the block entity at given position from the server
	 *
	 * @return a future indicating when the sync operation completes
	 *   - If succeeded: future completes when syncing done
	 *   - If failed: future completes immediately
	 */
	public CompletableFuture<Void> syncBlockEntityAt(BlockPos pos)
	{
		Minecraft mc = Minecraft.getInstance();
		if (hasEnoughPermission() && mc.player != null)
		{
			Level world = EntityUtils.getEntityWorld(mc.player);
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity != null && pos.equals(blockEntity.getBlockPos()))
			{
				return syncBlockEntity(blockEntity);
			}
		}
		return CompletableFuture.allOf();
	}

	/**
	 * Sync the data of the underlying block entities from given inventory from the server
	 *
	 * @return a future indicating when all required sync operation completes
	 */
	public CompletableFuture<Void> syncBlockInventory(Container inventory)
	{
		if (inventory instanceof BlockEntity)
		{
			return this.syncBlockEntity((BlockEntity)inventory);
		}
		else if (inventory instanceof CompoundContainer)
		{
			DoubleInventoryAccessor accessor = (DoubleInventoryAccessor)inventory;
			return CompletableFuture.allOf(
					syncBlockInventory(accessor.getFirst()),
					syncBlockInventory(accessor.getSecond())
			);
		}
		return CompletableFuture.allOf();
	}

	public CompletableFuture<Void> syncEverything(TargetPair pair, ProgressCallback callback)
	{
		AtomicInteger beCounter = new AtomicInteger(0);
		AtomicInteger entityCounter = new AtomicInteger(0);
		CompletableFuture<Void> beSynced = CompletableFuture.allOf(
				pair.getBlockEntityPositions().stream().
						map(this::syncBlockEntityAt).
						map(f -> f.thenRun(
								() -> callback.apply(beCounter.addAndGet(1), entityCounter.get())
						)).
						toArray(CompletableFuture[]::new)
		);
		CompletableFuture<Void> entitySynced = CompletableFuture.allOf(
				pair.getEntities().stream().
						map(this::syncEntity).
						map(f -> f.thenRun(
								() -> callback.apply(beCounter.get(), entityCounter.addAndGet(1))
						)).
						toArray(CompletableFuture[]::new)
		);
		return CompletableFuture.allOf(beSynced, entitySynced);
	}

	@FunctionalInterface
	public interface ProgressCallback
	{
		void apply(int blockEntityAmount, int entityAmount);
	}
}
