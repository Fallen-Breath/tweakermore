package me.fallenbreath.tweakermore.impl.serverDataSyncer;

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
