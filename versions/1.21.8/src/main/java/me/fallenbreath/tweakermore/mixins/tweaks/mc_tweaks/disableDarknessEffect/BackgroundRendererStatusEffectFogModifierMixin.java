/*
 * This file is part of the TweakerMore project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2025  Fallen_Breath and contributors
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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableDarknessEffect;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.client.render.fog.StatusEffectFogModifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/**
 * mc1.14.4 ~ mc1.19.4: subproject 1.15.2 (main project)
 * mc1.19.4 ~ mc1.21.5: subproject 1.19.4
 * mc1.21.6+          : subproject 1.21.8        <--------
 */
@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19"))
@Mixin(StatusEffectFogModifier.class)
public abstract class BackgroundRendererStatusEffectFogModifierMixin
{
	@ModifyReturnValue(method = "shouldApply", at = @At("TAIL"))
	private boolean disableDarknessEffect_doNotApplyIfItIsDarknessEffect(boolean shouldApply)
	{
		if (TweakerMoreConfigs.DISABLE_DARKNESS_EFFECT.getBooleanValue())
		{
			if ((Object)this instanceof DarknessEffectFogModifier)
			{
				shouldApply = false;
			}
		}
		return shouldApply;
	}
}
