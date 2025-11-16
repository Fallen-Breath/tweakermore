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
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;

@SuppressWarnings({"UnresolvedMixinReference", "UnusedMixin"})
@Restriction(require = @Condition(ModIds.optifine))
@Pseudo
@Mixin(targets = "net.optifine.shaders.Shaders")
public abstract class ShadersMixin
{
	@Group(min = 1, max = 1)
	@ModifyArg(
			method = {
					"useProgram",  // optifine < HD U G7 (1.16.4)
					"setProgramUniforms",  // optifine >= HD U G7 (1.16.4)
			},
			slice = @Slice(
					from = @At(
							value = "FIELD",
							target = "Lnet/optifine/shaders/Shaders;uniform_worldTime:Lnet/optifine/shaders/uniform/ShaderUniform1i;",
							remap = false
					)
			),
			at = @At(
					value = "INVOKE",
					target = "Lnet/optifine/shaders/Shaders;setProgramUniform1i(Lnet/optifine/shaders/uniform/ShaderUniform1i;I)V",
					ordinal = 0,
					remap = false
			),
			remap = false
	)
	private static int ofShaderGameTimeAsWorldTime(int worldTime)
	{
		if (TweakerMoreConfigs.SHADER_GAME_TIME_AS_WORLD_TIME.getBooleanValue())
		{
			ClientLevel clientWorld = Minecraft.getInstance().level;
			if (clientWorld != null)
			{
				worldTime = (int)(clientWorld.getGameTime() % 24000L);
			}
		}
		return worldTime;
	}
}
