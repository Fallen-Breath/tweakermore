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

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.features.pistorder.PistorderRenderer;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11600
//$$ import net.minecraft.block.AbstractBlock;
//#else
import net.minecraft.block.BlockState;
//#endif

@Restriction(conflict = @Condition(value = ModIds.pistorder, versionPredicates = "<=1.6.0"))
@Mixin(
		//#if MC >= 11600
		//$$ AbstractBlock.AbstractBlockState.class
		//#else
		BlockState.class
		//#endif
)
public abstract class AbstractBlockStateMixin
{
	@Inject(
			//#if MC >= 11500
			method = "onUse",
			//#else
			//$$ method = "activate",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void tkmPistorder_onPlayerRightClickBlock(
			World world,
			PlayerEntity player,
			//#if MC < 12006
			Hand hand,
			//#endif
			BlockHitResult hit,

			//#if MC >= 11500
			CallbackInfoReturnable<ActionResult> cir
			//#else
			//$$ CallbackInfoReturnable<Boolean> cir
			//#endif
	)
	{
		if (!PistorderRenderer.getInstance().isEnabled())
		{
			return;
		}

		if (world.isClient())
		{
			ActionResult result = PistorderRenderer.getInstance().
					//#if MC >= 12006
					//$$ onPlayerRightClickBlockWithMainHand(world, player, hit);
					//#else
					onPlayerRightClickBlock(world, player, hand, hit);
					//#endif

			//#if MC >= 11500
			boolean ok = result.isAccepted();
			//#else
			//$$ boolean ok = result == ActionResult.SUCCESS;
			//#endif

			if (ok && TweakerMoreConfigs.PISTORDER_SWING_HAND.getBooleanValue())
			{
				cir.setReturnValue(
						//#if MC >= 11500
						result
						//#else
						//$$ true
						//#endif
				);
			}
		}
	}
}
