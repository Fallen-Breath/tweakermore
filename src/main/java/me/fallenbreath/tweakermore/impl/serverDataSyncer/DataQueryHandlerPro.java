package me.fallenbreath.tweakermore.impl.serverDataSyncer;

import com.google.common.collect.Maps;
import me.fallenbreath.tweakermore.mixins.tweaks.serverDataSyncer.DataQueryHandlerAccessor;
import me.fallenbreath.tweakermore.util.collection.ExpiringMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.play.QueryBlockNbtC2SPacket;
import net.minecraft.network.packet.c2s.play.QueryEntityNbtC2SPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

/**
 * Reference: {@link net.minecraft.client.network.DataQueryHandler}
 * But stores multiple callbacks
 */
public class DataQueryHandlerPro
{
	private final Map<Integer, Callback> callbacks = new ExpiringMap<>(Maps.newHashMap(), 30_000);

	private static Optional<ClientPlayNetworkHandler> getNetworkHandler()
	{
		return Optional.ofNullable(MinecraftClient.getInstance().getNetworkHandler());
	}

	public int generateTransactionId(ClientPlayNetworkHandler networkHandler)
	{
		DataQueryHandlerAccessor accessor = (DataQueryHandlerAccessor)networkHandler.getDataQueryHandler();
		int id = accessor.getExpectedTransactionId();
		accessor.setExpectedTransactionId(id + 1);
		return id;
	}

	public boolean handleQueryResponse(int transactionId, @Nullable NbtCompound tag)
	{
		Callback callback = this.callbacks.remove(transactionId);
		if (callback != null)
		{
			callback.accept(tag);
			return true;
		}
		else
		{
			return false;
		}
	}

	private int nextQuery(ClientPlayNetworkHandler networkHandler, Callback callback)
	{
		int transactionId = this.generateTransactionId(networkHandler);
		this.callbacks.put(transactionId, callback);
		return transactionId;
	}

	public void clearCallbacks()
	{
		this.callbacks.clear();
	}

	public void queryEntityNbt(int entityNetworkId, Callback callback)
	{
		getNetworkHandler().ifPresent(networkHandler -> {
			int id = this.nextQuery(networkHandler, callback);
			networkHandler.sendPacket(new QueryEntityNbtC2SPacket(id, entityNetworkId));
		});
	}

	public void queryBlockNbt(BlockPos pos, Callback callback)
	{
		getNetworkHandler().ifPresent(networkHandler -> {
			int id = this.nextQuery(networkHandler, callback);
			networkHandler.sendPacket(new QueryBlockNbtC2SPacket(id, pos));
		});
	}

	@FunctionalInterface
	public interface Callback
	{
		void accept(NbtCompound nbt);
	}
}
