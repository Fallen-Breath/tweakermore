package me.fallenbreath.tweakermore.impl.mc_tweaks.signEditingRequireHotkey;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

// used in mc >= 1.20.1
public final class SignEditRejectionTracker implements IWorldLoadListener
{
	public static final SignEditRejectionTracker INSTANCE = new SignEditRejectionTracker();

	private final Cache<Pair<BlockPos, Boolean>, MutableInt> rejections = CacheBuilder.newBuilder()
			.maximumSize(64)
			.expireAfterWrite(10, TimeUnit.SECONDS)
			.build();

	private SignEditRejectionTracker()
	{
	}

	public void record(BlockPos pos, boolean front)
	{
		Pair<BlockPos, Boolean> key = Pair.of(pos.immutable(), front);
		MutableInt count = this.rejections.getIfPresent(key);
		this.rejections.put(key, new MutableInt(count == null ? 1 : count.intValue() + 1));
	}

	public boolean consume(BlockPos pos, boolean front)
	{
		Pair<BlockPos, Boolean> key = Pair.of(pos, front);
		MutableInt count = this.rejections.getIfPresent(key);
		if (count == null)
		{
			return false;
		}
		count.decrement();
		if (count.intValue() == 0)
		{
			this.rejections.invalidate(key);
		}
		return true;
	}

	public void clear()
	{
		this.rejections.invalidateAll();
	}

	@Override
	public void onWorldLoadPre(@Nullable ClientLevel worldBefore, @Nullable ClientLevel worldAfter, Minecraft mc)
	{
		if (worldBefore != worldAfter)
		{
			this.clear();
		}
	}
}
