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

package me.fallenbreath.tweakermore.mixins.tweaks.features.pistorder;

import me.fallenbreath.tweakermore.impl.features.pistorder.ImmovableBlockPosRecorder;
import net.minecraft.block.piston.PistonHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonHandler.class)
public abstract class PistonHandlerMixin implements ImmovableBlockPosRecorder
{
	@Shadow @Final private BlockPos posTo;

	@Unique
	private BlockPos immovableBlockPos = null;

	@Inject(method = "calculatePush", at = @At("HEAD"))
	private void tkmPistorder_resetImmovableBlockPos(CallbackInfoReturnable<Boolean> cir)
	{
		this.immovableBlockPos = null;
	}

	@Inject(
			method = "calculatePush",
			at = @At(
					value = "RETURN",
					ordinal = 1
			)
	)
	private void tkmPistorder_recordImmovableBlockPos(CallbackInfoReturnable<Boolean> cir)
	{
		this.immovableBlockPos = this.posTo;
	}

	@ModifyVariable(
			method = "tryMove",
			slice = @Slice(
					from = @At(
							value = "INVOKE",
							target = "Lnet/minecraft/block/PistonBlock;isMovable(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/math/Direction;ZLnet/minecraft/util/math/Direction;)Z",
							ordinal = 2
					)
			),
			at = @At(
					value = "RETURN",
					ordinal = 0
			),
			ordinal = 1
	)
	private BlockPos tkmPistorder_recordImmovableBlockPos(BlockPos blockPos)
	{
		this.immovableBlockPos = blockPos;
		return blockPos;
	}

	@Override
	public @Nullable BlockPos getImmovableBlockPos$TKM()
	{
		return this.immovableBlockPos;
	}
}
