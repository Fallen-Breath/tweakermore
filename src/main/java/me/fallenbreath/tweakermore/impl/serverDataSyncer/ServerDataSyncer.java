package me.fallenbreath.tweakermore.impl.serverDataSyncer;

import com.google.common.collect.Sets;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerDataSyncer extends LimitedTaskRunner implements IClientTickHandler
{
	private static final ServerDataSyncer INSTANCE = new ServerDataSyncer();

	private final Set<BlockPos> syncBlockEntityPos = Sets.newHashSet();
	private final Set<Integer> syncedEntityId = Sets.newHashSet();
	private final DataQueryHandlerPro queryHandler = new DataQueryHandlerPro();

	private ServerDataSyncer()
	{
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
		MinecraftClient mc = MinecraftClient.getInstance();
		return mc.player != null && mc.player.hasPermissionLevel(2);
	}

	public void resetSyncLimiter()
	{
		this.syncBlockEntityPos.clear();
		this.syncedEntityId.clear();
	}

	@Override
	public void onClientTick(MinecraftClient mc)
	{
		this.resetSyncLimiter();
		this.tickTask();
	}

	public void onDimensionChanged()
	{
		this.resetSyncLimiter();
		this.queryHandler.clearCallbacks();
	}

	public CompletableFuture<Void> syncEntity(Entity entity)
	{
		if (hasEnoughPermission())
		{
			int entityId = entity.getEntityId();
			if (!this.syncedEntityId.contains(entityId))
			{
				this.syncedEntityId.add(entityId);
				CompletableFuture<Void> future = new CompletableFuture<>();
				this.addTask(() -> {
					TweakerMoreMod.LOGGER.debug("Syncing entity data of {}", entity);
					this.queryHandler.queryEntityNbt(entityId, nbt -> {
						if (nbt != null)
						{
							// net.minecraft.entity.Entity.fromTag is designed to be used server-side
							// so things like casting e.g. (ServerWorld)this.world might happen
							// that will definitely cause an exception since this.world is a ClientWorld
							// so a try-catch is required here
							try
							{
								entity.readNbt(nbt);
								TweakerMoreMod.LOGGER.debug("Synced entity data of {}", entity);
							}
							catch (Exception exception)
							{
								TweakerMoreMod.LOGGER.warn("Failed to sync entity data of {}: {}", entity, exception);
								if (TweakerMoreConfigs.TWEAKERMORE_DEBUG_MODE.getBooleanValue())
								{
									TweakerMoreMod.LOGGER.warn("[TweakerMore Debug]", exception);
								}
							}
						}
						future.complete(null);
					});
				});
				return future;
			}
		}
		return CompletableFuture.allOf();
	}

	public CompletableFuture<Void> syncBlockEntity(BlockPos pos)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		if (hasEnoughPermission() && mc.player != null)
		{
			if (!this.syncBlockEntityPos.contains(pos))
			{
				this.syncBlockEntityPos.add(pos);

				World world = mc.player.clientWorld;
				BlockEntity blockEntity = world.getBlockEntity(pos);
				if (blockEntity != null)
				{
					CompletableFuture<Void> future = new CompletableFuture<>();
					this.addTask(() -> {
						TweakerMoreMod.LOGGER.debug("Syncing block entity data at {}", pos);
						this.queryHandler.queryBlockNbt(blockEntity.getPos(), nbt -> {
							if (nbt != null)
							{
								blockEntity.fromTag(world.getBlockState(pos), nbt);
								TweakerMoreMod.LOGGER.debug("Synced block entity data at {}", pos);
							}
							future.complete(null);
						});
					});
					return future;
				}
			}
		}
		return CompletableFuture.allOf();
	}

	public CompletableFuture<Void> syncEverything(TargetPair pair, ProgressCallback callback)
	{
		AtomicInteger beCounter = new AtomicInteger(0);
		AtomicInteger entityCounter = new AtomicInteger(0);
		CompletableFuture<Void> beSynced = CompletableFuture.allOf(
				pair.getBlockEntityPositions().stream().
						map(this::syncBlockEntity).
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
