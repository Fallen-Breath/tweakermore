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

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Collections;

public class TargetPair extends Pair<Collection<BlockPos>, Collection<Entity>>
{
	public TargetPair(Collection<BlockPos> blockEntityPositions, Collection<Entity> entities)
	{
		super(blockEntityPositions, entities);
	}

	public static TargetPair of(Collection<BlockPos> blockEntityPositions, Collection<Entity> entities)
	{
		return new TargetPair(blockEntityPositions, entities);
	}

	public static TargetPair none()
	{
		return new TargetPair(Collections.emptySet(), Collections.emptySet());
	}

	public Collection<BlockPos> getBlockEntityPositions()
	{
		return super.getFirst();
	}

	public int getBlockEntityAmount()
	{
		return this.getBlockEntityPositions().size();
	}

	public Collection<Entity> getEntities()
	{
		return super.getSecond();
	}

	public int getEntityAmount()
	{
		return this.getEntities().size();
	}
}
