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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.minihudDisableLightLevelSpawnableCheck;

import fi.dy.masa.minihud.renderer.OverlayRendererLightLevel;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Restriction(require = @Condition(ModIds.minihud))
@Mixin(OverlayRendererLightLevel.class)
public abstract class OverlayRendererLightLevelMixin
{
	@Inject(
			method = "canSpawnAt",
			at = @At("HEAD"),
			remap = false,
			cancellable = true
	)
	private
	//#if MC < 11500
	//$$ static
	//#endif
	void minihudDisableLightOverlaySpawnCheck(
			int x, int y, int z, ChunkAccess chunk, Level world,
			//#if MC >= 11800
			//$$ boolean skipBlockCheck,
			//#endif
			CallbackInfoReturnable<Boolean> cir
	)
	{
		if (TweakerMoreConfigs.MINIHUD_DISABLE_LIGHT_OVERLAY_SPAWN_CHECK.getBooleanValue())
		{
			// simple checks
			BlockPos pos = new BlockPos(x, y, z);
			BlockPos posDown = pos.below();
			BlockState state = chunk.getBlockState(pos);
			boolean inSolidBlock = state.
					//#if MC >= 11600
					//$$ isSolidBlock
					//#else
					isRedstoneConductor
					//#endif
							(world, pos);
			BlockState stateDown = chunk.getBlockState(posDown);
			boolean aboveNonAirBlock = !stateDown.isAir() && !(stateDown.getBlock() instanceof LiquidBlock);
			cir.setReturnValue(!inSolidBlock && aboveNonAirBlock);
		}
	}
}
