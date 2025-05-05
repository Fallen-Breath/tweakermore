/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmMaterialListIgnoreStateMismatches;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import fi.dy.masa.litematica.scheduler.tasks.TaskCountBlocksPlacement;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Slice;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(TaskCountBlocksPlacement.class)
public abstract class TaskCountBlocksPlacementMixin
{
	@WrapOperation(
			method = "countAtPosition",
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lfi/dy/masa/litematica/scheduler/tasks/TaskCountBlocksPlacement;countsMissing:Lit/unimi/dsi/fastutil/objects/Object2IntOpenHashMap;",
							ordinal = 1
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lit/unimi/dsi/fastutil/objects/Object2IntOpenHashMap;addTo(Ljava/lang/Object;I)I"
			),
			require = 2,
			allow = 2,
			remap = false
	)
	private int lmMaterialListIgnoreStateMismatches_cancelCall(
			Object2IntOpenHashMap<BlockState> instance, Object curr, int key,
			Operation<Integer> original,
			@Local(ordinal = 0) BlockState stateSchematic,
			@Local(ordinal = 1) BlockState stateClient
	)
	{
		if (TweakerMoreConfigs.LM_MATERIAL_LIST_IGNORE_STATE_MISMATCHES.getBooleanValue())
		{
			// do nothing if block matches
			if (stateSchematic.getBlock() == stateClient.getBlock())
			{
				return 0;  // the return value is unused on the caller-side
			}
		}

		return original.call(instance, curr, key);
	}
}
