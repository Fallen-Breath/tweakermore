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

package me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import fi.dy.masa.litematica.config.Configs;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.config.options.TweakerMoreConfigBooleanHotkeyed;
import me.fallenbreath.tweakermore.impl.features.tweakmSchematicProPlace.restrict.PlacementRestrictor;
import me.fallenbreath.tweakermore.util.StringUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

public class ProPlaceImpl
{
	private static final List<TweakerMoreConfigBooleanHotkeyed> PRO_PLACE_CONFIGS = Lists.newArrayList(
			TweakerMoreConfigs.TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK,
			TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION
	);

	public static String modifyComment(String comment)
	{
		String lines = StringUtil.configsToListLines(PRO_PLACE_CONFIGS);
		return comment.replaceFirst("##CONFIGS##", lines);
	}

	public static void handleRightClick(PlacementContextProvider contextProvider, CallbackInfoReturnable<ActionResult> cir)
	{
		if (Configs.Generic.EASY_PLACE_MODE.getBooleanValue())
		{
			return;
		}

		MinecraftClient mc = MinecraftClient.getInstance();

		boolean proPlace = TweakerMoreConfigs.TWEAKM_SCHEMATIC_PRO_PLACE.getBooleanValue();
		boolean autoPick = proPlace || TweakerMoreConfigs.TWEAKM_AUTO_PICK_SCHEMATIC_BLOCK.getBooleanValue();
		boolean restrict = proPlace || TweakerMoreConfigs.TWEAKM_SCHEMATIC_BLOCK_PLACEMENT_RESTRICTION.getBooleanValue();

		if (autoPick)
		{
			ItemPlacementContext ctx = contextProvider.provide().getSecond();
			SchematicBlockPicker.doSchematicWorldPickBlock(mc, ctx.getBlockPos(), ctx.getHand());
		}

		if (restrict)
		{
			// for some tweakeroo stuffs
			Pair<BlockHitResult, ItemPlacementContext> pair = contextProvider.provide();

			if (!PlacementRestrictor.canDoBlockPlacement(mc, pair.getFirst(), pair.getSecond()))
			{
				// return fail so no more further actions that might cause issues e.g. water bucket using
				cir.setReturnValue(ActionResult.FAIL);
			}
		}
	}

	@FunctionalInterface
	public interface PlacementContextProvider
	{
		Pair<BlockHitResult, ItemPlacementContext> provide();
	}
}
