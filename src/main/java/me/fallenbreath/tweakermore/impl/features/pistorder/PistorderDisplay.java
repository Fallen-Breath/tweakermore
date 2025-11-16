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
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.pistorder.pushlimit.PushLimitManager;
import me.fallenbreath.tweakermore.mixins.tweaks.features.pistorder.PistonBlockAccessor;
import me.fallenbreath.tweakermore.util.Messenger;
import me.fallenbreath.tweakermore.util.PositionUtils;
import me.fallenbreath.tweakermore.util.TemporaryBlockReplacer;
import me.fallenbreath.tweakermore.util.render.ColorHolder;
import me.fallenbreath.tweakermore.util.render.TextRenderer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class PistorderDisplay
{
	private static final String INDICATOR_SUCCESS = ChatFormatting.GREEN + "√";
	private static final String INDICATOR_FAIL = ChatFormatting.RED + "×";

	private List<BlockPos> movedBlocks;
	private List<BlockPos> brokenBlocks;
	private boolean moveSuccess;
	@Nullable
	private BlockPos immovableBlockPos;

	public final Level world;
	public final BlockPos pistonPos;
	public final BlockState blockState;
	public final Direction direction;
	public PistonActionType actionType;
	public final int color;

	private DisplayMode displayMode;

	// used for dynamically_information_update
	private long lastUpdateTime = -1;

	public PistorderDisplay(Level world, BlockPos pistonPos, BlockState blockState, Direction direction, PistonActionType actionType)
	{
		this.world = world;
		this.pistonPos = pistonPos;
		this.blockState = blockState;
		this.direction = direction;
		this.actionType = actionType;
		this.setDisplayMode(DisplayMode.DIRECT);
		Random random = new Random(pistonPos.hashCode());
		this.color = (random.nextInt(0x50) + 0xAF) + ((random.nextInt(0x50) + 0xAF) << 8) + ((random.nextInt(0x50) + 0xAF) << 16);
	}

	private Direction getPistonFacing()
	{
		return this.blockState.getValue(BlockStateProperties.FACING);
	}

	private boolean isStickyPiston()
	{
		return ((PistonBlockAccessor)this.blockState.getBlock()).getIsSticky();
	}

	public boolean isDisabled()
	{
		return this.displayMode == DisplayMode.DISABLED;
	}

	private void disable()
	{
		this.displayMode = DisplayMode.DISABLED;
	}

	/**
	 * Will trigger a refresh
	 */
	private void setDisplayMode(DisplayMode mode)
	{
		this.displayMode = mode;
		this.refreshInformation();
	}

	private void refreshInformation()
	{
		BlockPos simulatedPistonPos = null;
		switch (this.displayMode)
		{
			case DIRECT:
				simulatedPistonPos = this.pistonPos;
				break;
			case INDIRECT:
				simulatedPistonPos = this.pistonPos;
				if (!this.isStickyPiston())
				{
					simulatedPistonPos = simulatedPistonPos.relative(this.getPistonFacing());
				}
				break;
			case DISABLED:
				break;
		}
		if (simulatedPistonPos != null)
		{
			this.analyze(this.world, simulatedPistonPos, this.getPistonFacing(), this.actionType);
		}
	}

	/**
	 * Might make the piston blink for a while if the action type is retract
	 */
	private void analyze(Level world, BlockPos simulatedPistonPos, Direction pistonFacing, PistonActionType PistonActionType)
	{
		BlockState air = Blocks.AIR.defaultBlockState();

		// backing up block states for potential piston (head) block position
		TemporaryBlockReplacer blockReplacer = new TemporaryBlockReplacer(this.world);
		// not necessary to replace the piston pos with barrier or something in-movable since vanilla PistonStructureResolver handles it
		if (!this.pistonPos.equals(simulatedPistonPos))
		{
			blockReplacer.add(this.pistonPos, air);  // piston pos, in case it's in-direct mode
		}
		if (PistonActionType.isRetract())
		{
			blockReplacer.add(simulatedPistonPos.relative(pistonFacing), air);  // simulated piston head
		}
		blockReplacer.removeBlocks();

		PistonStructureResolver pistonHandler = new PistonStructureResolver(world, simulatedPistonPos, pistonFacing, PistonActionType.isPush());
		this.moveSuccess = pistonHandler.resolve();

		if (!this.moveSuccess)
		{
			int newPushLimit = Math.max(PushLimitManager.getInstance().getPushLimit(), TweakerMoreConfigs.PISTORDER_MAX_SIMULATION_PUSH_LIMIT.getIntegerValue());
			PushLimitManager.getInstance().overwritePushLimit(newPushLimit);
			pistonHandler.resolve();
		}

		// restoring things
		blockReplacer.restoreBlocks();
		PushLimitManager.getInstance().restorePushLimit();  // it's ok if the push limit hasn't been overwritten

		this.brokenBlocks = Lists.newArrayList(pistonHandler.getToDestroy());
		this.movedBlocks = Lists.newArrayList(pistonHandler.getToPush());
		this.immovableBlockPos = ((ImmovableBlockPosRecorder)pistonHandler).getImmovableBlockPos$TKM();
		// reverse the list for the correct order
		Collections.reverse(this.brokenBlocks);
		Collections.reverse(this.movedBlocks);
	}

	private boolean tryIndirectMode()
	{
		BlockState blockInFront1 = this.world.getBlockState(this.pistonPos.relative(this.getPistonFacing(), 1));
		BlockState blockInFront2 = this.world.getBlockState(this.pistonPos.relative(this.getPistonFacing(), 2));
		if (blockInFront1.isAir() && !blockInFront2.isAir())
		{
			if (this.isStickyPiston())
			{
				this.actionType = PistonActionType.RETRACT;
			}
			this.setDisplayMode(DisplayMode.INDIRECT);
			return true;
		}
		return false;
	}

	public void onClick()
	{
		switch (this.displayMode)
		{
			case DIRECT:
				if (!this.tryIndirectMode())
				{
					this.disable();
				}
				break;
			case INDIRECT:
				this.disable();
				break;
			case DISABLED:
				// do nothing
				break;
		}
	}

	private static TextRenderer drawString(BlockPos pos, float offsetY, BaseComponent text, int color)
	{
		ColorHolder colorHolder = ColorHolder.of(color).modify(h -> h.alpha = (int)(0xFF * TweakerMoreConfigs.PISTORDER_TEXT_ALPHA.getDoubleValue()));
		TextRenderer renderer =  TextRenderer.create().
				at(PositionUtils.centerOf(pos)).
				text(text).color(colorHolder.pack()).
				fontScale(TextRenderer.DEFAULT_FONT_SCALE * TweakerMoreConfigs.PISTORDER_TEXT_SCALE.getDoubleValue()).
				seeThrough().
				shadow(TweakerMoreConfigs.PISTORDER_TEXT_SHADOW.getBooleanValue());
		renderer.shift(0, offsetY * renderer.getLineHeight());
		return renderer;
	}

	private boolean checkState(Level world)
	{
		if (!Objects.equals(world, this.world))
		{
			return false;
		}
		BlockGetter chunk = world.getChunkSource().getChunkForLighting(this.pistonPos.getX() >> 4, this.pistonPos.getZ() >> 4);
		if (chunk instanceof LevelChunk && !((LevelChunk)chunk).isEmpty())  // it's really a loaded chunk
		{
			return chunk.getBlockState(this.pistonPos).equals(this.blockState);
		}
		return true;
	}

	@SuppressWarnings("ConstantConditions")
	public List<@NotNull TextRenderer> render()
	{
		List<TextRenderer> texts = Lists.newArrayList();

		if (!this.isDisabled())
		{
			Minecraft client = Minecraft.getInstance();
			if (!this.checkState(client.level))
			{
				this.disable();
				return Collections.emptyList();
			}

			String actionKey = this.actionType.isPush() ? "tweakermore.impl.pistorder.push" : "tweakermore.impl.pistorder.retract";
			String actionResult = this.moveSuccess ? INDICATOR_SUCCESS : INDICATOR_FAIL;

			texts.add(drawString(
					this.pistonPos, -0.5F,
					Messenger.s(String.format("%s %s", I18n.get(actionKey), actionResult), ChatFormatting.GOLD),
					this.color
			));
			texts.add(drawString(
					this.pistonPos, 0.5F,
					Messenger.c(
							Messenger.formatting(Messenger.tr("tweakermore.impl.pistorder.block_count.pre"), ChatFormatting.GOLD),
							Messenger.s(this.movedBlocks.size()),
							Messenger.formatting(Messenger.tr("tweakermore.impl.pistorder.block_count.post"), ChatFormatting.GOLD)
					),
					this.color
			));

			for (int i = 0; i < this.movedBlocks.size(); i++)
			{
				texts.add(drawString(this.movedBlocks.get(i), 0.0F, Messenger.s(i + 1), this.color));
			}
			for (int i = 0; i < this.brokenBlocks.size(); i++)
			{
				texts.add(drawString(this.brokenBlocks.get(i), 0.0F, Messenger.s(i + 1), ChatFormatting.RED.getColor()));
			}

			if (this.immovableBlockPos != null)
			{
				texts.add(drawString(this.immovableBlockPos, 0.0F, Messenger.s("×"), ChatFormatting.DARK_RED.getColor()));
			}
		}

		return texts.stream().filter(Objects::nonNull).collect(Collectors.toList());
	}

	public void tick()
	{
		if (TweakerMoreConfigs.PISTORDER_DYNAMICALLY_INFO_UPDATE.getBooleanValue())
		{
			if (this.world.getGameTime() != this.lastUpdateTime)
			{
				this.refreshInformation();
				this.lastUpdateTime = this.world.getGameTime();
			}
		}
	}
}
