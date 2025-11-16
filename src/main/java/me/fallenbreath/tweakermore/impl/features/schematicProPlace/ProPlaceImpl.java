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

package me.fallenbreath.tweakermore.impl.features.schematicProPlace;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.litematica.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.impl.features.schematicProPlace.restrict.PlacementRestrictor;
import me.fallenbreath.tweakermore.util.StringUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class ProPlaceImpl
{
	private static final List<TweakerMoreConfigBooleanHotkeyed> PRO_PLACE_CONFIGS = Lists.newArrayList(
			TweakerMoreConfigs.AUTO_PICK_SCHEMATIC_BLOCK,
			TweakerMoreConfigs.SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION
	);

	public static String modifyComment(String comment)
	{
		String lines = StringUtils.configsToListLines(PRO_PLACE_CONFIGS);
		return comment.replaceFirst("##CONFIGS##", lines);
	}

	public static void handleRightClick(PlacementContextProvider contextProvider, CallbackInfoReturnable<InteractionResult> cir)
	{
		if (Configs.Generic.EASY_PLACE_MODE.getBooleanValue())
		{
			return;
		}

		Minecraft mc = Minecraft.getInstance();

		boolean proPlace = TweakerMoreConfigs.SCHEMATIC_PRO_PLACE.getBooleanValue();
		boolean autoPick = proPlace || TweakerMoreConfigs.AUTO_PICK_SCHEMATIC_BLOCK.getBooleanValue();
		boolean restrict = proPlace || TweakerMoreConfigs.SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION.getBooleanValue();

		if (autoPick)
		{
			BlockPlaceContext ctx = contextProvider.provide().getSecond();
			SchematicBlockPicker.doSchematicWorldPickBlock(mc, ctx.getClickedPos(), ctx.getHand());
		}

		if (restrict)
		{
			// for some tweakeroo stuffs
			Pair<BlockHitResult, BlockPlaceContext> pair = contextProvider.provide();

			if (!PlacementRestrictor.canDoBlockPlacement(mc, pair.getFirst(), pair.getSecond()))
			{
				// return fail so no more further actions that might cause issues e.g. water bucket using
				cir.setReturnValue(InteractionResult.FAIL);
			}
		}
	}

	@FunctionalInterface
	public interface PlacementContextProvider
	{
		Pair<BlockHitResult, BlockPlaceContext> provide();
	}
}
