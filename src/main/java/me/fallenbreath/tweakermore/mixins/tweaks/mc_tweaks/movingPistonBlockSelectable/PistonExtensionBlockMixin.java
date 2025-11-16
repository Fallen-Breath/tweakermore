/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.movingPistonBlockSelectable;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.movingPistonBlockSelectable.MovingPistonBlockSelectableHelper;
import net.minecraft.world.level.block.piston.MovingPistonBlock;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MovingPistonBlock.class)
public abstract class PistonExtensionBlockMixin
{
	@Inject(method = "getShape", at = @At("HEAD"), cancellable = true)
	private void movingPistonBlockSelectable_modifyOutlineShapeToFullCube(CallbackInfoReturnable<VoxelShape> cir)
	{
		// Add a check to avoid the TweakerMoreConfigs class being loaded too early (e.g. at Blocks.<clinit>)
		// since TweakerMoreConfigs.<clinit> might require accessing MinecraftClient.getInstance()
		if (MovingPistonBlockSelectableHelper.enabled)
		{
			if (TweakerMoreConfigs.MOVING_PISTON_BLOCK_SELECTABLE.getBooleanValue() && MovingPistonBlockSelectableHelper.applyOutlineShapeOverride.get())
			{
				cir.setReturnValue(Shapes.block());
			}
		}
	}
}
