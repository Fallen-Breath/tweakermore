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

package me.fallenbreath.tweakermore.impl.mc_tweaks.blockEventThrottler;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.PositionUtil;
import me.fallenbreath.tweakermore.util.RegistryUtil;
import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class BlockEventThrottler
{
	public static void throttle(World world, BlockPos pos, Block block, TimedCounter counter, CallbackInfo ci)
	{
		if (!TweakerMoreConfigs.BLOCK_EVENT_THROTTLER.getBooleanValue())
		{
			// tweak not enabled, don't throttle
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();
		if (mc.player == null || world != mc.world)
		{
			// unknown world / player state, don't throttle
			return;
		}

		// target block check
		String blockId = RegistryUtil.getBlockId(block);
		if (TweakerMoreConfigs.BLOCK_EVENT_THROTTLER_TARGET_BLOCKS.getStrings().
				stream().noneMatch(target -> target.equals(blockId))
		)
		{
			// not in target, don't throttle
			return;
		}

		// reset the counter for new game tick
		counter.updateTime(world.getTime());

		// threshold counter
		if (++counter.amount <= TweakerMoreConfigs.BLOCK_EVENT_THROTTLER_THRESHOLD.getIntegerValue())
		{
			// still within the threshold, don't throttle
			return;
		}

		// whitelist range
		double disSqr = mc.player.squaredDistanceTo(PositionUtil.centerOf(pos));
		double whitelistRange = TweakerMoreConfigs.BLOCK_EVENT_THROTTLER_WHITELIST_RANGE.getDoubleValue();
		if (disSqr < whitelistRange * whitelistRange)
		{
			// in whitelist range, don't throttle
			return;
		}

		// cancel the block event, do throttle
		ci.cancel();
	}
}
