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

package me.fallenbreath.tweakermore.mixins.tweaks.mod_tweaks.shaderGameTimeAsWorldTime;

import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@SuppressWarnings("UnresolvedMixinReference")
@Restriction(require = @Condition(ModIds.iris))
@Pseudo
@Mixin(targets = "net.coderbot.iris.uniforms.WorldTimeUniforms")
public abstract class WorldTimeUniformsMixin
{
	@Shadow(remap = false)
	private static ClientLevel getWorld() { return null; }

	@Inject(method = "getWorldDayTime", at = @At("HEAD"), cancellable = true, remap = false)
	private static void irisShaderGameTimeAsWorldTime(CallbackInfoReturnable<Integer> cir)
	{
		if (TweakerMoreConfigs.SHADER_GAME_TIME_AS_WORLD_TIME.getBooleanValue())
		{
			ClientLevel clientWorld = getWorld();
			if (clientWorld != null)
			{
				cir.setReturnValue((int)(clientWorld.getGameTime() % 24000L));
			}
		}
	}
}
