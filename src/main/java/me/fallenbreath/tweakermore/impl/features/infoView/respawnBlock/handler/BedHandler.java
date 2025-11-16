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

package me.fallenbreath.tweakermore.impl.features.infoView.respawnBlock.handler;

import me.fallenbreath.tweakermore.impl.features.infoView.cache.RenderVisitorWorldView;
import me.fallenbreath.tweakermore.util.PositionUtils;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.biome.Biomes;

/**
 * Bed logic:
 * - Click at bed head: Always explodes. Explodes at bed head pos
 * - Click at bed root: Explodes when bed head exists. Explodes at bed head pos
 */
public class BedHandler extends AbstractBlockHandler
{
	private static final BlockState AIR = Blocks.AIR.defaultBlockState();

	private BlockPos bedHeadPos;
	private Direction toBedRootDirection;
	private BlockPos bedRootPos;

	public BedHandler(RenderVisitorWorldView world, BlockPos blockPos, BlockState blockState)
	{
		super(world, blockPos, blockState);
		this.bedHeadPos = null;
		this.bedRootPos = null;
		if (blockState.getBlock() instanceof BedBlock)
		{
			// ref: net.minecraft.block.BedBlock#onUse
			boolean currentIsHead = blockState.getValue(BedBlock.PART) == BedPart.HEAD;
			Direction direction = blockState.getValue(BedBlock.FACING);
			if (currentIsHead)
			{
				direction = direction.getOpposite();
			}
			BlockPos otherPos = blockPos.relative(direction);
			BlockState otherState = world.getBlockState(otherPos);

			if (currentIsHead)
			{
				this.bedHeadPos = blockPos;  // handle when the bed only has the head half
				this.toBedRootDirection = direction;
			}
			else
			{
				this.toBedRootDirection = direction.getOpposite();
			}
			if (blockState.getBlock() == otherState.getBlock())
			{
				this.bedHeadPos = currentIsHead ? blockPos : otherPos;
				this.bedRootPos = currentIsHead ? otherPos : blockPos;
			}
		}
	}

	@Override
	public boolean isValid()
	{
		// ref: net.minecraft.block.BedBlock#onUse
		if (this.bedHeadPos != null)  // isBed check
		{
			//#if MC >= 11600
			//$$ return !BedBlock.isOverworld(world.getBestWorld());
			//#else
			return !this.world.getBestWorld().dimension.mayRespawn() || this.world.getBestWorld().getBiome(blockPos) == Biomes.NETHER;
			//#endif
		}
		return false;
	}

	@Override
	public Vec3 getExplosionCenter()
	{
		return PositionUtils.centerOf(this.bedHeadPos);
	}

	@Override
	public BlockPos getDeduplicationKey()
	{
		return this.bedHeadPos;
	}

	public void addBlocksToRemove(TemporaryBlockReplacer replacer)
	{
		replacer.add(this.bedHeadPos, AIR);
		if (this.bedRootPos != null)
		{
			replacer.add(this.bedRootPos, AIR);
		}
	}

	@Override
	public float getExplosionPower()
	{
		return 5.0F;
	}

	@Override
	public Vec3 getTextPosition()
	{
		Vec3 headCenter = PositionUtils.centerOf(this.bedHeadPos);
		Vec3 shiftToMiddle =
				//#if MC >= 11600
				//$$ Vec3.of
				//#else
				new Vec3
				//#endif
						(this.toBedRootDirection.getNormal()).scale(0.5);
		return headCenter.add(shiftToMiddle);
	}
}
