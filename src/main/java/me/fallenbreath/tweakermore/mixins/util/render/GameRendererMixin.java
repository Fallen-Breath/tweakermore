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

package me.fallenbreath.tweakermore.mixins.util.render;

import com.llamalad7.mixinextras.sugar.Local;
import me.fallenbreath.tweakermore.util.render.RenderUtils;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

//#if MC >= 12106
//$$ import me.fallenbreath.tweakermore.util.render.context.InWorldGuiDrawer;
//#endif

//#if MC >= 12100
//$$ import net.minecraft.client.render.RenderTickCounter;
//#endif

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin
{
	@Inject(method = "render", at = @At("HEAD"))
	private void recordTickDelta_renderStart(
			CallbackInfo ci,
			//#if MC >= 12100
			//$$ @Local(argsOnly = true) RenderTickCounter tickCounter
			//#else
			@Local(argsOnly = true) float tickDelta
			//#endif
	)
	{
		RenderUtils.tickDelta =
				//#if MC >= 12100
				//$$ tickCounter.getTickDelta(false);
				//#else
				tickDelta;
				//#endif
	}

	@Inject(method = "render", at = @At("TAIL"))
	private void recordTickDelta_renderEnd(CallbackInfo ci)
	{
		RenderUtils.tickDelta = 1.0F;
	}

	//#if MC >= 12106
	//$$ @Inject(method = "close", at = @At("TAIL"))
	//$$ private void hookGameRendererClose(CallbackInfo ci)
	//$$ {
	//$$ 	InWorldGuiDrawer.closeInstance();
	//$$ }
	//#endif
}
