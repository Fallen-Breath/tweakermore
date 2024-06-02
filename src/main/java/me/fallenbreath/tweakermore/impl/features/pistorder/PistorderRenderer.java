/*
 * This file is part of the Pistorder project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2023  Fallen_Breath and contributors
 *
 * Pistorder is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Pistorder is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Pistorder.  If not, see <https://www.gnu.org/licenses/>.
 */

package me.fallenbreath.tweakermore.impl.features.pistorder;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.malilib.interfaces.IClientTickHandler;
import fi.dy.masa.malilib.util.InfoUtils;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.mixins.tweaks.features.pistorder.PistonBlockAccessor;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import me.fallenbreath.tweakermore.util.render.TweakerMoreIRenderer;
import me.fallenbreath.tweakermore.util.render.context.WorldRenderContextImpl;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.PistonBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class PistorderRenderer implements TweakerMoreIRenderer, IClientTickHandler
{
	private static final PistorderRenderer INSTANCE = new PistorderRenderer();

	private final Map<Pair<World, BlockPos>, PistorderDisplay> displayMap = Maps.newHashMap();

	public static PistorderRenderer getInstance()
	{
		return INSTANCE;
	}

	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public boolean isEnabled()
	{
		return TweakerMoreConfigs.PISTORDER.getBooleanValue() && TweakerMoreConfigs.PISTORDER.getTweakerMoreOption().isEnabled();
	}

	public ActionResult onPlayerRightClickBlock(World world, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!this.isEnabled())
		{
			return ActionResult.FAIL;
		}

		// click with empty main hand, not sneaking
		if (hand == Hand.MAIN_HAND)
		{
			return this.onPlayerRightClickBlockWithMainHand(world, player, hit);
		}
		return ActionResult.FAIL;
	}

	public ActionResult onPlayerRightClickBlockWithMainHand(World world, PlayerEntity player, BlockHitResult hit)
	{
		if (!this.isEnabled())
		{
			return ActionResult.FAIL;
		}

		if (player.getMainHandStack().isEmpty() && !player.isSneaking())
		{
			BlockPos pos = hit.getBlockPos();
			BlockState blockState = world.getBlockState(pos);
			Block block = blockState.getBlock();
			if (block instanceof PistonBlock)
			{
				boolean extended = blockState.get(PistonBlock.EXTENDED);
				if (!extended || ((PistonBlockAccessor)block).getIsSticky())
				{
					this.click(world, pos, blockState, blockState.get(Properties.FACING), extended ? PistonActionType.RETRACT : PistonActionType.PUSH);
					return ActionResult.SUCCESS;
				}
			}
		}

		return ActionResult.FAIL;
	}

	private void click(World world, BlockPos pos, BlockState blockState, Direction pistonFacing, PistonActionType PistonActionType)
	{
		Pair<World, BlockPos> key = Pair.of(world, pos);
		PistorderDisplay display = this.displayMap.get(key);
		if (display == null)
		{
			this.displayMap.put(key, new PistorderDisplay(world, pos, blockState, pistonFacing, PistonActionType));
		}
		else
		{
			display.onClick();
			if (display.isDisabled())
			{
				this.displayMap.remove(key);
			}
		}
	}

	@Override
	public void onRenderWorldLast(WorldRenderContextImpl context)
	{
		if (!this.isEnabled())
		{
			this.displayMap.clear();
			return;
		}

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player == null)
		{
			return;
		}

		List<Pair<World, BlockPos>> removeList = Lists.newArrayList();
		List<TextRenderer> texts = Lists.newArrayList();
		this.displayMap.forEach((key, display) -> {
			texts.addAll(display.render());
			if (display.isDisabled())
			{
				removeList.add(key);
			}
		});
		removeList.forEach(this.displayMap::remove);

		double maxDistance = TweakerMoreConfigs.PISTORDER_MAX_RENDER_DISTANCE.getIntegerValue();
		texts.stream().
				map(tr -> Pair.of(client.player.squaredDistanceTo(tr.getPos()), tr)).
				filter(p -> p.getFirst() <= maxDistance * maxDistance).
				sorted(Collections.reverseOrder(Comparator.comparingDouble(Pair::getFirst))).
				forEach(p -> p.getSecond().render());
	}

	@Override
	public void onClientTick(MinecraftClient mc)
	{
		this.displayMap.values().forEach(PistorderDisplay::tick);
	}

	public void clearDisplay()
	{
		if (TweakerMoreConfigs.PISTORDER.getBooleanValue())
		{
			InfoUtils.printActionbarMessage("tweakermore.impl.pistorder.display_cleared");
			this.displayMap.clear();
		}
	}
}
