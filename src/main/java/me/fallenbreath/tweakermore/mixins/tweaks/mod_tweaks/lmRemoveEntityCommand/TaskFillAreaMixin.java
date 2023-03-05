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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.lmRemoveEntityCommand;

import fi.dy.masa.litematica.scheduler.tasks.TaskFillArea;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.impl.mod_tweaks.lmRemoveEntityCommand.LitematicaRemoveEntityCommandOverrider;
import me.fallenbreath.tweakermore.util.ModIds;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Restriction(require = @Condition(ModIds.litematica))
@Mixin(TaskFillArea.class)
public abstract class TaskFillAreaMixin
{
	@ModifyArg(
			//#if MC >= 11800
			//$$ method = "queueFillCommandsForBox",
			//#else
			method = "fillBoxCommands",
			//#endif
			at = @At(
					value = "INVOKE",
					target = "Ljava/lang/String;format(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;",
					ordinal = 0,
					remap = false
			),
			index = 0,
			remap = false
	)
	private String lmRemoveEntityCommand$TKM(String formatter)
	{
		return LitematicaRemoveEntityCommandOverrider.applyOverride(formatter);
	}
}
