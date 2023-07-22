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

package me.fallenbreath.tweakermore.mixins.tweaks.mc_tweaks.disableDarknessEffect;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import me.fallenbreath.tweakermore.config.TweakerMoreConfigs;
import me.fallenbreath.tweakermore.util.ModIds;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Restriction(require = @Condition(value = ModIds.minecraft, versionPredicates = ">=1.19"))
@Mixin(targets = "net.minecraft.client.render.BackgroundRenderer$StatusEffectFogModifier")
public interface BackgroundRendererStatusEffectFogModifierMixin
{
	@Shadow StatusEffect getStatusEffect();

	@ModifyReturnValue(method = "shouldApply", at = @At("TAIL"))
	default boolean disableDarknessEffect_doNotApplyIfItIsDarknessEffect(boolean shouldApply)
	{
		if (TweakerMoreConfigs.DISABLE_DARKNESS_EFFECT.getBooleanValue())
		{
			if (this.getStatusEffect() == StatusEffects.DARKNESS)
			{
				shouldApply = false;
			}
		}
		return shouldApply;
	}
}
