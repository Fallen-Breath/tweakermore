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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableDarkSkyRendering;

import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

//#if MC >= 11500
import net.minecraft.client.multiplayer.ClientLevel;
//#else
//$$ import net.minecraft.world.level.Level;
//#endif

@Mixin(
		//#if MC >= 11600
		//$$ ClientLevel.ClientLevelData.class
		//#elseif MC >= 11500
		ClientLevel.class
		//#else
		//$$ Level.class
		//#endif
)
public abstract class ClientWorldMixin
{
	@Inject(
			//#if MC >= 11500
			method = "getHorizonHeight",
			//#else
			//$$ method = "getHorizonHeight",
			//#endif
			at = @At("HEAD"),
			cancellable = true
	)
	private void disableDarkSkyRendering(CallbackInfoReturnable<Double> cir)
	{
		if (TweakerMoreConfigs.DISABLE_DARK_SKY_RENDERING.getBooleanValue())
		{
			cir.setReturnValue(-Double.MAX_VALUE);
		}
	}
}
