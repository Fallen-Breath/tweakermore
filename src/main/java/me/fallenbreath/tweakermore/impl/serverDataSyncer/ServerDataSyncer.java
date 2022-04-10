package me.fallenbreath.tweakermore.impl.serverDataSyncer;

import com.google.common.collect.Sets;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import me.fallenbreath.tweakermore.TweakerMoreMod;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
		// default 1024
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
		return mc.player != null && mc.player.allowsPermissionLevel(2);
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
					TweakerMoreMod.LOGGER.debug("Syncing entity data of " + entity);
					this.queryHandler.queryEntityNbt(entityId, nbt -> {
						if (nbt != null)
						{
							TweakerMoreMod.LOGGER.debug("Synced entity data of " + entity);
							entity.fromTag(nbt);
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
						TweakerMoreMod.LOGGER.debug("Syncing block entity data at " + pos);
						this.queryHandler.queryBlockNbt(blockEntity.getPos(), nbt -> {
							if (nbt != null)
							{
								blockEntity.fromTag(nbt);
								TweakerMoreMod.LOGGER.debug("Synced block entity data at " + pos);
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

	public void syncEverythingWithin(BlockPos pos1, BlockPos pos2, @Nullable SyncAllCallback callback)
	{
		MinecraftClient mc = MinecraftClient.getInstance();
		ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
		if (networkHandler == null || mc.player == null)
		{
			return;
		}

		int minX = Math.min(pos1.getX(), pos2.getX());
		int minY = Math.min(pos1.getY(), pos2.getY());
		int minZ = Math.min(pos1.getZ(), pos2.getZ());
		int maxX = Math.max(pos1.getX(), pos2.getX());
		int maxY = Math.max(pos1.getY(), pos2.getY());
		int maxZ = Math.max(pos1.getZ(), pos2.getZ());
		World world = mc.player.clientWorld;

		List<BlockPos> bePositions = BlockPos.stream(minX, minY, minZ, maxX, maxY, maxZ).
				map(BlockPos::toImmutable).
				// same check in fi.dy.masa.litematica.schematic.LitematicaSchematic.takeBlocksFromWorldWithinChunk
				filter(blockPos -> world.getBlockState(blockPos).getBlock().hasBlockEntity()).
				collect(Collectors.toList());
		CompletableFuture<Void> beSynced = CompletableFuture.allOf(
				bePositions.stream().map(this::syncBlockEntity).toArray(CompletableFuture[]::new)
		);

		Box box = new Box(minX, minY, minZ, maxX + 1, maxY + 1, maxZ + 1);
		List<Entity> entities = world.getEntities((Entity) null, box, null);
		CompletableFuture<Void> entitySynced = CompletableFuture.allOf(
				entities.stream().map(this::syncEntity).toArray(CompletableFuture[]::new)
		);

		if (callback != null)
		{
			CompletableFuture.allOf(entitySynced, beSynced).thenRun(
					() -> callback.apply(bePositions.size(), entities.size())
			);
		}
	}

	@FunctionalInterface
	public interface SyncAllCallback
	{
		void apply(int blockEntityAmount, int entityAmount);
	}
}
