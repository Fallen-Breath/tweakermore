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

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.impl.mc_tweaks.movingPistonBlockSelectable.MovingPistonBlockSelectableHelper;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public abstract class WorldRendererMixin
{
	@Inject(
			//#if MC >= 11500
			method = "drawBlockOutline",
			//#else
			//$$ method = "drawHighlightedBlockOutline",
			//#endif
			at = @At("HEAD")
	)
	private void movingPistonBlockSelectable_blockOutlineRender_start(CallbackInfo ci, @Share("") LocalBooleanRef hasSet)
	{
		if (TweakerMoreConfigs.MOVING_PISTON_BLOCK_SELECTABLE.getBooleanValue())
		{
			MovingPistonBlockSelectableHelper.applyOutlineShapeOverride.set(true);
			hasSet.set(true);
		}
	}

	@Inject(
			//#if MC >= 11500
			method = "drawBlockOutline",
			//#else
			//$$ method = "drawHighlightedBlockOutline",
			//#endif
			at = @At("TAIL")
	)
	private void movingPistonBlockSelectable_blockOutlineRender_end(CallbackInfo ci, @Share("") LocalBooleanRef hasSet)
	{
		if (hasSet.get())
		{
			MovingPistonBlockSelectableHelper.applyOutlineShapeOverride.set(false);
		}
	}
}
