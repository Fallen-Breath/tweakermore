/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2026  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.impl.mc_tweaks.disableSignGuiOnPlacement;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import fi.dy.masa.malilib.interfaces.IWorldLoadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.TimeUnit;

public final class SignPlacementTracker implements IWorldLoadListener
{
	public static final SignPlacementTracker INSTANCE = new SignPlacementTracker();

	private final Cache<BlockPos, Boolean> placements = CacheBuilder.newBuilder()
			.maximumSize(64)
			.expireAfterWrite(10, TimeUnit.SECONDS)
			.build();

	private SignPlacementTracker()
	{
	}

	public void record(BlockPos pos)
	{
		this.placements.put(pos.immutable(), true);
	}

	public boolean consume(BlockPos pos)
	{
		return this.placements.asMap().remove(pos) != null;
	}

	public void clear()
	{
		this.placements.invalidateAll();
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
