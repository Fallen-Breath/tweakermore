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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableVignetteDarkness;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import net.minecraft.client.gui.Gui;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class InGameHudMixin
{
	@Shadow public float
			//#if MC >= 11500
			vignetteBrightness;
			//#else
			//$$ field_2013;
			//#endif

	@Inject(method = "renderVignette", at = @At(value = "HEAD"))
	private void disableVignetteDarkness_modifyVignetteDarkness(CallbackInfo ci, @Share("pvd") LocalRef<Float> prevVignetteDarkness)
	{
		if (TweakerMoreConfigs.DISABLE_VIGNETTE_DARKNESS.getBooleanValue())
		{
			//#if MC >= 11500
			prevVignetteDarkness.set(this.vignetteBrightness);
			this.vignetteBrightness = 0.0F;
			//#else
			//$$ prevVignetteDarkness.set(this.field_2013);
			//$$ this.field_2013 = 0.0F;
			//#endif
		}
	}

	@Inject(method = "renderVignette", at = @At(value = "TAIL"))
	private void disableVignetteDarkness_restoreVignetteDarkness(CallbackInfo ci, @Share("pvd") LocalRef<Float> prevVignetteDarkness)
	{
		if (prevVignetteDarkness.get() != null)
		{
			//#if MC >= 11500
			this.vignetteBrightness = prevVignetteDarkness.get();
			//#else
			//$$ this.field_2013 = prevVignetteDarkness.get();
			//#endif
		}
	}
}
